package com.groupProject.groupProject.controlles;

import com.groupProject.groupProject.Config.JwtProvider;
import com.groupProject.groupProject.model.*;
import com.groupProject.groupProject.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AddCourseController
{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CoursesAndUsersRepository coursesAndUsersRepository;
    @Autowired
    private CoursesAndRolesRepository coursesAndRolesRepository;
    @Autowired
    private JwtProvider jwtProvider;


    @GetMapping("*/userMain/{userId}/createCourse")
    public String courseCreate
            (
                    @PathVariable(value = "userId") long userId,
                    Model model
            )
    {
        if (!userRepository.existsById(userId))
        {
            return "redirect:/userMain/" + userId;
        }
            model.addAttribute("title", "Smart Course");
            model.addAttribute("create", "createCourse");
            return "courseAdd";
    }

    @PostMapping("*/userMain/{userId}/createCourse")
    public String CourseAdd
            (
                    @PathVariable(value = "userId") long userId,
                    @RequestParam String name,
                    Model model
            )
    {
        Course course = new Course(name);
        Role role=roleRepository.findByName("ROLE_ADMIN");
        User user = userRepository.findById(userId).get();
        courseRepository.save(course);
        user.addCourse(course);
        user.addRole(role);
        course.addRole(role);
        courseRepository.save(course);
        userRepository.save(user);

        CoursesAndUsers coursesAndUsers= new CoursesAndUsers();
        CoursesAndRoles coursesAndRoles=new CoursesAndRoles();

        coursesAndUsers.setUserId(userId);
        coursesAndUsers.setCourseId(course.getId());;
        coursesAndRoles.setCourseId(course.getId());
        coursesAndRoles.setRoleId(role.getId());
        coursesAndUsersRepository.save(coursesAndUsers);
        coursesAndRolesRepository.save(coursesAndRoles);
        coursesAndRoles.addCoursesAndUsers(coursesAndUsers);
        coursesAndUsersRepository.save(coursesAndUsers);
        coursesAndRolesRepository.save(coursesAndRoles);
        String token = jwtProvider.generateToken(user.getEmail(),course.getId());
        coursesAndUsers.setToken(token);
        coursesAndUsersRepository.save(coursesAndUsers);
        return "redirect:/admin/"+userId+"/coursePage/"+course.getId()+"?token="+token;
    }
}
