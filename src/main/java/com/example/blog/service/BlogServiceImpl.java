package com.example.blog.service;

import com.example.blog.model.Blog;
import com.example.blog.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class BlogServiceImpl implements BlogService{
    private final BlogRepository blogRepository;

    public BlogServiceImpl(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @Override
    @Query(nativeQuery = true, value = "select * from blogs inner join \n" +
            "(select count(liked.user_id) as count_liked,blogs.blog_id as blog_id from (blogs inner join liked on blogs.blog_id=liked.blog_id) \n" +
            "group by liked.blog_id ) as like_table on blogs.blog_id=like_table.blog_id \n" +
            "order by count_liked desc limit 5")
    public List<Blog> findTop5LikedBlog() {
        return blogRepository.findTop5LikedBlog();
    }

    @Override
    @Query("SELECT b from Blog b where b.title like  %:keyword%")
    public Page<Blog> findByNameContaining(String keyword, Pageable pageable) {
        return blogRepository.findByNameContaining(keyword, pageable);
    }

    @Override
    @Query("SELECT b FROM Blog b ORDER BY b.createdAt DESC")
    public List<Blog> findBlogByTime() {
        return blogRepository.findBlogByTime();
    }

    @Override
    public List<Blog> findAll() {
        return blogRepository.findAll();
    }

    @Override
    public Optional<Blog> findByTitle(String title) {
        return blogRepository.findByTitle(title);
    }

    @Override
    public List<Blog> findAll(Sort sort) {
        return blogRepository.findAll(sort);
    }

    @Override
    public List<Blog> findAllById(Iterable<Long> longs) {
        return blogRepository.findAllById(longs);
    }

    @Override
    public <S extends Blog> List<S> saveAll(Iterable<S> entities) {
        return blogRepository.saveAll(entities);
    }

    @Override
    public void flush() {
        blogRepository.flush();
    }

    @Override
    public <S extends Blog> S saveAndFlush(S entity) {
        return blogRepository.saveAndFlush(entity);
    }

    @Override
    public Blog getById(Long aLong) {
        return blogRepository.getById(aLong);
    }

    @Override
    public Page<Blog> findAll(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    @Override
    public <S extends Blog> S save(S entity) {
        return blogRepository.save(entity);
    }

    @Override
    public Optional<Blog> findById(Long aLong) {
        return blogRepository.findById(aLong);
    }

    @Override
    public boolean existsById(Long aLong) {
        return blogRepository.existsById(aLong);
    }

    @Override
    public long count() {
        return blogRepository.count();
    }

    @Override
    public void deleteById(Long aLong) {
        blogRepository.deleteById(aLong);
    }

    @Override
    public void delete(Blog entity) {
        blogRepository.delete(entity);
    }

    @Override
    public void deleteAll() {
        blogRepository.deleteAll();
    }
}
