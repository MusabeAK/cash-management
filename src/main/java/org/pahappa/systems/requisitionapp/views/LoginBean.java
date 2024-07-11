package org.pahappa.systems.requisitionapp.views;

import org.pahappa.systems.requisitionapp.constant.Hyperlink;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;

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
        String redirectPath = null;
        try {
            User user = userService.loginUser(identifier, password);
            if (user != null) {
                FacesContext context = FacesContext.getCurrentInstance();
                ExternalContext externalContext = context.getExternalContext();
                externalContext.getSessionMap().put("currentUser", user);
                switch (user.getRole()) {
                    case EMPLOYEE:
                        redirectPath = Hyperlink.EMPLOYEE_VIEW;
                        break;
                    case OPERATIONS:
                        redirectPath = Hyperlink.OPERATIONS_VIEW;
                        break;
                    case FINANCE:
                        redirectPath = Hyperlink.FINANCE_VIEW;
                        break;
                    case CEO:
                        redirectPath = Hyperlink.CEO_VIEW;
                        break;
                    case ADMIN:
                        redirectPath = Hyperlink.ADMIN_VIEW;
                        break;
                    default:
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid username or password", null));
                        redirectPath ="";
                }
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
            e.printStackTrace();
        }

        return redirectPath;
    }


    public void logout() throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.getSessionMap().put("logout", true);
        externalContext.redirect(externalContext.getRequestContextPath() + Hyperlink.LOGIN_VIEW);
    }

}
