package com.groupProject.groupProject.repo;

import com.groupProject.groupProject.model.CoursesAndRoles;
import com.groupProject.groupProject.model.CoursesAndUsers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursesAndUsersRepository extends JpaRepository<CoursesAndUsers, Long> {
    CoursesAndUsers findCoursesAndUsersByCourseIdAndUserId(Long courseId, Long userId);
    Iterable<CoursesAndUsers> findByCourseId(Long courseId);
}
