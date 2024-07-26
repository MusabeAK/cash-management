package org.pahappa.systems.requisitionapp.views;

import org.pahappa.systems.requisitionapp.constant.Hyperlink;
import org.pahappa.systems.requisitionapp.exceptions.UserDoesNotExistException;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.Permission;
import org.pahappa.systems.requisitionapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

import static org.pahappa.systems.requisitionapp.models.utils.Permission.VIEW_DASHBOARD;
import static org.pahappa.systems.requisitionapp.models.utils.Permission.VIEW_USERS;

@ManagedBean
@SessionScoped
@Component
public class LoginBean {
    private String identifier;
    private String password;

    private final UserService userService;

    @Autowired
    public LoginBean(UserService userService) {
        this.userService = userService;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String login() throws UserDoesNotExistException {

        try {
            User user = userService.loginUser(identifier, password);
            identifier = password = "";
            if (user != null) {
                FacesContext context = FacesContext.getCurrentInstance();
                ExternalContext externalContext = context.getExternalContext();
                externalContext.getSessionMap().put("currentUser", user);

                return getRedirectPath(user);

            }
            else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid username or password", null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Something went wrong", null));
            e.printStackTrace();
        }

        return null;
    }


    public void logout() throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.getSessionMap().put("logout", true);
        externalContext.redirect(externalContext.getRequestContextPath() + Hyperlink.LOGIN_VIEW);
    }

    public static User getCurrentUser() {
        // Used to fetch currently logged in user
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null) {
            // Log this occurrence or handle it appropriately
            return null;
        }
        ExternalContext externalContext = context.getExternalContext();
        return (User) externalContext.getSessionMap().get("currentUser");
    }

    public User currentUser() {
        return getCurrentUser();
    }

    public boolean hasPermission(String permissionName) {
        return currentUser().getRole().getPermissions().stream()
                .anyMatch(p -> p.name().equals(permissionName));
    }

    public String getRedirectPath(User user){
        for(Permission permission: user.getRole().getPermissions()) {
            if (permission.equals(Permission.VIEW_DASHBOARD)) {
                return Hyperlink.ADMIN_VIEW;
            }
        }

        for(Permission permission: user.getRole().getPermissions()) {
            if (permission.equals(Permission.VIEW_USERS)) {
                return Hyperlink.USERS_VIEW;
            }
        }

        for(Permission permission: user.getRole().getPermissions()) {
            if (permission.equals(Permission.VIEW_ROLES)) {
                return Hyperlink.ROLES_VIEW;
            }
        }

        for(Permission permission: user.getRole().getPermissions()) {
            if (permission.equals(Permission.VIEW_ALL_REQUISITIONS)) {
                return Hyperlink.ALL_REQUISITIONS_VIEW;
            }
        }

        for(Permission permission: user.getRole().getPermissions()) {
            if (permission.equals(Permission.VIEW_REQUISITIONS)) {
                return Hyperlink.REQUISITIONS_VIEW;
            }
        }

        for(Permission permission: user.getRole().getPermissions()) {
            if (permission.equals(Permission.VIEW_BUDGET_LINES)) {
                return Hyperlink.BUDGET_LINES_VIEW;
            }
        }

        for(Permission permission: user.getRole().getPermissions()) {
            if (permission.equals(Permission.VIEW_SETTINGS)) {
                return Hyperlink.SETTINGS_VIEW;
            }
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "User does not have permissions to view any pages", null));

        return null;
    }

}
