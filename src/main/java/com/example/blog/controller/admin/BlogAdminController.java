package com.example.blog.controller.admin;

import com.example.blog.dto.CommentDto;
import com.example.blog.model.Blog;
import com.example.blog.model.Comment;
import com.example.blog.model.User;
import com.example.blog.service.BlogService;
import com.example.blog.service.CommentService;
import com.example.blog.service.StorageService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("admin/blogs")
public class BlogAdminController {
    private final BlogService blogService;
    private final CommentService commentService;
    private final UserService userService;
    private final StorageService storageService;
    public BlogAdminController(BlogService blogService, CommentService commentService, UserService userService, StorageService storageService) {
        this.blogService = blogService;
        this.commentService = commentService;
        this.userService = userService;
        this.storageService = storageService;
    }

    @GetMapping("view/{blogId}")
    public String view(@PathVariable("blogId")Long blogId, Model model){
        Optional<Blog> existed=blogService.findById(blogId);
        Blog entity=existed.get();
        model.addAttribute("blog",entity);
        return "userContent";
    }
    @GetMapping("delete/{blogId}")
    public String delete(@PathVariable("blogId")Long blogId,Model model) throws IOException {
        Optional<Blog> blogOptional=blogService.findById(blogId);
        Blog entity=blogOptional.get();
        List<User> userList=new ArrayList<>(entity.getLikedUser());
        String title=entity.getTitle();
        for (User user:userList
        ) {
            user.removeLikedBlog(entity);
            userService.save(user);
        }
        if(entity.getImageTitle()!=null){
            storageService.delete(entity.getImageTitle());
        }
        blogService.delete(entity);
        model.addAttribute("deleteMess","Truyện " + title + " đã bị xóa");
        return "forward:/admin/blogs";
    }
    @GetMapping("deleteComment/{commentId}")
    public String deleteComment(@PathVariable("commentId")Long commentId){
        Optional<Comment> existed=commentService.findById(commentId);
        Comment entity=existed.get();
        Long blogId=entity.getBlogComment().getBlogId();
        commentService.delete(entity);
        return "redirect:/admin/blogs/view/" + blogId;
    }
    @GetMapping("")
    public String list(@RequestParam("page")Optional<Integer> page,
                       @RequestParam("size")Optional<Integer> size,
                       Model model){
        int currentPage=page.orElse(1);
        int pageSize=size.orElse(5);
        Pageable pageable= PageRequest.of(currentPage-1,pageSize);
        Page<Blog> resultPage=blogService.findAll(pageable);
        paginateBlog(model,currentPage,resultPage);
        return "adminBlogList";
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
            Page<Blog> resultPage=blogService.findByNameContaining(name.trim(),pageable);
            paginateBlog(model,currentPage,resultPage);
        }
        else{
            Page<Blog> resultPage=blogService.findAll(pageable);
            paginateBlog(model,currentPage,resultPage);
        }
        model.addAttribute("keyword",name);
        return "adminBlogSearch";
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
