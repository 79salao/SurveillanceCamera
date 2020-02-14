package dev.futurepath.videovigilancia.model.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dev.futurepath.videovigilancia.model.entity.Record;


public interface IRecordDao {
	
	public List<Record> findAll();
	
	public List<Record> findRecordsByDates(String from, String to);

	public List<Date> findDatesByDates(String from, String to);

	public boolean saveRecording(String str);	
}