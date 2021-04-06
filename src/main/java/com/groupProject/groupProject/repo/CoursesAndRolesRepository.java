package com.groupProject.groupProject.repo;

import com.groupProject.groupProject.model.CoursesAndRoles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursesAndRolesRepository extends JpaRepository<CoursesAndRoles, Long> {
    Iterable<CoursesAndRoles> findByCourseId(Long courseId);
}
