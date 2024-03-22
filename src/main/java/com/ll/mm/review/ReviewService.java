package com.ll.mm.review;

import com.ll.mm.DataNotFoundException;
import com.ll.mm.answer.Answer;
import com.ll.mm.user.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Page<Review> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.reviewRepository.findAllByKeyword(kw, pageable);
    }

    public Review getReview(Integer id) {
        Optional<Review> review = this.reviewRepository.findById(id);
        if (review.isPresent()) {
            return review.get();
        } else {
            throw new DataNotFoundException("review not found");
        }
    }

    public void create(SiteUser user, String subject, String name, String address, String addressDetail, String content) {
        Review review = new Review();
        review.setAuthor(user);
        review.setSubject(subject);
        review.setName(name);
        review.setAddress(address);
        review.setAddressDetail(addressDetail);
        review.setContent(content);
        review.setCreateDate(LocalDateTime.now());
        this.reviewRepository.save(review);
    }

    public void modify(Review review, String subject, String name, String address, String addressDetail, String content) {
        review.setSubject(subject);
        review.setName(name);
        review.setAddress(address);
        review.setAddressDetail(addressDetail);
        review.setContent(content);
        review.setModifyDate(LocalDateTime.now());
        this.reviewRepository.save(review);
    }

    public void delete(Review review) {
        this.reviewRepository.delete(review);
    }

    public void vote(Review review, SiteUser siteUser) {
        review.getVoter().add(siteUser);
        this.reviewRepository.save(review);
    }
}
