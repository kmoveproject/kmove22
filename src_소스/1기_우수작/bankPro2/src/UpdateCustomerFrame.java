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

public class UpdateCustomerFrame extends JFrame implements ActionListener{
	
	private JPanel customerPane;
	private String sql;
	private String c_no;
	private String c_name;
	private String c_addr;
	private String c_phone;
	private String c_dist;
	
	private JTextField txtNo;
	private JTextField txtName;
	private JTextField txtAddr;
	private JTextField txtPhone;
//	private JTextField txtDist;
	private JComboBox cbDist;
	private JButton btnSave;
	private JButton btnCancel;
		
	public UpdateCustomerFrame(JPanel pane, String c_no, String c_name, String c_addr, String c_phone, String c_dist){
		customerPane = pane;
		this.c_no = c_no;
		this.c_name = c_name;
		this.c_addr = c_addr;
		this.c_phone = c_phone;
		this.c_dist = c_dist;
		initialize();
	}
	
	public void initialize(){
		this.setTitle("고객정보수정");
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
		
		txtNo = new JTextField(c_no);
		txtNo.setBounds(130, 50, 150, 25);
		txtNo.setEditable(false);
		this.getContentPane().add(txtNo);
		
		txtName = new JTextField(c_name);
		txtName.setBounds(130, 100, 150, 25);
		this.getContentPane().add(txtName);
		
		txtAddr = new JTextField(c_addr);
		txtAddr.setBounds(130, 150, 150, 25);
		this.getContentPane().add(txtAddr);
		
		txtPhone = new JTextField(c_phone);
		txtPhone.setBounds(130, 200, 150, 25);
		this.getContentPane().add(txtPhone);
		
		cbDist = new JComboBox();
		cbDist.setBounds(130, 250, 150, 25);
		this.getContentPane().add(cbDist);
		
		for(int i=0; i<CustomerPane.customer_dist.length; i++){
			cbDist.addItem(CustomerPane.customer_dist[i]);
		}
		
		if(c_dist.equals("00")){
			cbDist.setSelectedIndex(0);
		}
		else if(c_dist.equals("11")){
			cbDist.setSelectedIndex(1);
		}
		
		btnSave.addActionListener(this);
		btnCancel.addActionListener(this);
	}
	
	public boolean getInformation(){
		c_name = txtName.getText().trim();
		if(c_name == null) {
			JOptionPane.showMessageDialog(null, "고객성명이 누락되었습니다.");
			return false;
		}
//		System.out.println(c_name.getBytes().length);
		c_addr = txtAddr.getText().trim();
		c_phone = txtPhone.getText().trim();
		int idx = cbDist.getSelectedIndex();
		switch (idx) {
		case 0: c_dist = "00"; break;
		case 1: c_dist = "11"; break;
		default: break;
		}
		return true;
	}
	
	public void eventBtnSave(){
		if(!getInformation()) return;
		sql = "update customer set c_no='"+c_no+"', c_name='"+c_name+"', c_addr='"+c_addr+"', c_phone='"+c_phone+"', c_dist='"+c_dist+"' where c_no='"+c_no+"'";
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
			((CustomerPane) customerPane).eventBtnSearch();
			dispose();
		}
	}
}