package org.pahappa.systems.requisitionapp.dao;

import org.hibernate.SessionFactory;
import org.pahappa.systems.requisitionapp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

    public User getById(int id) {
        return (User) sessionFactory.getCurrentSession().get(User.class, id);
    }

    public List<User> getAllUsers() {
        return sessionFactory.getCurrentSession().createQuery("from User").list();
    }

    public void update(User user) {
        sessionFactory.getCurrentSession().update(user);
    }

    public void delete(User user) {
        sessionFactory.getCurrentSession().delete(user);
    }

    public User getUserByUsername(String username) {
        return (User) sessionFactory.getCurrentSession().get(User.class, username);
    }

}
