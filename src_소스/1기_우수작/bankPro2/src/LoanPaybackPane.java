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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class LoanPaybackPane extends JPanel implements ActionListener, KeyListener, ItemListener{
	
	private JTextField txtCNo, txtMoney, txtLoan, txtMoney1, txtMoney2, txtMoney3;
	private JComboBox cbANo;
	private JButton btnCustomerSeach, btnCustomerSelect, btnSave;
	private JScrollPane scrollPane;
	private JTable table;
	private int a_serial_no;
	
	public LoanPaybackPane(){
		initialize();
	}
	
	public void initialize(){
		this.setLayout(null);
		scrollPane = new JScrollPane();
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		JLabel lbl1 = new JLabel("고객번호");
		JLabel lbl2 = new JLabel("계좌번호");
		JLabel lbl3 = new JLabel("대출종류");
		JLabel lbl4 = new JLabel("대출금액");
		JLabel lbl5 = new JLabel("상환한 금액");
		JLabel lbl6 = new JLabel("남은 금액");
		JLabel lbl7 = new JLabel("상환할 금액");
		
		txtCNo = new JTextField();
		txtMoney1 = new JTextField("0");
		txtMoney2 = new JTextField("0");
		txtMoney3 = new JTextField("0");
		txtLoan = new JTextField("");
		txtMoney = new JTextField("");
		
		cbANo = new JComboBox();
		
		btnCustomerSelect = new JButton(new ImageIcon(CellButton.SRC_CUSTOMER_SEARCH));
		btnCustomerSeach = new JButton(new ImageIcon(CellButton.SRC_SEARCH));
		btnSave = new JButton("상환하기");
		
		lbl1.setBounds(50, 30, 80, 25);
		lbl2.setBounds(50, 80, 80, 25);
		lbl3.setBounds(300, 80, 80, 25);
		lbl4.setBounds(50, 130, 80, 25);
		lbl5.setBounds(300, 130, 80, 25);
		lbl6.setBounds(550, 130, 80, 25);
		lbl7.setBounds(430, 200, 80, 25);
		
		txtCNo.setBounds(130, 30, 150, 25);
		txtMoney1.setBounds(130, 130, 150, 25);
		txtMoney2.setBounds(380, 130, 150, 25);
		txtMoney3.setBounds(630, 130, 150, 25);
		
		txtMoney.setBounds(510, 200, 150, 25);
		txtLoan.setBounds(380, 80, 150, 25);
		
		cbANo.setBounds(130, 80, 150, 25);
		
		btnCustomerSeach.setBounds(300, 30, 100, 25);
		btnCustomerSelect.setBounds(420, 30, 100, 25);
		btnSave.setBounds(680, 200, 100, 25);
		
		
		this.add(lbl1);
		this.add(lbl2);
		this.add(lbl3);
		this.add(lbl4);
		this.add(lbl5);
		this.add(lbl6);
		this.add(lbl7);
		this.add(txtCNo);
		this.add(txtMoney1);
		this.add(txtMoney2);
		this.add(txtMoney3);
		this.add(txtMoney);
		this.add(txtLoan);
		this.add(btnCustomerSeach);
		this.add(btnCustomerSelect);
		this.add(btnSave);
		this.add(cbANo);
		this.add(scrollPane);
		
		txtMoney1.setEditable(false);
		txtMoney2.setEditable(false);
		txtMoney3.setEditable(false);
		txtLoan.setEditable(false);
		
		cbANo.addItemListener(this);
		txtCNo.addKeyListener(this);
		txtMoney.addKeyListener(this);
		btnCustomerSeach.addActionListener(this);
		btnCustomerSelect.addActionListener(this);
		btnSave.addActionListener(this);
		
	}

	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		scrollPane.setBounds(50, 240, width-110, height-320);
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
		txtMoney1.setText("0");
		txtMoney2.setText("0");
		txtMoney3.setText("0");
		txtMoney.setText("");
		txtLoan.setText("");
		scrollPane.setVisible(false);
		String no = txtCNo.getText().trim();
		if(no.equals("")) {
			MsgBox.alert("고객번호를 입력해주세요.");
			return;
		}
		if(!checkCustomer()) {
			MsgBox.alert("존재하지 않은 고객번호입니다.");
			return;
		}
		table = new JTable();
		String sql = "select distinct a_no from account where a_c_no='" + no + "' and a_item_dist='L1'";
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
		txtMoney.setText("");
		txtMoney1.setText("0");
		txtMoney2.setText("0");
		txtMoney3.setText("0");
		if(c_no.equals("") || cbANo == null) return;
		String a_no = cbANo.getSelectedItem().toString();
		String sql = "select sum(a_amount), max(a_serial_no)+1 from account where a_no='"+a_no+"' and a_c_no='"+c_no+"'";
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		ResultSet rs = db.getResultSet();
		try {
			if(rs.next()) {
				long money3 = rs.getInt(1) * -1;
				a_serial_no = rs.getInt(2);
				sql = "select a_amount, a_item_name from account where a_no='"+a_no+"' and a_c_no='"+c_no+"' and a_serial_no = 1";
				db.executeQuery(sql);
				rs = db.getResultSet();
				if(rs.next()) {
					long money1 = rs.getInt(1) * -1;
					long money2 = money1 - money3;
					DecimalFormat formatter = new DecimalFormat("###,###,###,###,###");
					txtMoney1.setText(formatter.format(money1));
					txtMoney2.setText(formatter.format(money2));
					txtMoney3.setText(formatter.format(money3));
					txtLoan.setText(rs.getString(2));	
				}
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
		String strAmount = txtMoney.getText().trim();
		if(strAmount.equals("")) {
			MsgBox.alert("상환할 금액을 입력해주세요.");
			return;
		}
		if(!numCheck(strAmount)) {
			MsgBox.alert("숫자만 입력하세요");
			return;
		}
		if(strAmount.length() >= 13) {
			MsgBox.alert("1조원미만으로 입력해주세요.");
			return;
		}
		long a_amount = Long.valueOf(strAmount);
		if(a_amount > Long.valueOf(removeComma(txtMoney3.getText()))) {
			MsgBox.alert("남은 금액이하로 입력해주세요.");
			return;
		}
		DecimalFormat formatter = new DecimalFormat("###,###,###,###,###");
		strAmount = formatter.format(a_amount);
		String a_no = cbANo.getSelectedItem().toString();
		int check = MsgBox.confirm(strAmount + "원 상환 하시겠습니까?");
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
		txtMoney.setText("");
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