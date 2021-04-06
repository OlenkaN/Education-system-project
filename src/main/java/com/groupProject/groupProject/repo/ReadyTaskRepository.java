package com.groupProject.groupProject.repo;


import com.groupProject.groupProject.model.ReadyTask;
import com.groupProject.groupProject.model.Task;
import com.groupProject.groupProject.model.User;
import org.springframework.data.repository.CrudRepository;

public interface ReadyTaskRepository extends CrudRepository<ReadyTask, Long> {
    Iterable<ReadyTask> findByTask(Task task);
    ReadyTask  findByTaskAndUser(Task task, User user);
}
