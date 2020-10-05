import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javafx.scene.layout.Pane;

public class UpdateBranchFrame extends JFrame implements ActionListener{
	
	private JPanel branchPane;
	private String sql;
	private String b_no;
	private String b_name;
	private String b_addr;
	private String b_phone;
	private String b_assets;
	
	private JTextField txtNo;
	private JTextField txtName;
	private JTextField txtAddr;
	private JTextField txtPhone;
	private JTextField txtAssets;
	private JButton btnSave;
	private JButton btnCancel;
	
	public UpdateBranchFrame(JPanel pane, String b_no, String b_name, String b_addr, String b_phone, String b_assets){
		this.branchPane = pane;
		this.b_no = b_no;
		this.b_name = b_name;
		this.b_addr = b_addr;
		this.b_phone = b_phone;
		this.b_assets = b_assets;
		initialize();
	}
	
	public void initialize(){
		this.setTitle("지점정보수정");
		
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
		
		txtNo = new JTextField(b_no);
		txtNo.setBounds(130, 50, 150, 25);
		txtNo.setEditable(false);
		this.getContentPane().add(txtNo);
		
		txtName = new JTextField(b_name);
		txtName.setBounds(130, 100, 150, 25);
		this.getContentPane().add(txtName);
		
		txtAddr = new JTextField(b_addr);
		txtAddr.setBounds(130, 150, 150, 25);
		this.getContentPane().add(txtAddr);
		
		txtPhone = new JTextField(b_phone);
		txtPhone.setBounds(130, 200, 150, 25);
		this.getContentPane().add(txtPhone);
		
		txtAssets = new JTextField(b_assets);
		txtAssets.setBounds(130, 250, 150, 25);
		txtAssets.setEditable(false);
		this.getContentPane().add(txtAssets);
		
		
		btnSave.addActionListener(this);
		btnCancel.addActionListener(this);
	}
	
	public boolean getInformation(){
		b_name = txtName.getText().trim();
		if(b_name == null) {
			JOptionPane.showMessageDialog(null, "지점명이 누락되었습니다.");
			return false;
		}
		b_addr = txtAddr.getText().trim();
		b_phone = txtPhone.getText().trim();
		b_assets = txtAssets.getText().trim();
		return true;
	}
	
	public void eventBtnSave(){
		if(!getInformation()) return;
		sql = "update branch set b_no='"+b_no+"', b_name='"+b_name+"', b_addr='"+b_addr+"', b_phone='"+b_phone+"', b_assets='"+b_assets+"' where b_no='"+b_no+"'";
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		db.disConnect();
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnCancel){
			dispose();
		}
		else if(e.getSource() == btnSave){
			eventBtnSave();
			((BranchPane) branchPane).eventBtnSelect();
			dispose();
		}
	}
}