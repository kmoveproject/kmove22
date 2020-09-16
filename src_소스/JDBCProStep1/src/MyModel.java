import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import javax.swing.table.AbstractTableModel;

public class MyModel extends AbstractTableModel {

	Object [][]data;
	String []columnName;
	
	int rows, cols;
	
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columnName.length;   //4개의 필드
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return data.length;         //2개의 레코드 
	}
	
	//select count(*) from customer
	public void getRowCount(ResultSet rsScroll) {
		try {
			rsScroll.last();
			rows=rsScroll.getRow();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public Object getValueAt(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return data[arg0][arg1];   //낱개씩 리턴
	}

	@Override
	public String getColumnName(int column) {
		// TODO Auto-generated method stub
		return columnName[column];
	}
	
	
	public void setData(ResultSet rs) {
		try {
			ResultSetMetaData rsmd;
			
			rsmd=rs.getMetaData();
			cols=rsmd.getColumnCount();  //4개의 컬럼(필드)
			columnName=new String[cols];
			for(int i=0; i<cols; i++)
				columnName[i]=rsmd.getColumnName(i+1);
			data=new Object[rows][cols];
			int r=0, c;
			while(rs.next()){
				for( c=0; c<cols; c++){
						data[r][c]=rs.getObject(c+1);
					}
				r++;
			}
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

}
