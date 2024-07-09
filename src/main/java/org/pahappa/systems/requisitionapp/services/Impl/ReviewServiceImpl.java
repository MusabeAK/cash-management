package org.pahappa.systems.requisitionapp.services.Impl;

import org.pahappa.systems.requisitionapp.dao.RequisitionDAO;
import org.pahappa.systems.requisitionapp.dao.ReviewDAO;
import org.pahappa.systems.requisitionapp.models.Requisition;
import org.pahappa.systems.requisitionapp.models.Review;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ReviewService")
public class ReviewServiceImpl implements ReviewService {

    private final ReviewDAO reviewDAO;
    private final RequisitionDAO requisitionDAO;

    @Autowired
    public ReviewServiceImpl(ReviewDAO reviewDAO, RequisitionDAO requisitionDAO) {
        this.reviewDAO = reviewDAO;
        this.requisitionDAO = requisitionDAO;
    }

    public void addReview(Review review, Requisition requisition){
        Requisition existingRequisition = requisitionDAO.getRequisitionById(requisition.getId());
        if(existingRequisition == null){
            throw new RuntimeException("Cannot add a review to a non existing requisition");
        }
        if(existingRequisition.getReview() != null){
            throw new RuntimeException("Cannot review an already reviewed requisition");
        }
        if(review.getComment().trim().isEmpty()){
            throw new RuntimeException("Please add a comment to your review");
        }
        reviewDAO.addReviewToRequisition(review, requisition);
    }

    public void updateReview(Review review){
        Review reviewToUpdate = reviewDAO.getReviewById(review.getId());
        if(reviewToUpdate == null){
            throw new RuntimeException("Cannot update a non existing review");
        }
        if(review.getComment().trim().isEmpty()){
            throw new RuntimeException("Please add a comment to your review");
        }
        reviewDAO.updateReview(review);
    }

    public void deleteReview(Review review){
        Review reviewToDelete = reviewDAO.getReviewById(review.getId());
        if(reviewToDelete == null){
            throw new RuntimeException("Cannot delete a non existing review");
        }
        reviewDAO.deleteReview(review);
    }

    public List<Review> getReviews(){
        return reviewDAO.getAllReviews();
    }

    public List<Review> getUserReviews(User user){
        return reviewDAO.getReviewsByUser(user);
    }

    public Review getReviewById(long id){
        Review review = reviewDAO.getReviewById(id);
        if(review == null){
            throw new RuntimeException("Cannot get review");
        }
        return reviewDAO.getReviewById(id);
    }

}
