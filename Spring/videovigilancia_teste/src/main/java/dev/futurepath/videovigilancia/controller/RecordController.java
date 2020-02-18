package dev.futurepath.videovigilancia.controller;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import dev.futurepath.videovigilancia.model.dao.IRecordDao;
import dev.futurepath.videovigilancia.model.dao.IUserDao;
import dev.futurepath.videovigilancia.model.entity.User;


@Controller
public class RecordController {
	
	@Autowired
	private IRecordDao recordDao;
	@Autowired
	private IUserDao userDao;
	
	@RequestMapping(value= "/main")
	public ModelAndView mainPage(HttpServletRequest request) throws ParseException{
		ModelAndView mainModelAndView = new ModelAndView("html/mainpage");
		mainModelAndView.addObject("title","TB/O Records");
		String from, to;		
		if(request.getQueryString() != null) {
			from = request.getQueryString().substring(5,15);
			to = request.getQueryString().substring(19);
		}else {
			from = LocalDateTime.now().minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}
		mainModelAndView.addObject("record", recordDao.findRecordsByDates(from, to));
		mainModelAndView.addObject("from", from);
		mainModelAndView.addObject("to", to);
		
		/*String cookieUsername = (cookieID(request));
		//Long cookieId = Long.parseLong(cookieID(request));
		User user = userDao.findUserByUsername(cookieUsername);*/
		User user = findCookies(request);
		mainModelAndView.addObject("user", user);
		return mainModelAndView;
	}
	/*
     * Método que devulve la página de charts. Incluye atributos for, to y records,
     * que es un array de Dates.
     */
    @RequestMapping(value = "/charts")
    public ModelAndView chartsPage(HttpServletRequest request) {
    	ModelAndView chartsModelAndView = new ModelAndView("html/statsPage");    	
    	chartsModelAndView.addObject("title", "TB/O Charts");
        String from, to;
        if (request.getQueryString() != null) {
            from = request.getQueryString().substring(5, 15);
            to = request.getQueryString().substring(19);
        } else {
            from = LocalDateTime.now().minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            to = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        chartsModelAndView.addObject("records", recordDao.findDatesByDates(from, to));
        chartsModelAndView.addObject("from", from);
        chartsModelAndView.addObject("to", to);        
        
        User user = findCookies(request);
        chartsModelAndView.addObject("user", user);        
        return chartsModelAndView;
    }
    //copypasteado
    @RequestMapping(value = "/charts2")
    public ModelAndView chartsPage2(HttpServletRequest request) {
    	ModelAndView chartsModelAndView = new ModelAndView("html/statsPage2");    	
    	chartsModelAndView.addObject("title", "TB/O Charts");
        String from, to;
        if (request.getQueryString() != null) {
            from = request.getQueryString().substring(5, 15);
            to = request.getQueryString().substring(19);
        } else {
            from = LocalDateTime.now().minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            to = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        
        List<Date> records = recordDao.findDatesByDates(from, to);
        chartsModelAndView.addObject("records", records);
        for (Date date : records) {
        	System.out.println(date);
		}
        
        chartsModelAndView.addObject("from", from);
        chartsModelAndView.addObject("to", to);  
        User user = findCookies(request);
        chartsModelAndView.addObject("user", user);
        return chartsModelAndView;
    }
    
    private User findCookies(HttpServletRequest request) {
    	String cookieUsername = (cookieID(request));
    	User user = userDao.findUserByUsername(cookieUsername);
		return user;
    }
    
	@GetMapping("/all-cookies")
	public String readAllCookies(HttpServletRequest request) {
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	        return Arrays.stream(cookies)
	                .map(c -> c.getValue()).collect(Collectors.joining(", "));
	    }
	    return "No cookies";
	}
	@GetMapping("/cookieid")
	public String cookieID(HttpServletRequest request) {
	    Cookie idUser = WebUtils.getCookie(request, "username");
	    if (idUser != null) {
	        return idUser.getValue();
	    } else {
	        return "Not found!";
	    }
	}
}