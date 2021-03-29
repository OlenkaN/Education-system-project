package com.groupProject.groupProject.controlles;

import com.groupProject.groupProject.model.Course;
import com.groupProject.groupProject.model.Document;
import com.groupProject.groupProject.model.Post;
import com.groupProject.groupProject.repo.CourseRepository;
import com.groupProject.groupProject.repo.CoursesAndUsersRepository;
import com.groupProject.groupProject.repo.DocumentRepository;
import com.groupProject.groupProject.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Controller
public class PostsController {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private DocumentRepository docRep;

    @Autowired
    private CoursesAndUsersRepository coursesAndUsersRepository;

    @GetMapping("/admin/{userId}/post/{courseId}/{postId}")
    public String PostDetails(@PathVariable(value = "postId") long postId,
                              @PathVariable(value = "userId") long userId,
                              @PathVariable(value = "courseId") long courseId, Model model) {
        if (!postRepository.existsById(postId)) {
            return "redirect:/admin/" + userId + "/coursePage/" + courseId;
        }
        Optional<Post> post = postRepository.findById(postId);
        ArrayList<Post> res = new ArrayList<>();
        post.ifPresent(res::add);
        Post post1 = postRepository.findById(postId).get();
        Iterable<Document> docs = post1.getDocuments();
        model.addAttribute("docs", docs);

        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndAndUserId(courseId, userId).getToken();
        model.addAttribute("token", token);

        model.addAttribute("courseId", courseId);
        model.addAttribute("post", res);
        model.addAttribute("postId", postId);
        model.addAttribute("userId", userId);
        return "post-details";
    }

    @GetMapping("/admin/{userId}/course/{courseId}/post/{postId}/edit")
    public String PostEdit(@PathVariable(value = "postId") long postId,
                           @PathVariable(value = "userId") long userId,
                           @PathVariable(value = "courseId") long courseId, Model model) {

        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndAndUserId(courseId, userId).getToken();
        model.addAttribute("token", token);

        if (!postRepository.existsById(postId)) {
            return "redirect:/admin/" + userId + "/coursePage/" + courseId+"?token=" + token;
        }
        Optional<Post> post = postRepository.findById(postId);
        ArrayList<Post> res = new ArrayList<>();
        post.ifPresent(res::add);
        Post post1 = postRepository.findById(postId).get();
        Iterable<Document> docs = post1.getDocuments();
        model.addAttribute("docs", docs);
        model.addAttribute("courseId", courseId);
        model.addAttribute("post", res);
        model.addAttribute("postId", postId);
        model.addAttribute("userId", userId);
        return "post-edit";
    }

    @PostMapping("/admin/{userId}/course/{courseId}/post/{postId}/edit")
    public String PostUpdate(@PathVariable(value = "postId") long postId,
                             @PathVariable(value = "courseId") long courseId,
                             @PathVariable(value = "userId") long userId,
                             @RequestParam String title,
                             @RequestParam String announce, @RequestParam String post, Model model) {

        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndAndUserId(courseId, userId).getToken();

        if (!postRepository.existsById(postId)) {
            return "redirect:/admin/" + userId + "/coursePage/" + courseId+"?token=" + token;
        }
        Post post1 = postRepository.findById(postId).get();
        post1.setTitle(title);
        post1.setAnnounce(announce);
        post1.setPost(post);
        postRepository.save(post1);
        return "redirect:/admin/" + userId + "/post/" + courseId + "/" + postId+"?token=" + token;
    }

    public void uploadFile(MultipartFile multipartFile, Post post, Course course) throws IOException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        Document document = new Document();
        document.setName(fileName);
        document.setContent(multipartFile.getBytes());
        document.setSize(multipartFile.getSize());
        document.setUpload_time(new Date());
        document.setPost(post);
        post.addDocument(document);
        document.setCourse(course);
        course.addDocument(document);
        docRep.save(document);
    }

    @PostMapping("/admin/{userId}/course/{courseId}")
    public String AddPost(@PathVariable(value = "courseId") long courseId,
                          @PathVariable(value = "userId") long userId,
                          @RequestParam String title,
                          @RequestParam String announce, @RequestParam String post,
                          @RequestParam MultipartFile exampleInputFile,
                          @RequestParam MultipartFile exampleInputFile1,
                          @RequestParam MultipartFile exampleInputFile2,
                          Model model) throws IOException {
        Course course = courseRepository.findById(courseId).get();
        Post p = new Post();
        p.setTitle(title);
        p.setAnnounce(announce);
        p.setPost(post);
        p.setCourses(course);
        postRepository.save(p);
        course.addPost(p);
        if (!exampleInputFile.isEmpty()) {
            uploadFile(exampleInputFile, p, course);
        }
        if (!exampleInputFile1.isEmpty()) {
            uploadFile(exampleInputFile1, p, course);
        }
        if (!exampleInputFile2.isEmpty()) {
            uploadFile(exampleInputFile2, p, course);
        }

        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndAndUserId(courseId, userId).getToken();
        return "redirect:/admin/" + userId + "/coursePage/" + course.getId()+"?token=" + token;
    }

    @PostMapping("/admin/{userId}/course/{courseId}/post/{postId}/upload/materials")
    public String uploadFile(@PathVariable(value = "postId") long postId,
                             @PathVariable(value = "courseId") long courseId,
                             @PathVariable(value = "userId") long userId,
                             @RequestParam("document") MultipartFile multipartFile,
                             RedirectAttributes ra) throws IOException {

        Post post = postRepository.findById(postId).get();
        Course course = courseRepository.findById(courseId).get();
        if (!multipartFile.isEmpty()) {
            uploadFile(multipartFile, post, course);
        }
        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndAndUserId(courseId, userId).getToken();
        return "redirect:/admin/" + userId + "/post/" + courseId + "/" + postId+"?token=" + token;
    }

    @PostMapping("/admin/{userId}/course/{courseId}/post/{postId}/remove/doc/{docId}")
    public String CourseMaterialDelete(@PathVariable(value = "postId") long postId,
                                       @PathVariable(value = "docId") long docId,
                                       @PathVariable(value = "userId") long userId,
                                       @PathVariable(value = "courseId") long courseId,
                                       Model model) {
        Post post = postRepository.findById(postId).get();
        Course course = courseRepository.findById(courseId).get();
        Document doc = docRep.findById(docId).get();
        post.removeDocument(doc);
        course.removeDocument(doc);
        docRep.deleteById(docId);
        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndAndUserId(courseId, userId).getToken();
        return "redirect:/admin/" + userId + "/post/" + courseId + "/" + postId+"?token=" + token;

    }

    @PostMapping("/admin/{userId}/course/{courseId}/post/{postId}/remove/post")
    public String CoursePostDelete(@PathVariable(value = "postId") long postId,
                                   @PathVariable(value = "userId") long userId,
                                   @PathVariable(value = "courseId") long courseId,
                                   Model model) {
        Post post = postRepository.findById(postId).orElseThrow();
        Iterable<Document> docs = post.getDocuments();
        for (Document doc : docs) {
            docRep.deleteById(doc.getId());
        }
        Course course = courseRepository.findById(courseId).get();
        course.removePost(post);
        postRepository.delete(post);
        String token = coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndAndUserId(courseId, userId).getToken();

        return "redirect:/admin/" + userId + "/coursePage/" + courseId+"?token=" + token;
    }
}
