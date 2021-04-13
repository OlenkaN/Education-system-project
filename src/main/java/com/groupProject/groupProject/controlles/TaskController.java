package com.groupProject.groupProject.controlles;

import com.groupProject.groupProject.antiplagiarism.Normalizer;
import com.groupProject.groupProject.antiplagiarism.Plagiator;
import com.groupProject.groupProject.antiplagiarism.Program;
import com.groupProject.groupProject.model.*;
import com.groupProject.groupProject.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class TaskController {
    @Autowired
    private DocumentRepository docRep;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CoursesAndUsersRepository coursesAndUsersRepository;

    @Autowired
    private ReadyTaskRepository readyTaskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private ToDoItemRepository toDoItemRepository;


    @GetMapping("/admin/{userId}/course/{courseId}/tasks")
    public String viewTasksPage(@PathVariable(value = "courseId") long courseId,
                                @PathVariable(value = "userId") long userId,
                                Model model) {
        Course course = courseRepository.findById(courseId).get();

        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndUserId(courseId, userId).getToken();
        model.addAttribute("token", token);

        model.addAttribute("courseName", course.getName());
        model.addAttribute("courseId", course.getId());

        Iterable<Task> tasks = course.getTasks();
        model.addAttribute("tasks", tasks);

        model.addAttribute("userId", userId);
        return "task";
    }

    @PostMapping("/admin/{userId}/course/{courseId}/taskAdd")
    public String addTask(@PathVariable(value = "courseId") long courseId,
                          @PathVariable(value = "userId") long userId,
                          @RequestParam String title,
                          @RequestParam String date, @RequestParam String task,
                          @RequestParam MultipartFile exampleInputFile,
                          @RequestParam MultipartFile exampleInputFile1,
                          @RequestParam MultipartFile exampleInputFile2,
                          Model model) throws IOException, ParseException {
        Course course = courseRepository.findById(courseId).get();
        List<User> users = course.getUsers();
        Task t = new Task();
        t.setTitle(title);
        t.setTask(task);
        t.setCourse(course);
        t.setTime(date);
        taskRepository.save(t);
        course.addTask(t);
        for (User user : users) {
            if (user.getId() != userId) {
                ToDoItem toDoItem = new ToDoItem();
                toDoItem.setTitle(title+" from "+course.getName());
                toDoItem.setDone(false);
                toDoItem.setUser(user);
                toDoItem.setTask(t);
                toDoItemRepository.save(toDoItem);
            }

        }

        if (!exampleInputFile.isEmpty()) {
            uploadFile(exampleInputFile, t, course);
        }
        if (!exampleInputFile1.isEmpty()) {
            uploadFile(exampleInputFile1, t, course);
        }
        if (!exampleInputFile2.isEmpty()) {
            uploadFile(exampleInputFile2, t, course);
        }
        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndUserId(courseId, userId).getToken();


        return "redirect:/admin/" + userId + "/course/" + courseId + "/tasks?token=" + token;
    }

    @PostMapping("/admin/{userId}/course/{courseId}/task/{taskId}/student/{studentId}")
    public String plagiatCheck(@PathVariable(value = "courseId") long courseId,
                               @PathVariable(value = "userId") long userId,
                               @PathVariable(value = "studentId") long studentId,
                               @PathVariable(value = "taskId") long taskId,
                               @RequestParam MultipartFile exampleInputFile1,
                               @RequestParam MultipartFile exampleInputFile2,
                               Model model, RedirectAttributes ra) throws IOException, ParseException {
        Course course = courseRepository.findById(courseId).get();

        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndUserId(courseId, userId).getToken();
        String program1 = Program.getInput(Program.convert(exampleInputFile1));
        String program2 = Program.getInput(Program.convert(exampleInputFile2));
        Normalizer normalizer = new Normalizer();
        String normalizedCode1 = normalizer.getNormalizedCode(program1);

        String normalizedCode2 = normalizer.getNormalizedCode(program2);
        Plagiator plag = new Plagiator();
        System.out.println(plag.AveragePlagiatTest(normalizedCode1, normalizedCode2));
        System.out.println(plag.longestCommonSubstringTest(normalizedCode1, normalizedCode2));
        System.out.println(plag.WShinglingTest(normalizedCode1, normalizedCode2));
        ra.addFlashAttribute("message", "The result is: " + (double) Math.round((plag.AveragePlagiatTest(normalizedCode1, normalizedCode2)) * 1000d) / 1000d);

        return "redirect:/admin/" + userId + "/course/" + courseId + "/task/" + taskId + "/responses/user/" + studentId + "?token=" + token;
    }

    @PostMapping("/admin/{userId}/course/{courseId}/task/{taskId}/student/{studentId}/grade")
    public String setGrade(@PathVariable(value = "courseId") long courseId,
                           @PathVariable(value = "userId") long userId,
                           @PathVariable(value = "studentId") long studentId,
                           @PathVariable(value = "taskId") long taskId,
                           @Valid @ModelAttribute("grade") GradeRequest grade,
                           BindingResult bindingResult, Model model, RedirectAttributes attr) {
        Task task = taskRepository.findById(taskId).get();
        User studentUser = userRepository.findById(studentId).get();
        ReadyTask readyTask = readyTaskRepository.findByTaskAndUser(task, studentUser);
        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndUserId(courseId, userId).getToken();
        if (bindingResult.hasErrors()) {
            System.out.println("has error...");
            attr.addFlashAttribute("org.springframework.validation.BindingResult.grade", bindingResult);
            attr.addFlashAttribute("grade", grade);
            return "redirect:/admin/" + userId + "/course/" + courseId + "/task/" + taskId + "/responses/user/" + studentId + "?token=" + token;
        }
        Grade grade1 = gradeRepository.findByTaskAndUser(task, studentUser);
        if (grade1 == null) {
            grade1 = new Grade();
        }
        grade1.setValue(grade.getValue());
        gradeRepository.save(grade1);
        task.addGrade(grade1);
        studentUser.addGrade(grade1);
        gradeRepository.save(grade1);
        readyTask.setGrade(grade1);
        readyTaskRepository.save(readyTask);
        return "redirect:/admin/" + userId + "/course/" + courseId + "/task/" + taskId + "/responses/user/" + studentId + "?token=" + token;

    }


    @PostMapping("/admin/{userId}/course/{courseId}/task/{taskId}/delete")
    public String taskDelete(@PathVariable(value = "taskId") long taskId,
                             @PathVariable(value = "userId") long userId,
                             @PathVariable(value = "courseId") long courseId,
                             Model model) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        Iterable<Document> docs = task.getDocuments();
        for (Document doc : docs) {
            docRep.deleteById(doc.getId());
        }
        List<ToDoItem> toDoItems=toDoItemRepository.findByTask(task);
        for(ToDoItem toDoItem:toDoItems)
        {
            toDoItemRepository.delete(toDoItem);
        }


        Course course = courseRepository.findById(courseId).get();
        course.removeTask(task);
        taskRepository.delete(task);

        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndUserId(courseId, userId).getToken();

        return "redirect:/admin/" + userId + "/course/" + courseId + "/tasks?token=" + token;
    }

    @GetMapping("/admin/{userId}/course/{courseId}/task/{taskId}/edit")
    public String TaskEdit(@PathVariable(value = "taskId") long taskId,
                           @PathVariable(value = "userId") long userId,
                           @PathVariable(value = "courseId") long courseId, Model model) {

        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndUserId(courseId, userId).getToken();

        if (!taskRepository.existsById(taskId)) {
            return "redirect:/admin/" + userId + "/course/" + courseId + "/tasks?token=" + token;
        }
        Course course = courseRepository.findById(courseId).get();
        Optional<Task> task = taskRepository.findById(taskId);
        ArrayList<Task> res = new ArrayList<>();
        task.ifPresent(res::add);
        Task t = taskRepository.findById(taskId).get();
        Iterable<Document> docs = t.getDocuments();

        model.addAttribute("docs", docs);
        model.addAttribute("courseId", courseId);
        model.addAttribute("courseName", course.getName());
        model.addAttribute("tasks", res);
        model.addAttribute("taskId", taskId);
        model.addAttribute("token", token);
        model.addAttribute("userId", userId);

        return "task-edit";
    }

    @GetMapping("/admin/{userId}/course/{courseId}/task/{taskId}/responses")
    public String ResponsesAll(@PathVariable(value = "taskId") long taskId,
                               @PathVariable(value = "userId") long userId,
                               @PathVariable(value = "courseId") long courseId, Model model) {

        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndUserId(courseId, userId).getToken();

        if (!taskRepository.existsById(taskId)) {
            return "redirect:/admin/" + userId + "/course/" + courseId + "/tasks?token=" + token;
        }
        Task task = taskRepository.findById(taskId).get();
        Iterable<ReadyTask> readyTask = readyTaskRepository.findByTask(task);
        model.addAttribute("usersTasks", readyTask);
        model.addAttribute("courseId", courseId);
        model.addAttribute("taskName", task.getTitle());
        model.addAttribute("taskId", taskId);
        model.addAttribute("token", token);
        model.addAttribute("userId", userId);

        return "taskResponses";
    }

    @GetMapping("/admin/{userId}/course/{courseId}/task/{taskId}/responses/user/{studentId}")
    public String EstimateUserTask(@PathVariable(value = "taskId") long taskId,
                                   @PathVariable(value = "userId") long userId,
                                   @PathVariable(value = "courseId") long courseId,
                                   @PathVariable(value = "studentId") long studentId,
                                   Model model) {

        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndUserId(courseId, userId).getToken();

        if (!taskRepository.existsById(taskId)) {
            return "redirect:/admin/" + userId + "/course/" + courseId + "/tasks?token=" + token;
        }
        if (!userRepository.existsById(studentId)) {
            return "redirect:/admin/" + userId + "/course/" + courseId + "/tasks?token=" + token;
        }
        Task task = taskRepository.findById(taskId).get();
        User studentUser = userRepository.findById(studentId).get();
        Grade grade1 = gradeRepository.findByTaskAndUser(task, studentUser);
        if (grade1 == null) {
            model.addAttribute("taskGrade", null);
        } else {
            model.addAttribute("taskGrade", grade1.getValue());
        }
        ReadyTask readyTask = readyTaskRepository.findByTaskAndUser(task, studentUser);
        Iterable<Document> docs = readyTask.getDocuments();
        model.addAttribute("docs", docs);

        Iterable<Comment> comments = readyTask.getComments();
        model.addAttribute("comments", comments);

        model.addAttribute("courseId", courseId);
        model.addAttribute("taskName", task.getTitle());
        model.addAttribute("taskId", taskId);

        model.addAttribute("studentName", studentUser.getName());
        model.addAttribute("studentSurname", studentUser.getSurname());

        model.addAttribute("token", token);
        model.addAttribute("userId", userId);
        if (!model.containsAttribute("grade")) {
            model.addAttribute("grade", new GradeRequest());
        }

        return "taskEstimateUser";
    }

    @PostMapping("/admin/{userId}/course/{courseId}/task/{taskId}/user/{studentId}/comments")
    public String commentUserTaskbyAdmin(@PathVariable(value = "taskId") long taskId,
                                         @PathVariable(value = "userId") long userId,
                                         @PathVariable(value = "courseId") long courseId,
                                         @PathVariable(value = "studentId") long studentId,
                                         @RequestParam String comment,
                                         Model model) {
        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndUserId(courseId, userId).getToken();
        if (!taskRepository.existsById(taskId)) {
            return "redirect:/admin/" + userId + "/course/" + courseId + "/tasks?token=" + token;
        }
        if (!userRepository.existsById(studentId)) {
            return "redirect:/admin/" + userId + "/course/" + courseId + "/tasks?token=" + token;
        }
        Task task = taskRepository.findById(taskId).get();
        User studentUser = userRepository.findById(studentId).get();
        ReadyTask readyTask = readyTaskRepository.findByTaskAndUser(task, studentUser);
        Comment userComment = new Comment();
        userComment.setComment(comment);
        userComment.setUser(userRepository.findById(userId).get());
        userComment.setTask(task);
        commentRepository.save(userComment);
        readyTask.addComment(userComment);
        commentRepository.save(userComment);
        return "redirect:/admin/" + userId + "/course/" + courseId + "/task/" + taskId + "/responses/user/" + studentId + "?token=" + token;


    }


    @PostMapping("/admin/{userId}/course/{courseId}/task/{taskId}/edit")
    public String TaskUpdate(@PathVariable(value = "taskId") long taskId,
                             @PathVariable(value = "userId") long userId,
                             @PathVariable(value = "courseId") long courseId,
                             @RequestParam String title,
                             @RequestParam String date, @RequestParam String task, Model model) {
        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndUserId(courseId, userId).getToken();
        if (!taskRepository.existsById(taskId)) {
            return "redirect:/admin/" + userId + "/course/" + courseId + "/tasks?token=" + token;
        }
        Task t = taskRepository.findById(taskId).get();
        t.setTitle(title);
        t.setTime(date);
        t.setTask(task);
        taskRepository.save(t);
        return "redirect:/admin/" + userId + "/course/" + courseId + "/tasks?token=" + token;
    }

    @PostMapping("/admin/{userId}/course/{courseId}/task/{taskId}/upload/materials")
    public String uploadFile(
            @PathVariable(value = "taskId") long taskId,
            @PathVariable(value = "userId") long userId,
            @PathVariable(value = "courseId") long courseId,
            @RequestParam("document") MultipartFile multipartFile,
            RedirectAttributes ra) throws IOException {

        Task task = taskRepository.findById(taskId).get();
        Course course = courseRepository.findById(courseId).get();
        if (!multipartFile.isEmpty()) {
            uploadFile(multipartFile, task, course);
        }
        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndUserId(courseId, userId).getToken();
        return "redirect:/admin/" + userId + "/course/" + courseId + "/task/" + taskId + "/edit?token=" + token;
    }

    @PostMapping("/admin/{userId}/course/{courseId}/task/{taskId}/delete/doc/{docId}")
    public String TaskMaterialDelete(@PathVariable(value = "taskId") long taskId,
                                     @PathVariable(value = "docId") long docId,
                                     @PathVariable(value = "userId") long userId,
                                     @PathVariable(value = "courseId") long courseId, Model model) {
        Task task = taskRepository.findById(taskId).get();
        Course course = courseRepository.findById(courseId).get();
        Document doc = docRep.findById(docId).get();
        task.removeDocument(doc);
        course.removeDocument(doc);
        docRep.deleteById(docId);
        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndUserId(courseId, userId).getToken();
        return "redirect:/admin/" + userId + "/course/" + courseId + "/task/" + taskId + "/edit?token=" + token;

    }

    @GetMapping("/studentMain/{userId}/studentCourse/{courseId}/tasksForStudent")
    public String viewStudentTasksPage(@PathVariable(value = "courseId") long courseId,
                                       @PathVariable(value = "userId") long userId,
                                       Model model) {
        Course course = courseRepository.findById(courseId).get();
        model.addAttribute("courseName", course.getName());
        model.addAttribute("courseId", course.getId());
        model.addAttribute("userId", userId);

        Iterable<Task> tasks = course.getTasks();
        model.addAttribute("tasks", tasks);
        return "tasksForStudent";
    }

    @GetMapping("studentMain/{userId}/studentCourse/{courseId}/taskForStudent/{taskId}/response")
    public String viewAddReadyTask
            (
                    @PathVariable(value = "courseId") long courseId,
                    @PathVariable(value = "taskId") long taskId,
                    @PathVariable(value = "userId") long userId,
                    Model model
            ) {
        User user = userRepository.findById(userId).get();
        Course course = courseRepository.findById(courseId).get();
        Task task = taskRepository.findById(taskId).get();
        model.addAttribute("taskName", task.getTitle());
        model.addAttribute("taskInfo", task.getTask());
        ReadyTask readyTask = readyTaskRepository.findByTaskAndUser(task, user);
        if (readyTask != null) {
            Iterable<Comment> comments = readyTask.getComments();
            model.addAttribute("comments", comments);
            model.addAttribute("readyTask", readyTask);
            model.addAttribute("readyTaskTitle", readyTask.getTitle());
            Iterable<Document> docsUser = readyTask.getDocuments();
            model.addAttribute("docsUser", docsUser);

        } else {
            model.addAttribute("comments", null);
            model.addAttribute("readyTask", null);
            ;
            model.addAttribute("docsUser", null);
            model.addAttribute("readyTaskTitle", null);
        }
        Iterable<Document> docs = task.getDocuments();
        model.addAttribute("docs", docs);

        model.addAttribute("courseName", course.getName());
        model.addAttribute("userId", userId);
        model.addAttribute("courseId", courseId);
        model.addAttribute("taskId", taskId);
        model.addAttribute("title", "Smart Course");
        return "readyTaskAdd";
    }

    @PostMapping("/studentMain/{userId}/studentCourse/{courseId}/taskForStudent/{taskId}/response")
    public String addReadyTask(@PathVariable(value = "courseId") long courseId,
                               @PathVariable(value = "taskId") long taskId,
                               @PathVariable(value = "userId") long userId,
                               @RequestParam String title,
                               @RequestParam MultipartFile exampleInputFile,
                               @RequestParam MultipartFile exampleInputFile1,
                               @RequestParam MultipartFile exampleInputFile2,
                               Model model) throws IOException, ParseException {
        Task task = taskRepository.findById(taskId).get();
        User user = userRepository.findById(userId).get();
        Course course = courseRepository.findById(courseId).get();
        ReadyTask readyTask = readyTaskRepository.findByTaskAndUser(task, user);
        if (readyTask == null) {
            readyTask = new ReadyTask();
            ToDoItem toDoItem = toDoItemRepository.findByUserAndTask(user, task);
            toDoItem.setDone(true);
            toDoItemRepository.save(toDoItem);

        } else {
            try {
                Iterable<Document> docs = readyTask.getDocuments();
                for (Document doc : docs) {
                    readyTask.removeDocument(doc);
                    docRep.deleteById(doc.getId());
                }
            } catch (java.util.ConcurrentModificationException exception) {
                // Catch ConcurrentModificationExceptions.
                System.out.println((exception));
            }


        }
        readyTask.setTitle(title);

        readyTaskRepository.save(readyTask);

        task.addReadyTask(readyTask);
        user.addReadyTask(readyTask);

        readyTaskRepository.save(readyTask);

        if (!exampleInputFile.isEmpty()) {
            uploadReadyTaskFile(exampleInputFile, readyTask, course);
        }
        if (!exampleInputFile1.isEmpty()) {
            uploadReadyTaskFile(exampleInputFile1, readyTask, course);
        }
        if (!exampleInputFile2.isEmpty()) {
            uploadReadyTaskFile(exampleInputFile2, readyTask, course);
        }

        return "redirect:/studentMain/" + userId + "/studentCourse/" + courseId + "/taskForStudent/" + taskId + "/response";
    }

    @PostMapping("/user/{userId}/course/{courseId}/task/{taskId}/comments")
    public String commentUserTaskbyUser(@PathVariable(value = "taskId") long taskId,
                                        @PathVariable(value = "userId") long userId,
                                        @PathVariable(value = "courseId") long courseId,
                                        @RequestParam String comment,
                                        Model model) {

        if (!taskRepository.existsById(taskId)) {
            return "redirect:/studentMain/" + userId + "/studentCourse/" + courseId + "/tasksForStudent";
        }

        Task task = taskRepository.findById(taskId).get();
        User studentUser = userRepository.findById(userId).get();
        ReadyTask readyTask = readyTaskRepository.findByTaskAndUser(task, studentUser);
        Comment userComment = new Comment();
        userComment.setComment(comment);
        userComment.setUser(studentUser);
        userComment.setTask(task);
        commentRepository.save(userComment);
        readyTask.addComment(userComment);
        commentRepository.save(userComment);
        return "redirect:/studentMain/" + userId + "/studentCourse/" + courseId + "/taskForStudent/" + taskId + "/response";


    }

    private void uploadReadyTaskFile(MultipartFile multipartFile, ReadyTask readyTask, Course course) throws
            IOException {
        Document document = createDocument(multipartFile);
        document.setReadyTask(readyTask);
        readyTask.addDocument(document);
        document.setCourse(course);
        course.addDocument(document);
        docRep.save(document);
    }

    public void uploadFile(MultipartFile multipartFile, Task task, Course course) throws IOException {
        Document document = createDocument(multipartFile);
        document.setTask(task);
        task.addDocument(document);
        document.setCourse(course);
        course.addDocument(document);
        docRep.save(document);
    }

    private Document createDocument(MultipartFile multipartFile) throws IOException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        Document document = new Document();
        document.setName(fileName);
        document.setContent(multipartFile.getBytes());
        document.setSize(multipartFile.getSize());
        document.setUpload_time(new Date());
        return document;
    }


}
