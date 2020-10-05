

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


public class mainBank  implements ActionListener{

	private JFrame frame;
	JMenuItem itemExit,itemCustomer;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainBank window = new mainBank();
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
	public mainBank() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 434, 21);
		frame.getContentPane().add(menuBar);
		
		JMenu mnNewMenu = new JMenu("시스템");
		menuBar.add(mnNewMenu);
		
		
		itemExit = new JMenuItem("종료");  //종료
		mnNewMenu.add(itemExit);
		
		JMenu mnNewMenu_1 = new JMenu("고객/지점관리");
		menuBar.add(mnNewMenu_1);
		
		itemCustomer = new JMenuItem("고객관리"); //고객관리
		mnNewMenu_1.add(itemCustomer);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("지점관리");
		mnNewMenu_1.add(mntmNewMenuItem_1);
		
		itemExit.addActionListener(this);
		itemCustomer.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==itemExit) {
			System.exit(0);
		}
		else if(e.getSource()==itemCustomer) {
			System.out.println("고객관리");
		}
	}
}
