package com.groupProject.groupProject.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "courses")
@NoArgsConstructor
@AllArgsConstructor
public class Course
{
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "invitation")
    private String invitation;

    @ManyToMany(mappedBy = "courses",cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Role> roles = new ArrayList<>();

    public Course(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "courses", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();


    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();


    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();
    //Getters and setters are omitted for brevity

    public void addRole(Role role) {
        roles.add(role);
        role.getCourses().add(this);
    }

    public void removeRole(Role role) {
        roles.remove(role);
        role.getCourses().remove(this);
    }


    public void addPost(Post post) {
        posts.add( post );
        post.setCourses( this );
    }
    public void removePost(Post post) {
        posts.remove( post );
        post.setCourses( null );
    }

    public void addTask(Task task) {
        tasks.add( task );
        task.setCourse( this );
    }
    public void removeTask(Task task) {
        tasks.remove( task );
        task.setCourse( null );
    }

    public void removeDocument(Document document) {
        documents.remove( document );
        document.setCourse( null );
    }
    public void addDocument(Document document) {
        documents.add( document );
        document.setCourse( this );
    }

}
