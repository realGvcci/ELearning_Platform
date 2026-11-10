package com.esb.elearning.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.esb.elearning.model.Category;
import com.esb.elearning.model.Course;
import com.esb.elearning.model.CourseLevel;
import com.esb.elearning.model.Enrollment;
import com.esb.elearning.model.EnrollmentStatus;
import com.esb.elearning.model.Instructor;
import com.esb.elearning.model.Review;
import com.esb.elearning.model.Student;
import com.esb.elearning.model.Tag;
import com.esb.elearning.repository.CategoryRepository;
import com.esb.elearning.repository.CourseRepository;
import com.esb.elearning.repository.EnrollmentRepository;
import com.esb.elearning.repository.InstructorRepository;
import com.esb.elearning.repository.ReviewRepository;
import com.esb.elearning.repository.StudentRepository;
import com.esb.elearning.repository.TagRepository;

import lombok.RequiredArgsConstructor;

/** Inserts a small demo data set at startup, only when the database is empty. */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private final InstructorRepository instructorRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public void run(String... args) {
        if (courseRepository.count() > 0) {
            return;
        }

        Category programming = category("Programming", "Software development courses");
        Category data = category("Data Science", "Data analysis and machine learning");
        Category design = category("Design", "UI/UX and graphic design");

        Tag java = tag("java");
        Tag spring = tag("spring");
        Tag sql = tag("sql");
        Tag python = tag("python");
        Tag figma = tag("figma");

        Instructor sarah = instructor("Sarah", "Ben Ali", "sarah.benali@elearning.com", "Senior Java developer and trainer");
        Instructor mehdi = instructor("Mehdi", "Trabelsi", "mehdi.trabelsi@elearning.com", "Data scientist, 8 years of experience");
        Instructor lina = instructor("Lina", "Gharbi", "lina.gharbi@elearning.com", "Product designer");

        Course c1 = course("Spring Boot from Zero to Hero", "Build REST APIs with Spring Boot and JPA", "49.90",
                CourseLevel.INTERMEDIATE, 25, true, sarah, programming, Set.of(java, spring));
        Course c2 = course("Java Fundamentals", "Learn the Java language step by step", "29.90",
                CourseLevel.BEGINNER, 18, true, sarah, programming, Set.of(java));
        Course c3 = course("SQL for Data Analysis", "Queries, joins and aggregations", "39.90",
                CourseLevel.BEGINNER, 12, true, mehdi, data, Set.of(sql));
        Course c4 = course("Machine Learning with Python", "Supervised and unsupervised learning", "79.90",
                CourseLevel.ADVANCED, 40, true, mehdi, data, Set.of(python));
        Course c5 = course("UI Design with Figma", "Design modern interfaces", "34.90",
                CourseLevel.BEGINNER, 10, false, lina, design, Set.of(figma));

        Student amine = student("Amine Khelifi", "amine@mail.com", "Tunisia");
        Student sofia = student("Sofia Haddad", "sofia@mail.com", "Algeria");
        Student youssef = student("Youssef Mansour", "youssef@mail.com", "Morocco");
        student("Nour Jaziri", "nour@mail.com", "Tunisia"); // no enrollment -> shows up in custom query

        Enrollment e1 = enrollment(amine, c1, EnrollmentStatus.COMPLETED, 100);
        Enrollment e2 = enrollment(amine, c3, EnrollmentStatus.ACTIVE, 40);
        Enrollment e3 = enrollment(sofia, c1, EnrollmentStatus.ACTIVE, 65);
        Enrollment e4 = enrollment(sofia, c4, EnrollmentStatus.ACTIVE, 10);
        Enrollment e5 = enrollment(youssef, c2, EnrollmentStatus.COMPLETED, 100);
        enrollment(youssef, c1, EnrollmentStatus.CANCELLED, 5);

        review(e1, 5, "Excellent course, very clear!");
        review(e3, 4, "Good content, a bit fast in the JPA part.");
        review(e5, 5, "Perfect for beginners.");
        review(e2, 3, "Fine but I expected more exercises.");
        review(e4, 5, "Great introduction to ML.");
    }

    private Category category(String name, String description) {
        Category c = new Category();
        c.setName(name);
        c.setDescription(description);
        return categoryRepository.save(c);
    }

    private Tag tag(String name) {
        Tag t = new Tag();
        t.setName(name);
        return tagRepository.save(t);
    }

    private Instructor instructor(String first, String last, String email, String bio) {
        Instructor i = new Instructor();
        i.setFirstName(first);
        i.setLastName(last);
        i.setEmail(email);
        i.setBio(bio);
        return instructorRepository.save(i);
    }

    private Student student(String fullName, String email, String country) {
        Student s = new Student();
        s.setFullName(fullName);
        s.setEmail(email);
        s.setCountry(country);
        s.setRegistrationDate(LocalDate.now());
        return studentRepository.save(s);
    }

    private Course course(String title, String description, String price, CourseLevel level, int hours,
            boolean published, Instructor instructor, Category category, Set<Tag> tags) {
        Course c = new Course();
        c.setTitle(title);
        c.setDescription(description);
        c.setPrice(new BigDecimal(price));
        c.setLevel(level);
        c.setDurationHours(hours);
        c.setPublished(published);
        c.setCreatedAt(LocalDate.now());
        c.setInstructor(instructor);
        c.setCategory(category);
        c.setTags(new HashSet<>(tags));
        return courseRepository.save(c);
    }

    private Enrollment enrollment(Student student, Course course, EnrollmentStatus status, int progress) {
        Enrollment e = new Enrollment();
        e.setStudent(student);
        e.setCourse(course);
        e.setEnrollmentDate(LocalDate.now());
        e.setStatus(status);
        e.setProgress(progress);
        e.setPricePaid(course.getPrice());
        return enrollmentRepository.save(e);
    }

    private Review review(Enrollment enrollment, int rating, String comment) {
        Review r = new Review();
        r.setStudent(enrollment.getStudent());
        r.setCourse(enrollment.getCourse());
        r.setRating(rating);
        r.setComment(comment);
        r.setCreatedAt(LocalDate.now());
        return reviewRepository.save(r);
    }
}
