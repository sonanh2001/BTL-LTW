package com.example.blog.service;

import com.example.blog.dto.UserDto;
import com.example.blog.model.Blog;
import com.example.blog.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    @Query("SELECT b FROM Blog b WHERE b.userBlog.username=:username")
    Page<Blog> findPostBlogByUser(String username, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from users inner join \n" +
            "(select count(b.blog_id) as count_blog,u.user_id as user_id \n" +
            "from blogs b,users u where b.user_id=u.user_id \n" +
            "group by u.user_id) \n" +
            "as count_table on users.user_id=count_table.user_id \n" +
            "order by count_blog desc limit 5;")
    List<User> findTop5User();

    @Query("SELECT u FROM User u WHERE u.blogs.size>0")
    Page<User> findUserWriteBlog(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.blogs.size>0 and u.username like %:keyword%")
    Page<User> findUserWriteBlogContaining(String keyword, Pageable pageable);

    @Query("SELECT b FROM Blog b INNER JOIN b.likedUser u WHERE u.username=:username")
    Page<Blog> findLikeBlogByUser(String username, Pageable pageable);

    void deleteById(Long aLong);

    void delete(User entity);

    User save(UserDto userDto);


    @Query("SELECT u from User u where u.username like  %:keyword%")
    Page<User> findByNameContaining(String keyword, Pageable pageable);

    List<User> findAll();

    List<User> findAll(Sort sort);

    User getById(Long aLong);

    Page<User> findAll(Pageable pageable);

    Optional<User> findById(Long aLong);

    boolean existsById(Long aLong);

    long count();

    <S extends User> S save(S entity);

    Optional<User> findByUsername(String username);

}
