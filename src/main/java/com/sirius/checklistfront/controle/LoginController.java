package com.sirius.checklistfront.controle;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
	 @GetMapping("/login")
	 public String login() {
	    return "login"; // carrega login.html
	 }
}
