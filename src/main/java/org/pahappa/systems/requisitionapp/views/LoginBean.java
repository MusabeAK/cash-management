package org.pahappa.systems.requisitionapp.views;

import org.pahappa.systems.requisitionapp.exceptions.UserDoesNotExistException;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.Role;
import org.pahappa.systems.requisitionapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean
@RequestScoped
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
        User user = userService.loginUser(identifier, password);
        if (user != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            externalContext.getSessionMap().put("currentUser", user);
            if (user.getRole().equals(Role.ADMIN)) {
                return "/pages/admin/budgetlines.xhtml?faces-redirect=true";
            } else if (user.getRole().equals(Role.EMPLOYEE)){
                return "/pages/employee/dashboard.xhtml?faces-redirect=true";
            } else if (user.getRole().equals(Role.OPERATIONS)) {
                return "/pages/operations/dashboard.xhtml?faces-redirect=true";
            } else if (user.getRole().equals(Role.CEO)) {
                return "/pages/ceo/dashboard.xhtml?faces-redirect=true";
            } else
                return "/pages/login/login.xhtml?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid username or password", null));
            return "";
        }
    }


}
