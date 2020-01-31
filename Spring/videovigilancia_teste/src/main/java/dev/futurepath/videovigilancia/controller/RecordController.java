package dev.futurepath.videovigilancia.controller;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dev.futurepath.videovigilancia.model.dao.IRecordDao;


@Controller
public class RecordController {
	
	@Autowired
	private IRecordDao recordDao;
	
	@RequestMapping(value= "/main")
	public String mainPage(HttpServletRequest request, Model model) throws ParseException{
		model.addAttribute("title","TB/O Records");
		
		String from, to;
		
		if(request.getQueryString() != null) {
			model.addAttribute("title","TB/O Records");
			from = request.getQueryString().substring(5,15);
			to = request.getQueryString().substring(19);

		}else {
			from = LocalDateTime.now().minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}
		model.addAttribute("record", recordDao.findByDates(from, to));
		model.addAttribute("from", from);
		model.addAttribute("to", to);

		return "mainpage";
	}
	
	/*@RequestMapping(value = "/charts")
	public String chartsPage(Model model) {
		model.addAttribute("data", recordDao.findByDates());
		return "charts";
	}*/

}
