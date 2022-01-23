package springboard.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

//스프링 컨테이너가 시작될 때 scan의 대상이 되므로 자동으로 빈이 생성된다.
@Repository
public class JDBCTemplateDAO {
	
	/*
	servlet-context.xml에서 생성한 빈을 자동 주입 받는다.
	오라클 연결정보를 가지고 있다.
	 */
	@Autowired
	JdbcTemplate template;
	
	public JDBCTemplateDAO() {
		System.out.println("JDBCTemplateDAO() 생성자 호출");
	}
	
	public void close() {}
	
	public int getTotalCount(Map<String, Object> map) {
		String sql = "SELECT COUNT(*) FROM springboard";
		
		if (map.get("Word") != null) {
			sql += " WHERE " + map.get("Column") + " "
				+ " 	LIKE '%" + map.get("Word") + "%' ";
		}
		
		return template.queryForObject(sql, Integer.class);
	}
	
	public ArrayList<SpringBbsDTO> list(Map<String, Object> map) {
		String sql = ""
				+ "SELECT * FROM springboard ";
		if (map.get("Word") != null) {
			sql += " WHERE " + map.get("Column") + " "
				+ " LIKE '%" + map.get("Word") + "%' ";
		}
		sql += " ORDER BY idx DESC";
		
		return (ArrayList<SpringBbsDTO>)
				template.query(sql, new BeanPropertyRowMapper<SpringBbsDTO>(
						SpringBbsDTO.class));
	}
	
	public int write(final SpringBbsDTO springBbsDTO) {
		
		int result = template.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) 
					throws SQLException {
				
				String sql = "INSERT INTO springboard ( "
						+ " idx, name, title, contents, hits, "
						+ " bgroup, bstep, bindent, pass) "
						+ " VALUES ( "
						+ " springboard_seq.NEXTVAL, ?, ?, ?, 0, "
						+ " springboard_seq.NEXTVAL, 0, 0, ?)";
				
				PreparedStatement psmt = con.prepareStatement(sql);
				psmt.setString(1, springBbsDTO.getName());
				psmt.setString(2, springBbsDTO.getTitle());
				psmt.setString(3, springBbsDTO.getContents());
				psmt.setString(4, springBbsDTO.getPass());
				
				return psmt;
			}
		});
		
		return result;
	}
	
	public void updateHit(final String idx) {
		String sql = "UPDATE springboard SET "
				+ " hits = hits + 1 "
				+ " WHERE idx = ? ";
		
		template.update(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, Integer.parseInt(idx));
			}
		});
	}
	
	public SpringBbsDTO view(String idx) {
		
		updateHit(idx);
		
		SpringBbsDTO dto = new SpringBbsDTO();
		String sql = "SELECT * FROM springboard "
				+ " WHERE idx = " + idx;
		
		try {
			dto = template.queryForObject(sql, 
				new BeanPropertyRowMapper<SpringBbsDTO>(
					SpringBbsDTO.class));
		}
		catch (Exception e) {
			System.out.println("View()실행시 예외발생");
		}
		
		return dto;
	}
	
	public int password(String idx, String pass) {
		
		int retNum = 0;
		String sql = "SELECT * FROM springboard "
				+ " WHERE pass='"+pass+"' AND idx="+idx;
		
		try {
			SpringBbsDTO dto = template.queryForObject(sql, 
				new BeanPropertyRowMapper<SpringBbsDTO>(
						SpringBbsDTO.class));
			
			retNum = dto.getIdx();
		}
		catch (Exception e) {
			System.out.println("password() 예외발생");
		}
		
		return retNum;
	}
	
	public void edit(final SpringBbsDTO dto) {
		String sql = "UPDATE springboard "
				+ " SET name=?, title=?, contents=? "
				+ " WHERE idx=? AND pass=?";
		
		template.update(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				
				ps.setString(1, dto.getName());
				ps.setString(2, dto.getTitle());
				ps.setString(3, dto.getContents());
				ps.setInt(4, dto.getIdx());
				ps.setString(5, dto.getPass());
			}
		});
	}
	
	
	public void delete(final String idx, final String pass) {
		String sql = "DELETE FROM springboard "
				+ " WHERE idx=? AND pass=?";
		
		template.update(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				
				ps.setString(1, idx);
				ps.setString(2, pass);
			}
		});
	}
	
	
	public ArrayList<SpringBbsDTO> listPage(Map<String, Object> map){
		int start = Integer.parseInt(map.get("start").toString());
		int end = Integer.parseInt(map.get("end").toString());
		
		String sql = ""
				+"SELECT * FROM ( "
				+"    SELECT Tb.*, rownum rNum FROM ( "
				+"        SELECT * FROM springboard ";				
			if (map.get("Word")!=null) {
				sql +=" WHERE "+map.get("Column")+" "
					+ " LIKE '%"+map.get("Word")+"%' ";				
			}			
			sql += " ORDER BY idx DESC "
			+"    ) Tb "
			+" ) "
			+" WHERE rNum BETWEEN "+start+" and "+end;
		
		return (ArrayList<SpringBbsDTO>)
			template.query(sql, 
				new BeanPropertyRowMapper<SpringBbsDTO>(SpringBbsDTO.class));
	}
}
