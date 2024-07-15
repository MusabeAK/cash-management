package org.pahappa.systems.requisitionapp.views;

import org.pahappa.systems.requisitionapp.models.BudgetLineCategory;
import org.pahappa.systems.requisitionapp.models.Role;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.Gender;
import org.pahappa.systems.requisitionapp.models.utils.Permission;
import org.pahappa.systems.requisitionapp.services.RoleService;
import org.pahappa.systems.requisitionapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
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
    private Permission permission;
    private String roleName;
    private Set<String> availablePermissions;
    private String phoneNumber;
    private Gender gender;
    private List<String> availableGenders;
    private List<User> users;
    private List<User> filteredUsers;
    private String searchQuery;
    private Set<Permission> selectedPermissions;
    private List<String> availableRoles;
    private User selectedUser;

    @PostConstruct
    public void init() {
        availableRoles = roleService.getAllRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        availablePermissions = Arrays.stream(Permission.values())
                .map(Enum::name)
                .collect(Collectors.toSet());

        availableGenders = Arrays.stream(Gender.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        users = filteredUsers = userService.getAllUsers();
    }


    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserBean(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String  roleName) {
        this.roleName = roleName;
    }

    public void registerUser(){
        try {
            Role role = roleService.getRoleByName(roleName);
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setGender(gender);
            user.setRole(role);
            user.setPhoneNumber(phoneNumber);
            userService.addUser(user);

            filteredUsers = userService.getAllUsers();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "User Creation Success", null));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "User creation failed", null));
            System.out.println("Error Creating User:  "+e.getMessage());
        }
    }

    public void updateUser(){
        try{
            userService.updateUser(selectedUser);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "User Update Success", null));
        }catch (Exception e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Updating User", null));
            System.out.println("Error Updating User:  "+e.getMessage());
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

    public void filterUsersByPermission(){
        if (selectedPermissions == null || selectedPermissions.isEmpty()) {
            filteredUsers = users;
        } else {
            for(Permission permission : Permission.values())
                if(selectedPermissions.contains(permission)){
                    filteredUsers = userService.filterUsersByPermission(permission);
                }

        }
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

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getAvailablePermissions() {
        return availablePermissions;
    }

    public void setAvailablePermissions(Set<String> availablePermissions) {
        this.availablePermissions = availablePermissions;
    }

    public Set<Permission> getSelectedPermission() {
        return selectedPermissions;
    }

    public void setSelectedPermission(Set<Permission> selectedPermissions) {
        this.selectedPermissions = selectedPermissions;
    }

    public Set<Permission> getSelectedPermissions() {
        return selectedPermissions;
    }

    public void setSelectedPermissions(Set<Permission> selectedPermissions) {
        this.selectedPermissions = selectedPermissions;
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

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }
}
