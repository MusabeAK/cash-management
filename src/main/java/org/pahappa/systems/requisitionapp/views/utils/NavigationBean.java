package org.pahappa.systems.requisitionapp.views.utils;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "navigationBean")
@SessionScoped
public class NavigationBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<NavLink> links;
    private String activePage;

    public NavigationBean() {
        links = new ArrayList<>();
        links.add(new NavLink("Dashboard", "dashboard", "pi pi-home"));
        links.add(new NavLink("Users", "users", "pi pi-users"));
        links.add(new NavLink("Requisitions", "requisitions", "pi pi-file"));
        links.add(new NavLink("Budget Line", "budgetlines", "pi pi-chart-line"));
        links.add(new NavLink("Disburse", "dispatch", "pi pi-send"));
        links.add(new NavLink("Settings", "settings", "pi pi-cog"));
        activePage = "dashboard"; // Default active page
    }

    public List<NavLink> getLinks() {
        return links;
    }

    public String getActivePage() {
        return activePage;
    }

    public void setActivePage(String activePage) {
        this.activePage = activePage;
    }

    public void updateActivePage(ComponentSystemEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String activePageParam = facesContext.getExternalContext().getRequestParameterMap().get("activePage");
        if (activePageParam != null) {
            activePage = activePageParam;
        } else {
            String viewId = facesContext.getViewRoot().getViewId();
            if (viewId != null) {
                // Extract the page name from the view ID and set it as the active page
                activePage = viewId.substring(viewId.lastIndexOf('/') + 1, viewId.lastIndexOf('.'));
            }
        }
    }

    public static class NavLink {
        private String label;
        private String outcome;
        private String icon;

        public NavLink(String label, String outcome, String icon) {
            this.label = label;
            this.outcome = outcome;
            this.icon = icon;
        }

        public String getLabel() {
            return label;
        }

        public String getOutcome() {
            return outcome;
        }

        public String getIcon() {
            return icon;
        }
    }
}
