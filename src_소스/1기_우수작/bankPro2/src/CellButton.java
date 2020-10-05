import java.awt.Color;
import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

class CellButton extends AbstractCellEditor implements TableCellEditor, TableCellRenderer{

	private JButton btn;
	private JTable table;
	private JPanel superPane;
	private JFrame superFrame;
	private ImageIcon imageName;
	
//	public static final String SRC_UPDATE_IMAGE = "./images/customer_info.png";
//	public static final String SRC_DELETE_IMAGE = "./images/DELETE.GIF";
//	public static final String SRC_SEARCH_IMAGE = "./images/SEARCH.GIF";
//	public static final String SRC_SAVE_IMAGE = "./images/SAVE.GIF";
	
	public static final String SRC_SEARCH = "./images/search.png";
	public static final String SRC_INIT = "./images/init.png";
	public static final String SRC_SAVE = "./images/SAVE.GIF";
	public static final String SRC_CANCEL = "./images/DELETE.GIF";
	
	public static final String SRC_CUSTOMER_SEARCH = "./images/customer_search.png";
	public static final String SRC_CUSTOMER_DELETE = "./images/customer_delete.png";
	public static final String SRC_CUSTOMER_INSERT = "./images/customer_insert.png";
	public static final String SRC_CUSTOMER_INFO = "./images/customer_info.png";
	public static final String SRC_CUSTOMER_UPDATE = "./images/customer_update.png";
	
	public static final String SRC_BRANCH_INSERT = "./images/branch_insert.png";
	public static final String SRC_BRANCH_DELETE = "./images/branch_delete.png";
	public static final String SRC_BRANCH_UPDATE = "./images/branch_update.png";
	
	public static final String SRC_ITEM_UPDATE = "./images/branch_update.png";
	public static final String SRC_ITEM_DELETE = "./images/branch_DELETE.png";
	
	public static final String SRC_ACCOUNT_DELETE = "./images/branch_DELETE.png";
		
	public CellButton(JPanel pane, ImageIcon name) {
		superPane = pane;
		imageName = name;
		superFrame = null;
		initialize();
	}
	
	public CellButton(JFrame frame, ImageIcon name) {
		superFrame = frame;
		imageName = name;
		superPane = null;
		initialize();
	}
	
	public void initialize() {
		table = getTable();
		btn = new JButton(imageName);
		btn.setBackground(Color.WHITE);
		btn.setBorder(BorderFactory.createEmptyBorder());
//		btn.setContentAreaFilled(false);
		btn.setOpaque(false);
		btn.addActionListener(e -> {
			
			if(imageName.toString() == CellButton.SRC_CUSTOMER_UPDATE) {
				customerUpdate();
			}
			else if(imageName.toString() == CellButton.SRC_CUSTOMER_DELETE){
				customerDelete();
			}
			else if(imageName.toString() == CellButton.SRC_BRANCH_UPDATE){
				branchUpdate();
			}
			else if(imageName.toString() == CellButton.SRC_BRANCH_DELETE){
				branchDelete();
			}
			else if(imageName.toString() == CellButton.SRC_CUSTOMER_INFO) {
				customerInfo();
			}
			else if(imageName.toString() == CellButton.SRC_ACCOUNT_DELETE) {
				accountDelete();
			}
		});
	}
	
	public void customerUpdate() {
		if(superPane != null) {
			if (superPane instanceof CustomerPane){
				String c_no = table.getValueAt(table.getSelectedRow(), 0).toString();
				String c_name = table.getValueAt(table.getSelectedRow(), 1).toString();
				String c_addr = "", c_phone = "";
				if(table.getValueAt(table.getSelectedRow(), 2) != null) {
					c_addr = table.getValueAt(table.getSelectedRow(), 2).toString();	
				}
				if(table.getValueAt(table.getSelectedRow(), 3) != null) {
					c_phone = table.getValueAt(table.getSelectedRow(), 3).toString();
				}
				String c_dist = table.getValueAt(table.getSelectedRow(), 4).toString();
				new UpdateCustomerFrame(superPane, c_no, c_name, c_addr, c_phone, c_dist);
			}
		}
	}
	
	public void customerDelete() {
		if(superPane != null) {
			if(superPane instanceof CustomerPane){
				int check = MsgBox.confirm("삭제하시겠습니까?");
				if(check == 0) {
					if(checkDeposit()) MsgBox.alert("계좌에 잔액이 남아있습니다.\n출금해주세요.");
					else if(checkLoan()) MsgBox.alert("상환할 금액이 남아있습니다.\n상환해주세요.");
					else{
						String c_no = table.getValueAt(table.getSelectedRow(), 0).toString();
						String sql = "delete from account where a_c_no='" + c_no + "'";
						goDataBase(sql);
						sql = "delete from customer where c_no='" + c_no + "'";
						goDataBase(sql);
						((CustomerPane) superPane).eventBtnSearch();
					}
				}
				
			}
		}
	}
	
