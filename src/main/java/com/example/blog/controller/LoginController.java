package com.example.blog.controller;
import com.example.blog.model.User;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("login")
public class LoginController {
    @Autowired
    private UserService userService;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @GetMapping("")
    public String login(Principal principal){
        if(principal!=null){
            Optional<User> existed=userService.findByUsername(principal.getName());
            if(existed.isPresent()){
                if(existed.get().getRole().equals("ADMIN")){
                    return "redirect:/admin/categories";
                }
                else{
                    return "redirect:/";
                }
            }
        }
        return "login";
    }
}
