package com.groupProject.groupProject.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "courses_users")
@NoArgsConstructor
@AllArgsConstructor
public class CoursesAndUsers {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "token")
    private String token;

    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)

    @JoinColumn(name = "courses_roles_id")
    private CoursesAndRoles coursesAndRoles;
}

