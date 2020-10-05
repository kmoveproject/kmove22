import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class CheckAccountFrame extends JFrame implements ActionListener{

	private JTable table;
	private JScrollPane scrollPane;
	private String columnName[]= {"상품명", "상품구분", "계좌번호", "지점번호", "고객번호"};
	private Container container;
	private String sqlTotal;
	
	private JButton btnAllDelete, btnClose;
	
	public CheckAccountFrame(String sql) {
		this.sqlTotal = sql;
		initialize();
		viewTable();
	}
	
	public void initialize() {
		this.setLayout(null);
		container = this.getContentPane();
		scrollPane = new JScrollPane();
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		container.add(scrollPane);
		
		btnAllDelete = new JButton("전체삭제");
		btnClose = new JButton("닫기");
		
//		btnAllDelete.addActionListener(this);
		btnClose.addActionListener(this);
		
//		container.add(btnAllDelete);
		container.add(btnClose);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = screenSize.width / 2 ;
		int height = screenSize.height / 2;
		int x = screenSize.width / 4 + 30;
		int y = screenSize.height / 4 + 30;
		this.setBounds(x, y, width, height);
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				if(scrollPane != null) {
					int width = container.getWidth();
					int height = container.getHeight();
					scrollPane.setBounds(20, 30, width-40, height-130);
//					btnAllDelete.setBounds(30, height-70, 100, 35);
					btnClose.setBounds(width-130, height-70, 100, 35);
				}
			}
		});
		this.setVisible(true);
	}
	
	public void viewTable() {
		scrollPane.setVisible(false);
		scrollPane.setVisible(true);
		
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sqlTotal);
		DefaultTableModel model = new DefaultTableModel(db.getData(), columnName){
			public boolean isCellEditable(int row, int column){
				if(column < this.getColumnCount()-2){
					return false;
				}
				return true;
			}
		};
		db.disConnect();

		
		model.addColumn("고객정보보기");
		model.addColumn("계좌삭제");
		
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
		
		table.getColumnModel().getColumn(model.getColumnCount()-2).setCellRenderer(new CellButton(this, new ImageIcon(CellButton.SRC_CUSTOMER_INFO)));
		table.getColumnModel().getColumn(model.getColumnCount()-2).setCellEditor(new CellButton(this, new ImageIcon(CellButton.SRC_CUSTOMER_INFO)));
		table.getColumnModel().getColumn(model.getColumnCount()-1).setCellRenderer(new CellButton(this, new ImageIcon(CellButton.SRC_ACCOUNT_DELETE)));
		table.getColumnModel().getColumn(model.getColumnCount()-1).setCellEditor(new CellButton(this, new ImageIcon(CellButton.SRC_ACCOUNT_DELETE)));
		
		table.updateUI();
		scrollPane.setViewportView(table);
	}
	
	
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		scrollPane.setBounds(20, 60, width - 50, height - 150);
	}

	public JTable getTable() {
		return table;
	}
	
//	public void eventBtnAllDelete() {
//		int check = JOptionPane.showConfirmDialog(null, "전체 삭제하시겠습니까?", "확인", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
//		if(check == 0) {
//			String item_name = sqlTotal.substring(sqlTotal.indexOf("'")+1, sqlTotal.lastIndexOf("'"));
//			String sql = "delete from account where a_item_name=(select item_name from item where item_name='"+ item_name +"')";
//			DBControler db = new DBControler();
//			db.connect();
//			db.executeQuery(sql);
//			db.disConnect();
//			viewTable();
//		}
//	}

	public void actionPerformed(ActionEvent e) {
/*		if(e.getSource() == btnAllDelete) {
			eventBtnAllDelete();
		}
		else */if(e.getSource() == btnClose) {
			dispose();
		}
	}
}