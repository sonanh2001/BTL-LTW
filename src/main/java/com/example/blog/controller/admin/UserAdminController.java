package com.example.blog.controller.admin;

import com.example.blog.model.Blog;
import com.example.blog.model.Category;
import com.example.blog.model.User;
import com.example.blog.service.BlogService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("admin/users")
public class UserAdminController {
    private final UserService userService;
    @Autowired
    private BlogService blogService;
    public UserAdminController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("viewPost/{username}")
    public String userPost(@PathVariable("username")String username,
                           @RequestParam("size")Optional<Integer> size,
                           @RequestParam("page")Optional<Integer> page,
                           Model model){
        Optional<User> existed=userService.findByUsername(username);
        User entity=existed.get();
        int currentPage=page.orElse(1);
        int pageSize=size.orElse(5);
        Pageable pageable= PageRequest.of(currentPage-1,pageSize);
        Page<Blog> resultPage=userService.findPostBlogByUser(username,pageable);
        paginateBlog(model,currentPage,resultPage);
        model.addAttribute("user",entity);
        return "userManagePost";
    }
    @GetMapping("viewLike/{username}")
    public String userLike(@PathVariable("username")String username,
                           @RequestParam("size")Optional<Integer> size,
                           @RequestParam("page")Optional<Integer> page,
                           Model model){
        Optional<User> existed=userService.findByUsername(username);
        User entity=existed.get();
        int currentPage=page.orElse(1);
        int pageSize=size.orElse(5);
        Pageable pageable= PageRequest.of(currentPage-1,pageSize);
        Page<Blog> resultPage=userService.findLikeBlogByUser(username,pageable);
        paginateBlog(model,currentPage,resultPage);
        model.addAttribute("user",entity);
        return "userManageLike";
    }
    @GetMapping("delete/{userId}")
    public String delete(@PathVariable("userId")Long userId,
                         Model model) throws ConcurrentModificationException {
        Optional<User> optionalUser=userService.findById(userId);
        User entity=optionalUser.get();
        String title=entity.getUsername();
        List<Blog> likedBlog=new ArrayList<>(entity.getLikedBlog());
        for (Blog blog: likedBlog
             ) {
            blog.removeLikedUser(entity);
            blogService.save(blog);
        }
        model.addAttribute("deleteMess","Người dùng " + title + " và truyện của người đó đã bị xóa");
        userService.delete(entity);
        return "forward:/admin/users";
    }
    @GetMapping("")
    public String list(@RequestParam("page")Optional<Integer> page,
                       @RequestParam("size")Optional<Integer> size,
                       Model model){
        int currentPage= page.orElse(1);
        int pageSize=size.orElse(5);
        Pageable pageable= PageRequest.of(currentPage-1,pageSize);
        Page<User> resultPage=userService.findAll(pageable);
        paginate(model,currentPage,resultPage);
        return "adminUserList";
    }
    private void paginate(Model model, int currentPage, Page<User> resultPage) {
        int totalPages=resultPage.getTotalPages();
        if(totalPages>0){
            int start=Math.max(1,currentPage-2);
            int end=Math.min(currentPage+2,totalPages);
            if(totalPages>5){
                if(end==totalPages) start=end-4;
                else if(start==1) end=start+4;
            }
            List<Integer> pageNumbers= IntStream.rangeClosed(start,end)
                    .boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers",pageNumbers);
        }
        model.addAttribute("users",resultPage);
    }
    @GetMapping("search")
    public String search(@RequestParam(value = "searchWord",required = false)String name,
                         @RequestParam("page") Optional<Integer> page,
                         @RequestParam("size") Optional<Integer> size,
                         Model model){
        int currentPage=page.orElse(1);
        int pageSize=size.orElse(5);
        Pageable pageable= PageRequest.of(currentPage-1,pageSize);
        if(StringUtils.hasText(name)){
            Page<User> resultPage=userService.findByNameContaining(name.trim(),pageable);
            paginate(model,currentPage,resultPage);
        }
        else{
            Page<User> resultPage=userService.findAll(pageable);
            paginate(model,currentPage,resultPage);
        }
        model.addAttribute("keyword",name);
        return "adminUserSearch";
    }
    private void paginateBlog(Model model, int currentPage, Page<Blog> resultPage) {
        int totalPages=resultPage.getTotalPages();
        if(totalPages>0){
            int start=Math.max(1,currentPage-2);
            int end=Math.min(currentPage+2,totalPages);
            if(totalPages>5){
                if(end==totalPages) start=end-4;
                else if(start==1) end=start+4;
            }
            List<Integer> pageNumbers= IntStream.rangeClosed(start,end)
                    .boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers",pageNumbers);
        }
        model.addAttribute("blogs",resultPage);
    }
}
