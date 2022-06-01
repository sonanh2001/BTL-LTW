package com.example.blog.service;

import com.example.blog.model.Blog;
import com.example.blog.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    @Query("SELECT b FROM Blog b inner join b.categories c where c.categoryId = :categoryId")
    Page<Blog> findBlogByCategory(Pageable pageable, Long categoryId);

    @Query(nativeQuery = true, value = "select * from categories c inner join\n" +
            "(select count(b_c.blog_id) as count_blog,c.category_id as category_id \n" +
            "from (categories c inner join blogs_categories b_c \n" +
            "on c.category_id=b_c.category_id) group by b_c.category_id ) \n" +
            "as count_table on c.category_id=count_table.category_id\n" +
            "order by count_blog desc limit 5;")
    List<Category> findTop5Category();

    Page<Category> findAll(Pageable pageable);


    @Query("SELECT c FROM Category c WHERE c.name LIKE %:name%")
    Page<Category> findByNameContaining(Pageable pageable, String name);

    @Query("SELECT c FROM Category c WHERE c.name LIKE %:name%")
    List<Category> findByNameContaining(String name);

    Optional<Category> findByName(String name);

    List<Category> findAll();

    List<Category> findAll(Sort sort);

    List<Category> findAllById(Iterable<Long> longs);

    <S extends Category> List<S> saveAll(Iterable<S> entities);

    Category getById(Long aLong);

    <S extends Category> S save(S entity);

    Optional<Category> findById(Long aLong);

    boolean existsById(Long aLong);

    long count();

    void deleteById(Long aLong);

    void delete(Category entity);

    void deleteAll();
}
