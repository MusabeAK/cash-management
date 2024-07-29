package org.pahappa.systems.requisitionapp.views;

import org.pahappa.systems.requisitionapp.models.Role;

import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.Permission;
import org.pahappa.systems.requisitionapp.models.utils.PermissionCategory;
import org.pahappa.systems.requisitionapp.services.RoleService;
import org.pahappa.systems.requisitionapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.*;
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

    private List<PermissionCategory> permissionCategories;

    private Map<String, String[]> categoryPermissions;
    private Map<String, String[]> updateCategoryPermissions;

    @PostConstruct
    public void init() {
        selectedPermissions = new HashSet<>();
        updateSelectedPermissions = new HashSet<>();

        availablePermissions = Arrays.stream(Permission.values())
                .map(Enum::name)
                .collect(Collectors.toSet());
        roles = roleService.getAllRoles();
        filteredRoles = roleService.getAllRoles();

        permissionCategories = new ArrayList<>();

        permissionCategories.add(new PermissionCategory("Users", Arrays.asList("VIEW_USERS", "EDIT_USER", "DELETE_USER", "CREATE_USER", "SEARCH_USERS")));
        permissionCategories.add(new PermissionCategory("Roles", Arrays.asList("CREATE_ROLE", "DELETE_ROLE", "EDIT_ROLE", "VIEW_ROLES")));
        permissionCategories.add(new PermissionCategory("Accountability", Arrays.asList("CREATE_ACCOUNTABILITY", "EDIT_ACCOUNTABILITY", "APPROVE_ACCOUNTABILITY")));
        permissionCategories.add(new PermissionCategory("Requisitions", Arrays.asList("CREATE_REQUISITION", "EDIT_REQUISITION", "DELETE_REQUISITION", "APPROVE_REQUISITION", "REJECT_REQUISITION", "VIEW_REQUISITIONS", "VIEW_ALL_REQUISITIONS", "REVIEW_REQUISITION")));
        permissionCategories.add(new PermissionCategory("Budget Lines", Arrays.asList("CREATE_BUDGET_LINE", "EDIT_BUDGET_LINE", "DELETE_BUDGET_LINE", "APPROVE_BUDGET_LINE", "REJECT_BUDGET_LINE", "VIEW_BUDGET_LINES")));
        permissionCategories.add(new PermissionCategory("Budget Line Categories", Arrays.asList("CREATE_BUDGET_LINE_CATEGORY", "EDIT_BUDGET_LINE_CATEGORY", "DELETE_BUDGET_LINE_CATEGORY", "APPROVE_BUDGET_LINE_CATEGORY", "REJECT_BUDGET_LINE_CATEGORY", "VIEW_BUDGET_LINE_CATEGORY")));
        permissionCategories.add(new PermissionCategory("Other", Arrays.asList("DISBURSE_MONEY", "VIEW_DASHBOARD", "VIEW_SETTINGS")));

        categoryPermissions = new HashMap<>();
        updateCategoryPermissions = new HashMap<>();
        for (PermissionCategory category : permissionCategories) {
            categoryPermissions.put(category.getCategoryName(), new String[0]);
            updateCategoryPermissions.put(category.getCategoryName(), new String[0]);
        }
    }

    private final RoleService roleService;
    private final UserService userService;
    private final UserBean userBean;

    @Autowired
    public RoleBean(RoleService roleService, UserService userService, UserBean userBean) {
        this.roleService = roleService;
        this.userService = userService;
        this.userBean = userBean;
    }

    public void createRole(){
        try {
            Role role = new Role();
            role.setName(roleName);
            Set<Permission> allPermissions = new HashSet<>();
            for (String[] permissions : categoryPermissions.values()) {
                allPermissions.addAll(convertToPermissionSet(Arrays.asList(permissions)));
            }

            System.out.println("All selected permissions: " + allPermissions);
            role.setPermissions(allPermissions);

            roleService.createRole(role);

            roles = roleService.getAllRoles();
            filteredRoles.add(role);
            filteredRoles = roleService.getAllRoles();
            userBean.init();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,"Role creation success", null));
        }catch (Exception e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error creating role", null));
            System.out.println("Error creating role: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private Set<Permission> convertToPermissionSet(Collection<String> stringPermissions) {
        return stringPermissions.stream()
                .map(Permission::valueOf)
                .collect(Collectors.toSet());
    }

    public void updateRole() {
        try {
            Set<Permission> allPermissions = new HashSet<>();
            for (String[] permissions : updateCategoryPermissions.values()) {
                allPermissions.addAll(convertToPermissionSet(Arrays.asList(permissions)));
            }

            System.out.println("All updated permissions: " + allPermissions);
            selectedRole.setPermissions(allPermissions);
            roleService.updateRole(selectedRole);
            roles = roleService.getAllRoles();
            userBean.init();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Role Update success", null));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Updating role", null));
            System.out.println("Error Updating role: " + e.getMessage());
        }
    }

    public int permissionCount(Role role){
        return role.getPermissions().size();
    }

    public void deleteRole(Role role){
        try {
            Role defaultRole = roleService.getRoleByName("DEFAULT");
            for(User user : roleService.findUsersByRole(role)){
//                System.out.println(user);
                user.setRole(defaultRole);
                userService.updateUser(user);

            }
            roleService.deleteRole(role);
            filteredRoles.remove(role);
            roles.remove(role);
            roles = roleService.getAllRoles();
            userBean.init();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Role deleted", null));
        } catch (Exception e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error deleting role: " + e.getMessage(), null));
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
        System.out.println("Getting selectedPermissions: " + selectedPermissions);
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

    public String convertRoleIdToString(int id){
        return String.format("RL%05d", id);
    }

    public String rolesStringLabel(){
        return filteredRoles.size() != 1 ? " Roles":" Role";
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

    public void selectRole(Role role) {
        this.selectedRole = role;
        // Populate updateCategoryPermissions with the current role's permissions
        for (Map.Entry<String, String[]> entry : updateCategoryPermissions.entrySet()) {
            String categoryName = entry.getKey();
            List<String> categoryPerms = permissionCategories.stream()
                    .filter(cat -> cat.getCategoryName().equals(categoryName))
                    .findFirst()
                    .map(PermissionCategory::getPermissions)
                    .orElse(Collections.emptyList());

            entry.setValue(role.getPermissions().stream()
                    .map(Permission::name)
                    .filter(categoryPerms::contains)
                    .toArray(String[]::new));
        }
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
        filteredRoles.sort((r1, r2) -> Long.compare(r2.getId(), r1.getId()));
        return filteredRoles;
    }

    public void setFilteredRoles(List<Role> filteredRoles) {
        this.filteredRoles = filteredRoles;
    }

    public List<PermissionCategory> getPermissionCategories() {
        return permissionCategories;
    }

    public Map<String, String[]> getCategoryPermissions() {
        return categoryPermissions;
    }

    public void setCategoryPermissions(Map<String, String[]> categoryPermissions) {
        this.categoryPermissions = categoryPermissions;
    }

    public void setPermissionCategories(List<PermissionCategory> permissionCategories) {
        this.permissionCategories = permissionCategories;
    }

    public Map<String, String[]> getUpdateCategoryPermissions() {
        return updateCategoryPermissions;
    }

    public void setUpdateCategoryPermissions(Map<String, String[]> updateCategoryPermissions) {
        this.updateCategoryPermissions = updateCategoryPermissions;
    }
}
