package dev.futurepath.videovigilancia.model.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import dev.futurepath.videovigilancia.model.entity.Record;

@Repository
public class RecordDaoImpl implements IRecordDao {
	
	@PersistenceContext
	private EntityManager em; 

	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	@Override
	public List<Record> findAll() {
		return em.createQuery("from Record").getResultList();
	}

	@Override
	public List<Record> findByDates(String from, String to) {
		return em.createQuery("FROM Record WHERE date >= '"+ from+ " 00:00:00' AND date <= '" + to + " 23:59:59'").getResultList();
	}

}
