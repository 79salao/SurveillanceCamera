package dev.futurepath.videovigilancia.model.dao;

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

}
