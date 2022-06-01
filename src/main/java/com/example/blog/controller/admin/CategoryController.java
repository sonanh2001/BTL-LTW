package com.example.blog.controller.admin;

import com.example.blog.dto.CategoryDto;
import com.example.blog.model.Category;
import com.example.blog.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("admin/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("create")
    public String create(Model model){
        model.addAttribute("category",new CategoryDto());
        return "adminCategoryAdd";
    }
    @GetMapping("")
    public String list(Model model,
                       @RequestParam("size") Optional<Integer> size,
                       @RequestParam("page") Optional<Integer> page){
        int currentPage=page.orElse(1);
        int pageSize=size.orElse(5);
        Pageable pageable= PageRequest.of(currentPage-1,pageSize);
        Page<Category> resultPage=categoryService.findAll(pageable);
        paginate(model, currentPage, resultPage);
        return "adminCategoryList";
    }
    private void paginate(Model model, int currentPage, Page<Category> resultPage) {
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
        model.addAttribute("categories",resultPage);
    }
    @GetMapping("edit/{categoryId}")
    public ModelAndView edit(@PathVariable("categoryId")Long id, ModelMap model){
        Optional<Category> category=categoryService.findById(id);
        CategoryDto categoryDto=new CategoryDto();
        Category entity=category.get();
        BeanUtils.copyProperties(entity,categoryDto);
        categoryDto.setIsEdit(true);
        model.addAttribute("category",categoryDto);
        return new ModelAndView("adminCategoryAdd", model);
    }
    @GetMapping("delete/{categoryId}")
    public String delete(@PathVariable("categoryId")Long id,Model model){
        Optional<Category> categoryOptional=categoryService.findById(id);
        String title=categoryOptional.get().getName();
        model.addAttribute("deleteMess","Thể loại " + title + " và các truyện của nó đã bị xóa");
        categoryService.delete(categoryOptional.get());
        return "forward:/admin/categories";
    }
    @PostMapping("saveOrUpdate")
    public String saveOrUpdate(@ModelAttribute("category") @Valid CategoryDto dto, BindingResult result,Model model){
        if(result.hasErrors()){
            return "adminCategoryAdd";
        }
        Optional<Category> existed=categoryService.findByName(dto.getName());
        if(existed.isPresent()){
            model.addAttribute("message","Thể loại này đã tồn tại");
            return "adminCategoryAdd";
        }
        Category category=new Category();
        BeanUtils.copyProperties(dto,category);
        categoryService.save(category);
        return "redirect:/admin/categories";
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
            Page<Category> resultPage=categoryService.findByNameContaining(pageable,name.trim());
            paginate(model,currentPage,resultPage);
        }
        else{
            Page<Category> resultPage=categoryService.findAll(pageable);
            paginate(model,currentPage,resultPage);
        }
        model.addAttribute("keyword",name);
        return "adminCategorySearch";
    }
}
