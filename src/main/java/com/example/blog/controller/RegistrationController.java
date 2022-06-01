package com.example.blog.controller;

import com.example.blog.dto.UserDto;
import com.example.blog.model.User;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("registration")
public class RegistrationController {
    @Autowired
    private UserService userService;
    @GetMapping()
    public String showRegistrationForm(Model model){
        model.addAttribute("user",new UserDto());
        return "registration";
    }
    @PostMapping()
    public String register(@ModelAttribute("user") @Valid UserDto dto,
                                 BindingResult result,
                                 @RequestParam("confirmPassword") String confirmPass,
                                 Model model){
        if(result.hasErrors()){
            model.addAttribute("user",dto);
            return "registration";
        }
        Optional<User> existed=userService.findByUsername(dto.getUsername());
        if(existed.isPresent()){
            model.addAttribute("message","Tên tài khoản đã tồn tại");
            return "registration";
        }
        if(!confirmPass.equals(dto.getPassword())){
            model.addAttribute("user",dto);
            return "redirect:/registration?error";
        }
        userService.save(dto);
        String regisSuccess="Tạo tài khoản thành công";
        model.addAttribute("regisSuccess",regisSuccess);
        return "login";
    }
}