	public boolean checkDeposit() {
		boolean check = false;
		String c_no = table.getValueAt(table.getSelectedRow(), 0).toString();
		String sql = "select sum(a_amount) from account where a_c_no='" + c_no + "' and a_item_dist = 'A0'";
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		ResultSet rs = db.getResultSet();
		try {
			rs.next();
			int count = rs.getInt(1);
			if(count > 0) check = true;
			db.disConnect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return check;
	}
	
	public boolean checkLoan() {
		boolean check = false;
		String c_no = table.getValueAt(table.getSelectedRow(), 0).toString();
		String sql = "select sum(a_amount) from account where a_c_no='" + c_no + "' and a_item_dist = 'L1'";
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		ResultSet rs = db.getResultSet();
		try {
			rs.next();
			long count = rs.getInt(1);
			if(count < 0) check = true;
			db.disConnect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return check;
	}
	
	public void branchUpdate() {
		if(superPane != null) {
			if (superPane instanceof BranchPane){
				String b_no = table.getValueAt(table.getSelectedRow(), 0).toString();
				String b_name = table.getValueAt(table.getSelectedRow(), 1).toString();
				String b_addr = "", b_phone = "", b_assets = "";
				if(table.getValueAt(table.getSelectedRow(), 2) != null) {
					b_addr = table.getValueAt(table.getSelectedRow(), 2).toString();
				}
				if(table.getValueAt(table.getSelectedRow(), 3) != null) {
					b_phone = table.getValueAt(table.getSelectedRow(), 3).toString();	
				}
				if(table.getValueAt(table.getSelectedRow(), 4) != null) {
					b_assets = table.getValueAt(table.getSelectedRow(), 4).toString();	
				}
				new UpdateBranchFrame(superPane, b_no, b_name, b_addr, b_phone, b_assets);
			}	
		}
	}
	
	public void branchDelete() {
		if(superPane != null) {
			if(superPane instanceof BranchPane) {
				int check = MsgBox.confirm("삭제하시겠습니까?");
				if(check == 0) {
					String b_no = table.getValueAt(table.getSelectedRow(), 0).toString();
					if(checkCustomer(b_no)) {
						MsgBox.alert("고객이 남아있습니다.\n삭제할 수 없습니다.");
						return;
					}
					String sql = "delete from branch where b_no='" + b_no + "'";
					goDataBase(sql);
					((BranchPane) superPane).eventBtnSelect();
				}	
			}
		}
	}
	
	public boolean checkCustomer(String b_no) {
		boolean check = false;
		String sql = "select count(*) from account where a_b_no='" + b_no + "'";
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		ResultSet rs = db.getResultSet();
		try {
			rs.next();
			int count = rs.getInt(1);
			if(count > 0) check = true;
		} catch (SQLException e) {
			e.printStackTrace();
			db.disConnect();
		}
		
		db.disConnect();
		return check;
	}
	
	public void customerInfo() {
		if(superFrame != null) {
			if(superFrame instanceof CheckAccountFrame) {
				String a_c_no = table.getValueAt(table.getSelectedRow(), 4).toString();
				String sql = "select c_no, c_name, c_addr, c_phone, c_dist from customer where c_no='"+ a_c_no +"'";
//				select c_no, c_name, c_addr, c_phone, c_dist from customer where c_no='660419-1623112';
				ViewCustomerFrame viewCustomerFrame = new ViewCustomerFrame(sql);
			}
		}
	}
	
	public void accountDelete() {
		int check = JOptionPane.showConfirmDialog(null, "삭제 하시겠습니까?", "취소", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(check == 0) {
			if(superFrame != null) {
				if(superFrame instanceof CheckAccountFrame) {
					String a_no = table.getValueAt(table.getSelectedRow(), 2).toString();
					String c_no = table.getValueAt(table.getSelectedRow(), 4).toString();
					String a_item_dist = table.getValueAt(table.getSelectedRow(), 1).toString();
					String sql = "select sum(a_amount) from account where a_no='"+a_no+"' and a_c_no='"+c_no+"'";
					DBControler db = new DBControler();
					db.connect();
					db.executeQuery(sql);
					ResultSet rs = db.getResultSet();
					try {
						rs.next();
						long money = rs.getInt(1);
						if(a_item_dist.equals("L1")) money *= -1;
						DecimalFormat formatter = new DecimalFormat("###,###,###,###");
						String amount = formatter.format(money);
						db.disConnect();
						if(money > 0) {
							if(a_item_dist.equals("A0")) MsgBox.alert("계좌잔고가 " + amount + "원 남아있습니다.");	
							else MsgBox.alert("상환할 금액이 "+ amount + "원 남이 있습니다.");
						}
						else {
							sql = "delete from account where a_no='" + a_no + "' and a_c_no='" + c_no + "'";
							goDataBase(sql);
							((CheckAccountFrame) superFrame).viewTable();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public JTable getTable(){
		JTable table = null;
		if(superPane != null) {
			if(superPane instanceof CustomerPane){
				table = ((CustomerPane) superPane).getTable();
			}
			else {
				table = ((BranchPane) superPane).getTable();
			}
		}
		else {
			if(superFrame instanceof CheckAccountFrame) {
				table = ((CheckAccountFrame) superFrame).getTable();
			}
		}
		return table;
	}
	
	public void goDataBase(String sql) {
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sql);
		db.disConnect();
	}
	
	public Object getCellEditorValue() {
		return null;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		return btn;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		return btn;
	}
	
}