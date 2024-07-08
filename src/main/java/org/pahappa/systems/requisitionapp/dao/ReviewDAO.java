package org.pahappa.systems.requisitionapp.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.pahappa.systems.requisitionapp.models.Requisition;
import org.pahappa.systems.requisitionapp.models.Review;
import org.pahappa.systems.requisitionapp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReviewDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public ReviewDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void addReviewToRequisition(Review review, User user, Requisition requisition) {
        Session session = sessionFactory.getCurrentSession();
        review.setUser(user);
        review.setRequisition(requisition);
        requisition.setReview(review);
        user.getReviews().add(review);

        session.saveOrUpdate(review);
        session.saveOrUpdate(requisition);
        session.saveOrUpdate(user);
    }

    public void updateReview(Review review) {
        sessionFactory.getCurrentSession().update(review);
    }

    public void deleteReview(Review review) {
        sessionFactory.getCurrentSession().delete(review);
    }

    public Review getReviewById(long id) {
        return (Review) sessionFactory.getCurrentSession().get(Review.class, id);
    }

    public List<Review> getAllReviews() {
        return sessionFactory.getCurrentSession().createCriteria(Review.class).list();
    }

    public List<Review> getReviewsByUser(User user) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = null;
        List<Review> reviews = null;
        try {
            transaction = session.beginTransaction();
            String hql = "from Review where user = :user";
            reviews = session.createQuery(hql)
                    .setParameter("user", user)
                    .list();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
        return reviews;
    }

}
