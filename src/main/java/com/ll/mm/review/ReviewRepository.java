package com.ll.mm.review;

import com.ll.mm.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Review findBySubject(String subject);

    Review findBySubjectAndContent(String subject, String content);

    List<Review> findBySubjectLike(String subject);

    Page<Review> findAll(Pageable pageable);

    Page<Review> findAll(Specification<Review> spec, Pageable pageable);

    @Query("select "
            + "distinct r "
            + "from Review r "
            + "left outer join SiteUser u1 on r.author=u1 "
            + "left outer join Answer a on a.review=r "
            + "left outer join SiteUser u2 on a.author=u2 "
            + "where "
            + "   r.subject like %:kw% "
            + "   or r.name like %:kw% "
            + "   or r.address like %:kw% "
            + "   or r.content like %:kw% "
            + "   or u1.username like %:kw% "
            + "   or a.content like %:kw% "
            + "   or u2.username like %:kw% ")
    Page<Review> findAllByKeyword(@Param("kw") String kw, Pageable pageable);

    List<Review> findByAuthorOrderByIdDesc(SiteUser user);
}
