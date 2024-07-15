package org.pahappa.systems.requisitionapp.views.utils;

import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.Permission;
import org.pahappa.systems.requisitionapp.views.LoginBean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean
@SessionScoped
public class UserPermissionBean implements Serializable {

    private final User currentUser = LoginBean.getCurrentUser();

    public boolean hasPermission(Permission permission) {
        return currentUser != null && currentUser.getRole().getPermissions().contains(permission);
    }
}
