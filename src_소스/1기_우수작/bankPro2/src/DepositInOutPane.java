import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class DepositInOutPane extends JPanel implements ActionListener, KeyListener, ItemListener{
	
	private JTextField txtCNo, txtSum, txtInOut, txtDeposit;
	private JComboBox cbANo, cbInOut;
	private JButton btnCustomerSeach, btnCustomerSelect, btnSave;
	private JScrollPane scrollPane;
	private JTable table;
	private String inout_dist[] = {"입금", "출금"};
	private int a_serial_no;
	
	public DepositInOutPane(){
		initialize();
	}
	
	public void initialize(){
		this.setLayout(null);
		scrollPane = new JScrollPane();
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		JLabel lbl1 = new JLabel("고객번호");
		JLabel lbl2 = new JLabel("계좌번호");
		JLabel lbl3 = new JLabel("입금/출금");
		JLabel lbl4 = new JLabel("잔액");
		JLabel lbl5 = new JLabel("금액");
		JLabel lbl6 = new JLabel("예금종류");
		
		txtCNo = new JTextField();
		txtSum = new JTextField("0");
		txtInOut = new JTextField("");
		txtDeposit = new JTextField("");
		
		cbANo = new JComboBox();
		cbInOut = new JComboBox();
		for(int i=0; i<inout_dist.length; i++){
			cbInOut.addItem(inout_dist[i]);
		}
		
		btnCustomerSelect = new JButton(new ImageIcon(CellButton.SRC_CUSTOMER_SEARCH));
		btnCustomerSeach = new JButton(new ImageIcon(CellButton.SRC_SEARCH));
		btnSave = new JButton("입금하기");
		
		lbl1.setBounds(50, 30, 80, 25);
		lbl2.setBounds(50, 80, 80, 25);
		lbl3.setBounds(550, 80, 80, 25);
		lbl4.setBounds(50, 130, 80, 25);
		lbl5.setBounds(300, 130, 80, 25);
		
		lbl6.setBounds(300, 80, 80, 25);
		
		txtCNo.setBounds(130, 30, 150, 25);
		txtSum.setBounds(130, 130, 150, 25);
		txtInOut.setBounds(380, 130, 150, 25);
		
		txtDeposit.setBounds(380, 80, 150, 25);
		
		cbANo.setBounds(130, 80, 150, 25);
		cbInOut.setBounds(630, 80, 150, 25);
		
		btnCustomerSeach.setBounds(300, 30, 100, 25);
		btnCustomerSelect.setBounds(420, 30, 100, 25);
		btnSave.setBounds(550, 130, 100, 25);
		
		
		this.add(lbl1);
		this.add(lbl2);
		this.add(lbl3);
		this.add(lbl4);
		this.add(lbl5);
		this.add(lbl6);
		this.add(txtCNo);
		this.add(txtSum);
		this.add(txtInOut);
		this.add(txtDeposit);
		this.add(btnCustomerSeach);
		this.add(btnCustomerSelect);
		this.add(btnSave);
		this.add(cbANo);
		this.add(cbInOut);
		this.add(scrollPane);
		
		txtSum.setEditable(false);
		txtDeposit.setEditable(false);
		
		cbANo.addItemListener(this);
		cbInOut.addItemListener(this);
		txtCNo.addKeyListener(this);
		txtInOut.addKeyListener(this);
		btnCustomerSeach.addActionListener(this);
		btnCustomerSelect.addActionListener(this);
		btnSave.addActionListener(this);
		
	}

	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		scrollPane.setBounds(50, 170, width-110, height-250);
	}
	
	public void viewTable(){
		if(table == null) return;
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
		table.setRowHeight(25);
		
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
		txtSum.setText("0");
		txtInOut.setText("");
		String no = txtCNo.getText().trim();
		scrollPane.setVisible(false);
		if(no.equals("")) {
			MsgBox.alert("고객번호를 입력해주세요.");
			return;
		}
		if(!checkCustomer()) {
			MsgBox.alert("존재하지 않은 고객번호입니다.");
			return;
		}
		table = new JTable();
		String sql = "select distinct a_no from account where a_c_no='" + no + "' and a_item_dist='A0'";
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		ResultSet rs = db.getResultSet();
		try {
			while(rs.next()){
				cbANo.addItem(rs.getString(1).trim());
			}
			rs.last();
			int row = rs.getRow();
			if(row == 0){
				scrollPane.setVisible(false);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.disConnect();
	}
	
	public void eventCbANo(){
		String c_no = txtCNo.getText().trim();
		txtInOut.setText("");
		if(c_no.equals("") || cbANo == null) return;
		String a_no = cbANo.getSelectedItem().toString();
		String sql = "select to_char(sum(nvl(a_amount,0)), '9,999,999,999,999'), max(a_serial_no)+1 from account where a_no='"+a_no+"' and a_c_no='"+c_no+"'";
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		ResultSet rs = db.getResultSet();
		try {
			rs.next();
			txtSum.setText(rs.getString(1).trim());
			a_serial_no = rs.getInt(2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		sql = "select distinct a_item_name from account where a_no='"+a_no+"' and a_c_no='"+c_no+"'";;
		db.executeQuery(sql);
		rs = db.getResultSet();
		try {
			if(rs.next()) {
				txtDeposit.setText(rs.getString(1).trim());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.disConnect();
		viewTable();
	}
	
	public void eventBtnSave(){
		String c_no = txtCNo.getText().trim();
		if(c_no.equals("")) {
			MsgBox.alert("고객번호를 입력해주세요.");
			return;
		}
		if(!checkCustomer()) {
			MsgBox.alert("존재하지 않은 고객번호입니다.");
			return;
		}
		if(cbANo.getSelectedItem() == null) {
			MsgBox.alert("고객번호를 검색해주세요.");
			return;
		}
		String strAmount = txtInOut.getText().trim();
		String strInOut = cbInOut.getSelectedItem().toString();
		if(strAmount.equals("")) {
			MsgBox.alert(strInOut + "할 금액을 입력해주세요.");
			return;
		}
		if(!numCheck(strAmount)) {
			MsgBox.alert("숫자만 입력하세요");
			return;
		}
		if(strAmount.length() >= 13) {
			MsgBox.alert("1조미만으로 입력해주세요.");
			return;
		}
		long a_amount = Long.valueOf(strAmount);
		String a_no = cbANo.getSelectedItem().toString();
		String dist = cbInOut.getSelectedItem().toString();
		if(dist.equals(inout_dist[1])) {
			a_amount *= -1;
			long money = Long.valueOf(removeComma(txtSum.getText().trim()));
			if(a_amount + money < 0) {
				MsgBox.alert("계좌에 돈이 부족합니다.");
				return;
			}
		}
		if(dist.equals(inout_dist[1])) {
			DecimalFormat formatter = new DecimalFormat("###,###,###,###,###");
			strAmount = formatter.format(-a_amount);
		}
		int check = MsgBox.confirm(strAmount + "원 " + dist + "하시겠습니까?");
		if(check == 0){
			String sql = "INSERT INTO account select '"+a_no+"', "+a_serial_no+", sysdate, "+a_amount+", a.a_open_date, null, a.a_term, a.a_item_dist, a.a_item_name, a.a_b_no, '"+c_no+"'"
						+ "from account a where a.a_no = '" + a_no + "' and a.a_c_no = '" + c_no + "' and a_serial_no = 1 ";
	        DBControler db = new DBControler();
	        db.connect();
	        db.executeQuery(sql);
	        db.disConnect();
		}
		else return;
		eventCbANo();
		viewTable();
	}
	
	public String removeComma(String str) {
		String temp = "";
		for(int i=0; i<str.length(); i++) {
			if(str.charAt(i) != ',') {
				temp += str.charAt(i);	
			}
		}
		return temp;
	}
	
	public void eventCbInOut() {
		txtInOut.setText("");
		btnSave.setText(cbInOut.getSelectedItem().toString() + "하기");
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnCustomerSelect){
			eventBtnCustomerSelect();
		}
		else if(e.getSource() == btnCustomerSeach){
			eventBtnCustomerSearch();
		}
		else if(e.getSource() == btnSave){
			eventBtnSave();
		}
	}
	

	public void keyPressed(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {
		if(e.getSource() == txtCNo){
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				eventBtnCustomerSearch();
			}	
		}
	}

	public void itemStateChanged(ItemEvent e) {
		if(e.getSource() == cbANo) {
			if(e.getStateChange()==ItemEvent.SELECTED){
				eventCbANo();
			}	
		}
		else if(e.getSource() == cbInOut) {
			if(e.getStateChange() == ItemEvent.SELECTED) {
				eventCbInOut();
			}
		}
	}
	
	public boolean numCheck(String str){
		for(int i=0; i<str.length(); i++){
			if(str.charAt(i) < '0' || str.charAt(i) > '9'){
				return false;
			}
		}
		return true;
	}
	
}