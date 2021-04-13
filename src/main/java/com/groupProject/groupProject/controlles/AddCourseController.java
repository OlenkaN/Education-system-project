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

import java.util.List;
import java.util.Properties;
import java.util.UUID;

@Controller
public class AddCourseController {
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
     @Autowired
    private ToDoItemRepository toDoItemRepository;


    private String generateUniqueIdString() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-", "").toUpperCase();
    }

    @GetMapping("*/userMain/{userId}/createCourse")
    public String courseCreate
            (
                    @PathVariable(value = "userId") long userId,
                    Model model
            ) {
        if (!userRepository.existsById(userId)) {
            return "redirect:/userMain/" + userId;
        }
        model.addAttribute("title", "Smart Course");
        model.addAttribute("create", "createCourse");
        return "courseAdd";
    }

    @GetMapping("*/userMain/{userId}/joinCourse")
    public String addToCourse
            (
                    @PathVariable(value = "userId") long userId,
                    Model model
            ) {
        if (!userRepository.existsById(userId)) {
            return "redirect:/userMain/" + userId;
        }
        model.addAttribute("title", "Smart Course");
        model.addAttribute("join", "joinCourse");
        return "addToCourse";
    }

    @PostMapping("*/userMain/{userId}/joinCourse")
    public String addToCourseByInv
            (
                    @PathVariable(value = "userId") long userId,
                    @RequestParam String code,
                    Model model
            )
    {
        Course course =courseRepository.findCourseByInvitation(code);

        if(course==null)
        {
            model.addAttribute("message","There no course with such invitation code");
            return "errorpage";
        }
        if(coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndUserId(course.getId(), userId)!=null)
        {
            model.addAttribute("message","You are already a member of this course");
            return "errorpage";
        }
        Role role = roleRepository.findByName("ROLE_USER");
        User user = userRepository.findById(userId).get();
        String token=madeDependencies(user,course,role,false);
        List<Task> tasks=course.getTasks();
        for(Task task :tasks)
        {
            ToDoItem toDoItem = new ToDoItem();
            toDoItem.setTitle(task.getTitle()+" from "+course.getName());
            toDoItem.setDone(false);
            toDoItem.setUser(user);
            toDoItem.setTask(task);
            toDoItemRepository.save(toDoItem);

        }
        return "redirect:/user/"+userId+"/courseStudentPage/"+course.getId();
    }

    @PostMapping("*/userMain/{userId}/createCourse")
    public String CourseAdd
            (
                    @PathVariable(value = "userId") long userId,
                    @RequestParam String name,
                    Model model
            ) {
        Course course = new Course(name);
        Role role = roleRepository.findByName("ROLE_ADMIN");
        User user = userRepository.findById(userId).get();
       String token =madeDependencies(user,course,role,true);
        return "redirect:/admin/" + userId + "/coursePage/" + course.getId() + "?token=" + token;
    }
    private String madeDependencies(User user,Course course,Role role,Boolean flag)
    {
        if(flag) {
            course.setInvitation(generateUniqueIdString());
            courseRepository.save(course);
        }
        user.addCourse(course);
        user.addRole(role);
        course.addRole(role);

        courseRepository.save(course);
        userRepository.save(user);

        CoursesAndUsers coursesAndUsers = new CoursesAndUsers();
        CoursesAndRoles coursesAndRoles = new CoursesAndRoles();

        coursesAndUsers.setUserId(user.getId());
        coursesAndUsers.setCourseId(course.getId());

        coursesAndRoles.setCourseId(course.getId());
        coursesAndRoles.setRoleId(role.getId());

        String token = jwtProvider.generateToken(user.getEmail(), course.getId());
        coursesAndUsers.setToken(token);


        coursesAndRolesRepository.save(coursesAndRoles);
        coursesAndUsers.setCoursesAndRoles(coursesAndRoles);
        coursesAndUsersRepository.save(coursesAndUsers);



        CoursesAndUsers TEST= coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndUserId(course.getId(), user.getId());
        CoursesAndRoles test2=TEST.getCoursesAndRoles();
        return token;
    }


}

