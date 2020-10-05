import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DepositItemPane extends JPanel implements MouseListener{
	
	static String item_dist[] = {"예금", "대출"};
		
	private JComboBox cbCustomerDist;
	private JComboBox cbItemDist;
	private JTextField txtField[];
	private String str;
	private String lblName[] = {"예금상품명", "예금한도", "상품구분", "고객구분", "기간1", "이율1", "기간2", "이율2", "기간3", "이율3"};
	private int cols = lblName.length;
	
	private JPanel superPane;
	
	private String preInfo[];
	
	private JLabel lblDelete, lblUpdate, lblSave, lblCancel;
	
	private Color background_color, font_color;
	
	public DepositItemPane(JPanel pane, String str, Color background_color, Color font_color) {
		this.background_color = background_color;
		this.font_color = font_color;
		this.superPane = pane;
		this.str = str;
		initialize();
	}
	
	public void initialize() {
		
		this.setLayout(new BorderLayout());
		JPanel pane[] = new JPanel[4];
		JPanel gridPane = new JPanel();
		gridPane.setLayout(new GridLayout(5, 4, 20, 20));
		
		for(int i=0; i<4; i++) {
			pane[i] = new JPanel();
			pane[i].setBackground(background_color);
		}
		gridPane.setBackground(background_color);
		
		JLabel lbl[] = new JLabel[cols];
		txtField = new JTextField[cols];
		StringTokenizer stk = new StringTokenizer(str, "|");
		int i=0;
		while(stk.hasMoreTokens()) {
			txtField[i] = new JTextField(stk.nextToken());
			txtField[i].setHorizontalAlignment(JTextField.CENTER);
			if(txtField[i].getText().equals("null")) 
				txtField[i].setText("0");
			lbl[i] = new JLabel(lblName[i]);
			lbl[i++].setHorizontalAlignment(JLabel.RIGHT);
		}
		
		cbCustomerDist = new JComboBox();
		for(i=0; i<CustomerPane.customer_dist.length; i++) {
			cbCustomerDist.addItem(CustomerPane.customer_dist[i]);
		}
		
		cbItemDist = new JComboBox();
		for(i=0; i<item_dist.length; i++) {
			cbItemDist.addItem(item_dist[i]);
		}
		
		allEnabled(false);
		
		initComboBox();
		
		for(i=0; i<cols; i++) {
			gridPane.add(lbl[i]);	
			if(i==2) gridPane.add(cbItemDist);
			else if(i==3) gridPane.add(cbCustomerDist);
			else gridPane.add(txtField[i]);
		}
		
		
		JLabel itemName = new JLabel(txtField[0].getText());
		itemName.setFont(new Font("serif", Font.BOLD, 25));
		itemName.setForeground(font_color);
		
		
		lblDelete = new JLabel(new ImageIcon(CellButton.SRC_ITEM_DELETE));
		lblUpdate = new JLabel(new ImageIcon(CellButton.SRC_ITEM_UPDATE));
		lblSave = new JLabel(new ImageIcon(CellButton.SRC_SAVE));
		lblCancel = new JLabel(new ImageIcon(CellButton.SRC_CANCEL));
		lblSave.setVisible(false);
		lblCancel.setEnabled(false);
		
		Box box = Box.createVerticalBox();
		Box box1 = Box.createHorizontalBox();
		Box box2 = Box.createHorizontalBox();
		Box box3 = Box.createHorizontalBox();
		
		JLabel lblTemp = new JLabel(" ");
		box3.add(lblTemp);
		
		lblTemp.addMouseListener(this);
		
		box1.add(lblDelete);
		box1.add(Box.createHorizontalStrut(300));
		box1.add(lblSave);
		box1.add(lblUpdate);
		box1.add(Box.createHorizontalStrut(10));
		box1.add(lblCancel);
		
		box2.add(itemName);
		
		box.add(box1);
		box.add(box2);
		box.add(box3);
		
		pane[0].add(box);
		this.add(pane[0], BorderLayout.NORTH);
		this.add(pane[1], BorderLayout.WEST);
		this.add(pane[2], BorderLayout.EAST);
		this.add(pane[3], BorderLayout.SOUTH);
		this.add(gridPane, BorderLayout.CENTER);
		
		lblDelete.addMouseListener(this);
		lblUpdate.addMouseListener(this);
		lblSave.addMouseListener(this);
		lblCancel.addMouseListener(this);
		
		
		
	}
	
	public void allEnabled(boolean bool) {
		if(bool) {
			for(int i=1; i<cols; i++) {
				txtField[i].setEnabled(true);
			}
			cbCustomerDist.setEnabled(true);
			cbItemDist.setEnabled(true);			
		}
		else {
			for(int i=0; i<cols; i++) {
				txtField[i].setEnabled(false);
			}
			cbCustomerDist.setEnabled(false);
			cbItemDist.setEnabled(false);
		}
	}
	
	public void changeLabel() {
		if(lblUpdate.isVisible()) {
			lblUpdate.setVisible(false);
			lblSave.setVisible(true);
			allEnabled(true);
			lblCancel.setEnabled(true);
		}
		else {
			lblUpdate.setVisible(true);
			lblSave.setVisible(false);
			allEnabled(false);
			lblCancel.setEnabled(false);
		}
	}
	
	public void initComboBox() {
		if(txtField[3].getText().equals("00"))
			cbCustomerDist.setSelectedIndex(0);	
		else cbCustomerDist.setSelectedIndex(1);
		cbItemDist.setSelectedIndex(0);
	}
	
	
	public void setPreInfo() {
		if(preInfo == null)
			preInfo = new String[cols];	
		for(int i=0; i<cols; i++)
			preInfo[i] = txtField[i].getText();
	}
	
	public void setTxtField(String preInfo[]) {
		for(int i=0; i<cols; i++) {
			txtField[i].setText(preInfo[i]);
		}
		initComboBox();
	}
	
	public void eventLblCancel() {
		if(lblCancel.isEnabled()) {
			changeLabel();
			setTxtField(preInfo);
		}
	}
	
	public void eventLblUpdate() {
		if(checkUsing()) {
			int check2 = JOptionPane.showConfirmDialog(null, "상품이 이용중입니다.\n이용내역을 확인하시겠습니까?", "확인", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(check2 == 0) {
				String sql = "select a_item_name, a_item_dist, a_no, a_b_no, a_c_no from account where a_item_name='"+txtField[0].getText().trim()+"'" + " and a_serial_no='1'";
				CheckAccountFrame checkAccountFrame = new CheckAccountFrame(sql);
			}
		}
		else {
			changeLabel();
			setPreInfo();	
		}
	}
	
	public String getItem_dist() {
		String str = "";
		int idx = cbItemDist.getSelectedIndex();
		switch (idx) {
		case 0: str="A0"; break;
		case 1: str="L1"; break;
		}
		return str;
	}
	
	public String getC_dist() {
		String str = "";
		int idx = cbCustomerDist.getSelectedIndex();
		switch (idx) {
		case 0: str="00"; break;
		case 1: str="11"; break;
		}
		return str;
	}
	
	public void eventLblSave() {
		String item_name = txtField[0].getText().trim();
		String item_limit = txtField[1].getText().trim();
		String item_dist = getItem_dist();
		String item_c_dist = getC_dist();
		String term1 = txtField[4].getText().trim();
		String rate1 = txtField[5].getText().trim();
		String term2 = txtField[6].getText().trim();
		String rate2 = txtField[7].getText().trim();
		String term3 = txtField[8].getText().trim();
		String rate3 = txtField[9].getText().trim();
		String sql = "update item set item_name='"+ item_name +"', item_limit=" + item_limit + ", item_dist='" + item_dist + "', item_c_dist='" + item_c_dist + 
				"', term1=" + term1 + ", rate1=" + rate1 + ", term2=" + term2 + ", rate2=" + rate2 + ", term3=" + term3 + ", rate3=" + rate3 + 
				" where item_name='" + preInfo[0] + "'";
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		db.disConnect();
		changeLabel();
		
	}
	
	public void eventLblDelete() {
		int check = JOptionPane.showConfirmDialog(null, "삭제하시겠습니까?", "확인", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(check == 0) {
			if(checkUsing()) {
				int check2 = JOptionPane.showConfirmDialog(null, "상품이 이용중입니다.\n이용내역을 확인하시겠습니까?", "확인", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(check2 == 0) {
					String sql = "select a_item_name, a_item_dist, a_no, a_b_no, a_c_no from account where a_item_name='"+txtField[0].getText().trim()+"'" + " and a_serial_no='1'";
					CheckAccountFrame checkAccountFrame = new CheckAccountFrame(sql);
				}
			}
			else {
				String item_name = txtField[0].getText().trim();
				String sql = "delete from item where item_name='"+item_name+"'";
				DBControler db = new DBControler();
				db.connect();
				db.executeQuery(sql);
				ResultSet rs = db.getResultSet();
				((DepositKindPane) superPane).eventBtnSelect();
			}
		}
	}
	
	public boolean checkUsing() {
		boolean check = false;
		String sql = "select a_item_name, a_no, a_b_no, a_c_no from account where a_item_name='"+txtField[0].getText().trim()+"' and a_serial_no='1'";
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		ResultSet rs = db.getResultSet();
		try {
			if(rs.next()) check = true;
			db.disConnect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return check;
	}
	

	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {
		if(e.getSource() == lblDelete) {
			eventLblDelete();
		}
		else if(e.getSource() == lblUpdate) {
			eventLblUpdate();
		}
		else if(e.getSource() == lblSave) {
			eventLblSave();
		}
		else if(e.getSource() == lblCancel) {
			eventLblCancel();
		}
	}
	public void mouseEntered(MouseEvent e) {
		((JLabel) e.getSource()).setCursor(new Cursor(Cursor.HAND_CURSOR));

	}
	public void mouseExited(MouseEvent e) {}
	
}