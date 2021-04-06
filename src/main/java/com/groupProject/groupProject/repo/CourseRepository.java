package com.groupProject.groupProject.repo;

import com.groupProject.groupProject.model.Course;
import org.springframework.data.repository.CrudRepository;

public interface CourseRepository extends CrudRepository<Course, Long> {
    Course findCourseByInvitation(String inv);
}