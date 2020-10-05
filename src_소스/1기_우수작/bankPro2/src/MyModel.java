import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

public class MyModel extends AbstractTableModel{
	private Object[][] data;
	private String[] columnName;
	private int rows, cols;
	
	public int getColumnCount() {
		return cols;
	}

	public int getRowCount() {
		return rows;
	}
	
	public Object[][] getData(){
		return data;
	}
	
	public String[] getColumnName(){
		return columnName;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}
	
	public String getColumnName(int columnIndex){
		return columnName[columnIndex];
	}
	
	public void setData(ResultSet rsScroll){
		try {
			rsScroll.last();
			rows = rsScroll.getRow();
			rsScroll.beforeFirst();
			ResultSetMetaData rsmd = rsScroll.getMetaData();
			cols = rsmd.getColumnCount();
			columnName = new String[cols];
			
			for(int i=0; i<cols; i++) {
				columnName[i] = rsmd.getColumnName(i+1);
			}
			
			data = new Object[rows][cols];
			int r = 0;
			
			while(rsScroll.next()){
				for(int c=0; c<cols; c++){
					data[r][c] = rsScroll.getObject(c+1);
				}
				r++;

			}
			rsScroll.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}