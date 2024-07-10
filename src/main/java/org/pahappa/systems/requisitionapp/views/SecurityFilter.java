package org.pahappa.systems.requisitionapp.views;

import org.pahappa.systems.requisitionapp.constant.Hyperlink;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.Role;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebFilter("/*")
public class SecurityFilter implements Filter {

    private Map<Role, String> roleToPathMap = new HashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        roleToPathMap.put(Role.EMPLOYEE, "/pages/employee/");
        roleToPathMap.put(Role.OPERATIONS, "/pages/operations/");
        roleToPathMap.put(Role.FINANCE, "/pages/finance/");
        roleToPathMap.put(Role.CEO, "/pages/ceo/");
        roleToPathMap.put(Role.ADMIN, "/pages/admin/");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        User currentUser = (User) req.getSession().getAttribute("currentUser");

        String requestedPath = req.getServletPath();
        String contextPath = req.getContextPath();

        // Allow access to login and logout pages
        if (requestedPath.equals(contextPath + "/pages/login/login.xhtml")) {
            chain.doFilter(request, response);
            return;
        }

        System.out.println("\n\n------------------------------------------\nContext Path: " + contextPath);
        System.out.println("LOGIN_VIEW Path: " + Hyperlink.LOGIN_VIEW+"\n\n-------------------------------------");

        if (currentUser != null) {
            String allowedPath = roleToPathMap.get(currentUser.getRole());

            if (allowedPath != null && !requestedPath.startsWith(allowedPath)) {
                // Redirect to the appropriate home page based on user role
                String redirectPath = determineRedirectPath(currentUser, contextPath);
                res.sendRedirect(redirectPath);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private String determineRedirectPath(User currentUser, String contextPath) {
        switch (currentUser.getRole()) {
            case EMPLOYEE:
                return contextPath + Hyperlink.EMPLOYEE_VIEW;
            case OPERATIONS:
                return contextPath + Hyperlink.OPERATIONS_VIEW;
            case FINANCE:
                return contextPath + Hyperlink.FINANCE_VIEW;
            case CEO:
                return contextPath + Hyperlink.CEO_VIEW;
            case ADMIN:
                return contextPath + Hyperlink.ADMIN_VIEW;
            default:
                return contextPath + Hyperlink.LOGIN_VIEW;
        }
    }

    @Override
    public void destroy() {
    }
}
