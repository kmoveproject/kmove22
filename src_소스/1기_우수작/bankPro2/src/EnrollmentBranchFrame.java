import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class EnrollmentBranchFrame extends JFrame implements ActionListener{
	
	private BranchPane branchPane;
	private String no, name, addr, phone, assets;
	private JTextField txtNo, txtName, txtAddr, txtPhone, txtAssets;
	private JButton btnSave, btnCancel;
	
	public EnrollmentBranchFrame(BranchPane pane){
		branchPane = pane;
		initialize();
	}
	
	public void initialize(){
		this.setTitle("지점등록");
		
		this.setSize(350, 420);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setResizable(false);
		this.getContentPane().setLayout(null);
		
		JLabel lbNo = new JLabel("지점번호");
		lbNo.setBounds(50, 50, 70, 25);
		this.getContentPane().add(lbNo);
		
		JLabel lbName = new JLabel("지점명");
		lbName.setBounds(50, 100, 70, 25);
		this.getContentPane().add(lbName);
		
		JLabel lbAddr = new JLabel("지점주소");
		lbAddr.setBounds(50, 150, 70, 25);
		this.getContentPane().add(lbAddr);
		
		JLabel lbPhone = new JLabel("전화번호");
		lbPhone.setBounds(50, 200, 70, 25);
		this.getContentPane().add(lbPhone);
		
		JLabel lbAssets = new JLabel("자산총액");
		lbAssets.setBounds(50, 250, 70, 25);
		this.getContentPane().add(lbAssets);
		
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
		
		txtAssets = new JTextField("");
		txtAssets.setBounds(130, 250, 150, 25);
		this.getContentPane().add(txtAssets);
		
		btnSave.addActionListener(this);
		btnCancel.addActionListener(this);
	}
	
	public boolean getInformation(){
		no = txtNo.getText().trim();
		if(no.equals("") || no.equals(null)) {
			msgBox("지점번호가 누락되었습니다.");
			return false;
		}
		else if(!numCheck(no, false)) {
			msgBox("지점번호는 숫자만 가능합니다.");
			return false;
		}
		else if(no.getBytes().length > 3) {
			msgBox("지점번호는 3자 이하만 가능합니다.");
			return false;
		}
		else {
			String sql = "select * from branch where b_no='" + no + "'";
			DBControler db = new DBControler();
			db.connect();
			db.executeQuery(sql);
			ResultSet rs = db.getResultSet();
			try {
				rs.last();
				int count = rs.getRow();
				if(count > 0) {
					msgBox("이미 있는 지점번호 입니다.");
					return false;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		
		name = txtName.getText().trim();
		if(name.equals("") || name.equals(null)) {
			msgBox("지점명이 누락되었습니다.");
			return false;
		}
		else if(name.getBytes().length > 20) {
			msgBox("지점명이 너무 깁니다.");
			return false;
		}
		
		addr = txtAddr.getText().trim();
		if(addr.getBytes().length > 60) {
			msgBox("주소가 너무 깁니다.");
			return false;
		}
		
		
		phone = txtPhone.getText().trim();
		if(!numCheck(phone, true) || phone.getBytes().length > 16) {
			msgBox("전화번호가 잘못된 형식입니다.");
			return false;
		}
		
		
		assets = txtAssets.getText().trim();
		if(!numCheck(assets, false)) {
			msgBox("자산총액은 숫자만 입력 가능합니다.");
			return false;
		}
		else if(assets.getBytes().length > 13) {
			msgBox("자산은 1조원이하만 입력가능합니다.");
			return false;
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
		String sql = "insert into branch values('"+no+"', '"+name+"', '"+addr+"', '"+phone+"', "+assets+")";
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		db.disConnect();
		branchPane.eventBtnSelect();
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