import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DBControler {
	private String driver;
	private String url;
	private String user;
	private String password;
	
	private Connection conn;
	private PreparedStatement pstmtScroll;
	private ResultSet rsScroll;
	
	private String[] columnName;
	private String[][] data;
	
	public DBControler(){
		driver = "oracle.jdbc.driver.OracleDriver";
		url = "jdbc:oracle:thin:@127.0.0.1:1521:xe";
		user = "system";
		password = "123456";
	}
	
	public void connect(){
		try {
			Class.forName(driver);
			conn=DriverManager.getConnection(url, user, password);
			} catch (Exception e) { 
				e.printStackTrace();
			} 
	}
	
	public void disConnect() {
		try {
			rsScroll.close();
			pstmtScroll.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean executeQuery(String sql){
		String msg = sql.substring(0, sql.indexOf(" "));
		boolean check = true;
		try {
			pstmtScroll = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rsScroll = pstmtScroll.executeQuery();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			check = false;
		}
		return check;
	}
	
	public ResultSet getResultSet(){
		return rsScroll;
	}
	
	private void setData(){
		try {
			rsScroll.last();
			int rows = rsScroll.getRow();
			rsScroll.beforeFirst();
			ResultSetMetaData rsmd = rsScroll.getMetaData();
			int cols = rsmd.getColumnCount();
			
			columnName = new String[cols];
			for(int i=0; i<cols; i++) {
				columnName[i] = rsmd.getColumnName(i+1);
			}
			
			data = new String[rows][cols];
			int r = 0;
			while(rsScroll.next()){
				for(int c=0; c<cols; c++){
					data[r][c] = rsScroll.getString(c+1);
				}
				r++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String[] getColumnName(){
		if(data == null) setData();
		return columnName;
	}
	
	public String[][] getData(){
		if(data == null) setData();
		return data;
	}
	
}