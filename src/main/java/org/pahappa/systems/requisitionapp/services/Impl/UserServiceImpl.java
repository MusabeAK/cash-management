package org.pahappa.systems.requisitionapp.services.Impl;

import org.pahappa.systems.requisitionapp.dao.UserDAO;
import org.pahappa.systems.requisitionapp.exceptions.InvalidInputException;
import org.pahappa.systems.requisitionapp.exceptions.NullUserException;
import org.pahappa.systems.requisitionapp.exceptions.UserAlreadyExistsException;
import org.pahappa.systems.requisitionapp.exceptions.UserDoesNotExistException;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.Gender;
import org.pahappa.systems.requisitionapp.models.utils.Permission;
import org.pahappa.systems.requisitionapp.models.Role;
import org.pahappa.systems.requisitionapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service("UserService")
@Transactional
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    @Autowired
    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public List<User> getAllUsers() {

        for(User user : userDAO.getAllUsers()) {
            if(user.getUsername().equals("Admin")) {
                List<User> allUsers = userDAO.getAllUsers();
                allUsers.remove(user);
                return allUsers;
            }
        }
        return Collections.emptyList();
    }

    public boolean adminUserExists() {
        return !userDAO.checkForAdminUser().isEmpty();
    }

    public User getUserById(Long id) throws UserDoesNotExistException {
        if (id <= 0) {
            throw new InvalidInputException("ID should be greater than zero");
        }
        User user = userDAO.getById(id);
        if (user == null) {
            throw new UserDoesNotExistException("User with ID " + id + " not found");
        }
        return user;
    }

    public User getUserByUsername(String username) throws UserDoesNotExistException {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("Username should not be null or empty");
        }
        User user = userDAO.getUserByUsername(username);
        if (user == null) {
            throw new UserDoesNotExistException("There is no user with username " + username);
        }
        return user;
    }

    public void addUser(User user) throws UserAlreadyExistsException {
        if (user == null) {
            throw new NullUserException("User should not be null");
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new InvalidInputException("User's username should not be null or empty");
        }
        if (userDAO.getUserByUsername(user.getUsername()) != null) {
            throw new UserAlreadyExistsException("User with username " + user.getUsername() + " already exists.");
        }

        for (User usr : userDAO.getAllUsers()) {
            if(usr.getEmail().equals(user.getEmail())){
                throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists.");
            }
        }
        userDAO.save(user);
    }

    public void updateUser(User user) {
        if (user == null) {
            throw new NullUserException("User should not be null");
        }
        userDAO.update(user);
    }

    public void deleteUser(User user) {
        if (user == null) {
            throw new NullUserException("User should not be null");
        }

        userDAO.delete(user);
    }

    public void deleteAll(){
        userDAO.deleteAll();
    }

    public User loginUser(String identifier, String password) throws UserDoesNotExistException {
        try {

            User existingUserWithUsername = userDAO.getUserByUsername(identifier);
            User existingUserWithEmail = userDAO.getUserByEmail(identifier);
            String enteredPasswordEncoded = Base64.getEncoder().encodeToString(password.getBytes());
            if (existingUserWithUsername != null) {
                String storedPassword = existingUserWithUsername.getPassword();
                if (storedPassword.equals(enteredPasswordEncoded)) {
                    return existingUserWithUsername;
                }
            }
            if (existingUserWithEmail != null) {
                String storedPassword = existingUserWithEmail.getPassword();
                if (storedPassword.equals(enteredPasswordEncoded)) {
                    return existingUserWithEmail;
                }
            }
        }catch (Exception e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Something went wrong", null));
            System.out.println("Error: "+e.getMessage());
        }
        return null;
    }

    public List<User> searchUsers(String searchTerm) {
        return userDAO.searchUsers(searchTerm);
    }

    public List<User> filterUsersByPermission(Permission permission){
        try {
            User admin = getUserByUsername("Admin");
            List<User> filteredUsers = userDAO.filterUsersByPermission(permission);
            if(admin != null && !filteredUsers.isEmpty()) {
                filteredUsers.remove(admin);
            }
            return filteredUsers;
        }catch (Exception e){
            System.out.println("Error in Service: "+e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }

    }

    public List<User> filterUsersByRole(Role role){
        try {
            User admin = getUserByUsername("Admin");
            List<User> filteredUsers = userDAO.filterUsersByRole(role);
            if(admin != null && !filteredUsers.isEmpty()) {
                filteredUsers.remove(admin);
            }
            return filteredUsers;
        }catch (Exception e){
            System.out.println("Error in Service: "+e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }

    }

    public List<User> filterUsersByGender(Gender gender){
        try {
            User admin = getUserByUsername("Admin");
            List<User> filteredUsers = userDAO.filterUsersByGender(gender);
            if(admin != null && !filteredUsers.isEmpty()) {
                filteredUsers.remove(admin);
            }
            return filteredUsers;
        }catch (Exception e){
            System.out.println("Error in Service: "+e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }

    }

}
