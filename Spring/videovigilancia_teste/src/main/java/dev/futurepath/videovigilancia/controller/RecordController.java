package dev.futurepath.videovigilancia.controller;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import dev.futurepath.videovigilancia.model.dao.IRecordDao;
import dev.futurepath.videovigilancia.model.entity.User;


@Controller
public class RecordController {
	
	@Autowired
	private IRecordDao recordDao;
	@Autowired
	private SharedMethods sharedMethods;
	
	@RequestMapping(value= "/main")
	public ModelAndView mainPage(HttpServletRequest request) throws ParseException{
		ModelAndView mainModelAndView = new ModelAndView("html/mainpage");
		ModelAndView redirectSignIn = new ModelAndView("redirect:/");
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
		User user = sharedMethods.findCookies(request);
		mainModelAndView.addObject("user", user);
		if(user==null) {
			return redirectSignIn;
		}
		return mainModelAndView;
	}
	/*
     * Método que devulve la página de charts. Incluye atributos for, to y records,
     * que es un array de Dates.
     */
    @RequestMapping(value = "/charts")
    public ModelAndView chartsPage(HttpServletRequest request) {
    	ModelAndView chartsModelAndView = new ModelAndView("html/statsPage");
    	ModelAndView redirectSignIn = new ModelAndView("redirect:/");
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
        
        User user = sharedMethods.findCookies(request);
        chartsModelAndView.addObject("user", user); 
        if(user==null) {
			return redirectSignIn;
		}
        return chartsModelAndView;
    }
    //copypasteado
    @RequestMapping(value = "/charts2")
    public ModelAndView chartsPage2(HttpServletRequest request) {
    	ModelAndView chartsModelAndView = new ModelAndView("html/statsPage2"); 
    	ModelAndView redirectSignIn = new ModelAndView("redirect:/");
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
        User user = sharedMethods.findCookies(request);
        chartsModelAndView.addObject("user", user);
        if(user==null) {
			return redirectSignIn;
		}
        return chartsModelAndView;
    }    
	


}