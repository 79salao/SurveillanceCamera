package dev.futurepath.videovigilancia.controller;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.List;


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
		return modelView(request, "html/mainpage");
	}
	
    @RequestMapping(value = "/charts")
    public ModelAndView chartsPage(HttpServletRequest request) {
    	return modelView(request, "html/statsPage");
    }

    @RequestMapping(value = "/charts2")
    public ModelAndView chartsPage2(HttpServletRequest request) {
    	return modelView(request, "html/statsPage2");
    }    
    
    /*
     * Method to avoid duplicated code in this class
     */
    private ModelAndView modelView(HttpServletRequest request, String direccion) {
    	
    	User user = sharedMethods.findCookies(request);
    	if(user==null) {
			return new ModelAndView("redirect:/");
		}else {
			String from, to;
			ModelAndView modelAndView = new ModelAndView(direccion);	
			if(request.getQueryString() != null) {
				try {
					from = request.getQueryString().substring(5,15);
					to = request.getQueryString().substring(19);
				}catch(StringIndexOutOfBoundsException ex) {
					from = LocalDateTime.now().minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
					to = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				}
			}else{
				from = LocalDateTime.now().minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				to = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			}
			
			if(direccion.equals("html/mainpage")) {
				modelAndView.addObject("record", recordDao.findRecordsByDates(from, to));
			}else{
				List<Date> records = recordDao.findDatesByDates(from, to);
				modelAndView.addObject("record", records);
			}
			
			modelAndView.addObject("from", from);
			modelAndView.addObject("to", to);
			modelAndView.addObject("title","TB/O");
			modelAndView.addObject("user", user);
			return modelAndView;
		}
    }
    
}