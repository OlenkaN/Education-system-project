package com.groupProject.groupProject.controlles;

import com.groupProject.groupProject.model.*;
import com.groupProject.groupProject.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Iterator;
import java.util.List;

@Controller
public class TeacherPageController {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CoursesAndUsersRepository coursesAndUsersRepository;
    @Autowired
    private CoursesAndRolesRepository coursesAndRolesRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DocumentRepository docRep;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private  ToDoItemRepository toDoItemRepository;

    @GetMapping("/admin/{userId}/coursePage/{courseId}")
    public String TeacherContr(@PathVariable(value = "courseId") long courseId,
                               @PathVariable(value = "userId") long userId,
                               Model model) {
        Course course = courseRepository.findById(courseId).get();

        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndUserId(courseId, userId).getToken();
        model.addAttribute("token", token);

        model.addAttribute("courseName", course.getName());
        model.addAttribute("courseId", course.getId());
        model.addAttribute("courseInv", course.getInvitation());

        Iterable<Post> posts = course.getPosts();
        model.addAttribute("posts", posts);

        model.addAttribute("userId", userId);
        return "courseTeacherPage";
    }


    @GetMapping("/admin/{userId}/course/{courseId}/students")
    public String viewUsersList(@PathVariable(value = "courseId") long courseId,
                                @PathVariable(value = "userId") long userId,
                                Model model) {
        Course course = courseRepository.findById(courseId).get();

        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndUserId(courseId, userId).getToken();
        model.addAttribute("token", token);

        model.addAttribute("courseName", course.getName());
        model.addAttribute("courseId", course.getId());

        Iterable<User> users = course.getUsers();
        model.addAttribute("users", users);

        model.addAttribute("userId", userId);
        return "usersList";
    }
    @GetMapping("/admin/{userId}/course/{courseId}/grades")
    public String viewUsersGrades(@PathVariable(value = "courseId") long courseId,
                                @PathVariable(value = "userId") long userId,
                                Model model) {
        System.out.println("In grades show controller");
        Course course = courseRepository.findById(courseId).get();

        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndUserId(courseId, userId).getToken();
        model.addAttribute("token", token);

        model.addAttribute("courseName", course.getName());
        model.addAttribute("courseId", course.getId());

        Iterable<Task> tasks=course.getTasks();
        model.addAttribute("tasks",tasks);
        Iterable<User> users = course.getUsers();
        model.addAttribute("users", users);

        model.addAttribute("userId", userId);
        return "courseTeacherGrades";
    }


    @PostMapping("/admin/{userId}/course/{courseId}/delete")
    public String deleteCourse(@PathVariable(value = "courseId") long courseId,
                               @PathVariable(value = "userId") long userId,
                               Model model) {
        User user = userRepository.findById(userId).get();
        Course course = courseRepository.findById(courseId).get();
        CoursesAndUsers coursesAndUsers = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndUserId(courseId, userId);
        CoursesAndRoles coursesAndRoles = coursesAndUsers.getCoursesAndRoles();
        Role role = roleRepository.findById(coursesAndRoles.getRoleId()).get();
        if (role.getName().equals("ROLE_ADMIN")) {
            Iterable<Task> tasks = course.getTasks();
            for (Task t : tasks) {
                Iterable<Document> d = t.getDocuments();
                for (Document doc : d) {
                    docRep.deleteById(doc.getId());
                }
                List<ToDoItem> toDoItems=toDoItemRepository.findByTask(t);
                for(ToDoItem toDoItem:toDoItems)
                {
                    toDoItemRepository.deleteById(toDoItem.getId());
                }

                taskRepository.delete(t);
            }

            Iterable<Document> docs = course.getDocuments();
            for (Document doc : docs) {
                docRep.deleteById(doc.getId());
            }
            Iterable<CoursesAndUsers> coursesAndUsers1 = coursesAndUsersRepository.findByCourseId(courseId);
            for (CoursesAndUsers cu: coursesAndUsers1) {
                coursesAndUsersRepository.delete(cu);
            }
            Iterable<CoursesAndRoles> coursesAndRoles1 = coursesAndRolesRepository.findByCourseId(courseId);
            for (CoursesAndRoles cr: coursesAndRoles1) {
                coursesAndRolesRepository.delete(cr);
            }

            Iterable<Post> posts = course.getPosts();
            for (Post p : posts) {
                Iterable<Document> d = p.getDocuments();
                for (Document doc : d) {
                    docRep.deleteById(doc.getId());
                }

                postRepository.delete(p);
            }
            try {
                List<User> users = course.getUsers();
                for (User u : users) {
                    u.removeCourse(course);
                }
            } catch (java.util.ConcurrentModificationException exception) {
                // Catch ConcurrentModificationExceptions.
                System.out.println((exception));
            }
            Role r1 = roleRepository.findById(1L).get();
            Role r2 = roleRepository.findById(2L).get();
            course.removeRole(r1);
            course.removeRole(r2);

            courseRepository.delete(course);
            return "redirect:/smartCourse/userMain/" + userId;

        } else {
            model.addAttribute("message", "You don't have access");
            return "errorpage";

        }


    }
}
