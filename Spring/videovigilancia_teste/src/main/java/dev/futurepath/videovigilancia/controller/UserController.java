package dev.futurepath.videovigilancia.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;


import dev.futurepath.videovigilancia.model.dao.IUserDao;
import dev.futurepath.videovigilancia.model.entity.User;
import dev.futurepath.videovigilancia.model.service.SendHTMLEmail;

@Controller
public class UserController {

	@Autowired
	private IUserDao userDao;
	@Autowired
	private SendHTMLEmail sendMailService;
	@Autowired
	private SharedMethods sharedMethods;

	/*
	 * Variables' class
	 * Email pattern
	 * Models attributes to sent to the view
	 */
	private final Pattern pattern = Pattern
			.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	private Long emailID = null;

	/*
	 * Show the index.html page with the URL '/'. Firstly, it's create a new user
	 * with temporal name and email (can't be null) to verify if the data entered in
	 * the login form is correct.
	 */
	@GetMapping(value = "/")
	public ModelAndView homePage() {
		ModelAndView signInModelAndView = new ModelAndView("html/index");
		User user = new User();
		user.setName("temp");
		user.setEmail("temp@temp.com");
		signInModelAndView.addObject("title", "TB/O SignIn");
		signInModelAndView.addObject("user", user);
		return signInModelAndView;
	}

	/*
	 * URL sent by email. The URL contains the user's id as a parameter to use it
	 * after. The if conditional verify if the ID is the same send by email (in case of it's manually
	 * changed).
	 */
	@GetMapping(value = "/resetpassword", params = "id")
	public ModelAndView resetPassword(@RequestParam(value = "id") Long id) {
		ModelAndView resetPasswordModelAndView = new ModelAndView("html/resetpassword");
		ModelAndView redirectSignIn = new ModelAndView("redirect:/");
		ModelAndView redirectReset = new ModelAndView("redirect:/resetpassword?id=" + emailID);
		
		User user = userDao.findUserByID(id);
		
		if (id == emailID){
			resetPasswordModelAndView.addObject("user", user);
			resetPasswordModelAndView.addObject("title", "TB/O Reset your Password");		
			return resetPasswordModelAndView;
		}else {
			if(emailID == null) {
				return redirectSignIn;
			}else{
				return redirectReset;
			}
		}		
	}

	/*
	 * This method is called after click on the button login in the login page. The
	 * annotation @Valid verify if all the fields are filled. If it's not, return
	 * the login page with error's messages. If it's all right, the program will
	 * search inside database if the username + password exists. If exists, redirect
	 * to the next page. If not, send a model attribute to the view thread it with a
	 * popUp. Create cookies with the user's username, and set expire time to 900
	 * sec/15min
	 */
	@PostMapping(value = "/")
	public ModelAndView validateUser(@Valid User user, BindingResult result,
			@CookieValue(value = "id", defaultValue = "0") Long userId, HttpServletResponse response) {
		
		ModelAndView signInModelAndView = new ModelAndView("html/index");

		if (result.hasErrors()){
			signInModelAndView.addObject("title", "TB/O SignIn");
			return signInModelAndView;
		}
		userId = userDao.findId(user);
		if (userId != null) {
			ModelAndView mainPageModelAndView = new ModelAndView("redirect:/main");
			Cookie cookie = new Cookie("username", user.getUsername());
			//Cookie cookie = new Cookie("id", userId.toString());
			cookie.setMaxAge(900);
			response.addCookie(cookie);
			return mainPageModelAndView;
		} else {
			signInModelAndView.addObject("title", "TB/O SignIn");
			signInModelAndView.addObject("message", "USER_ERROR");
			return signInModelAndView;
		}
	}

	/*
	 * This method is called after click on the button "Recuperar contraseña". If
	 * the field match with an email inside the database, is send an email to the typed
	 * email. It's used RedirectView because RedirectAttributes only work with it
	 * (changing the model attribute - message of the page before redirecting).
	 */
	@PostMapping(value = "/sendEmail")
	public RedirectView validateEmail(@RequestParam("email") String email, RedirectAttributes redir) {
		RedirectView redirectView = new RedirectView("/", true);
		Matcher mather = pattern.matcher(email);
		
		if (!email.equals(null) && mather.find() == true){
			User user = userDao.findUserByEmail(email);
			if (user != null) {
				sendEmail(user);
				addToTheModel(redir, "EMAIL_OK");
			}else{
				addToTheModel(redir, "EMAIL_ERROR1");
			}
		}else{
			addToTheModel(redir, "EMAIL_ERROR");
		}
		return redirectView;
	}

	/*
	 * Update the password and redirect to the login page if both password's field match.
	 * If not, will return the reset password page (with the ID in the URL).
	 */
	@PostMapping(value = "/forgotpassword")
	public RedirectView updatePassword(User user, RedirectAttributes redir, 
			@RequestParam("firstFieldPassword") String firstFieldPassword, @RequestParam("secondFieldPassword") String secondFieldPassword){		
				
		RedirectView redirectResetPage = new RedirectView("/resetpassword?id=" + user.getId(), true);
		RedirectView redirectSignIn = new RedirectView("/", true);
		
		if(!(firstFieldPassword.equals(secondFieldPassword))) {
			addToTheModel(redir, "FIELD_ERROR");
			return redirectResetPage;
		}else {
			user.setPassword(secondFieldPassword);
			userDao.update(user);
			addToTheModel(redir, "PASSWORD_OK");
			return redirectSignIn;
		}
	}

