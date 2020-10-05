import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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

public class CustomerPane extends JPanel implements ActionListener, KeyListener, MouseListener{
	
	private JTextField txtNo, txtName, txtAddr, txtPhone, txtDist;
	private JButton btnSearch, btnInit, btnEnrollment;
	
	private JTable table;
	private MyModel myModel;
	private JScrollPane scrollPane;
	
	static String customer_dist[] = {"개인고객", "기업고객"};
	
	private DBControler db;
	private String sqlTotal =  "select * from customer";
	private String columnName[] = {"고객번호", "고객이름", "고객주소", "전화번호", "고객구분"};
	private String sqlSelect = sqlTotal;
	
	
	public CustomerPane(){
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
		
		txtDist = new JTextField();
		this.add(txtDist);
		
		btnSearch = new JButton(new ImageIcon(CellButton.SRC_SEARCH));
		this.add(btnSearch);
		btnSearch.addActionListener(this);
		btnSearch.addMouseListener(this);
		
		btnInit = new JButton(new ImageIcon(CellButton.SRC_INIT));
		this.add(btnInit);
		btnInit.addActionListener(this);
		btnInit.addMouseListener(this);
		
		btnEnrollment = new JButton(new ImageIcon(CellButton.SRC_CUSTOMER_INSERT));
		this.add(btnEnrollment);
		btnEnrollment.addActionListener(this);
		
		scrollPane = new JScrollPane();
		scrollPane.setVisible(true);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		this.add(scrollPane);
		
//		internalFrameBorder = BorderFactory.createLineBorder(Color.GRAY, 1);
		
		txtNo.addKeyListener(this);
		txtName.addKeyListener(this);
		txtAddr.addKeyListener(this);
		txtPhone.addKeyListener(this);
		txtDist.addKeyListener(this);
		
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
		table.getColumnModel().getColumn(model.getColumnCount()-2).setCellRenderer(new CellButton(this, new ImageIcon(CellButton.SRC_CUSTOMER_UPDATE)));
		table.getColumnModel().getColumn(model.getColumnCount()-2).setCellEditor(new CellButton(this, new ImageIcon(CellButton.SRC_CUSTOMER_UPDATE)));
		table.getColumnModel().getColumn(model.getColumnCount()-1).setCellRenderer(new CellButton(this, new ImageIcon(CellButton.SRC_CUSTOMER_DELETE)));
		table.getColumnModel().getColumn(model.getColumnCount()-1).setCellEditor(new CellButton(this, new ImageIcon(CellButton.SRC_CUSTOMER_DELETE)));
		
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
		txtDist.setBounds(x=x+width+2, 20, width, 30);
		btnSearch.setBounds(x=x+width+2, 20, width, 30);
		btnInit.setBounds(x=x+width+2, 20, width, 30);
		btnEnrollment.setBounds(20, height-120, 100, 30);
	}
	
	public void clearText() {
		txtNo.setText("");
		txtName.setText("");
		txtAddr.setText("");
		txtPhone.setText("");
		txtDist.setText("");
	}
	
	public String getSqlSelect() {
		String sql = sqlTotal;
		String no = txtNo.getText().trim();
		String name = txtName.getText().trim();
		String addr = txtAddr.getText();
		String phone = txtPhone.getText().trim();
		String dist = txtDist.getText().trim();
		sql = sql + " where c_no like '%" + no + "%'";
		sql = sql + " and c_name like '%" + name + "%'";
		sql = sql + " and c_addr like '%" + addr + "%'";	
		sql = sql + " and c_phone like '%" + phone + "%'";
		sql = sql + " and c_dist like '%" + dist + "%'";
		return sql;
	}
	
	public void eventBtnSearch(){
		sqlSelect = getSqlSelect();
		viewTable(sqlSelect);
	}
	
	public void eventBtnInit(){
		clearText();
		viewTable(sqlTotal);	
	}
	
	public void eventBtnEnrollment() {
		new EnrollmentCustomerFrame(this);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnSearch) {
			eventBtnSearch();
		}
		else if(e.getSource() == btnInit) {
			eventBtnInit();
		}
		else if(e.getSource() == btnEnrollment) {
			eventBtnEnrollment();
		}
	}
	
	public void eventEscape(JTextField textField){
		textField.setText("");
	}

	public void keyPressed(KeyEvent e) {

	}

	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			eventBtnSearch();
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

	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	
	public void mouseEntered(MouseEvent e) {
		((JButton) e.getSource()).setCursor(new Cursor(Cursor.HAND_CURSOR));		
	}
	
	public void mouseExited(MouseEvent e) {}
}