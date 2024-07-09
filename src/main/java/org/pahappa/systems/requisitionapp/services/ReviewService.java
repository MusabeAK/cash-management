package org.pahappa.systems.requisitionapp.services;

import org.pahappa.systems.requisitionapp.models.Requisition;
import org.pahappa.systems.requisitionapp.models.Review;
import org.pahappa.systems.requisitionapp.models.User;

import java.util.List;

public interface ReviewService {

    void addReview(Review review, Requisition requisition);

    void updateReview(Review review);

    void deleteReview(Review review);

    List<Review> getReviews();

    List<Review> getUserReviews(User user);

    Review getReviewById(long id);

}
