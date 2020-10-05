import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class BranchPane extends JPanel implements ActionListener, KeyListener, MouseListener{
	
	private JTextField txtNo, txtName, txtAddr, txtPhone, txtAssets;
	private JButton btnSelect, btnCancel, btnEnrollment, btnCustomerInfo;
	
	private JTable table;
	private MyModel myModel;
	private JScrollPane scrollPane;
	
	private DBControler db;
	private String sqlTotal = "Select * from branch";
	private String sqlSelect = sqlTotal;
	private String columnName[] = {"지점번호", "지점명", "지점주소", "전화번호", "자산총액"};
	
	public BranchPane(){
		initialize();
		viewTable(sqlTotal);
	}
	
	public void initialize(){
		this.setLayout(null);
		
		txtNo = new JTextField();
		this.add(txtNo);
		
		txtName = new JTextField();
		this.add(txtName);
		
		txtAddr = new JTextField();
		this.add(txtAddr);
		
		txtPhone = new JTextField();
		this.add(txtPhone);
		
		txtAssets = new JTextField();
		this.add(txtAssets);
		
		btnSelect = new JButton(new ImageIcon(CellButton.SRC_SEARCH));
		this.add(btnSelect);
		btnSelect.addActionListener(this);
		btnSelect.addMouseListener(this);
		
		btnCancel = new JButton(new ImageIcon(CellButton.SRC_INIT));
		this.add(btnCancel);
		btnCancel.addActionListener(this);
		btnCancel.addMouseListener(this);
		
		btnEnrollment = new JButton(new ImageIcon(CellButton.SRC_BRANCH_INSERT));
		this.add(btnEnrollment);
		btnEnrollment.addActionListener(this);
		btnEnrollment.addMouseListener(this);
		
		btnCustomerInfo = new JButton(new ImageIcon(CellButton.SRC_CUSTOMER_INFO));
		this.add(btnCustomerInfo);
		btnCustomerInfo.addActionListener(this);
		btnCustomerInfo.addMouseListener(this);
		
		scrollPane = new JScrollPane();
		scrollPane.setVisible(true);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		this.add(scrollPane);
		
//		internalFrameBorder = BorderFactory.createLineBorder(Color.GRAY, 1);
		
		txtNo.addKeyListener(this);
		txtName.addKeyListener(this);
		txtAddr.addKeyListener(this);
		txtPhone.addKeyListener(this);
		txtAssets.addKeyListener(this);
		
	}

	
	public void viewTable(String sql) {
		
		scrollPane.setVisible(false);
		scrollPane.setVisible(true);
		
		db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		
		DefaultTableModel model = new DefaultTableModel(db.getData(), columnName){
			public boolean isCellEditable(int row, int column){
				if(column < this.getColumnCount()-2){
					return false;	
				}
				return true;
			}
		};
		db.disConnect();
		model.addColumn("수정");
		model.addColumn("삭제");
		
		table = new JTable();
		table.setModel(model);
		table.setRowSorter(new TableRowSorter(model));
		table.setBackground(new Color(255, 255, 224));
		table.setRowHeight(30);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		for(int i=0; i<columnName.length; i++) {
			table.getColumn(columnName[i]).setCellRenderer(centerRenderer);	
		}
		table.getColumnModel().getColumn(model.getColumnCount()-2).setCellRenderer(new CellButton(this, new ImageIcon(CellButton.SRC_BRANCH_UPDATE)));
		table.getColumnModel().getColumn(model.getColumnCount()-2).setCellEditor(new CellButton(this, new ImageIcon(CellButton.SRC_BRANCH_UPDATE)));
		table.getColumnModel().getColumn(model.getColumnCount()-1).setCellRenderer(new CellButton(this, new ImageIcon(CellButton.SRC_BRANCH_DELETE)));
		table.getColumnModel().getColumn(model.getColumnCount()-1).setCellEditor(new CellButton(this, new ImageIcon(CellButton.SRC_BRANCH_DELETE)));
		
		table.updateUI();
		scrollPane.setViewportView(table);
	}

	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		x=x+20;
		scrollPane.setBounds(x, y + 65, width - 50, height - 205);
		width = (width-60)/7;
		txtNo.setBounds(x, 20, width, 30);
		txtName.setBounds(x=x+width+2, 20, width, 30);
		txtAddr.setBounds(x=x+width+2, 20, width, 30);
		txtPhone.setBounds(x=x+width+2, 20, width, 30);
		txtAssets.setBounds(x=x+width+2, 20, width, 30);
		btnSelect.setBounds(x=x+width+2, 20, width, 30);
		btnCancel.setBounds(x=x+width+2, 20, width, 30);
		btnEnrollment.setBounds(20, height-120, 100, 30);
		btnCustomerInfo.setBounds(140, height-120, 100, 30);
	}
	
	public void clearText() {
		txtNo.setText("");
		txtName.setText("");
		txtAddr.setText("");
		txtPhone.setText("");
		txtAssets.setText("");
	}
	
	public String getSqlSelect() {
		String sql = sqlTotal;
		String no = txtNo.getText().trim();
		String name = txtName.getText().trim();
		String addr = txtAddr.getText();
		String phone = txtPhone.getText().trim();
		String assets = txtAssets.getText().trim();
		sql = sql + " where b_no like '%" + no + "%'";
		sql = sql + " and b_name like '%" + name + "%'";
		sql = sql + " and b_addr like '%" + addr + "%'";
		sql = sql + " and b_phone like '%" + phone + "%'";
		sql = sql + " and b_assets like '%" + assets + "%'";
		return sql;
	}
	
	public void eventBtnSelect(){
		sqlSelect = getSqlSelect();
		viewTable(sqlSelect);
	}
	
	public void eventBtnCancel(){
		clearText();
		viewTable(sqlTotal);	
	}
	
	public void eventBtnEnrollment() {
		new EnrollmentBranchFrame(this);
	}
	
	public void eventBtnCustomerInfo() {
		if(table.getSelectedRow() == -1) {
			MsgBox.alert("지점을 선택해주세요");
		}
		else {
			String b_no = table.getValueAt(table.getSelectedRow(), 0).toString();
			long b_assets = Long.valueOf(table.getValueAt(table.getSelectedRow(), 4).toString());
			if(checkCustomer(b_no)) new BranchTransfer(this, b_no, b_assets);
			else MsgBox.alert("해당지점에서 관리하는 고객이 없습니다.");
		}
	}
	
	public boolean checkCustomer(String b_no) {
		boolean check = false;
		String sql = "select distinct a_c_no from account where a_b_no='" + b_no + "'";
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		ResultSet rs = db.getResultSet();
		try {
			if(rs.next()) {
				check = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.disConnect();
		return check;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnSelect) {
			eventBtnSelect();
		}
		else if(e.getSource() == btnCancel) {
			eventBtnCancel();
		}
		else if(e.getSource() == btnEnrollment) {
			eventBtnEnrollment();
		}
		else if(e.getSource() == btnCustomerInfo) {
			eventBtnCustomerInfo();
		}
	}
	
	public void eventEscape(JTextField textField){
		textField.setText("");
	}

	public void keyPressed(KeyEvent e) {

	}

	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			eventBtnSelect();
		}
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			eventEscape((JTextField)e.getSource());
		}
	}

	public void keyTyped(KeyEvent e) {
		
	}
	
	public JTable getTable(){
		return table;
	}

	public void mouseClicked(MouseEvent e) {	}
	public void mousePressed(MouseEvent e) {	}
	public void mouseReleased(MouseEvent e) {	}

	public void mouseEntered(MouseEvent e) {
		((JButton) e.getSource()).setCursor(new Cursor(Cursor.HAND_CURSOR));		
	}
	
	public void mouseExited(MouseEvent e) {	}
}