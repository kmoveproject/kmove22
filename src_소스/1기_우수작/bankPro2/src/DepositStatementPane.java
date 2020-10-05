import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class DepositStatementPane extends JPanel implements ActionListener, KeyListener, ItemListener{
	
	private JTextField txtCNo, txtANo, txtSum;
	private JComboBox cbANo;
	private JButton btnCustomerSeach, btnCustomerSelect;
	private JScrollPane scrollPane;
	private JTable table;
	
	public DepositStatementPane(){
		initialize();
	}
	
	public void initialize(){
		this.setLayout(null);
		scrollPane = new JScrollPane();
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		JLabel lbl1 = new JLabel("고객번호");
		JLabel lbl2 = new JLabel("계좌번호");
		JLabel lbl3 = new JLabel("잔액");
		txtCNo = new JTextField();
		txtSum = new JTextField();
		cbANo = new JComboBox();
		btnCustomerSelect = new JButton(new ImageIcon(CellButton.SRC_CUSTOMER_SEARCH));
		btnCustomerSeach = new JButton(new ImageIcon(CellButton.SRC_SEARCH));
		
		lbl1.setBounds(50, 30, 80, 25);
		lbl2.setBounds(50, 80, 80, 25);
		lbl3.setBounds(320, 80, 50, 25);
		txtCNo.setBounds(130, 30, 150, 25);
		txtSum.setBounds(370, 80, 150, 25);
		cbANo.setBounds(130, 80, 150, 25);
		btnCustomerSeach.setBounds(300, 30, 100, 25);
		btnCustomerSelect.setBounds(420, 30, 100, 25);
		
		txtSum.setEditable(false);
		
		this.add(lbl1);
		this.add(lbl2);
		this.add(lbl3);
		this.add(txtCNo);
		this.add(txtSum);
		this.add(btnCustomerSeach);
		this.add(btnCustomerSelect);
		this.add(cbANo);
		this.add(scrollPane);
		
		cbANo.addItemListener(this);
		txtCNo.addKeyListener(this);
		btnCustomerSeach.addActionListener(this);
		btnCustomerSelect.addActionListener(this);
		
	}

	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		scrollPane.setBounds(50, 120, width-110, height-200);
	}
	
	public void viewTable(){
		
		String a_no = cbANo.getSelectedItem().toString();
		String c_no = txtCNo.getText().trim();
		String sql = "select a_serial_no, to_char(a_amount, '9,999,999,999,999'), to_char(a_date, 'yyyy-mm-dd') "
				+ "from account where a_no='" + a_no + "' and a_c_no='" + c_no + "'";
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		String columnName[] = {"일련번호", "거래금액", "거래일"};
		DefaultTableModel model = new DefaultTableModel(db.getData(), columnName){
			public boolean isCellEditable(int row, int column){
				if(column < this.getColumnCount()){
					return false;	
				}
				return true;
			}
		};
		db.disConnect();
		
		table.setModel(model);
		table.setRowSorter(new TableRowSorter(model));
		table.setBackground(new Color(255, 255, 224));
		table.setRowHeight(30);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		for(int i=0; i<columnName.length; i++){
			table.getColumn(columnName[i]).setCellRenderer(centerRenderer);	
		}
		scrollPane.setVisible(true);
		scrollPane.setViewportView(table);
	}

	public void eventBtnCustomerSelect(){
		CustomerSelectFrame frame = new CustomerSelectFrame(this, txtCNo);
	}
	
	public boolean checkCustomer() {
		String c_no = txtCNo.getText().trim();
		String sql = "select c_no from customer where c_no='" + c_no + "'";
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		ResultSet rs = db.getResultSet();
		boolean check=true;
		try {
			if(!rs.next()) {
				check = false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			check = false;
			db.disConnect();
		}
		return check;
	}
	
	public void eventBtnCustomerSearch(){
		cbANo.removeAllItems();
		txtSum.setText("");
		String no = txtCNo.getText().trim();
		scrollPane.setVisible(false);
		if(no.equals("")) {
			MsgBox.alert(("고객번호를 입력해주세요."));
			return;
		}
		if(!checkCustomer()) {
			MsgBox.alert("존재하지 않은 고객번호입니다.");
			return;
		}
		
		table = new JTable();
		String sql = "select distinct a_no from account where a_c_no='" + no + "'";
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		ResultSet rs = db.getResultSet();
		try {
			while(rs.next()){
				cbANo.addItem(rs.getString(1).trim());
			}
//			rs.last();
//			int row = rs.getRow();
//			if(row == 0){
//				scrollPane.setVisible(false);
//			}
		} catch (SQLException e) {
			e.printStackTrace();
			db.disConnect();
		}
		db.disConnect();
		
	}
	
	public void eventCbANo(){
		String a_no = cbANo.getSelectedItem().toString();
		String c_no = txtCNo.getText().trim();
		if(a_no.equals("") || a_no.equals(null) || c_no.equals("") || c_no.equals(null)) {return;}
		String sql = "select to_char(sum(nvl(a_amount,0)), '9,999,999,999,999') from account where a_no='"+a_no+"' and a_c_no='"+c_no+"'";
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		ResultSet rs = db.getResultSet();
		try {
			while(rs.next()){
				txtSum.setText(rs.getString(1).trim());	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.disConnect();
		viewTable();
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnCustomerSelect){
			eventBtnCustomerSelect();
		}
		else if(e.getSource() == btnCustomerSeach){
			eventBtnCustomerSearch();
		}
	}
	

	public void keyPressed(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			eventBtnCustomerSearch();
		}
	}

	public void itemStateChanged(ItemEvent e) {
		if(e.getStateChange()==ItemEvent.SELECTED){
			eventCbANo();
		}
	}	
	
}