package com.example.blog.controller.user;

import com.example.blog.dto.CategoryDto;
import com.example.blog.model.Blog;
import com.example.blog.service.BlogService;
import com.example.blog.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/")
public class HomeController {
    private final CategoryService categoryService;
    private final BlogService blogService;
    public HomeController(CategoryService categoryService, BlogService blogService) {
        this.categoryService = categoryService;
        this.blogService = blogService;
    }

    @ModelAttribute("categories")
    public List<CategoryDto> categoryList(){
        return categoryService.findAll().stream().map(item->{
            CategoryDto dto=new CategoryDto();
            BeanUtils.copyProperties(item,dto);
            return dto;
        }).toList();
    }
    @ModelAttribute("top6Blog")
    public List<Blog> top6Blog(){
        List<Blog> blogs=blogService.findBlogByTime();
        return blogs;
    }
    @GetMapping("")
    public String home(@RequestParam("page") Optional<Integer> page,
                       @RequestParam("size") Optional<Integer> size,
                       Model model){
        int currentPage=page.orElse(1);
        int pageSize=size.orElse(5);
        Pageable pageable= PageRequest.of(currentPage-1,pageSize);
        Page<Blog> resultPage=blogService.findAll(pageable);
        paginate(model,currentPage,resultPage);
        return "home";
    }
    private void paginate(Model model, int currentPage, Page<Blog> resultPage) {
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
