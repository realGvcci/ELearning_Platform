package com.esb.elearning.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

/**
 * End-to-end tests through the whole stack (controller -> service -> repository -> H2).
 * Each test is rolled back, so tests are independent.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ElearningApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // ---------- helpers ----------
    private ResultActions postJson(String url, String json) throws Exception {
        return mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private long createAndGetId(String url, String json) throws Exception {
        String body = postJson(url, json).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private long newInstructor() throws Exception {
        return createAndGetId("/api/instructors",
                "{\"firstName\":\"Sarah\",\"lastName\":\"Ben Ali\",\"email\":\"sarah@test.com\"}");
    }

    private long newCategory() throws Exception {
        return createAndGetId("/api/categories", "{\"name\":\"Programming\"}");
    }

    private long newCourse(long instructorId, long categoryId, String tagIds) throws Exception {
        return createAndGetId("/api/courses", "{\"title\":\"Spring Boot\",\"description\":\"REST APIs\","
                + "\"price\":49.90,\"level\":\"INTERMEDIATE\",\"durationHours\":20,"
                + "\"instructorId\":" + instructorId + ",\"categoryId\":" + categoryId
                + ",\"tagIds\":" + tagIds + "}");
    }

    private long newStudent(String email) throws Exception {
        return createAndGetId("/api/students",
                "{\"fullName\":\"Amine K\",\"email\":\"" + email + "\",\"country\":\"Tunisia\"}");
    }

    // ---------- CRUD + validation ----------
    @Test
    void createInstructor_returns201_thenGetReturns200() throws Exception {
        long id = newInstructor();
        mockMvc.perform(get("/api/instructors/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("sarah@test.com"));
    }

    @Test
    void createInstructor_withInvalidEmail_returns400WithFieldErrors() throws Exception {
        postJson("/api/instructors", "{\"firstName\":\"A\",\"lastName\":\"B\",\"email\":\"not-an-email\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.email").exists());
    }

    @Test
    void createInstructor_withDuplicateEmail_returns409() throws Exception {
        newInstructor();
        postJson("/api/instructors",
                "{\"firstName\":\"Other\",\"lastName\":\"Person\",\"email\":\"sarah@test.com\"}")
                .andExpect(status().isConflict());
    }

    @Test
    void getUnknownCourse_returns404() throws Exception {
        mockMvc.perform(get("/api/courses/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Course not found with id 99999"));
    }

    @Test
    void createCourse_withNegativePrice_returns400() throws Exception {
        postJson("/api/courses", "{\"title\":\"X\",\"price\":-5,\"level\":\"BEGINNER\",\"durationHours\":3,"
                + "\"instructorId\":1,\"categoryId\":1}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.price").exists());
    }

    @Test
    void createCourse_withInvalidLevel_returns400() throws Exception {
        postJson("/api/courses", "{\"title\":\"X\",\"price\":5,\"level\":\"EXPERT\",\"durationHours\":3,"
                + "\"instructorId\":1,\"categoryId\":1}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteInstructorWithCourses_returns422() throws Exception {
        long instructorId = newInstructor();
        newCourse(instructorId, newCategory(), "[]");
        mockMvc.perform(delete("/api/instructors/" + instructorId))
                .andExpect(status().isUnprocessableEntity());
    }

    // ---------- business flow ----------
    @Test
    void fullEnrollmentFlow() throws Exception {
        long courseId = newCourse(newInstructor(), newCategory(), "[]");
        long studentId = newStudent("amine@test.com");

        // 1. a draft course cannot be enrolled in
        postJson("/api/enrollments", "{\"studentId\":" + studentId + ",\"courseId\":" + courseId + "}")
                .andExpect(status().isUnprocessableEntity());

        // 2. publish, then enroll
        mockMvc.perform(patch("/api/courses/" + courseId + "/publish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.published").value(true));

        String body = postJson("/api/enrollments", "{\"studentId\":" + studentId + ",\"courseId\":" + courseId + "}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.pricePaid").value(49.90))
                .andReturn().getResponse().getContentAsString();
        long enrollmentId = ((Number) JsonPath.read(body, "$.id")).longValue();

        // 3. enrolling twice is refused
        postJson("/api/enrollments", "{\"studentId\":" + studentId + ",\"courseId\":" + courseId + "}")
                .andExpect(status().isConflict());

        // 4. progress to 100% completes the enrollment
        mockMvc.perform(patch("/api/enrollments/" + enrollmentId + "/progress")
                .contentType(MediaType.APPLICATION_JSON).content("{\"progress\":100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        // 5. invalid progress is refused
        mockMvc.perform(patch("/api/enrollments/" + enrollmentId + "/progress")
                .contentType(MediaType.APPLICATION_JSON).content("{\"progress\":150}"))
                .andExpect(status().isBadRequest());

        // 6. the student can now review the course (once)
        String review = "{\"studentId\":" + studentId + ",\"courseId\":" + courseId + ",\"rating\":5,\"comment\":\"Great\"}";
        postJson("/api/reviews", review).andExpect(status().isCreated());
        postJson("/api/reviews", review).andExpect(status().isConflict());

        // 7. custom queries
        mockMvc.perform(get("/api/reviews/top-rated?minRating=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].averageRating").value(5.0));
        mockMvc.perform(get("/api/enrollments/revenue-by-instructor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalRevenue").value(49.90));
        mockMvc.perform(get("/api/students/course/" + courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void reviewWithoutEnrollment_returns422() throws Exception {
        long courseId = newCourse(newInstructor(), newCategory(), "[]");
        long studentId = newStudent("nobody@test.com");
        postJson("/api/reviews", "{\"studentId\":" + studentId + ",\"courseId\":" + courseId + ",\"rating\":4}")
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void manyToMany_courseTags_andSearchByTag() throws Exception {
        long tagId = createAndGetId("/api/tags", "{\"name\":\"Java\"}");
        newCourse(newInstructor(), newCategory(), "[" + tagId + "]");

        mockMvc.perform(get("/api/courses/by-tag/java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tags[0].name").value("java"));
    }

    @Test
    void searchCourses_onlyReturnsPublishedMatches() throws Exception {
        long courseId = newCourse(newInstructor(), newCategory(), "[]");

        mockMvc.perform(get("/api/courses/search?keyword=spring"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(patch("/api/courses/" + courseId + "/publish")).andExpect(status().isOk());

        mockMvc.perform(get("/api/courses/search?keyword=spring&level=INTERMEDIATE&maxPrice=60"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(get("/api/courses/search?maxPrice=10"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void studentsWithoutEnrollment_returnsOnlyInactiveStudents() throws Exception {
        newStudent("lonely@test.com");
        mockMvc.perform(get("/api/students/without-enrollment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("lonely@test.com"));
    }
}
