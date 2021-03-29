package com.groupProject.groupProject.controlles;

import com.groupProject.groupProject.model.Course;
import com.groupProject.groupProject.model.Post;
import com.groupProject.groupProject.repo.CourseRepository;
import com.groupProject.groupProject.repo.CoursesAndUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class TeacherPageController {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CoursesAndUsersRepository coursesAndUsersRepository;
    @GetMapping("/admin/{userId}/coursePage/{courseId}")
    public String TeacherContr(@PathVariable(value = "courseId")long courseId,
                               @PathVariable(value = "userId")long userId,
                               Model model)
    {
        Course course =courseRepository.findById(courseId).get();

        String token=coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndAndUserId(courseId,userId).getToken();
        model.addAttribute("token",token);

        model.addAttribute("courseName",course.getName());
        model.addAttribute("courseId",course.getId());

        Iterable <Post> posts= course.getPosts();
        model.addAttribute("posts",posts);

        model.addAttribute("userId",userId);
        return "courseTeacherPage";
    }
}
