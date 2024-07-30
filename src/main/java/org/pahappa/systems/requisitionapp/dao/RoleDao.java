package org.pahappa.systems.requisitionapp.dao;

import org.hibernate.SessionFactory;
import org.pahappa.systems.requisitionapp.models.Role;
import org.pahappa.systems.requisitionapp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RoleDao{

    private final SessionFactory sessionFactory;

    @Autowired
    public RoleDao(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    public void create(Role role){
        sessionFactory.getCurrentSession().save(role);
    }

    public void update(Role role) {
        sessionFactory.getCurrentSession().update(role);
    }

    public void delete(Role role) {
        sessionFactory.getCurrentSession().delete(role);
    }

    public Role findById(long roleId) {
        return sessionFactory.getCurrentSession().get(Role.class, roleId);
    }

    public Role findByName(String name) {
        return (Role) sessionFactory.getCurrentSession().createQuery("from Role role where role.name = :name")
                .setParameter("name", name).uniqueResult();
    };

    public List<Role> findAll() {
        return sessionFactory.getCurrentSession().createQuery("from Role", Role.class).list();
    }

    public List<Role> searchRoles(String searchTerm) {
        String query = "FROM Role WHERE name LIKE :searchTerm ";
        return sessionFactory.getCurrentSession()
                .createQuery(query, Role.class)
                .setParameter("searchTerm", "%" + searchTerm + "%")
                .getResultList();
    }

    public List<User> findUsersByRole(Role role) {
        String hql = "FROM User WHERE role = :role";
        return sessionFactory.getCurrentSession()
                .createQuery(hql, User.class)
                .setParameter("role", role)
                .list();
    }
}
