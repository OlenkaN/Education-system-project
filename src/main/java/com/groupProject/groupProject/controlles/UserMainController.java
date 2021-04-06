package com.groupProject.groupProject.controlles;

import com.groupProject.groupProject.model.ToDoItem;
import com.groupProject.groupProject.model.User;
import com.groupProject.groupProject.repo.ToDoItemRepository;
import com.groupProject.groupProject.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;


@Controller
public class UserMainController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ToDoItemRepository toDoItemRepository;

    @GetMapping("*/userMain/{userId}")
    public String userMain(@PathVariable(value = "userId") long userId, Model model) {
        if (!userRepository.existsById(userId)) {
            return "redirect:";
        }
        User user = userRepository.findById(userId).get();
        model.addAttribute("greeting", "Hi, " + user.getName() + ", welcome back!");
        model.addAttribute("title", "SmartCourse");
        model.addAttribute("create", userId + "/createCourse");
        model.addAttribute("join", userId + "/joinCourse");
        model.addAttribute("courses", userId + "/myCourses");

        if (toDoItemRepository.existsByUser(user)) {
            List<ToDoItem> doneToDoItems = toDoItemRepository.findAllByUser(user)
                    .stream()
                    .filter(i -> i.getDone() == true)
                    .collect(Collectors.toList());

            List<ToDoItem> notDoneToDoItems = toDoItemRepository.findAllByUser(user)
                    .stream()
                    .filter(i -> i.getDone() != true)
                    .collect(Collectors.toList());

            model.addAttribute("notDoneToDoItems", notDoneToDoItems);
            model.addAttribute("doneToDoItems", doneToDoItems);
        }
        return "userMain";
    }


}
