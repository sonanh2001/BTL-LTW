package com.example.blog.service;

import com.example.blog.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    List<Comment> findAll();

    List<Comment> findAll(Sort sort);

    Comment getById(Long aLong);

    Page<Comment> findAll(Pageable pageable);

    <S extends Comment> S save(S entity);

    Optional<Comment> findById(Long aLong);

    boolean existsById(Long aLong);

    long count();

    void deleteById(Long aLong);

    void delete(Comment entity);
}
