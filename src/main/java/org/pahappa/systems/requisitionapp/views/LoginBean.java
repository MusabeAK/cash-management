package org.pahappa.systems.requisitionapp.views;


import org.pahappa.systems.requisitionapp.constant.Hyperlink;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Base64;

@ManagedBean(name = "loginBean")
@Component
@SessionScoped
public class LoginBean {

    private String username;
    private String password;

    private final UserService loginService;

    @Autowired
    public LoginBean(UserService loginService) {
        this.loginService = loginService;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @PostConstruct
    public void init() {

    }

    public String login() {
        User user = loginService.loginUser(username, password);

        if (user != null) {
            try {
                String encodedPassword = Base64.getEncoder().encodeToString("admin123".getBytes());;
                FacesContext context = FacesContext.getCurrentInstance();
                ExternalContext externalContext = context.getExternalContext();
                externalContext.getSessionMap().put("currentUser", user);
                if(user.getPassword().equals(encodedPassword) && user.getUsername().equals("123@admin"))
                    // Redirect to the dependants page
                    externalContext.redirect(externalContext.getRequestContextPath() + Hyperlink.ADMIN_VIEW);
                else
                    // Redirect to the dependants page
                    externalContext.redirect(externalContext.getRequestContextPath() + Hyperlink.CEO_VIEW);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid username or password", null));
            return "";
        }
        return null;
    }

    public void logout() throws IOException {

        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        if (session != null) {
            session.invalidate();
        }
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.getSessionMap().put("currentUser", null);
        externalContext.redirect(externalContext.getRequestContextPath() + Hyperlink.LOGIN_VIEW);

    }

}