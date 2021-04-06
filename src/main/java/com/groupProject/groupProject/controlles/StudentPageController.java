package com.groupProject.groupProject.controlles;

import com.groupProject.groupProject.model.Course;
import com.groupProject.groupProject.model.Post;
import com.groupProject.groupProject.repo.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class StudentPageController {
    @Autowired
    private CourseRepository courseRepository;

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
}