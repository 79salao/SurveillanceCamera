package dev.futurepath.videovigilancia.model.dao;

import java.util.List;

import dev.futurepath.videovigilancia.model.entity.Record;


public interface IRecordDao {
	
	public List<Record> findAll();
	
	List<Record> findByDates(String from, String to);	
	
}
