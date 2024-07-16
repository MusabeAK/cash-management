package org.pahappa.systems.requisitionapp.services;

import org.pahappa.systems.requisitionapp.models.Role;
import org.pahappa.systems.requisitionapp.models.User;

import java.util.List;

public interface RoleService {
    void createRole(Role role);
    void updateRole(Role role);
    void deleteRole(Role role);
    Role getRoleById(Long roleId);
    Role getRoleByName(String roleName);
    List<Role> getAllRoles();
    List<Role> searchRoles(String searchTerm);
    List<User> findUsersByRole(Role role);

}
