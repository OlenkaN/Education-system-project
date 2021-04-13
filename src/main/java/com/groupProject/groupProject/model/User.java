package com.groupProject.groupProject.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"email"})
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ToDoItem> toDoItems = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Course> courses = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Role> roles = new ArrayList<>();


    public void addRole(Role role) {
        roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(Role role) {
        roles.remove(role);
        role.getUsers().remove(this);
    }
    public void addCourse(Course course) {
        courses.add(course);
        course.getUsers().add(this);
    }

    public void removeCourse(Course course) {
        courses.remove(course);
        course.getUsers().remove(this);
    }
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();



    public void removeDocument(Document document) {
        documents.remove( document );
        document.setUser( null );
    }
    public void addDocument(Document document) {
        documents.add( document );
        document.setUser( this );
    }
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment>  comments = new ArrayList<>();


    public void removeComment(Comment comment) {
        comments.remove( comment );
        comment.setUser( null );
    }
    public void addComment(Comment comment) {
        comments.add( comment );
        comment.setUser( this );

    }
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReadyTask> readyTasks = new ArrayList<>();

    public void removeReadyTask(ReadyTask readyTask) {
        readyTasks.remove( readyTask );
        readyTask.setUser( null );
    }
    public void addReadyTask(ReadyTask readyTask) {
        readyTasks.add( readyTask );
        readyTask.setUser( this );
    }
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grade> grades = new ArrayList<>();
    public void removeGrade(Grade grade) {
        grades.remove( grade );
        grade.setUser( null );
    }
    public void addGrade(Grade grade) {
        grades.add( grade );
        grade.setUser( this );
    }


}