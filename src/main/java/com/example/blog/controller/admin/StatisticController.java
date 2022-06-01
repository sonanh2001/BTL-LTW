package com.example.blog.controller.admin;

import com.example.blog.model.Blog;
import com.example.blog.model.Category;
import com.example.blog.model.User;
import com.example.blog.service.BlogService;
import com.example.blog.service.CategoryService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("admin/statistic")
public class StatisticController {
    private final BlogService blogService;
    private final CategoryService categoryService;
    private final UserService userService;
    public StatisticController(BlogService blogService, CategoryService categoryService, UserService userService) {
        this.blogService = blogService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @ModelAttribute("top5Blog")
    List<Blog> top5LikedBlog(){
        return blogService.findTop5LikedBlog();
    }
    @ModelAttribute("top5Category")
    List<Category> top5Category(){
        return categoryService.findTop5Category();
    }
    @ModelAttribute("top5User")
    List<User> top5User(){
        return userService.findTop5User();
    }
    @GetMapping("")
    public String statistic(){
        return "statistic";
    }
}
