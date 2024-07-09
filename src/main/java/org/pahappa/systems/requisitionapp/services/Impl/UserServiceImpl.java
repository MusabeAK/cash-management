package org.pahappa.systems.requisitionapp.services.Impl;

import org.pahappa.systems.requisitionapp.dao.UserDAO;
import org.pahappa.systems.requisitionapp.exceptions.InvalidInputException;
import org.pahappa.systems.requisitionapp.exceptions.NullUserException;
import org.pahappa.systems.requisitionapp.exceptions.UserAlreadyExistsException;
import org.pahappa.systems.requisitionapp.exceptions.UserDoesNotExistException;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return userDAO.getAllUsers();
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

    public User loginUser(String identifier, String password) throws UserDoesNotExistException {
        User existingUserWithUsername = userDAO.getUserByUsername(identifier);
        User existingUserWithEmail = userDAO.getUserByEmail(identifier);
        if (existingUserWithUsername != null){
            if (existingUserWithUsername.getPassword().equals(password)){
                return existingUserWithUsername;
            }
        }
        if (existingUserWithEmail != null){
            if (existingUserWithEmail.getPassword().equals(password)){
                return existingUserWithEmail;
            }
        }
        return null;
    }

}
