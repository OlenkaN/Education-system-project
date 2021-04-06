package com.groupProject.groupProject.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "documents")
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;
    @Column(name = "size")
    private Long size;
    @Column(name = "upload_time")
    private Date upload_time;
    @Column(name = "content")
    private byte[] content;
    @ManyToOne
    private Post post;
    @ManyToOne
    private Course course;
    @ManyToOne
    private User user;
    @ManyToOne
    private ReadyTask readyTask;
    @ManyToOne
    private Task task;
}