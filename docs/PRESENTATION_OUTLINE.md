# 5-minute presentation outline (6 slides)

| Slide | Time | Content |
|---|---|---|
| 1. Context | 30 s | E-Learning platform: instructors, courses, students, enrollments, reviews. Stack: Spring Boot 3, JPA, MySQL, Postman. |
| 2. Data model | 1 min | Show the ER diagram (README). 7 entities. Point out: OneToMany (Instructor→Course), ManyToMany (Course↔Tag with join table `course_tag`), and `Enrollment` as an association entity (Student↔Course + status, progress, pricePaid). |
| 3. Architecture | 45 s | Controller → Service → Repository → DB. DTOs for requests, entities for responses. `@JsonIgnore` on back-references to avoid infinite recursion. |
| 4. Custom queries & business rules | 1 min 15 s | 7 custom JPQL / derived queries (search, popular, revenue per instructor, top rated…). Rules: only published courses can be bought, one enrollment per student/course, progress 100 % ⇒ COMPLETED, only learners can review. |
| 5. Errors & validation | 45 s | Bean Validation annotations + `GlobalExceptionHandler`: 400 / 404 / 409 / 422 with a uniform JSON body. |
| 6. Live demo (Postman) | 45 s | Run the collection: all green. Show one 422 (enroll in a draft course) and one 400 (invalid email). |

## Questions you should be ready to answer
* Why is `Enrollment` an entity and not a simple `@ManyToMany` between Student and Course? → it carries its own attributes (status, progress, pricePaid, date).
* Which side owns the Course↔Tag relation? → `Course` (`@JoinTable`); `Tag` uses `mappedBy`.
* What does `mappedBy` mean? → the field of the other entity that owns the foreign key.
* Why DTOs (`CourseRequest`)? → the client sends ids, the service loads the real entities and checks they exist.
* Why `@Transactional` on services? → each business operation is atomic; `readOnly = true` for queries.
* Why `spring.jpa.open-in-view=false`? → no lazy loading in the web layer; collections are not serialized.
* Where is the business logic? → only in the service layer.
