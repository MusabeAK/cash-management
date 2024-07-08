package org.pahappa.systems.requisitionapp.services;

import org.pahappa.systems.requisitionapp.models.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(Long id);

    User getUserByUsername(String username);

    void addUser(User user);

    void updateUser(User user);

    void deleteUser(User user);

}