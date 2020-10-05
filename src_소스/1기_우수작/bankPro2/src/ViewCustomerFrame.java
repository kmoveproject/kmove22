import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class ViewCustomerFrame extends JFrame implements ActionListener{

	private JButton btnCancel;
	
	public ViewCustomerFrame(String sql) {
		initialize(sql);
	}
	
	public void initialize(String sql) {
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		ResultSet rs = db.getResultSet();
		
		String info[] = new String[5];		
		
		int i=0;

		try {
			rs.next();
			for(i=0; i<5; i++) {
				info[i] = rs.getString(i+1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		this.setTitle("°í°´Á¤º¸È®ÀÎ");
		this.setSize(350, 420);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setResizable(false);
		this.getContentPane().setLayout(null);
		this.getContentPane().setVisible(true);
		
		JLabel lbNo = new JLabel("°í°´¹øÈ£");
		lbNo.setBounds(50, 50, 70, 25);
		this.getContentPane().add(lbNo);
		
		JLabel lbName = new JLabel("°í°´ÀÌ¸§");
		lbName.setBounds(50, 100, 70, 25);
		this.getContentPane().add(lbName);
		
		JLabel lbAddr = new JLabel("°í°´ÁÖ¼Ò");
		lbAddr.setBounds(50, 150, 70, 25);
		this.getContentPane().add(lbAddr);
		
		JLabel lbPhone = new JLabel("ÀüÈ­¹øÈ£");
		lbPhone.setBounds(50, 200, 70, 25);
		this.getContentPane().add(lbPhone);
		
		JLabel lbDist = new JLabel("°í°´±¸ºÐ");
		lbDist.setBounds(50, 250, 70, 25);
		this.getContentPane().add(lbDist);
		
		btnCancel = new JButton("´Ý±â");
		btnCancel.setBounds(180, 320, 100, 25);
		this.getContentPane().add(btnCancel);
		
		JTextField txtNo = new JTextField(info[0]);
		txtNo.setBounds(130, 50, 150, 25);
		this.getContentPane().add(txtNo);
		
		JTextField txtName = new JTextField(info[1]);
		txtName.setBounds(130, 100, 150, 25);
		this.getContentPane().add(txtName);
		
		JTextField txtAddr = new JTextField(info[2]);
		txtAddr.setBounds(130, 150, 150, 25);
		this.getContentPane().add(txtAddr);
		
		JTextField txtPhone = new JTextField(info[3]);
		txtPhone.setBounds(130, 200, 150, 25);
		this.getContentPane().add(txtPhone);
		
		JComboBox cbDist = new JComboBox();
		cbDist.setBounds(130, 250, 150, 25);
		this.getContentPane().add(cbDist);
		
		for(i=0; i<CustomerPane.customer_dist.length; i++){
			cbDist.addItem(CustomerPane.customer_dist[i]);
		}
		
		if(info[4].equals("00")){
			cbDist.setSelectedIndex(0);
		}
		else if(info[4].equals("11")){
			cbDist.setSelectedIndex(1);
		}
		
		txtNo.setEditable(false);
		txtName.setEditable(false);
		txtAddr.setEditable(false);
		txtPhone.setEditable(false);
		cbDist.setEnabled(false);
		
		btnCancel.addActionListener(this);
		
		db.disConnect();
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnCancel) {
			dispose();
		}
	}

	
	
	
}