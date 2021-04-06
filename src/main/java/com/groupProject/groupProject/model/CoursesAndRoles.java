package com.groupProject.groupProject.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "roles_courses")
@NoArgsConstructor
@AllArgsConstructor
public class CoursesAndRoles {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "role_id")
    private Long roleId;


   /* @OneToOne(
            mappedBy = "coursesAndRoles",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
   private CoursesAndUsers coursesAndUsers;

    public void addCoursesAndUsers(CoursesAndUsers coursesAndUsers) {
        coursesAndUsers.setCoursesAndRoles(this);
        this.coursesAndUsers = coursesAndUsers;
    }

    public void removeCoursesAndUsers() {
        if (coursesAndUsers != null) {
            coursesAndUsers.setCoursesAndRoles(null);
            this.coursesAndUsers = null;
        }
    }*/
}
