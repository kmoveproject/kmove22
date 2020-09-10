import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

//1단계: implements ActionListener 상속받음
public class JDBCProStep1 implements ActionListener{

	private JFrame frame;
	private JTextField txtNo;
	private JTextField txtName;
	private JTextField txtEmail;
	private JTextField txtTel;
	private JTable table;
	private JButton btnTotal, btnAdd, btnSearch, btnDel, btnCancel;
	
	private static final int NONE=0;
	private static final int ADD=1;
	private static final int DELETE=2;
	private static final int SEARCH=3;
	private static final int TOTAL=4;
	int cmd=NONE;     
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JDBCProStep1 window = new JDBCProStep1();
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
	public JDBCProStep1() {
		initialize();
		init();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("고객관리프로그램");
		frame.setBounds(100, 100, 516, 394);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("번호");
		lblNewLabel.setBounds(24, 42, 57, 15);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("이름");
		lblNewLabel_1.setBounds(24, 80, 57, 15);
		frame.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("이메일");
		lblNewLabel_2.setBounds(24, 118, 57, 15);
		frame.getContentPane().add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("전화번호");
		lblNewLabel_3.setBounds(24, 156, 57, 15);
		frame.getContentPane().add(lblNewLabel_3);
		
		txtNo = new JTextField();
		txtNo.setBounds(93, 39, 116, 21);
		frame.getContentPane().add(txtNo);
		txtNo.setColumns(10);
		
		txtName = new JTextField();
		txtName.setBounds(93, 77, 116, 21);
		frame.getContentPane().add(txtName);
		txtName.setColumns(10);
		
		txtEmail = new JTextField();
		txtEmail.setBounds(93, 115, 116, 21);
		frame.getContentPane().add(txtEmail);
		txtEmail.setColumns(10);
		
		txtTel = new JTextField();
		txtTel.setBounds(93, 153, 116, 21);
		frame.getContentPane().add(txtTel);
		txtTel.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(224, 29, 253, 243);
		frame.getContentPane().add(scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		
		//전체보기
		btnTotal = new JButton("전체보기"); 
		btnTotal.setBounds(12, 304, 81, 23);
		frame.getContentPane().add(btnTotal);
		
		//추가
		btnAdd = new JButton("추가"); 
		btnAdd.setBounds(96, 304, 81, 23);
		frame.getContentPane().add(btnAdd);
		
		//삭제
		btnDel = new JButton("삭제");   
		btnDel.setBounds(180, 304, 81, 23);
		frame.getContentPane().add(btnDel);
		
		//검색
		btnSearch = new JButton("검색"); 
		btnSearch.setBounds(264, 304, 81, 23);
		frame.getContentPane().add(btnSearch);
		
		//취소
		btnCancel = new JButton("취소");   
		btnCancel.setBounds(348, 304, 81, 23);
		frame.getContentPane().add(btnCancel);
		
		//2단계: 컴포넌트에 액션리스너를 추가한다
		btnTotal.addActionListener(this);
		btnAdd.addActionListener(this);
		btnDel.addActionListener(this);
		btnSearch.addActionListener(this);
		btnCancel.addActionListener(this);
		
	}

	//3단계: actionPerformed 구현한다
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==btnAdd) {  //추가버튼 두번
			if(cmd!=ADD)  {
				call(ADD);
				return;
			}
			frame.setTitle("추가");
		} 
		else if(e.getSource()==btnDel) { //삭제버튼 두번
			if(cmd!=DELETE) {
				call(DELETE);
				return;
			}
			frame.setTitle("삭제");
		} 
		else if(e.getSource()==btnSearch) { //검색버튼 두번
			if(cmd!=SEARCH) {
				call(SEARCH);
				return;
			}
			frame.setTitle("검색");
		} 
		else if(e.getSource()==btnTotal) { //전체검색 한번
			call(TOTAL);
			frame.setTitle("전체보기");
		} 
		//취소버튼 인 경우
		System.out.println("취소");
		call(NONE);
		init();
	}

	public void init() {
		txtNo.setText("");
		txtName.setText("");
		txtEmail.setText("");
		txtTel.setText("");
		txtNo.setEditable(false);
		txtName.setEditable(false);
		txtEmail.setEditable(false);
		txtTel.setEditable(false);
		btnTotal.setEnabled(true);
		btnAdd.setEnabled(true);
		btnDel.setEnabled(true);
		btnSearch.setEnabled(true);
		btnCancel.setEnabled(true);
	}
	
	public void call(int command) {  //cmd=1,2,3
		btnTotal.setEnabled(false);
		btnAdd.setEnabled(false);
		btnDel.setEnabled(false);
		btnSearch.setEnabled(false);
		btnCancel.setEnabled(true);
		switch(command) {
		case ADD:
			txtNo.setEditable(true);
			txtName.setEditable(true);
			txtEmail.setEditable(true);
			txtTel.setEditable(true);
			
			btnAdd.setEnabled(true);   //add버튼만 켜기
			cmd=ADD;
			break;
		case DELETE:
			txtName.setEditable(true);   //이름만 편집가능
			
			btnDel.setEnabled(true);   //del버튼만 켜기
			cmd=DELETE;
			break;
		case SEARCH:
			txtName.setEditable(true);   //이름만 편집가능
			
			btnSearch.setEnabled(true);   //search버튼만 켜기
			cmd=SEARCH;
			break;
		case TOTAL:
			cmd=TOTAL;
			break;
		case NONE:
			cmd=NONE;
			break;
		}
	}
	
}
