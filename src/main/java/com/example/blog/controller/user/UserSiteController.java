package com.example.blog.controller.user;

import com.example.blog.dto.CategoryDto;
import com.example.blog.dto.UserDto;
import com.example.blog.dto.UserInfoDto;
import com.example.blog.model.Blog;
import com.example.blog.model.User;
import com.example.blog.service.CategoryService;
import com.example.blog.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("user")
public class UserSiteController {
    private final UserService userService;
    private final CategoryService categoryService;
    public UserSiteController(UserService userService, CategoryService categoryService) {
        this.userService = userService;
        this.categoryService = categoryService;
    }
    @ModelAttribute("categories")
    public List<CategoryDto> categoryList(){
        return categoryService.findAll().stream().map(item->{
            CategoryDto dto=new CategoryDto();
            BeanUtils.copyProperties(item,dto);
            return dto;
        }).toList();
    }
    @GetMapping("")
    public String list(@RequestParam("size")Optional<Integer> size,
                       @RequestParam("page")Optional<Integer> page,
                       Model model){
        int currentPage=page.orElse(1);
        int pageSize=size.orElse(5);
        Pageable pageable= PageRequest.of(currentPage-1,pageSize);
        Page<User> resultPage=userService.findUserWriteBlog(pageable);
        paginate(model, currentPage, resultPage);
        return "userList";
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
    public String search(@RequestParam("size")Optional<Integer> size,
                         @RequestParam("page")Optional<Integer> page,
                         @RequestParam("keyword")String keyword,
                         Model model){
        int currentPage=page.orElse(1);
        int pageSize=size.orElse(5);
        Pageable pageable= PageRequest.of(currentPage-1,pageSize);
        if(StringUtils.hasText(keyword)){
            Page<User> resultPage=userService.findUserWriteBlogContaining(keyword.trim(),pageable);
            paginate(model,currentPage,resultPage);
        }
        else{
            Page<User> resultPage=userService.findUserWriteBlog(pageable);
            paginate(model,currentPage,resultPage);
        }
        model.addAttribute("keyword",keyword);
        return "userListSearch";
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
    @GetMapping("updateInfo/{username}")
    public String showUpdateForm(@PathVariable("username")String username,
                                 Model model){
        Optional<User> userOptional=userService.findByUsername(username);
        User entity=userOptional.get();
        model.addAttribute("userDto",new UserInfoDto());
        model.addAttribute("user",entity);
        return "updateInfo";
    }
    @PostMapping ("saveInfo/{username}")
    public String updateInfo(@PathVariable("username")String username,
                             @ModelAttribute("userDto") @Valid UserInfoDto dto,
                             BindingResult result,
                             Model model){
        Optional<User> userOptional=userService.findByUsername(username);
        User entity=userOptional.get();
        if(result.hasErrors()){
            model.addAttribute("user",entity);
            return "updateInfo";
        }
        entity.setPhone(dto.getPhone());
        entity.setAddress(dto.getAddress());
        entity.setFullName(dto.getFullName());
        userService.save(entity);
        return "redirect:/user/viewPost/" + username;
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
