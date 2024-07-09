package org.pahappa.systems.requisitionapp.views;

import org.pahappa.systems.requisitionapp.constant.Hyperlink;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.Role;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;

@ManagedBean
@RequestScoped
public class UserPageBean {

    public UserPageBean() {
    }

    @PostConstruct
    public void init() {
        try {
            User currentUser = getCurrentUser();
            if (currentUser != null) {
                FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
                ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
                String redirectPath = null;

                switch (currentUser.getRole()) {
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
                        redirectPath = Hyperlink.LOGIN_VIEW;
                        break;
                }

                externalContext.redirect(externalContext.getRequestContextPath() + redirectPath);
            }
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
            e.printStackTrace();
        }
    }

    public User getCurrentUser() {
        // Used to fetch currently logged in user
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        return (User) externalContext.getSessionMap().get("currentUser");
    }
}
