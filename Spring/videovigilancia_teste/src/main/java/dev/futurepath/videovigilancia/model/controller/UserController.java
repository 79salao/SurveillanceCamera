package dev.futurepath.videovigilancia.model.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dev.futurepath.videovigilancia.model.dao.IUserDao;
import dev.futurepath.videovigilancia.model.entity.User;

@Controller
public class UserController {

	@Autowired
	private IUserDao userDao;
	
	@RequestMapping(value= "/")
	public String homePage(Model model) {
		model.addAttribute("title","TB/O SignIn");
		model.addAttribute("user", new User());
		return "signIn";		
	}
	
	@PostMapping(value="/")
	public String validate(@ModelAttribute("user") User user) {
		Long userId = userDao.findId(user);	
		System.out.println(userId);
		if(userId != null) {
			return "redirect:recordPage";
		}else{
			return "redirect:/"; //CAMBIAR AQUI PARA QUE SE ENSEÑE UN ERROR DE USER
		}		
	}
	
	
	

}
