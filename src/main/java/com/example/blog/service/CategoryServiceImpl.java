package com.example.blog.service;

import com.example.blog.model.Blog;
import com.example.blog.model.Category;
import com.example.blog.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Query("SELECT b FROM Blog b inner join b.categories c where c.categoryId = :categoryId")
    public Page<Blog> findBlogByCategory(Pageable pageable, Long categoryId) {
        return categoryRepository.findBlogByCategory(pageable, categoryId);
    }

    @Override
    @Query(nativeQuery = true, value = "select * from categories c inner join\n" +
            "(select count(b_c.blog_id) as count_blog,c.category_id as category_id \n" +
            "from (categories c inner join blogs_categories b_c \n" +
            "on c.category_id=b_c.category_id) group by b_c.category_id ) \n" +
            "as count_table on c.category_id=count_table.category_id\n" +
            "order by count_blog desc limit 5;")
    public List<Category> findTop5Category() {
        return categoryRepository.findTop5Category();
    }

    private final CategoryRepository categoryRepository;

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    @Query("SELECT c FROM Category c WHERE c.name LIKE %:name%")
    public Page<Category> findByNameContaining(Pageable pageable, String name) {
        return categoryRepository.findByNameContaining(pageable, name);
    }

    @Override
    @Query("SELECT c FROM Category c WHERE c.name LIKE %:name%")
    public List<Category> findByNameContaining(String name) {
        return categoryRepository.findByNameContaining(name);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> findAll(Sort sort) {
        return categoryRepository.findAll(sort);
    }

    @Override
    public List<Category> findAllById(Iterable<Long> longs) {
        return categoryRepository.findAllById(longs);
    }

    @Override
    public <S extends Category> List<S> saveAll(Iterable<S> entities) {
        return categoryRepository.saveAll(entities);
    }

    @Override
    public Category getById(Long aLong) {
        return categoryRepository.getById(aLong);
    }

    @Override
    public <S extends Category> S save(S entity) {
        return categoryRepository.save(entity);
    }

    @Override
    public Optional<Category> findById(Long aLong) {
        return categoryRepository.findById(aLong);
    }

    @Override
    public boolean existsById(Long aLong) {
        return categoryRepository.existsById(aLong);
    }

    @Override
    public long count() {
        return categoryRepository.count();
    }

    @Override
    public void deleteById(Long aLong) {
        categoryRepository.deleteById(aLong);
    }

    @Override
    public void delete(Category entity) {
        categoryRepository.delete(entity);
    }

    @Override
    public void deleteAll() {
        categoryRepository.deleteAll();
    }
}
