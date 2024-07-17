package org.pahappa.systems.requisitionapp.utils;

import org.pahappa.systems.requisitionapp.dao.UserDAO;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.Gender;
import org.pahappa.systems.requisitionapp.models.Role;
import org.pahappa.systems.requisitionapp.models.utils.Permission;
import org.pahappa.systems.requisitionapp.services.RoleService;
import org.pahappa.systems.requisitionapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.List;
import java.util.Set;

@Component
@Service
@Transactional
public class Initializer {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public Initializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostConstruct
    public void init() {
        createAdminUser();
        /*
        createEmployeeUser();
        createOperationsUser();
        createCEOUser();
        createFinanceUser();

         */
    }

    private void createAdminUser(){
        if(userService.adminUserExists()){
            return;
        }
        try {
            Role deafaultRole = new Role();
            deafaultRole.setName("DEFAULT");
            deafaultRole.setPermissions(Set.of(Permission.VIEW_SETTINGS));
            roleService.createRole(deafaultRole);

            Role role = new Role();
            role.setName("ADMIN");
            role.setPermissions(Set.of(Permission.values()));
            roleService.createRole(role);
            String password = Base64.getEncoder().encodeToString("@dmin123".getBytes());

            User user = new User();
            user.setUsername("Admin");
            user.setPassword(password);
            user.setEmail("ahumuzaariyo@gmail.com");
            user.setFirstName("Admin");
            user.setLastName("Admin");
            user.setPhoneNumber("+256751461761");
            user.setGender(Gender.MALE);
            user.setRole(role);
            userService.addUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/*
    private void createEmployeeUser(){
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            if(user.getUsername().equals("Employee")){
                return;
            }
        }
        try {
            Role role = new Role();
            role.setName("ROLE_EMPLOYEE");
            role.setPermissions(Set.of(Permission.CREATE_USER, Permission.APPROVE_BUDGET_LINE, Permission.EDIT_USER));
            roleService.createRole(role);

            User user = new User();
            user.setUsername("Employee");
            user.setPassword("employee");
            user.setEmail("ariyo@gmail.com");
            user.setFirstName("Employee");
            user.setLastName("Employee");
            user.setPhoneNumber("+256772461761");
            user.setGender(Gender.MALE);
            user.setRole(role);
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
            Role role = new Role();
            role.setName("ROLE_OPERATIONS");
            role.setPermissions(Set.of(Permission.CREATE_USER, Permission.APPROVE_BUDGET_LINE, Permission.EDIT_USER));
            roleService.createRole(role);

            User user = new User();
            user.setUsername("Operations");
            user.setPassword("operations");
            user.setEmail("operations@gmail.com");
            user.setFirstName("Operations");
            user.setLastName("Operations");
            user.setPhoneNumber("+256701461762");
            user.setGender(Gender.FEMALE);
            user.setRole(role);
            userService.addUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createCEOUser(){
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            if(user.getUsername().equals("CEO")){
                return;
            }
        }
        try {
            Role role = new Role();
            role.setName("ROLE_CEO");
            role.setPermissions(Set.of(Permission.CREATE_USER, Permission.APPROVE_BUDGET_LINE, Permission.EDIT_USER));
            roleService.createRole(role);

            User user = new User();
            user.setUsername("CEO");
            user.setPassword("ceo");
            user.setEmail("ceo@gmail.com");
            user.setFirstName("CEO");
            user.setLastName("CEO");
            user.setPhoneNumber("+256701461762");
            user.setGender(Gender.FEMALE);
            user.setRole(role);
            userService.addUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createFinanceUser(){
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            if(user.getUsername().equals("Finance")){
                return;
            }
        }
        try {
            Role role = new Role();
            role.setName("ROLE_FINANCE");
            role.setPermissions(Set.of(Permission.values()));
            roleService.createRole(role);

            User user = new User();
            user.setUsername("Finance");
            user.setPassword("finance");
            user.setEmail("money@gmail.com");
            user.setFirstName("Finance");
            user.setLastName("Finance");
            user.setPhoneNumber("+256701461762");
            user.setGender(Gender.MALE);
            user.setRole(role);
            userService.addUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/
}
