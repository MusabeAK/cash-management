//package org.pahappa.systems.requisitionapp.views;
//
//import org.pahappa.systems.requisitionapp.models.User;
//import org.pahappa.systems.requisitionapp.models.utils.Gender;
//import org.pahappa.systems.requisitionapp.services.UserService;
//import org.pahappa.systems.requisitionapp.services.utils.ServiceUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.faces.application.FacesMessage;
//import javax.faces.bean.ManagedBean;
//import javax.faces.bean.RequestScoped;
//import javax.faces.context.FacesContext;
//import java.io.Serializable;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@ManagedBean(name="updateCurrentUserBean")
//@Component
//@RequestScoped
//public class UpdateCurrentUserBean implements Serializable {
//    private static final long serialVersionUID = 1L;
//
//   private User currentUser;
//   private List<String> availableRoles;
//   private List<String> availableGenders;
//
//    @PostConstruct
//    public void init() {
//        availableGenders = Arrays.stream(Gender.values())
//                .map(Enum::name)
//                .collect(Collectors.toList());
//
////        currentUser = LoginBean.getCurrentUser();
//    }
//
//    private final UserService userService;
//    @Autowired
//    public UpdateCurrentUserBean(UserService userService) {
//        this.userService = userService;
//
//    }
//
//    public void updateUser(){
//        try{
//            String username = ServiceUtils.testUserNameInput(currentUser.getUsername());
//            String password = ServiceUtils.testPasswordInput(currentUser.getPassword());
//            String firstname = ServiceUtils.testStringInput(currentUser.getFirstName(), "First Name");
//            String lastname = ServiceUtils.testStringInput(currentUser.getLastName(), "Last Name");
//            String email = ServiceUtils.testEmailInput(currentUser.getEmail());
//            String phone = ServiceUtils.testPhoneNumberInput(currentUser.getPhoneNumber());
//            currentUser.setUsername(username);
//            currentUser.setFirstName(firstname);
//            currentUser.setLastName(lastname);
//            currentUser.setEmail(email);
//            currentUser.setPhoneNumber(phone);
//            currentUser.setPassword(password);
//            userService.updateUser(currentUser);
//            FacesContext.getCurrentInstance().addMessage(null,
//                    new FacesMessage(FacesMessage.SEVERITY_INFO, "User Update Success", null));
//        }catch (Exception e){
//            FacesContext.getCurrentInstance().addMessage(null,
//                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Updating User: "+e.getMessage(), null));
//            System.out.println("Error Updating User:  "+e.getMessage());
//        }
//
//    }
//
//    public User getCurrentUser() {
//        return currentUser;
//    }
//
//    public void setCurrentUser(User currentUser) {
//        this.currentUser = currentUser;
//    }
//
//    public List<String> getAvailableRoles() {
//        return availableRoles;
//    }
//
//    public void setAvailableRoles(List<String> availableRoles) {
//        this.availableRoles = availableRoles;
//    }
//
//    public List<String> getAvailableGenders() {
//        return availableGenders;
//    }
//
//    public void setAvailableGenders(List<String> availableGenders) {
//        this.availableGenders = availableGenders;
//    }
//}
