import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class LoanKindPane extends JPanel implements ActionListener, KeyListener, MouseListener{
	
	private JPanel pane;
	private JPanel gridPane;
	private JScrollPane scrollPane;
	private JTextField txtSelect;
	private JButton btnSelect;
	private JButton btnUpdate;
	private JButton btnCancel;
	
	private String sqlTotal = "select item_name, item_limit, item_dist, item_c_dist, term1, rate1, term2, rate2, term3, rate3 from item where ITEM_DIST = 'L1'";
	private String sqlSelect = sqlTotal;
	private ResultSet resultSet;
	
	int max=2;
	boolean check[];
	
	public LoanKindPane() {
		initialize();
	}
	
	public void initialize() {
		this.setLayout(null);
		
		pane = new JPanel();
		pane.setLayout(null);
		txtSelect = new JTextField();
		btnSelect = new JButton(new ImageIcon(CellButton.SRC_SEARCH));
		pane.add(txtSelect);
		pane.add(btnSelect);
		
		gridPane = new JPanel(new GridBagLayout());
		
		scrollPane = new JScrollPane(gridPane);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);

		check = new boolean[4];
		
		
		btnSelect.addActionListener(this);
		
		txtSelect.addKeyListener(this);
		
		this.add(pane);
		this.add(scrollPane);
		
		btnSelect.addMouseListener(this);
		
	}
	
	public void selectItem() {
		DBControler db = new DBControler();
		db.connect();
		db.executeQuery(sqlSelect);
		resultSet = db.getResultSet();
		createItem(resultSet);
		db.disConnect();
	}
	
	public String getSql() {
		String sql = sqlTotal;
		String name = txtSelect.getText().trim();
		if(!name.equals("") && !name.equals(null)) {
			sql = sql + " and item_name like '%"+name+"%'";
		}
		return sql;
	}
	
	public void createItem(ResultSet rs) {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			int x=0, y=0;
			GridBagConstraints gbc = new GridBagConstraints();
			Color color[][] = {{new Color(205, 255, 255), new Color(255, 205, 255), new Color(255, 255, 205), 
				new Color(255, 205, 205), new Color(205, 255, 205), new Color(205, 205, 255), 
				new Color(215, 255, 255), new Color(255, 215, 255), new Color(255, 255, 215)},{
				new Color(20, 100, 100), new Color(100, 20, 100), new Color(100, 100, 20), 
				new Color(100, 20, 20), new Color(20, 100, 20), new Color(20, 20, 100),
				new Color(40, 100, 100), new Color(100, 40, 100), new Color(100, 100, 40)}};
			int idx = 0;
			while(rs.next()) {
				String str = "";
				for(int i=0; i<cols; i++) {
					str = str + rs.getString(i+1) + "|";
				}
				LoanItemPane item = new LoanItemPane(this, str, color[0][idx], color[1][idx]);
				gbc.insets=new Insets(30,30,30,30);
				gbc.gridx = x++;
				gbc.gridy = y;
				gridPane.add(item, gbc);
				if(x == max) {
					x=0;
					y++;
				}
				if(idx < color[0].length - 1) idx++;
				else idx = 0;
			}
			gridPane.updateUI();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		pane.setBounds(0, 0, width-20, 100);
		txtSelect.setBounds(width/2 - 220, 30, 300, 30);
		btnSelect.setBounds(width/2 + 100, 30, 100, 30);
		scrollPane.setBounds(0, 100, width-17, height-170);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		if(width <= 925 && !check[0]) {
			max=1;
			positionSetItem(max);
		}
		else if(925 < width && width <= 1400 && !check[1]) {
			max=2;
			positionSetItem(max);
		}
		else if(1400 < width && width <= 1850 && !check[2]) {
			max=3;
			positionSetItem(max);
		}
		else if(width > 1850 && !check[3]) {
			max=4;
			positionSetItem(max);
		}
	}
	
	public void positionSetItem(int max) {
		initCheck();
		check[max-1] = true;
		gridPane.removeAll();
		selectItem();
	}
	
	public void initCheck() {
		for(int i=0; i<4; i++) {
			check[i] = false;
		}
	}
	
	public void eventBtnSelect() {
		sqlSelect = getSql();
		positionSetItem(max);
	}
	
	public void eventEscape(JTextField textField){
		textField.setText("");
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnSelect) {
			eventBtnSelect();
		}
	}

	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			eventBtnSelect();
		}
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			eventEscape((JTextField)e.getSource());
		}
	}

	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {
		((JButton) e.getSource()).setCursor(new Cursor(Cursor.HAND_CURSOR));
	}
	public void mouseExited(MouseEvent e) {	}
	
	
}