package com.groupProject.groupProject.Service;

import com.groupProject.groupProject.exception.UserAlreadyExistException;
import com.groupProject.groupProject.model.*;
import com.groupProject.groupProject.repo.CoursesAndRolesRepository;
import com.groupProject.groupProject.repo.CoursesAndUsersRepository;
import com.groupProject.groupProject.repo.RoleRepository;
import com.groupProject.groupProject.repo.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CoursesAndRolesRepository coursesAndRolesRepository;
    @Autowired
    private CoursesAndUsersRepository coursesAndUsersRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User saveUser(User user) throws UserAlreadyExistException {
        //RoleEntity userRole = roleEntityRepository.findByName("ROLE_USER");
        if (findByEmail(user.getEmail())!=null) {
            throw new UserAlreadyExistException(
                    "There is an account with that email address: "
                            +  user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByLoginAndPassword(String email, String password) {
        User userEntity = findByEmail(email);
        if (userEntity != null) {
            if (passwordEncoder.matches(password, userEntity.getPassword())) {
                return userEntity;
            }
        }
        return null;
    }
    public Role findByUserIdAndCourseId(Long courseId,Long userId)
    {
        CoursesAndUsers coursesAndUsers= coursesAndUsersRepository.findCoursesAndUsersByCourseIdAndUserId(courseId,userId);
        Optional<CoursesAndRoles> coursesAndRoles=coursesAndRolesRepository.findById(coursesAndUsers.getCoursesAndRoles().getId());
        ArrayList<CoursesAndRoles> res = new ArrayList<>();
        coursesAndRoles.ifPresent(res::add);
        return roleRepository.findById(res.get(0).getRoleId()).get();
    }
}