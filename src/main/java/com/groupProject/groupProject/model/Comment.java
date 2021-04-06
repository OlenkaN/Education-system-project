package com.groupProject.groupProject.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "comments")
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "comment")
    private String comment;
    @ManyToOne
    private User user;
    @ManyToOne
    private Task task;

    @ManyToOne
    private ReadyTask readyTask;
}
