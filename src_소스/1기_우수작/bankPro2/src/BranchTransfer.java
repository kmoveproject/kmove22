import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class BranchTransfer extends JFrame implements ActionListener{
	private String branchNo;
	private JScrollPane scrollPane;
	private JTable table;
	private Container container;
	private JButton btnTransfer, btnCancel;
	private BranchPane superPane;
	private long branchAsset;
	
	private String columnName[] = {"고객번호", "고객이름", "고객주소", "전화번호", "고객구분"};
	
	public BranchTransfer(BranchPane pane, String branchNo, long branchAsset){
		superPane = pane;
		this.branchNo = branchNo;
		this.branchAsset = branchAsset;
		initialize();
		viewTable();
	}
	
	public void initialize() {
		this.setTitle("고객정보확인");
		this.setVisible(true);
		this.setLayout(null);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = screenSize.width / 2;
		int height = screenSize.height / 2;
		int x = screenSize.width / 4;
		int y = screenSize.height / 4;
		this.setBounds(x + 20, y+ 20, width, height);
		
		container = this.getContentPane();
		container.setLayout(null);
		container.setVisible(true);
		
		scrollPane = new JScrollPane();
		scrollPane.setVisible(true);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		btnTransfer = new JButton("지점이동");
		btnTransfer.addActionListener(this);
		
		btnCancel = new JButton("닫기");
		btnCancel.addActionListener(this);
		
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				int width = container.getWidth();
				int height = container.getHeight();
				scrollPane.setBounds(20, 20, width-40, height -100);
				btnTransfer.setBounds(20, height -50, 100, 30);
				btnCancel.setBounds(width - 120, height -50, 100, 30);
			}
		});
		container.add(scrollPane);
		container.add(btnTransfer);
		container.add(btnCancel);
	}
	
	public void viewTable() {
		
		scrollPane.setVisible(false);
		scrollPane.setVisible(true);
		
		String sql = "select c_no, c_name, c_addr, c_phone, c_dist from customer "
				+ "where c_no in(select distinct a_c_no from account where a_b_no='" + branchNo + "')";
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		
		DefaultTableModel model = new DefaultTableModel(db.getData(), columnName){
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
		table.setRowHeight(30);
		
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		for(int i=0; i<columnName.length; i++) {
			table.getColumn(columnName[i]).setCellRenderer(centerRenderer);	
		}
		
		db.disConnect();
		table.updateUI();
		scrollPane.setViewportView(table);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnTransfer) {
			new branchSearchFrame(this, branchNo, branchAsset);
		}
		else if(e.getSource() == btnCancel) {
			superPane.eventBtnCancel();
			dispose();
		}
	}
}