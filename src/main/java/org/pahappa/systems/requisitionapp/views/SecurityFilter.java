package org.pahappa.systems.requisitionapp.views;

import org.pahappa.systems.requisitionapp.constant.Hyperlink;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.Role;

import javax.faces.context.FacesContext;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebFilter("/*")
public class SecurityFilter implements Filter {

    private Map<Role, String> roleToPathMap = new HashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
//        roleToPathMap.put(Role.EMPLOYEE, "/pages/employee/");
//        roleToPathMap.put(Role.OPERATIONS, "/pages/operations/");
//        roleToPathMap.put(Role.FINANCE, "/pages/finance/");
//        roleToPathMap.put(Role.CEO, "/pages/ceo/");
        roleToPathMap.put(Role.ADMIN, "/pages/admin/");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        User currentUser = (User) req.getSession().getAttribute("currentUser");

        String requestedPath = req.getServletPath();
        String contextPath = req.getContextPath();

        HttpSession session = req.getSession(false);
        Boolean logoutFlag = (session != null) ? (Boolean) session.getAttribute("logout") : null;


        // Allow access to static resources
        if (requestedPath.startsWith("/javax.faces.resource/")
                || requestedPath.startsWith("/resources/")
                || requestedPath.startsWith("/static/")
                || requestedPath.startsWith("/public/")
                || requestedPath.startsWith("/WEB-INF/")) {
            chain.doFilter(request, response);
            return;
        }


        // Handle logout flag
        if (logoutFlag != null && logoutFlag) {
            session.invalidate();
            res.sendRedirect(contextPath + Hyperlink.LOGIN_VIEW);
            return;
        }

        // Allow access to login page
        if (requestedPath.equals(contextPath+Hyperlink.LOGIN_VIEW) || requestedPath.equals(contextPath + "/pages/login/login.xhtml")) {
            chain.doFilter(request, response);
            return;
        }

        if (currentUser != null) {
            String allowedPath = roleToPathMap.get(currentUser.getRole());

            if (allowedPath != null && !requestedPath.startsWith(allowedPath)) {
                // Redirect to the appropriate home page based on user role
//                String redirectPath = determineRedirectPath(currentUser, contextPath);
//                res.sendRedirect(redirectPath);
                return;
            }
        }else {
            // If no user is logged in and trying to access a protected page, redirect to the login page
            if (requestedPath.startsWith("/pages/") && !requestedPath.startsWith(contextPath + Hyperlink.LOGIN_VIEW)) {
                res.sendRedirect(contextPath + Hyperlink.LOGIN_VIEW);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    /*private String determineRedirectPath(User currentUser, String contextPath) {
        switch (currentUser.getRole().getName()) {
            case "ROLE_EMPLOYEE":
                return contextPath + Hyperlink.ROLES_VIEW;
            case "ROLE_OPERATIONS":
                return contextPath + Hyperlink.ALL_REQUISITIONS_VIEW;
            case "ROLE_FINANCE":
                return contextPath + Hyperlink.SETTINGS_VIEW;
            case "ROLE_CEO":
                return contextPath + Hyperlink.CEO_VIEW;
            case "ROLE_ADMIN":
                return contextPath + Hyperlink.ADMIN_VIEW;
            default:
                return contextPath + Hyperlink.LOGIN_VIEW;
        }


    }

     */

    @Override
    public void destroy() {
    }
}
