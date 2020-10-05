import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class CustomerSelectFrame extends JFrame implements ActionListener, KeyListener{
	
	private JTextField txtName;
	private JTextField txtPhone;
	
	private JTextField SuperNo;
	private JComboBox superDist;
	
	private JButton btnSelect;
	private JButton btnEnrollment;
	private JButton btnCancel;

	private Container container;
	private String sqlSelect;
	private JScrollPane scrollPane;
	private JTable table;
	
	private JPanel superPane;
	
	public CustomerSelectFrame(JTextField c_no, JComboBox c_dist){
		SuperNo = c_no;
		superDist = c_dist;
		initialize();
	}
	
	public CustomerSelectFrame(JPanel pane, JTextField c_no){
		superPane = pane;
		SuperNo = c_no;
		initialize();
	}
	
	public void initialize(){
		this.setTitle("고객정보검색");
		this.setVisible(true);
		this.getContentPane().setLayout(null);
		
		container = this.getContentPane();
		
		
		JLabel lbName = new JLabel("고객이름");
		lbName.setBounds(50, 50, 70, 25);
		container.add(lbName);
		
		JLabel lbPhone = new JLabel("전화번호");
		lbPhone.setBounds(240, 50, 70, 25);
		container.add(lbPhone);
		
		txtName = new JTextField();
		txtName.setBounds(120, 50, 100, 25);
		container.add(txtName);
		
		txtPhone = new JTextField();
		txtPhone.setBounds(310, 50, 120, 25);
		container.add(txtPhone);
		
		btnSelect = new JButton(new ImageIcon(CellButton.SRC_SEARCH));
		btnSelect.setBounds(450, 50, 100, 25);
		container.add(btnSelect);
		
		btnEnrollment = new JButton("선택");
		container.add(btnEnrollment);
		
		btnCancel = new JButton("취소");
		container.add(btnCancel);
		
		
		btnSelect.addActionListener(this);
		btnEnrollment.addActionListener(this);
		btnCancel.addActionListener(this);
		
		scrollPane = new JScrollPane();
		scrollPane.setVisible(true);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		container.add(scrollPane);
		
		txtName.addKeyListener(this);
		txtPhone.addKeyListener(this);
		
		
		this.setSize(620, 600);
		this.setLocationRelativeTo(null);
		
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				int width = container.getWidth();
				int height = container.getHeight();
				scrollPane.setBounds(20, 100, width -50, height - 200);
				width = scrollPane.getWidth();
				height = scrollPane.getHeight();
				btnEnrollment.setBounds(width-200, height+120, 100, 25);
				btnCancel.setBounds(width-80, height+120, 100, 25);
			}
		});
	}
	
	public void eventBtnSelect(){
		sqlSelect = getSqlSelect();
		veiwTable(sqlSelect);
	}
	
	public String getSqlSelect(){
		String name = txtName.getText().trim();
		String phone = txtPhone.getText().trim();
		String sql = "select c_no \"고객번호\", c_name \"고객이름\", c_addr \"고객주소\", c_phone \"전화번호\", c_dist \"고객구분\" from customer";
		sql = sql + " where c_name like '%" + name + "%'";
		sql = sql + " and c_phone like '%" + phone + "%'";
		return sql;
	}
	
	public void veiwTable(String sql){
		scrollPane.setVisible(false);
		scrollPane.setVisible(true);
		
		MyModel myModel = new MyModel();
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		myModel.setData(db.getResultSet());
		db.disConnect();
		
		DefaultTableModel model = new DefaultTableModel(myModel.getData(), myModel.getColumnName()){
			public boolean isCellEditable(int row, int column){
				if(column < this.getColumnCount()){
					return false;	
				}
				return true;
			}
		};
		
		table = new JTable();
		table.setModel(model);
		table.setRowSorter(new TableRowSorter(model));
		table.setBackground(new Color(255, 255, 224));
		table.setRowHeight(25);
		
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		table.getColumn("고객번호").setCellRenderer(centerRenderer);
		table.getColumn("고객이름").setCellRenderer(centerRenderer);
		table.getColumn("고객주소").setCellRenderer(centerRenderer);
		table.getColumn("전화번호").setCellRenderer(centerRenderer);
		table.getColumn("고객구분").setCellRenderer(centerRenderer);
		
		table.updateUI();
		scrollPane.setViewportView(table);
	}
	
	public void eventBtnEnrollment(){
		if(table == null) {
			MsgBox.alert("고객을 검색해주세요.");
			return;
		}
		if(table.getSelectedRow() == -1) {
			JOptionPane.showMessageDialog(null, "고객을 선택해주세요.");
			return;
		}
		String no = table.getValueAt(table.getSelectedRow(), 0).toString();
		SuperNo.setText(no);
		
		if(superDist != null){
			setComboBox();
		}
		if(superPane != null){
			if(superPane instanceof DepositStatementPane){
				((DepositStatementPane)superPane).eventBtnCustomerSearch();	
			}
			else if(superPane instanceof DepositInOutPane){
				((DepositInOutPane)superPane).eventBtnCustomerSearch();
			}
			else if(superPane instanceof LoanPaybackPane) {
				((LoanPaybackPane)superPane).eventBtnCustomerSearch();
			}
		}
		dispose();
	}
	
	public void setComboBox(){
		String dist = table.getValueAt(table.getSelectedRow(), 4).toString();
		if(dist.equals("00")){
			superDist.setSelectedIndex(0);	
		}
		else superDist.setSelectedIndex(1);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnSelect){
			eventBtnSelect();
		}
		else if(e.getSource() == btnCancel){
			dispose();
		}
		else if(e.getSource() == btnEnrollment){
			eventBtnEnrollment();
		}
	}

	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			eventBtnSelect();
		}
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			eventEscape((JTextField)e.getSource());
		}
	}
	public void eventEscape(JTextField textField){
		textField.setText("");
	}

}