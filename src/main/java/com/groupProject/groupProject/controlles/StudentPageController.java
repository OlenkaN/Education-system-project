package com.groupProject.groupProject.controlles;

import com.groupProject.groupProject.model.*;
import com.groupProject.groupProject.repo.CourseRepository;
import com.groupProject.groupProject.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class StudentPageController {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/{userId}/courseStudentPage/{courseId}")
    public String getStudentMainPage(@PathVariable(value = "courseId") long courseId,
                                     @PathVariable(value = "userId") long userId,
                                     Model model) {
        Course course = courseRepository.findById(courseId).get();
        model.addAttribute("courseName", course.getName());
        model.addAttribute("courseId", course.getId());
        model.addAttribute("userId",userId);
        Iterable<Post> posts = course.getPosts();
        model.addAttribute("posts", posts);
        return "courseStudentPage";
    }
    @GetMapping("/user/{userId}/course/{courseId}/grades")
    public String viewUsersGrades(@PathVariable(value = "courseId") long courseId,
                                  @PathVariable(value = "userId") long userId,
                                  Model model) {
        System.out.println("In grades show controller");
        Course course = courseRepository.findById(courseId).get();
        User user=userRepository.findById(userId).get();

        model.addAttribute("courseName", course.getName());
        model.addAttribute("courseId", course.getId());

        Iterable<ReadyTask> readyTasks=user.getReadyTasks();
        model.addAttribute("readyTasks",readyTasks);
        model.addAttribute("user", user);
        model.addAttribute("userId", userId);
        return "courseStudentGrades";
    }
}