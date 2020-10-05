import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DepositOrderPane extends JPanel implements ActionListener, ItemListener{
	
	private JTextField txtAccountNo, txtCustomerNo, txtRate, txtMoney;
	private JComboBox cbCustomerDist, cbDepositKind, cbBranchName, cbTerm;
	private JButton btnCustomerSelect, btnSave, btnInit;
	private String rate_dist[][];
	private String term_dist[][];
	
	static String customer_dist[] = {"개인고객", "기업고객"};
	
	public DepositOrderPane(){
		initialize();
	}
	
	public void initialize(){
		this.setLayout(null);
		
		JLabel lblCustomerNo = new JLabel("고객번호");
		lblCustomerNo.setBounds(50, 30, 80, 25);
		this.add(lblCustomerNo);
		
		JLabel lblCustomerDist = new JLabel("고객구분");
		lblCustomerDist.setBounds(300, 30, 80, 25);
		this.add(lblCustomerDist);
		
		JLabel lblName = new JLabel("지점명");
		lblName.setBounds(50, 80, 80, 25);
		this.add(lblName);
		
		JLabel lblDeposiKind = new JLabel("예금종류");
		lblDeposiKind.setBounds(300, 80, 80, 25);
		this.add(lblDeposiKind);
		
		JLabel lblAccountNo = new JLabel("계좌번호");
		lblAccountNo.setBounds(50, 130, 80, 25);
		this.add(lblAccountNo);
		
		JLabel lblMoney = new JLabel("예금금액");
		lblMoney.setBounds(300, 130, 80, 25);
		this.add(lblMoney);
		
		JLabel lblTerm = new JLabel("계약기간");
		lblTerm.setBounds(50, 180, 80, 25);
		this.add(lblTerm);
		
		JLabel lblRate = new JLabel("이율");
		lblRate.setBounds(300, 180, 80, 25);
		this.add(lblRate);
		
		txtCustomerNo = new JTextField();
		txtCustomerNo.setBounds(130, 30, 150, 25);
		txtCustomerNo.setEditable(false);
		this.add(txtCustomerNo);
		
		cbCustomerDist = new JComboBox();
		cbCustomerDist.setBounds(380, 30, 150, 25);
		cbCustomerDist.setEnabled(false);
		this.add(cbCustomerDist);
		for(int i=0; i<customer_dist.length; i++){
			cbCustomerDist.addItem(customer_dist[i]);
		}
		
		cbBranchName = new JComboBox();
		cbBranchName.setBounds(130, 80, 150, 25);
		this.add(cbBranchName);
		setCbBranchName();
		
		txtAccountNo = new JTextField();
		txtAccountNo.setBounds(130, 130, 150, 25);
		this.add(txtAccountNo);
		
		txtMoney = new JTextField();
		txtMoney.setBounds(380, 130, 150, 25);
		this.add(txtMoney);
		
		cbDepositKind = new JComboBox();
		cbDepositKind.setBounds(380, 80, 150, 25);
		this.add(cbDepositKind);
		cbDepositKind.addItemListener(this);
		
		cbTerm = new JComboBox();
		cbTerm.setBounds(130, 180, 150, 25);
		this.add(cbTerm);
		cbTerm.addItemListener(this);
		
		txtRate = new JTextField();
		txtRate.setBounds(380, 180, 150, 25);
		this.add(txtRate);
		txtRate.setEditable(false);
		setCbDepositKind();
		setCbTerm();
		setTxtRate();
		
		btnCustomerSelect = new JButton(new ImageIcon(CellButton.SRC_CUSTOMER_SEARCH));
		btnCustomerSelect.setBounds(550, 30, 100, 25);
		this.add(btnCustomerSelect);
		btnCustomerSelect.addActionListener(this);
		
		btnSave = new JButton("저장");
		btnSave.setBounds(430, 230, 100, 25);
		this.add(btnSave);
		btnSave.addActionListener(this);
		
		btnInit = new JButton("초기화");
		btnInit.setBounds(550, 230, 100, 25);
		this.add(btnInit);
		btnInit.addActionListener(this);
	}
	
	public String getBranchNo(String branchName) {
		String b_no = "";
		String sql = "select b_no from branch where b_name='" + branchName + "'";
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		ResultSet rs = db.getResultSet();
		try {
			rs.next();
			b_no = rs.getString(1);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.disConnect();
		return b_no;
	}
	
	public boolean checkAccountNo(String accountNo) {
		String sql = "select distinct a_no from account where a_no='" + accountNo + "'";
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		ResultSet rs = db.getResultSet();
		try {
			if(rs.next()) return false;
		} catch (SQLException e) {
			e.printStackTrace();
			db.disConnect();
			return false;
		}
		db.disConnect();
		return true;
	}
	
	public void eventBtnSave() {
		String a_c_no = txtCustomerNo.getText().trim();
		if(a_c_no.equals("")) {
			MsgBox.alert("고객 번호를 조회해 주세요.");
			return;
		}
		String a_no = txtAccountNo.getText().trim();
		if(a_no.equals("")) {
			MsgBox.alert("계좌번호를 입력해 주세요.");
			return;
		}
		if (!checkAccountNo(a_no)) {
			MsgBox.alert("이미 존재하는 계좌번호입니다.");
			return;
		}
		String a_money = txtMoney.getText().trim();
		if(a_money.equals("")) {
			MsgBox.alert("예금금액을 입력해주세요.");
			return;
		}
		if((!numCheck(a_money))){
			MsgBox.alert("예금금액은 숫자만 입력가능합니다.");
			return;
		}
		if(a_money.length() >= 13) {
			MsgBox.alert("1조원미만으로 입력해주세요.");
			return;
		}
		long a_amount = Long.valueOf(a_money);
		int check = MsgBox.confirm("저장하시겠습니까?");
		if(check == 0) {
			String a_b_no = getBranchNo(cbBranchName.getSelectedItem().toString());
			String a_item_name	= cbDepositKind.getSelectedItem().toString();
			String a_term = cbTerm.getSelectedItem().toString();
			String sql ="insert into account(a_no, a_serial_no, a_date, a_amount, a_open_date, a_term, a_item_dist, a_item_name, a_b_no, a_c_no) "
					+ "values('" + a_no + "', 1, sysdate, " + a_amount + ", sysdate, '" + a_term + "', 'A0', '" + a_item_name + "', '" + a_b_no + "', '" + a_c_no + "')";
			DBControler db = new DBControler();
			db.connect();
			db.executeQuery(sql);
			db.disConnect();
			eventBtnInit();	
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
	
	public void eventBtnInit() {
		txtCustomerNo.setText("");
		txtAccountNo.setText("");
		txtMoney.setText("");
		cbCustomerDist.setSelectedIndex(0);
		cbDepositKind.setSelectedIndex(0);
		cbBranchName.setSelectedIndex(0);
	}
	
	public void eventBtnCustomerSelect() {
		new CustomerSelectFrame(txtCustomerNo, cbCustomerDist);
	}
	
	public void setCbBranchName() {
		cbBranchName.removeAllItems();
		String sql = "select b_name from branch";
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		ResultSet rs = db.getResultSet();
		try {
			while(rs.next()) {
				cbBranchName.addItem(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.disConnect();
		
	}
	
	public void setCbDepositKind() {
		cbDepositKind.removeAllItems();
		String sql = "select item_name, term1, term2, term3, rate1, rate2, rate3 from item where item_dist='A0'";
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		ResultSet rs = db.getResultSet();
		try {
			rs.last();
			int row = rs.getRow();
			rs.beforeFirst();
			term_dist = new String[row][3];
			rate_dist = new String[row][3];
			int r=0;
			while(rs.next()) {
				cbDepositKind.addItem(rs.getString(1));
				for(int c=0; c<3; c++) {
					term_dist[r][c] = rs.getString(2+c);
					rate_dist[r][c] = rs.getString(5+c);
				}
				r++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.disConnect();
	}
	
	public void setCbTerm() {
		cbTerm.removeAllItems();
		if(cbDepositKind.getSelectedItem() == null) return;
		int r = cbDepositKind.getSelectedIndex();
		for(int c=0; c<3; c++) {
			if(term_dist[r][c] == null || term_dist[r][c].equals("") || term_dist[r][c].equals("0")) continue;
			cbTerm.addItem(term_dist[r][c]);
		}
	}
	
	public void setTxtRate() {
		txtRate.setText("");
		if(cbDepositKind.getSelectedItem() == null) return;
		if(cbTerm.getSelectedItem() == null) return;
		int r = cbDepositKind.getSelectedIndex();
		int c = cbTerm.getSelectedIndex();
		txtRate.setText(rate_dist[r][c]);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnCustomerSelect) {
			eventBtnCustomerSelect();
		}
		else if(e.getSource() == btnSave) {
			eventBtnSave();
		}
		else if(e.getSource() == btnInit) {
			eventBtnInit();
		}
	}

	public void itemStateChanged(ItemEvent e) {
		if(e.getSource() == cbDepositKind) {
			setCbTerm();
		}
		else if(e.getSource() == cbTerm) {
			setTxtRate();
		}
	}
}