package com.example.blog.service;

import com.example.blog.model.Comment;
import com.example.blog.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService{
    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public List<Comment> findAll(Sort sort) {
        return commentRepository.findAll(sort);
    }

    @Override
    public Comment getById(Long aLong) {
        return commentRepository.getById(aLong);
    }

    @Override
    public Page<Comment> findAll(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    @Override
    public <S extends Comment> S save(S entity) {
        return commentRepository.save(entity);
    }

    @Override
    public Optional<Comment> findById(Long aLong) {
        return commentRepository.findById(aLong);
    }

    @Override
    public boolean existsById(Long aLong) {
        return commentRepository.existsById(aLong);
    }

    @Override
    public long count() {
        return commentRepository.count();
    }

    @Override
    public void deleteById(Long aLong) {
        commentRepository.deleteById(aLong);
    }

    @Override
    public void delete(Comment entity) {
        commentRepository.delete(entity);
    }
}
