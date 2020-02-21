package dev.futurepath.videovigilancia.controller;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import dev.futurepath.videovigilancia.model.dao.IUserDao;
import dev.futurepath.videovigilancia.model.entity.User;

@Controller
public class SharedMethods {
	
	@Autowired
	private IUserDao userDao;

	/*
	 * Used to return the object User based on the cookies that contains the page.
	 */	
	public User findCookies(HttpServletRequest request) {
    	String cookieUsername = (cookieID(request));
    	User user = userDao.findUserByUsername(cookieUsername);
		return user;
    }	
	/*
	 * Used to return an specific cookie that contains the page.
	 */	
	@GetMapping("/cookieid")
	public String cookieID(HttpServletRequest request) {
	    Cookie idUser = WebUtils.getCookie(request, "username");
	    if (idUser != null) {
	        return idUser.getValue();
	    } else {
	        return "Not found!";
	    }
	}	
	/*
	 * Used to return all the cookies that contains the page.
	 */	
	@GetMapping("/all-cookies")
	public String readAllCookies(HttpServletRequest request) {
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	        return Arrays.stream(cookies)
	                .map(c -> c.getValue()).collect(Collectors.joining(", "));
	    }
	    return "No cookies";
	}
	
	/*
	 * Used to delete the cookies when the user click on SignOut. The parameter username is the cookie that will be delete.
	 */	
	@GetMapping(value = "/deleteCookies")
	public RedirectView deleteCookies(HttpServletResponse response) {
		 RedirectView signOut = new RedirectView("/", true);
		 Cookie cookie = new Cookie("username", null);
		 cookie.setMaxAge(0);
		 response.addCookie(cookie);
		 return signOut;		 
	}
	
}
