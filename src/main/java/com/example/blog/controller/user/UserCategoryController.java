package com.example.blog.controller.user;

import com.example.blog.dto.CategoryDto;
import com.example.blog.model.Blog;
import com.example.blog.model.Category;
import com.example.blog.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("categories")
public class UserCategoryController {
    private final CategoryService categoryService;

    public UserCategoryController(CategoryService categoryService) {
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
    @GetMapping("{categoryId}")
    public String list(@RequestParam("page") Optional<Integer> page,
                       @RequestParam("size") Optional<Integer> size,
                       @PathVariable("categoryId")Long categoryId,
                       Model model){
        Optional<Category> existed=categoryService.findById(categoryId);
        Category entity=existed.get();
        int currentPage=page.orElse(1);
        int pageSize=size.orElse(5);
        Pageable pageable= PageRequest.of(currentPage-1,pageSize);
        Page<Blog> resultPage=categoryService.findBlogByCategory(pageable,categoryId);
        paginate(model,currentPage,resultPage);
        model.addAttribute("selectedCategory",entity);
        return "userCategory";
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
