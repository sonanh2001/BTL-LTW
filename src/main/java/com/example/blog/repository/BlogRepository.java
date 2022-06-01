package com.example.blog.repository;

import com.example.blog.model.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog,Long> {
    Optional<Blog> findByTitle(String title);
    @Query( value= "SELECT b from Blog b where b.title like  %:keyword%")
    Page<Blog> findByNameContaining(String keyword, Pageable pageable);
    @Query(nativeQuery = true, value= "SELECT * FROM blogs b ORDER BY b.created_at DESC LIMIT 6")
    List<Blog> findBlogByTime();
    @Query(nativeQuery = true,value = "select * from blogs inner join \n" +
            "(select count(liked.user_id) as count_liked,blogs.blog_id as blog_id from (blogs inner join liked on blogs.blog_id=liked.blog_id) \n" +
            "group by liked.blog_id ) as like_table on blogs.blog_id=like_table.blog_id \n" +
            "order by count_liked desc limit 5;")
    List<Blog> findTop5LikedBlog();
}
