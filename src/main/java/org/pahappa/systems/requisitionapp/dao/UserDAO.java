package org.pahappa.systems.requisitionapp.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.Role;
import org.pahappa.systems.requisitionapp.models.utils.Gender;
import org.pahappa.systems.requisitionapp.models.utils.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Repository
public class UserDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(User user) {
        sessionFactory.getCurrentSession().save(user);
    }

    public User getById(Long id) {
        return (User) sessionFactory.getCurrentSession().get(User.class, id);
    }

    public List<User> getAllUsers() {
        return sessionFactory.getCurrentSession().createQuery("from User")
                .list();
    }

    public List<User> checkForAdminUser() {
        return sessionFactory.getCurrentSession().createQuery("from User " +
                        "where username = :admin")
                .setString("admin", "Admin")
                .list();
    }

    public void update(User user) {
        sessionFactory.getCurrentSession().update(user);
    }

    public void delete(User user) {
        sessionFactory.getCurrentSession().delete(user);
    }

    public void deleteAll(){
        sessionFactory.getCurrentSession().createQuery("delete from User where username != :admin")
                .setParameter("admin", "Admin")
                .executeUpdate();
    }

    public User getUserByUsername(String username) {
//        return sessionFactory.getCurrentSession().get(User.class, username);
        Session session = sessionFactory.getCurrentSession();
        User user = null;
        try{
            String hql = "from User where username=:username ";
            user = (User) session.createQuery(hql, User.class).setParameter("username", username).getSingleResult();
        }catch(Exception e){
            e.printStackTrace();
        }
        return user;
    }

    public User getUserByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        User user = null;
        try {
            String hql = "from User where email=:email";
            user = (User) session.createQuery(hql).setParameter("email", email).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public List<User> searchUsers(String searchTerm) {
        String query = "FROM User WHERE (username LIKE :searchTerm " +
                "OR firstName LIKE :searchTerm " +
                "OR lastName LIKE :searchTerm) AND username != :admin";
        return sessionFactory.getCurrentSession()
                .createQuery(query, User.class)
                .setParameter("searchTerm", "%" + searchTerm + "%")
                .setParameter("admin", "Admin")
                .getResultList();
    }

    public List<User> filterUsersByPermission(Permission permission) {
        try {
            String hql = "SELECT u FROM User u JOIN u.role r JOIN r.permissions p WHERE p = :permission";

            return sessionFactory.getCurrentSession().createQuery(hql, User.class)
                    .setParameter("permission", permission)
                    .getResultList();
        }catch (Exception e){
            System.out.println("From the Bean"+e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    public List<User> filterUsersByRole(Role role) {
        String hql = "FROM User WHERE role = :role";
        return sessionFactory.getCurrentSession()
                .createQuery(hql, User.class)
                .setParameter("role", role)
                .list();
    }

    public List<User> filterUsersByGender(Gender gender) {
        String hql = "FROM User WHERE gender = :gender";
        return sessionFactory.getCurrentSession()
                .createQuery(hql, User.class)
                .setParameter("gender", gender)
                .list();
    }

}
