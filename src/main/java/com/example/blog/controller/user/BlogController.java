package com.example.blog.controller.user;

import com.example.blog.dto.BlogDto;
import com.example.blog.dto.CategoryDto;
import com.example.blog.dto.CommentDto;
import com.example.blog.model.Blog;
import com.example.blog.model.Category;
import com.example.blog.model.Comment;
import com.example.blog.model.User;
import com.example.blog.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("blog")
public class BlogController {
    private final CategoryService categoryService;
    private final BlogService blogService;
    private final UserService userService;
    private final StorageService storageService;
    private final CommentService commentService;
    public BlogController(CategoryService categoryService, BlogService blogService, UserService userService, StorageService storageService, CommentService commentService) {
        this.categoryService = categoryService;
        this.blogService = blogService;
        this.userService = userService;
        this.storageService = storageService;
        this.commentService = commentService;
    }

    @ModelAttribute("categories")
    public List<CategoryDto> categoryList(){
        return categoryService.findAll().stream().map(item->{
            CategoryDto dto=new CategoryDto();
            BeanUtils.copyProperties(item,dto);
            return dto;
        }).toList();
    }
    @GetMapping("create")
    public String create(Model model){
        model.addAttribute("blog",new BlogDto());
        return "userPost";
    }
    @GetMapping("edit/{blogId}")
    public String edit(@PathVariable("blogId")Long blogId,Model model){
        Optional<Blog> existed=blogService.findById(blogId);
        Blog entity=existed.get();
        BlogDto dto=new BlogDto();
        dto.setCreatedAt(timeConvert(entity.getCreatedAt()));
        BeanUtils.copyProperties(entity,dto);
        dto.setIsEdit(true);
        Set<Long> categorySet=new HashSet<>();
        for (Category category: entity.getCategories()
             ) {
            categorySet.add(category.getCategoryId());
        }
        dto.setCategoriesBlog(categorySet);
        model.addAttribute("blog",dto);
        return "userPost";
    }
    private String timeConvert(Date date){
        DateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String[] data= dateFormat.format(date).split("\\s+");
        String result=data[0]+" "+data[1];
        return result;
    }
    @GetMapping("view/{blogId}")
    public String view(@PathVariable("blogId")Long blogId,Model model){
        Optional<Blog> existed=blogService.findById(blogId);
        Blog entity=existed.get();
        model.addAttribute("blog",entity);
        model.addAttribute("comment",new CommentDto());
        return "userContent";
    }
    @GetMapping("delete/{blogId}")
    public String delete(@PathVariable("blogId")Long blogId) throws IOException {
        Optional<Blog> blogOptional=blogService.findById(blogId);
        Blog entity=blogOptional.get();
        List<User> userList=new ArrayList<>(entity.getLikedUser());
        for (User user:userList
        ) {
            user.removeLikedBlog(entity);
            userService.save(user);
        }
        if(entity.getImageTitle()!=null){
            storageService.delete(entity.getImageTitle());
        }
        blogService.delete(entity);
        return "redirect:/";
    }
    @GetMapping("deleteComment/{commentId}")
    public String deleteComment(@PathVariable("commentId")Long commentId){
        Optional<Comment> existed=commentService.findById(commentId);
        Comment entity=existed.get();
        Long blogId=entity.getBlogComment().getBlogId();
        commentService.delete(entity);
        return "redirect:/blog/view/" + blogId;
    }
    @PostMapping("saveOrUpdate")
    public String saveOrUpdate(@ModelAttribute("blog") @Valid BlogDto dto,
                               BindingResult result,
                               Principal principal,
                               @RequestParam("imageFile") MultipartFile file,
                               Model model) throws ParseException {
        if(result.hasErrors()){
            return "userPost";
        }
        if(dto.getBlogId()==null){
            Optional<Blog> existed=blogService.findByTitle(dto.getTitle());
            if(existed.isPresent()){
                model.addAttribute("message","Tiêu đề bị trùng tên");
                return "userPost";
            }
        }
        if(dto.getCategoriesBlog().size()==0){
            model.addAttribute("categoryMess","Bạn cần chọn ít nhất một thể loại");
            return "userPost";
        }
        String authUsername= principal.getName();
        Optional<User> user= userService.findByUsername(authUsername);
        Blog entity=new Blog();
        entity.setUserBlog(user.get());
        BeanUtils.copyProperties(dto,entity);
        if(dto.getCreatedAt().trim().length()==16){
            SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date=format.parse(dto.getCreatedAt());
            entity.setCreatedAt(date);
        }
        if(!file.isEmpty()){
            UUID uuid=UUID.randomUUID();
            String uuString=uuid.toString();
            entity.setImageTitle(storageService.getStoredFileName(file, uuString));
            storageService.store(file, entity.getImageTitle());
        }
        else{
            if(dto.getBlogId()!=null){
                Optional<Blog> existed=blogService.findById(dto.getBlogId());
                Blog blog=existed.get();
                entity.setImageTitle(blog.getImageTitle());
            }
        }
        if(dto.getBlogId()!=null){
            Optional<Blog> existed=blogService.findById(dto.getBlogId());
            Blog blog=existed.get();
            entity.setLikedUser(blog.getLikedUser());
        }
        Set<Category> categorySet=new HashSet<>();
        for (Long categoryId: dto.getCategoriesBlog()
             ) {
            Optional<Category> category=categoryService.findById(categoryId);
            categorySet.add(category.get());
        }
        entity.setCategories(categorySet);
        blogService.save(entity);
        Long blogId=blogService.findByTitle(dto.getTitle()).get().getBlogId();
        return "redirect:/blog/view/"+blogId;
    }
    @GetMapping("postComment/{blogId}")
    public String postComment(@ModelAttribute("comment") @Valid CommentDto dto,
                              BindingResult result,
                              @PathVariable("blogId")Long blogId,
                              Principal principal,
                              Model model){
        if(result.hasErrors()){
            Optional<Blog> existed=blogService.findById(blogId);
            Blog entity=existed.get();
            model.addAttribute("blog",entity);
            return "userContent";
        }
        Optional<Blog> existed=blogService.findById(blogId);
        Blog blog=existed.get();
        Comment entity=new Comment();
        BeanUtils.copyProperties(dto,entity);
        User user=userService.findByUsername(principal.getName()).get();
        entity.setBlogComment(blog);
        entity.setUserComment(user);
        commentService.save(entity);
        return "redirect:/blog/view/"+blog.getBlogId();
    }
    @GetMapping("like/{blogId}")
    public String like(@PathVariable("blogId")Long blogId,Principal principal){
        Optional<Blog> existed=blogService.findById(blogId);
        Blog entity=existed.get();
        Optional<User> user=userService.findByUsername(principal.getName());
        entity.addLikedUser(user.get());
        blogService.save(entity);
        return "redirect:/blog/view/"+entity.getBlogId();
    }
    @GetMapping("unlike/{blogId}")
    public String unlike(@PathVariable("blogId")Long blogId,Principal principal){
        Optional<Blog> existed=blogService.findById(blogId);
        Blog entity=existed.get();
        Optional<User> user=userService.findByUsername(principal.getName());
        entity.removeLikedUser(user.get());
        blogService.save(entity);
        return "redirect:/blog/view/"+entity.getBlogId();
    }
}
