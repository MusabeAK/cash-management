package org.pahappa.systems.requisitionapp.services.Impl;

import org.pahappa.systems.requisitionapp.dao.RoleDao;
import org.pahappa.systems.requisitionapp.models.Role;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("RoleService")
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleDao roleDao;

    @Autowired
    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public void createRole(Role role) {
        roleDao.create(role);
    }

    public void updateRole(Role role) {
        roleDao.update(role);
    }

    public void deleteRole(Role role) {
        if (!role.getUsers().isEmpty()){
            throw new RuntimeException("Cannot delete a role that has users. Please delete or change their roles first.");
        }

        roleDao.delete(role);
    }

    public List<User> findUsersByRole(Role role) {
        return roleDao.findUsersByRole(role);
    }

    public Role getRoleById(Long roleId) {
        return roleDao.findById(roleId);
    }

    public Role getRoleByName(String name) {
        return roleDao.findByName(name);
    }

    public List<Role> getAllRoles() {
        return roleDao.findAll();
    }

    @Override
    public List<Role> searchRoles(String searchTerm) {
        return roleDao.searchRoles(searchTerm);
    }
}
