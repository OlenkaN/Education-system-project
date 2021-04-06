package com.groupProject.groupProject.controlles;

import com.groupProject.groupProject.model.*;
import com.groupProject.groupProject.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ShowCoursesController {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CoursesAndUsersRepository coursesAndUsersRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoursesAndRolesRepository coursesAndRolesRepository;

    @Autowired
    private RoleRepository roleRepository;
    @GetMapping("*/userMain/{userId}/myCourses")
    public String Courses(
                               @PathVariable(value = "userId")long userId,
                               Model model)
    {

        User user=userRepository.findById(userId).get();

        Iterable <Course> courses= user.getCourses();
        model.addAttribute("courses",courses);

        model.addAttribute("userId",userId);
        model.addAttribute("userName",user.getName());

        return "myCourses";
    }
    @GetMapping("/user/{userId}/courses/{courseId}")
    public String RedirectToCourse(
            @PathVariable(value = "userId")long userId,
            @PathVariable(value = "courseId")long courseId,
            Model model)
    {

        User user=userRepository.findById(userId).get();
        Course course= courseRepository.findById(courseId).get();
        CoursesAndUsers coursesAndUsers=coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndUserId(courseId,userId);
        CoursesAndRoles coursesAndRoles=coursesAndUsers.getCoursesAndRoles();
        Role role=roleRepository.findById(coursesAndRoles.getRoleId()).get();
        String token=coursesAndUsers.getToken();
        if(role.getName().equals("ROLE_ADMIN"))
        {
            System.out.println("role admin show");
            return"redirect:/admin/" + userId + "/coursePage/" + course.getId() + "?token=" + token;
        }
        else
        {
            model.addAttribute("message","fromCoursesOKEY");
            return "errorpage";
        }
    }

}
