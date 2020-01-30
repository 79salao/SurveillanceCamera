package dev.futurepath.videovigilancia.controller;

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

	@RequestMapping(value = {"/signIn", "/"})
	public String indexPage(Model model) {
		model.addAttribute("title", "TB/O SignIn");
		model.addAttribute("user", new User());
		return "index";
	}

	@PostMapping(value = "/validateUser")
	public String validate(@ModelAttribute("user") User user, Model model) {
		if (userDao.findId(user) == null) {
			model.addAttribute("title", "TB/O SignIn");
			return "redirect:index";
		}
		return "redirect:main";
	}

}
