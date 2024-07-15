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
    private Set<Permission> selectedPermissions;
    private Set<String> availablePermissions;
    private List<Role> roles;
    private Role selectedRole;

    @PostConstruct
    public void init() {
        availablePermissions = Arrays.stream(Permission.values())
                .map(Enum::name)
                .collect(Collectors.toSet());
        roles = roleService.getAllRoles();
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
            role.setPermissions(selectedPermissions);
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

    public void updateRole(){
        try{
//            selectedRole.setPermissions(selectedPermissions);
            roleService.updateRole(selectedRole);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,"Role Update success", null));
        }catch (Exception e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error Updating role", null));
            System.out.println("Error Updating role: " + e.getMessage());
        }
    }

    public void deleteRole(Role role){
        roleService.deleteRole(role);
        roles = roleService.getAllRoles();
    }


    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String  roleName) {
        this.roleName = roleName;
    }

    public Set<Permission> getSelectedPermissions() {
        return selectedPermissions;
    }

    public void setSelectedPermissions(Set<Permission> selectedPermissions) {
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
}
