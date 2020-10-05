import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class fDepositOrder implements ActionListener {

	private JFrame frame;
	private JTextField tf1;
	private JTextField tf2;
	private JTextField tf3;
	private JTable table;
	
	//버튼들 전역으로 =========================
	JButton btnInsertItem,btnSaveItem,btnPrintItem,btnCloseWindow;
	JComboBox jcb1,jcb2;
	

	boolean bInsertFlag = true;
	static String customer_dist[] = { "개인고객", "기업고객" };
	Object columnName[] = {"계좌번호", "예금상품명", "고객번호", "고객명", "개설일"};
	Object dataTable[][] = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					fDepositOrder window = new fDepositOrder();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public fDepositOrder() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 441);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		

		btnInsertItem = new JButton("");
		btnInsertItem.setIcon(new ImageIcon("C:\\GitHub\\choimysil\\kmove2\\BankProject\\src\\TOOLBAR\\INSERT.GIF"));
		btnInsertItem.setBounds(12, 10, 33, 23);
		frame.getContentPane().add(btnInsertItem);
		
		btnSaveItem = new JButton("");
		btnSaveItem.setIcon(new ImageIcon("C:\\GitHub\\choimysil\\kmove2\\BankProject\\src\\TOOLBAR\\SAVE.GIF"));
		btnSaveItem.setBounds(57, 10, 33, 23);
		frame.getContentPane().add(btnSaveItem);
		
		btnPrintItem = new JButton("");
		btnPrintItem.setIcon(new ImageIcon("C:\\GitHub\\choimysil\\kmove2\\BankProject\\src\\TOOLBAR\\PRINT.GIF"));
		btnPrintItem.setBounds(102, 10, 33, 23);
		frame.getContentPane().add(btnPrintItem);
		
		btnCloseWindow = new JButton("");
		btnCloseWindow.setIcon(new ImageIcon("C:\\GitHub\\choimysil\\kmove2\\BankProject\\src\\TOOLBAR\\EXIT.GIF"));
		btnCloseWindow.setBounds(151, 10, 26, 23);
		frame.getContentPane().add(btnCloseWindow);
		
		JLabel lblNewLabel = new JLabel("지점");
		lblNewLabel.setBounds(12, 60, 57, 15);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("고객구분");
		lblNewLabel_1.setBounds(12, 85, 57, 15);
		frame.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("계좌번호");
		lblNewLabel_2.setBounds(12, 110, 57, 15);
		frame.getContentPane().add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("예금종류");
		lblNewLabel_3.setBounds(12, 139, 57, 15);
		frame.getContentPane().add(lblNewLabel_3);
		
		JLabel lblNewLabel_4 = new JLabel("계약기간");
		lblNewLabel_4.setBounds(12, 164, 57, 15);
		frame.getContentPane().add(lblNewLabel_4);
		
		JLabel lblNewLabel_5 = new JLabel("고객번호");
		lblNewLabel_5.setBounds(12, 194, 57, 15);
		frame.getContentPane().add(lblNewLabel_5);
		
		
		jcb1 = new JComboBox();
		jcb1.setBounds(81, 57, 96, 21);
		frame.getContentPane().add(jcb1);
		
		jcb2 = new JComboBox();
		jcb2.setBounds(81, 82, 96, 21);
		frame.getContentPane().add(jcb2);
		
		tf1 = new JTextField();
		tf1.setBounds(79, 107, 173, 21);
		frame.getContentPane().add(tf1);
		tf1.setColumns(10);
		
		JComboBox jcb3 = new JComboBox();
		jcb3.setBounds(81, 136, 96, 21);
		frame.getContentPane().add(jcb3);
		
		tf2 = new JTextField();
		tf2.setBounds(81, 161, 116, 21);
		frame.getContentPane().add(tf2);
		tf2.setColumns(10);
		
		tf3 = new JTextField();
		tf3.setBounds(81, 191, 171, 21);
		frame.getContentPane().add(tf3);
		tf3.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(22, 219, 400, 141);
		frame.getContentPane().add(scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		
		JLabel lbStatusMessage2 = new JLabel("New label");
		lbStatusMessage2.setBounds(0, 387, 434, 15);
		frame.getContentPane().add(lbStatusMessage2);
		
		btnInsertItem.addActionListener(this);
		btnSaveItem.addActionListener(this);
		btnPrintItem.addActionListener(this);
		btnCloseWindow.addActionListener(this);
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==btnInsertItem) {
			System.out.println("삽입");
		}
		else if(e.getSource()==btnSaveItem) {
			System.out.println("저장");
		}
		else if(e.getSource()==btnPrintItem) {
			System.out.println("프린트");
		}
		else if(e.getSource()==btnCloseWindow) {
			System.exit(0);
		}
		
	}
}
