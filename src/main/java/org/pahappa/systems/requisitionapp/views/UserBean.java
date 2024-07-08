package org.pahappa.systems.requisitionapp.views;

import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.Gender;
import org.pahappa.systems.requisitionapp.models.utils.Role;
import org.pahappa.systems.requisitionapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import java.io.Serializable;

@ManagedBean(name="userBean")
@Component
@RequestScoped
public class UserBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String firstName;
    private String lastName;

    private final UserService userService;

    @Autowired
    public UserBean(UserService userService) {
        this.userService = userService;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void registerUser(){
        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail("aaaaaa");
            user.setGender(Gender.MALE);
            user.setRole(Role.ADMIN);
            user.setPhoneNumber("999");
            userService.addUser(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
