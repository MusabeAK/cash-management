package org.pahappa.systems.requisitionapp.views;

import org.pahappa.systems.requisitionapp.models.Role;

import org.pahappa.systems.requisitionapp.models.utils.Permission;
import org.pahappa.systems.requisitionapp.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ManagedBean(name="roleBean")
@Component
@RequestScoped
public class RoleBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String roleName;
    private Set<String> selectedPermissions;
    private Set<String> availablePermissions;
    private List<Role> roles;
    private Role selectedRole;
    private Set<String> updateSelectedPermissions;
    private String searchQuery;
    private List<Role> filteredRoles;

    @PostConstruct
    public void init() {
        availablePermissions = Arrays.stream(Permission.values())
                .map(Enum::name)
                .collect(Collectors.toSet());
        roles = roleService.getAllRoles();
        filteredRoles = roleService.getAllRoles();
    }

    private final RoleService roleService;

    @Autowired
    public RoleBean(RoleService roleService) {
        this.roleService = roleService;
    }

    public void createRole(){
        try {
            Role role = new Role();
            role.setName(roleName);
            role.setPermissions(convertToPermissionSet(selectedPermissions));
            roleService.createRole(role);

            roles = roleService.getAllRoles();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,"Role creation success", null));
        }catch (Exception e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error creating role", null));
            System.out.println("Error creating role: " + e.getMessage());
        }

    }

    private Set<Permission> convertToPermissionSet(Set<String> stringPermissions) {
        return stringPermissions.stream()
                .map(Permission::valueOf)
                .collect(Collectors.toSet());
    }

    public void updateRole() {
        try {
            selectedRole.setPermissions(convertToPermissionSet(updateSelectedPermissions));
            roleService.updateRole(selectedRole);
            roles = roleService.getAllRoles();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Role Update success", null));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Updating role", null));
            System.out.println("Error Updating role: " + e.getMessage());
        }
    }

    public void deleteRole(Role role){
        try {
            roleService.deleteRole(role);
            roles = roleService.getAllRoles();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Role deleted", null));
        } catch (Exception e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error deleting role" + e.getMessage(), null));
        }

    }

    public void searchRoles() {
        if (!(searchQuery == null || searchQuery.isEmpty())) {
            filteredRoles = roleService.searchRoles(searchQuery);
            return;
        }
        filteredRoles = roleService.getAllRoles();

    }


    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String  roleName) {
        this.roleName = roleName;
    }

    public Set<String> getSelectedPermissions() {
        return selectedPermissions;
    }

    public void setSelectedPermissions(Set<String> selectedPermissions) {
        this.selectedPermissions = selectedPermissions;
    }

    public Set<String> getAvailablePermissions() {
        return availablePermissions;
    }

    public void setAvailablePermissions(Set<String> availablePermissions) {
        this.availablePermissions = availablePermissions;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Role getSelectedRole() {
        return selectedRole;
    }

    public void setSelectedRole(Role selectedRole) {
        this.selectedRole = selectedRole;
    }

    public void selectRole(Role role){
        this.selectedRole = role;
        this.updateSelectedPermissions = convertToStringSet(role.getPermissions());
    }

    private Set<String> convertToStringSet(Set<Permission> permissions) {
        return permissions.stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    public Set<String> getUpdateSelectedPermissions() {
        return updateSelectedPermissions;
    }

    public void setUpdateSelectedPermissions(Set<String> updateSelectedPermissions) {
        this.updateSelectedPermissions = updateSelectedPermissions;
    }


    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public List<Role> getFilteredRoles() {
        return filteredRoles;
    }

    public void setFilteredRoles(List<Role> filteredRoles) {
        this.filteredRoles = filteredRoles;
    }
}
