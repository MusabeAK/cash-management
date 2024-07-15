package org.pahappa.systems.requisitionapp.services;

import org.pahappa.systems.requisitionapp.models.Role;
import java.util.List;

public interface RoleService {
    void createRole(Role role);
    void updateRole(Role role);
    void deleteRole(Role role);
    Role getRoleById(Long roleId);
    Role getRoleByName(String roleName);
    List<Role> getAllRoles();

}
