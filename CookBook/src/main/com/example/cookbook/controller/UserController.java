package com.example.cookbook.controller;

import com.example.cookbook.domain.User;
import com.example.cookbook.service.EmailService;
import com.example.cookbook.service.UserService;
import com.example.cookbook.validation.RegistrationValidation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Validated(RegistrationValidation.class) @ModelAttribute("user") User user,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            model.addAttribute("error", "Hasła muszą być identyczne!");
            return "register";
        }

        if (userService.isEmailTaken(user.getEmail())) {
            model.addAttribute("error", "E-mail jest już zarejestrowany.");
            return "register";
        }

        userService.register(user);

        String activationLink = "http://localhost:8080/activate?code=" + user.getActivationCode();
        emailService.sendEmail(user.getEmail(), "Account Activation",
                "Kliknij w link, aby aktywować konto: " + activationLink);

        model.addAttribute("message", "Link aktywacyjny został wysłany na Twój adres e-mail.");
        return "registration-success";
    }

    @GetMapping("/activate")
    public String activateAccount(@RequestParam("code") String code, Model model) {
        User activatedUser = userService.activateUser(code);
        if (activatedUser != null) {
            model.addAttribute("message", "Aktywacja konta przebiegła pomyślnie!");
            return "redirect:/login";
        } else {
            model.addAttribute("message", "Nieprawidłowy kod aktywacyjny.");
            return "activation-failed";
        }
    }
}

