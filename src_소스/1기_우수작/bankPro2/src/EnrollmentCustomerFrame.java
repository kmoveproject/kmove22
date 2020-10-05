import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EnrollmentCustomerFrame extends JFrame implements ActionListener{
	
	private CustomerPane customerPane;
	
	private JTextField txtNo;
	private JTextField txtName;
	private JTextField txtAddr;
	private JTextField txtPhone;
	
	private JComboBox cbDist;
	private JButton btnSave;
	private JButton btnCancel;
	
	private String no = "", name = "", addr = "", phone = "", dist = "";
		
	public EnrollmentCustomerFrame(CustomerPane pane){
		customerPane = pane;
		initialize();
	}
	
	public void initialize(){
		this.setTitle("고객등록");
		this.setSize(350, 420);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setResizable(false);
		this.getContentPane().setLayout(null);
		
		JLabel lbNo = new JLabel("고객번호");
		lbNo.setBounds(50, 50, 70, 25);
		this.getContentPane().add(lbNo);
		
		JLabel lbName = new JLabel("고객이름");
		lbName.setBounds(50, 100, 70, 25);
		this.getContentPane().add(lbName);
		
		JLabel lbAddr = new JLabel("고객주소");
		lbAddr.setBounds(50, 150, 70, 25);
		this.getContentPane().add(lbAddr);
		
		JLabel lbPhone = new JLabel("전화번호");
		lbPhone.setBounds(50, 200, 70, 25);
		this.getContentPane().add(lbPhone);
		
		JLabel lbDist = new JLabel("고객구분");
		lbDist.setBounds(50, 250, 70, 25);
		this.getContentPane().add(lbDist);
		
		btnSave = new JButton("저장");
		btnSave.setBounds(50, 320, 100, 25);
		this.getContentPane().add(btnSave);
		
		btnCancel = new JButton("취소");
		btnCancel.setBounds(180, 320, 100, 25);
		this.getContentPane().add(btnCancel);
		
		txtNo = new JTextField("");
		txtNo.setBounds(130, 50, 150, 25);
		this.getContentPane().add(txtNo);
		
		txtName = new JTextField("");
		txtName.setBounds(130, 100, 150, 25);
		this.getContentPane().add(txtName);
		
		txtAddr = new JTextField("");
		txtAddr.setBounds(130, 150, 150, 25);
		this.getContentPane().add(txtAddr);
		
		txtPhone = new JTextField("");
		txtPhone.setBounds(130, 200, 150, 25);
		this.getContentPane().add(txtPhone);
		
		cbDist = new JComboBox();
		cbDist.setBounds(130, 250, 150, 25);
		this.getContentPane().add(cbDist);
		
		for(int i=0; i<CustomerPane.customer_dist.length; i++){
			cbDist.addItem(CustomerPane.customer_dist[i]);
		}
		cbDist.setSelectedIndex(0);
		cbDist.setEnabled(false);
		
		btnSave.addActionListener(this);
		btnCancel.addActionListener(this);
	}
	
	public boolean getInformation(){
		no = txtNo.getText().trim();
		if(no.equals("") || no.equals(null)) {
			msgBox("고객번호가 누락되었습니다.");
			return false;
		}
		else if(!numCheck(no, true)) {
			msgBox("고객번호가 잘못된 형식입니다.");
			return false;
		}
		else if(no.getBytes().length > 14) {
			msgBox("고객번호는 14자 이하만 입력가능합니다.");
			return false;
		}
		else {
			String sql = "select * from customer where c_no='" + no + "'";
			DBControler db = new DBControler();
			db.connect();
			db.executeQuery(sql);
			ResultSet rs = db.getResultSet();
			try {
				rs.last();
				int count = rs.getRow();
				if(count > 0) {
					msgBox("이미 있는 고객번호 입니다.");
					return false;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		
		name = txtName.getText().trim();
		if(name.equals("") || name.equals(null)) {
			msgBox("고객성명이 누락되었습니다.");
			return false;
		}
		else if(name.getBytes().length > 20) {
			msgBox("고객성명이 너무 깁니다.");
			return false;
		}
		
		
		addr = txtAddr.getText().trim();
		if(addr.getBytes().length > 40) {
			msgBox("주소가 너무 깁니다.");
			return false;
		}
		
		phone = txtPhone.getText().trim();
		if(!numCheck(phone, true) || phone.getBytes().length > 16) {
			msgBox("전화번호가 잘못된 형식입니다.");
			return false;
		}
		
		int idx = cbDist.getSelectedIndex();
		switch (idx) {
		case 0: dist = "00"; break;
		case 1: dist = "11"; break;
		default: break;
		}
		return true;
	}
	
	public boolean numCheck(String str, boolean bar) {
		char temp;
		for(int i=0; i<str.length(); i++) {
			temp = str.charAt(i);
			if(bar && temp == '-') continue;
			if(temp < '0' || temp > '9') {
				return false;
			}
		}
		return true;
	}
	
	public void msgBox(String str) {
		JOptionPane.showMessageDialog(null, str);
	}
	
	public void eventBtnSave(){
		if(!getInformation()) return;
		String sql = "insert into customer values('"+no+"', '"+name+"', '"+addr+"', '"+phone+"', '"+dist+"')";
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		db.disConnect();
		customerPane.eventBtnSearch();
		dispose();
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnCancel){
			dispose();
		}
		else if(e.getSource() == btnSave){
			eventBtnSave();
		}
	}
}