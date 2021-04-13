package com.groupProject.groupProject.model;


import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "to_do_items")
@NoArgsConstructor
@AllArgsConstructor
public class ToDoItem {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "title")
    private String title;


    @Column(name = "done")
    private Boolean done;

    @ManyToOne
    private User user;

    @OneToOne
    @JoinColumn(name = "task_id")
    private Task task;

}
