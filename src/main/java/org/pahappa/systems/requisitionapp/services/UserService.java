package org.pahappa.systems.requisitionapp.services;

import org.pahappa.systems.requisitionapp.exceptions.UserAlreadyExistsException;
import org.pahappa.systems.requisitionapp.exceptions.UserDoesNotExistException;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.Permission;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    boolean adminUserExists();

    User getUserById(Long id) throws UserDoesNotExistException;

    User getUserByUsername(String username) throws UserDoesNotExistException;

    void addUser(User user) throws UserAlreadyExistsException, UserDoesNotExistException;

    void updateUser(User user);

    void deleteUser(User user);

    void deleteAll();

    User loginUser(String username, String password) throws UserDoesNotExistException;

    List<User> searchUsers(String searchTerm);

    List<User> filterUsersByPermission(Permission permission);
}
