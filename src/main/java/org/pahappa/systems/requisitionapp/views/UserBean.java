package org.pahappa.systems.requisitionapp.views;

import org.pahappa.systems.requisitionapp.models.Role;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.Gender;
import org.pahappa.systems.requisitionapp.models.utils.Permission;
import org.pahappa.systems.requisitionapp.services.RoleService;
import org.pahappa.systems.requisitionapp.services.UserService;
import org.pahappa.systems.requisitionapp.services.utils.MailService;
import org.pahappa.systems.requisitionapp.services.utils.ServiceUtils;
import org.pahappa.systems.requisitionapp.views.utils.NewChartBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.*;
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
    private Permission selectedPermission;
    private List<String> availableRoles;
    private String selectedRole;
    private User selectedUser;
    private String newRole;
    private Gender selectedGender;
    private boolean userLoaded = false;

    private User currentUser;

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
    private final MailService mailService;
    private final NewChartBean newChartBean;

    @Autowired
    public UserBean(UserService userService, RoleService roleService, MailService mailService, NewChartBean newChartBean) {
        this.userService = userService;
        this.roleService = roleService;
        this.mailService = mailService;
        this.newChartBean = newChartBean;
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
            String name = ServiceUtils.testUserNameInput(username);
            String pass = ServiceUtils.testPasswordInput(password);
            String first = ServiceUtils.testStringInput(firstName, "First Name");
            String last = ServiceUtils.testStringInput(lastName, "Last Name");
            String mail = ServiceUtils.testEmailInput(email);
            String phone = ServiceUtils.testPhoneNumberInput(phoneNumber);

            user.setUsername(name);
            user.setPassword(pass);
            user.setFirstName(first);
            user.setLastName(last);
            user.setEmail(mail);
            user.setGender(gender);
            user.setRole(role);
            user.setPhoneNumber(phone);
            userService.addUser(user);

            filteredUsers = userService.getAllUsers();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "User Creation Success", null));


            try{
                mailService.send("ahumuzaariyo@gmail.com", "tiadbqtshilfdprn", user.getEmail(), "Log In Details", "Your username is: " + user.getUsername() + "\nYour password is: " + password);
            } catch (Exception e){
                System.out.println("Error while sending mail" + e.getMessage());
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "User registered successfully, but email could not be sent.", null));
            }

            username=firstName=lastName=email=phoneNumber="";

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "User creation failed: "+e.getMessage(), null));
            System.out.println("Error Creating User:  "+e.getMessage());
        }
    }

    public void updateUser(){
        try{
            String username = ServiceUtils.testUserNameInput(selectedUser.getUsername());
            // String password = ServiceUtils.testPasswordInput(selectedUser.getPassword());
            String firstname = ServiceUtils.testStringInput(selectedUser.getFirstName(), "First Name");
            String lastname = ServiceUtils.testStringInput(selectedUser.getLastName(), "Last Name");
            String email = ServiceUtils.testEmailInput(selectedUser.getEmail());
            String phone = ServiceUtils.testPhoneNumberInput(selectedUser.getPhoneNumber());
            selectedUser.setUsername(username);
            selectedUser.setFirstName(firstname);
            selectedUser.setLastName(lastname);
            selectedUser.setEmail(email);
            selectedUser.setPhoneNumber(phone);
            // selectedUser.setPassword(password);
            selectedUser.setRole(roleService.getRoleByName(newRole));
            userService.updateUser(selectedUser);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "User Update Success", null));
            newChartBean.refreshChartData();
        }catch (Exception e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Updating User: "+e.getMessage(), null));
            System.out.println("Error Updating User:  "+e.getMessage());
        }

    }

    public void updateCurrentUser(){
        try{
            String username =ServiceUtils.testUserNameInput(currentUser.getUsername());
            String password = ServiceUtils.testPasswordInput(currentUser.getPassword());
            String firstname = ServiceUtils.testStringInput(currentUser.getFirstName(), "First Name");
            String lastname = ServiceUtils.testStringInput(currentUser.getLastName(), "Last Name");
            String email = ServiceUtils.testEmailInput(currentUser.getEmail());
            String phone = ServiceUtils.testPhoneNumberInput(currentUser.getPhoneNumber());

            currentUser.setUsername(username);
            currentUser.setFirstName(firstname);
            currentUser.setLastName(lastname);
            currentUser.setEmail(email);
            currentUser.setPhoneNumber(phone);
            currentUser.setPassword(password);
            userService.updateUser(currentUser);

            filteredUsers = userService.getAllUsers();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "User Update Success", null));
            newChartBean.refreshChartData();
        }catch (Exception e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Updating User: "+e.getMessage(), null));
            System.out.println("Error Updating User:  "+e.getMessage());
            e.printStackTrace();
        }

    }

    public void loadCurrentUser() {
        if(!userLoaded){
            selectedUser = LoginBean.getCurrentUser();
            userLoaded = true;
        }

    }

    public void deleteUser(User user){
        userService.deleteUser(user);
        filteredUsers = userService.getAllUsers();
        newChartBean.refreshChartData();
    }

    public void deleteAllUsers(){
        userService.deleteAll();
    }


    public void searchUsers() {
        if (!(searchQuery == null || searchQuery.isEmpty())) {
            filteredUsers = userService.searchUsers(searchQuery);
            return;
        }
        filteredUsers = userService.getAllUsers();

    }

    public void filterUsersByPermission(){
        try {
            if (selectedPermission == null) {
                filteredUsers = users;
            } else {
                for(Permission permission : Permission.values())
                    if(selectedPermission.equals(permission)){
                        filteredUsers = userService.filterUsersByPermission(permission);
                    }
            }
        }catch (Exception e){
            System.out.println("Error In User Bean: "+e.getMessage());
            e.printStackTrace();
        }

    }

    public void filterUsersByRole(){
        try {
            if (selectedRole == null || selectedRole.isEmpty()) {
                filteredUsers = users;
            } else {
                for(Role role : roleService.getAllRoles())
                    if(selectedRole.equals(role.getName())){
                        filteredUsers = userService.filterUsersByRole(role);
                    }
            }
        }catch (Exception e){
            System.out.println("Error In User Bean: "+e.getMessage());
            e.printStackTrace();
        }

    }

    public void filterUsersByGender(){
        try {
            if (selectedGender == null) {
                filteredUsers = users;
            } else {
                for(Gender gender : Gender.values())
                    if(selectedGender.equals(gender)){
                        filteredUsers = userService.filterUsersByGender(gender);
                    }
            }
        }catch (Exception e){
            System.out.println("Error In User Bean: "+e.getMessage());
            e.printStackTrace();
        }

    }

    public List<User> getUsers() {
        users = userService.getAllUsers();
        return users;
    }

    public String usersStringLabel(){
        return filteredUsers.size() != 1 ? " Users":" User";
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

    public Permission getSelectedPermission() {
        return selectedPermission;
    }

    public void setSelectedPermission(Permission selectedPermission) {
        this.selectedPermission = selectedPermission;
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

    public String getSelectedRole() {
        return selectedRole;
    }

    public void setSelectedRole(String selectedRole) {
        this.selectedRole = selectedRole;
    }

    public String  getNewRole() {
        return newRole;
    }

    public void setNewRole(String newRole) {
        this.newRole = newRole;
    }

    public Gender getSelectedGender() {
        return selectedGender;
    }

    public void setSelectedGender(Gender selectedGender) {
        this.selectedGender = selectedGender;
    }

    public User getCurrentUser() {
        currentUser = LoginBean.getCurrentUser();
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
