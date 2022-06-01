package com.example.blog.service;

import com.example.blog.dto.UserDto;
import com.example.blog.model.Blog;
import com.example.blog.model.User;
import com.example.blog.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    @Override
    @Query("SELECT b FROM Blog b WHERE b.userBlog.username=:username")
    public Page<Blog> findPostBlogByUser(String username, Pageable pageable) {
        return userRepository.findPostBlogByUser(username, pageable);
    }

    @Override
    @Query(nativeQuery = true, value = "select * from users inner join \n" +
            "(select count(b.blog_id) as count_blog,u.user_id as user_id \n" +
            "from blogs b,users u where b.user_id=u.user_id \n" +
            "group by u.user_id) \n" +
            "as count_table on users.user_id=count_table.user_id \n" +
            "order by count_blog desc limit 5;")
    public List<User> findTop5User() {
        return userRepository.findTop5User();
    }

    @Override
    @Query("SELECT u FROM User u WHERE u.blogs.size>0")
    public Page<User> findUserWriteBlog(Pageable pageable) {
        return userRepository.findUserWriteBlog(pageable);
    }

    @Override
    @Query("SELECT u FROM User u WHERE u.blogs.size>0 and u.username like %:keyword%")
    public Page<User> findUserWriteBlogContaining(String keyword, Pageable pageable) {
        return userRepository.findUserWriteBlogContaining(keyword, pageable);
    }

    @Override
    @Query("SELECT b FROM Blog b INNER JOIN b.likedUser u WHERE u.username=:username")
    public Page<Blog> findLikeBlogByUser(String username, Pageable pageable) {
        return userRepository.findLikeBlogByUser(username, pageable);
    }

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void deleteById(Long aLong) {
        userRepository.deleteById(aLong);
    }

    @Override
    public void delete(User entity) {
        userRepository.delete(entity);
    }

    @Override
    public User save(UserDto userDto) {
        User user=new User();
        BeanUtils.copyProperties(userDto,user);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole("USER");
        return userRepository.save(user);
    }

    @Override
    @Query("SELECT u from User u where u.username like  %:keyword%")
    public Page<User> findByNameContaining(String keyword, Pageable pageable) {
        return userRepository.findByNameContaining(keyword, pageable);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findAll(Sort sort) {
        return userRepository.findAll(sort);
    }

    @Override
    public User getById(Long aLong) {
        return userRepository.getById(aLong);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Optional<User> findById(Long aLong) {
        return userRepository.findById(aLong);
    }

    @Override
    public boolean existsById(Long aLong) {
        return userRepository.existsById(aLong);
    }

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    public <S extends User> S save(S entity) {
        return userRepository.save(entity);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(username);
        Optional<User> user=userRepository.findByUsername(username);
        if(user.isEmpty()){
            throw new UsernameNotFoundException("Tên hoặc mật khẩu không hợp lệ");
        }
        return new org.springframework.security.core.userdetails.User(user.get().getUsername(),user.get().getPassword(),mapRolesToAuthorities(user.get().getRole()));
    }
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(String role){
        List<SimpleGrantedAuthority> authorities=new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }
}
