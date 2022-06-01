package com.example.blog.service;

import com.example.blog.model.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BlogService {
    @Query(nativeQuery = true, value = "select * from blogs inner join \n" +
            "(select count(liked.user_id) as count_liked,blogs.blog_id as blog_id from (blogs inner join liked on blogs.blog_id=liked.blog_id) \n" +
            "group by liked.blog_id ) as like_table on blogs.blog_id=like_table.blog_id \n" +
            "order by count_liked desc limit 5;")
    List<Blog> findTop5LikedBlog();

    @Query("SELECT b from Blog b where b.title like  %:keyword%")
    Page<Blog> findByNameContaining(String keyword, Pageable pageable);

    @Query("SELECT b FROM Blog b ORDER BY b.createdAt DESC")
    List<Blog> findBlogByTime();

    List<Blog> findAll();

    Optional<Blog> findByTitle(String title);

    List<Blog> findAll(Sort sort);

    List<Blog> findAllById(Iterable<Long> longs);

    <S extends Blog> List<S> saveAll(Iterable<S> entities);

    void flush();

    <S extends Blog> S saveAndFlush(S entity);

    Blog getById(Long aLong);

    Page<Blog> findAll(Pageable pageable);

    <S extends Blog> S save(S entity);

    Optional<Blog> findById(Long aLong);

    boolean existsById(Long aLong);

    long count();

    void deleteById(Long aLong);

    void delete(Blog entity);

    void deleteAll();
}
