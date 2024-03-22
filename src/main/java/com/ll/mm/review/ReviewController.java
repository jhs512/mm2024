package com.ll.mm.review;

import com.ll.mm.answer.AnswerForm;
import com.ll.mm.user.SiteUser;
import com.ll.mm.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Slf4j
@RequestMapping("/review")
@RequiredArgsConstructor
@Controller
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        log.info("page:{}, kw:{}", page, kw);
        Page<Review> paging = this.reviewService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "review_list";
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
        Review review = this.reviewService.getReview(id);
        model.addAttribute("review", review);
        return "review_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String reviewCreate(ReviewForm reviewForm) {
        return "review_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String reviewCreate(@Valid ReviewForm reviewForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "review_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.reviewService.create(
                siteUser,
                reviewForm.getSubject(),
                reviewForm.getName(),
                reviewForm.getAddress(),
                reviewForm.getAddressDetail(),
                reviewForm.getContent()
        );
        return "redirect:/review/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String reviewModify(ReviewForm reviewForm, @PathVariable("id") Integer id, Principal principal) {
        Review review = this.reviewService.getReview(id);
        if (!review.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        reviewForm.setSubject(review.getSubject());
        reviewForm.setName(review.getName());
        reviewForm.setAddress(review.getAddress());
        reviewForm.setAddressDetail(review.getAddressDetail());
        reviewForm.setContent(review.getContent());
        return "review_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String reviewModify(@Valid ReviewForm reviewForm, BindingResult bindingResult, Principal principal,
                                 @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "review_form";
        }
        Review review = this.reviewService.getReview(id);
        if (!review.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.reviewService.modify(
                review,
                reviewForm.getSubject(),
                reviewForm.getName(),
                reviewForm.getAddress(),
                reviewForm.getAddressDetail(),
                reviewForm.getContent()
        );
        return String.format("redirect:/review/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String reviewDelete(Principal principal, @PathVariable("id") Integer id) {
        Review review = this.reviewService.getReview(id);
        if (!review.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.reviewService.delete(review);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String reviewVote(Principal principal, @PathVariable("id") Integer id) {
        Review review = this.reviewService.getReview(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.reviewService.vote(review, siteUser);
        return String.format("redirect:/review/detail/%s", id);
    }
}
