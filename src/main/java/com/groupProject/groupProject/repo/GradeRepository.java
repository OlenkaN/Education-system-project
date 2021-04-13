package com.groupProject.groupProject.repo;

import com.groupProject.groupProject.model.CoursesAndUsers;
import com.groupProject.groupProject.model.Grade;
import com.groupProject.groupProject.model.Task;
import com.groupProject.groupProject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    Grade findByTaskAndUser(Task task, User user);
}
