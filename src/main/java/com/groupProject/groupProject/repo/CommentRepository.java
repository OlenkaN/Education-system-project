package com.groupProject.groupProject.repo;

import com.groupProject.groupProject.model.Comment;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Long> {
}