	/*
	 * Update the user's name and redirect to the main page.
	 */
	@PostMapping(value = "/updatename")
	public RedirectView updateName(@RequestParam("name") String name, RedirectAttributes redir, HttpServletRequest request) {
		RedirectView redirectMainPage = new RedirectView("/main", true);
		User user = sharedMethods.findCookies(request);		
		name = name.trim();
		if(name.length() != 0) {
			user.setName(name);
			userDao.update(user);
			addToTheModel(redir, "NAME_OK");			
		}else {
			addToTheModel(redir, "NAME_ERROR");			
		}
		return redirectMainPage;
	}
	
	/*
	 * Update the user's email if the user introduce the correct current password and both email's field match. After, redirect to the main page.
	 * If the typed email already exists in the database or the current password does't match with the typed one, send an error to the view. 
	 */
	@PostMapping(value = "/updateemail")
	public RedirectView updateEmail(@RequestParam("currentpassword") String currentpassword,
			@RequestParam("firstFieldEmail") String firstFieldEmail, @RequestParam("secondFieldEmail") String secondFieldEmail, RedirectAttributes redir,
			HttpServletRequest request) {
		
		RedirectView redirectMainPage = new RedirectView("/main", true);

		User user = sharedMethods.findCookies(request);	
		firstFieldEmail = firstFieldEmail.trim();
		secondFieldEmail = secondFieldEmail.trim();

		if (currentpassword.equals(user.getPassword())) {
			if (userDao.findUserByEmail(secondFieldEmail) == null) {
				Matcher mather = pattern.matcher(secondFieldEmail);
				if(mather.find() == true) {				
					user.setEmail(secondFieldEmail);
					userDao.update(user);
				}else {
					addToTheModel(redir, "EMAIL_ERROR");
				}
			} else {
				addToTheModel(redir, "EMAIL_ERROR1");
			}
		} else {
			addToTheModel(redir, "PASSWORD_ERROR");
		}
		return redirectMainPage;
	}
	
	/*
	 * Update the user's password if the user introduce the correct current password and both password's fields match. After, redirect to the main page.
	 * If the current password or the new password does't match with the typed one, send an error to the view.
	 */
	@PostMapping(value = "/updatepassword")
	public RedirectView updatePassword(@RequestParam("currentpassword") String currentpassword,
			@RequestParam("firstFieldPassword") String firstFieldPassword, @RequestParam("secondFieldPassword") String secondFieldPassword,
			RedirectAttributes redir, HttpServletRequest request) {
		
		RedirectView redirectMainPage = new RedirectView("/main", true);
		
		User user = sharedMethods.findCookies(request);
		
		if(!(firstFieldPassword.equals(secondFieldPassword))) {			
			addToTheModel(redir, "FIELD_ERROR");
			return redirectMainPage;
		}
		else{
			if (currentpassword.equals(user.getPassword())) {
				user.setPassword(secondFieldPassword);
				userDao.update(user);
				addToTheModel(redir, "PASSWORD_OK");
			}else {
				addToTheModel(redir, "PASSWORD_ERROR");
			}
		}
		return redirectMainPage;
	}

	/*
	 *  Method to send an email
	 */
	private void sendEmail(User user) {
		emailID = user.getId();
		String subject = "Reset TB/O password";
		String link = "<a style='text-decoration: none; border-radius: 5px; padding: 15px 23px; color: white; background-color: #3498db' href='http://localhost:8080/resetpassword?id="
				+ user.getId() + "' target='_blank'>Click here to reset it!</a>";

		String body = "	<table style='max-width: 600px; padding: 10px; margin:0 auto; border-collapse: collapse;'>"
				+ "	<tr>" 
				+ "		<td style='background:white; border:15px solid #0f407a'>"
				+ "			<div style='text-align:center'>"
				+ "				<img  src='https://pbs.twimg.com/profile_images/921275833221828609/emWox-Nh_400x400.jpg' width='20%'>"
				+ "			</div>"
				+ "			<hr style='border-bottom: 3px solid #0f407a; margin-bottom: 30px;margin-top: 30px;font-weight: bold;color: #0f407a;'>"
				+ "			<div style='color: #34495e; margin: 4% 10% 2%; text-align: justify;font-family: sans-serif'>"
				+ "				<h2 style='text-align: center;color: #0f407a; margin: 0 0 7px'>Hello " + user.getName()
				+ " !</h2>" 
				+ "				<p style='margin: 2px; font-size: 15px'>"
				+ "					We received a request to reset your TB/O password. If you really did, click on the link below to choose a new one."
				+ "				</p><br/>" 
				+ "				<div style='width: 100%; text-align: center'>" + link
				+ "<br/><br/>" 
				+ "				</div>" 
				+ "				<p style='margin: 2px; font-size: 15px'>"
				+ "					If you didn't meant to reset your password, then you can just ignore this email. Your password will not change"
				+ "				</p>"
				+ "				<p style='color: #b3b3b3; font-size: 12px; text-align: center;margin: 30px 0 0'>©Copyright 2019, TB/O. All rights reserved.</p>"
				+ "			</div>" + "		</td>" + "	</tr>" + "</table>";
		sendMailService.sendMail(user.getEmail(), subject, body);
	}
	/*
	 * Method to change the model attribute - message - of the page before redirecting
	 */
	private void addToTheModel(RedirectAttributes redir, String msg) {
		redir.addFlashAttribute("message", msg);
	}
	

	
	
}
