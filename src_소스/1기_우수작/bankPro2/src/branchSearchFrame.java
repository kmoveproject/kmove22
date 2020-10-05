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

public class branchSearchFrame extends JFrame implements ActionListener{
	private String branchNo;
	private JScrollPane scrollPane;
	private JTable table;
	private Container container;
	private JButton btnTransfer, btnCancel;
	private BranchTransfer superFrame;
	private long branchAssets;
	
	private String columnName[] = {"지점번호", "지점명", "지점주소", "전화번호", "자산총액"};
	
	public branchSearchFrame(BranchTransfer frame, String branchNo, long branchAssets){
		this.branchNo = branchNo;
		this.branchAssets = branchAssets;
		superFrame = frame;
		initialize();
		viewTable();
	}
	
	public void initialize() {
		this.setTitle("지점정보");
		this.setVisible(true);
		this.setLayout(null);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = screenSize.width / 2;
		int height = screenSize.height / 2;
		int x = screenSize.width / 4;
		int y = screenSize.height / 4;
		this.setBounds(x + 40, y+ 40, width, height);
		
		container = this.getContentPane();
		container.setLayout(null);
		container.setVisible(true);
		
		scrollPane = new JScrollPane();
		scrollPane.setVisible(true);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		btnTransfer = new JButton("선택");
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
		
		String sql = "select b_no, b_name, b_addr, b_phone, b_assets from branch where b_no != '" + branchNo + "'";
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
	
	public void eventBtnTransfer() {
		if(table.getSelectedColumn() == -1) {
			MsgBox.alert("이동시킬 지점믈 선택해주세요.");
			return;
		}
		String b_name = table.getValueAt(table.getSelectedRow(), 1).toString();
		int check = MsgBox.confirm(b_name + "로 이동시키겠습니까?");
		if(check == 0) {
			String b_no = table.getValueAt(table.getSelectedRow(), 0).toString();
			String sql = "update account set a_b_no = '" + b_no + "' where a_b_no = '" + branchNo + "'";
			DBControler db = new DBControler();
			db.connect();
			db.executeQuery(sql);
			sql = "update branch set b_assets = b_assets + " + branchAssets + " where b_no = '" + b_no + "'";
			db.executeQuery(sql);
			sql = "update branch set b_assets = 0 where b_no = '" + branchNo + "'";
			db.executeQuery(sql);
			db.disConnect();
			superFrame.viewTable();
			dispose();	
		}
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnTransfer) {
			eventBtnTransfer();
		}
		else if(e.getSource() == btnCancel) {
			dispose();
		}
	}
}