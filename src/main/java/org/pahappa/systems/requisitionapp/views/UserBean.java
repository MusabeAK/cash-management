package org.pahappa.systems.requisitionapp.views;

import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.Gender;
import org.pahappa.systems.requisitionapp.models.utils.Role;
import org.pahappa.systems.requisitionapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean(name="userBean")
@Component
@RequestScoped
public class UserBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private List<String> availableRoles;
    private String phoneNumber;
    private Gender gender;
    private List<String> availableGenders;
    private List<User> users;
    private List<User> filteredUsers;
    private String searchQuery;


    @PostConstruct
    public void init() {
        availableRoles = Arrays.stream(Role.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        availableGenders = Arrays.stream(Gender.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        filteredUsers = userService.getAllUsers();
    }


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
            user.setEmail(email);
            user.setGender(Gender.MALE);
            user.setRole(role);
            user.setPhoneNumber(phoneNumber);
            userService.addUser(user);

            filteredUsers = userService.getAllUsers();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUser(User user){
        userService.deleteUser(user);
        filteredUsers = userService.getAllUsers();
    }


    public void searchUsers() {
        if (!(searchQuery == null || searchQuery.isEmpty())) {
            filteredUsers = userService.searchUsers(searchQuery);
            return;
        }
        filteredUsers = userService.getAllUsers();

    }





    public List<User> getUsers() {
        users = userService.getAllUsers();
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<String> getAvailableGenders() {
        return availableGenders;
    }

    public void setAvailableGenders(List<String> availableGenders) {
        this.availableGenders = availableGenders;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getAvailableRoles() {
        return availableRoles;
    }

    public void setAvailableRoles(List<String> availableRoles) {
        this.availableRoles = availableRoles;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<User> getFilteredUsers() {
        return filteredUsers;
    }

    public void setFilteredUsers(List<User> filteredUsers) {
        this.filteredUsers = filteredUsers;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

}
