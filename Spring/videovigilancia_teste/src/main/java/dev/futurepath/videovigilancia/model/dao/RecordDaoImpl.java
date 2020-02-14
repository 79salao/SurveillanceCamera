package dev.futurepath.videovigilancia.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import dev.futurepath.videovigilancia.model.MySQLConnection;
import dev.futurepath.videovigilancia.model.entity.Record;

@Repository
public class RecordDaoImpl implements IRecordDao {
	
	@PersistenceContext
	private EntityManager em; 

	/*
	 * Returns all recordings.
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	@Override
	public List<Record> findAll() {
		return em.createQuery("FROM Record").getResultList();
	}
	
	/*
	 * With the from and to parameters searches for the records.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Record> findRecordsByDates(String from, String to) {
		return em.createQuery("FROM Record WHERE date >= '"+ from+ " 00:00:00' AND date <= '" + to + " 23:59:59'").getResultList();
	}
	/*
	 * With the from and to parameters searches for the dates between them.
	 * Returns them ordered by asc.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Date> findDatesByDates(String from, String to) {
		//Comprobar si funciona el order by
		return em.createQuery("SELECT date FROM Record WHERE date >= '"+ from + " 00:00:00' AND date <= '" + to + " 23:59:59' ORDER BY date ASC").getResultList();
	}

	/*
	 * ERROR: Doesn't record in the DB
	 * Saves the record receives from the camera into the DB
	 */
	@Override
	public boolean saveRecording(String str) {

        Connection connection = null;
        PreparedStatement query = null;
		String insert = "INSERT INTO records (date, duration, camera, video_location) VALUES (?,?,?,?)";
		Record record = stringToRecord(str);
        
        try{
            connection = MySQLConnection.getConnection();
            query = connection.prepareStatement(insert, PreparedStatement.RETURN_GENERATED_KEYS);
			query.setString(1, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(record.getDate()));
			query.setLong(2, record.getDuration());
			query.setLong(3, record.getCamera());
			query.setString(4, record.getVideoLocation());
            int rows = query.executeUpdate();
            if(rows > 0){
                ResultSet rs = query.getGeneratedKeys();
                rs.close();
            }
        }catch(SQLException ex){
            System.out.println(ex);
        }finally{
            try{
                query.close();
                connection.close();
                return true;
            }catch(SQLException e){
                System.out.println("Can't create anything");
                return false;
            }

        }
	}
	/*
	 * Method to split the string and generate a record element with it.
	 * The format we receive is like "RECORD,2020_02_04_11:30:55.h264,1,14"
	 */
	public Record stringToRecord(String str) {
		String[] data = str.split(",");
		String[] fileName = data[1].split("_|\\.");

		String dateStr = fileName[0] +"-"+ fileName[1] + "-" + fileName[2] + " " + fileName[3];
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}  
		int camera = Integer.parseInt(data[2]);
		int duration = Integer.parseInt(data[3]);
		String videoLocation = "/home/pi/Desktop/SurveillanceCamera-master/" + data[1];
		
		Record record = new Record();
		record.setDate(date);
		record.setCamera(camera);
		record.setDuration(duration);
		record.setVideoLocation(videoLocation);
		
		return record;
	}

}
