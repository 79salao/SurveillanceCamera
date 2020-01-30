package dev.futurepath.videovigilancia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import dev.futurepath.videovigilancia.model.dao.IRecordDao;

@Controller
public class RecordController {
	
	@Autowired
	private IRecordDao recordDao;
	
	@RequestMapping(value= "/main")
	public String mainPage(Model model) {
		model.addAttribute("title","TB/O Records");
		model.addAttribute("record", recordDao.findAll());
		return "mainpage";
	}
	
	/*@RequestMapping(value = "/charts")
	public String chartsPage(Model model) {
		model.addAttribute("data", recordDao.findByDates());
		return "charts";
	}*/

}
