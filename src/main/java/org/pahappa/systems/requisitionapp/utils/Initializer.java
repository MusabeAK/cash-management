package org.pahappa.systems.requisitionapp.utils;

import org.pahappa.systems.requisitionapp.dao.UserDAO;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.Gender;
import org.pahappa.systems.requisitionapp.models.utils.Role;
import org.pahappa.systems.requisitionapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class Initializer {

    private final UserService userService;

    @Autowired
    public Initializer(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        createAdminUser();
        createEmployeeUser();
        createOperationsUser();
    }

    private void createAdminUser(){
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            if(user.getUsername().equals("Admin")){
                return;
            }
        }
        try {
            User user = new User();
            user.setUsername("Admin");
            user.setPassword("@dmin123");
            user.setEmail("ahumuzaariyo@gmail.com");
            user.setFirstName("Admin");
            user.setLastName("Admin");
            user.setPhoneNumber("+256751461761");
            user.setGender(Gender.MALE);
            user.setRole(Role.ADMIN);
            userService.addUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createEmployeeUser(){
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            if(user.getUsername().equals("Employee")){
                return;
            }
        }
        try {
            User user = new User();
            user.setUsername("Employee");
            user.setPassword("empl0y3e");
            user.setEmail("ariyo@gmail.com");
            user.setFirstName("Employee");
            user.setLastName("Employee");
            user.setPhoneNumber("+256772461761");
            user.setGender(Gender.MALE);
            user.setRole(Role.EMPLOYEE);
            userService.addUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createOperationsUser(){
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            if(user.getUsername().equals("Operations")){
                return;
            }
        }
        try {
            User user = new User();
            user.setUsername("Operations");
            user.setPassword("Oper@tions");
            user.setEmail("operations@gmail.com");
            user.setFirstName("Operations");
            user.setLastName("Operations");
            user.setPhoneNumber("+256701461762");
            user.setGender(Gender.FEMALE);
            user.setRole(Role.OPERATIONS);
            userService.addUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
