package com.groupProject.groupProject.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Entity
@Table(name = "ready_task")
@NoArgsConstructor
@AllArgsConstructor
public class ReadyTask {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "title")
    private String title;

    @ManyToOne
    private Task task;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "readyTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();

    public void removeDocument(Document document) {
        documents.remove( document );
        document.setReadyTask( null );
    }

    public void addDocument(Document document) {
        documents.add( document );
        document.setReadyTask( this );
    }
    @OneToMany(mappedBy = "readyTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public void removeComment(Comment comment) {
        comments.remove( comment );
        comment.setReadyTask( null );
    }

    public void addComment(Comment comment) {
        comments.add( comment );
        comment.setReadyTask( this );
    }

}
