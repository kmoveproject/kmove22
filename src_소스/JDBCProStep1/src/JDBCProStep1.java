import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	JButton btnUpdate;
	
	private static final int NONE=0;  //취소
	private static final int ADD=1;   //추가(insert)
	private static final int DELETE=2; //삭제(delete)
	private static final int SEARCH=3;  //(select)
	private static final int TOTAL=4;   //(select)
	private static final int UPDATE=5;   //(update)
	int cmd=NONE;    
	
	MyModel model;
	
	
	//DB연결을 위한 변수들
	String driver="oracle.jdbc.OracleDriver";
	String url="jdbc:oracle:thin:@127.0.0.1:1521:xe";
	String user="system";
	String password="123456";
	Connection con=null;
	PreparedStatement pstmt=null;
	PreparedStatement pstmtTotal, pstmtTotalScroll;
	PreparedStatement pstmtSearch, pstmtSearchScroll;
	
	
	String sqlTotal="select * from customer";
	String sqlInsert="insert into customer values(?,?,?,?)";
	String sqlDelete="delete from customer where name=?";
	String sqlUpdate="update customer set email=?, tel=? where code=? ";
	String sqlSearch="select * from customer where name=?";
		

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
	public JDBCProStep1()  {
		initialize();
		init();
		dbcon();
	}

	public void dbcon() {
		try {
			Class.forName(driver);
			con=DriverManager.getConnection(url, user, password);
			System.out.println("성공");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("고객관리프로그램");
		frame.setBounds(100, 100, 563, 398);
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
		btnDel.setBounds(178, 304, 81, 23);
		frame.getContentPane().add(btnDel);
		
		//검색
		btnSearch = new JButton("검색"); 
		btnSearch.setBounds(260, 304, 81, 23);
		frame.getContentPane().add(btnSearch);
		
		//취소
		btnCancel = new JButton("취소");   
		btnCancel.setBounds(429, 304, 81, 23);
		frame.getContentPane().add(btnCancel);
		
		btnUpdate = new JButton("수정");
		btnUpdate.setBounds(342, 304, 81, 23);
		frame.getContentPane().add(btnUpdate);
		
		//2단계: 컴포넌트에 액션리스너를 추가한다
		btnTotal.addActionListener(this);
		btnAdd.addActionListener(this);
		btnDel.addActionListener(this);
		btnSearch.addActionListener(this);
		btnCancel.addActionListener(this);
		btnUpdate.addActionListener(this);
		
	}

	//추가버튼의 DB
	//String sqlInsert="insert into customer values(?,?,?,?)";
	public void add() {
		String no=txtNo.getText();
		String name=txtName.getText();
		String email=txtEmail.getText();
		String tel=txtTel.getText();
		System.out.println(no+","+name+","+email+","+tel);
		try {
			pstmt=con.prepareStatement(sqlInsert);
			pstmt.setInt(1, Integer.valueOf(no));  //?
			pstmt.setString(2, name);
			pstmt.setString(3, email);
			pstmt.setString(4, tel);
			int res=pstmt.executeUpdate();
			if(res==1) System.out.println("성공");
			else System.out.println("실패");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	//sqlDelete="delete from customer where name=?";
	//삭제버튼의 DB
	public void del() {
		System.out.println(txtName.getText()); //txtName상자의 값을 가져오라
		try {
			String name=txtName.getText();
			pstmt=con.prepareStatement(sqlDelete);  //1)준비된통
			pstmt.setString(1, name);   //2)준비된 통에 세팅
			int res=pstmt.executeUpdate(); //3)준비된 통 실행
			if(res==1) System.out.println("삭제되었습니다.");
			else System.out.println("실패했습니다.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	//검색버튼의 DB
	//sqlSearch="select * from customer where name=?";
	public void search() {
		try {
			String name=txtName.getText();
			pstmtSearchScroll=con.prepareStatement(sqlSearch,
					ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);   //동적인 준비통-->last()
			pstmtSearch=con.prepareStatement(sqlSearch);  //정적인 준비통
			pstmtSearchScroll.setString(1, name);  //1은 첫번째?
			pstmtSearch.setString(1, name);  //1은 첫번째?
			ResultSet rsScroll=pstmtSearchScroll.executeQuery();
			ResultSet rs=pstmtSearch.executeQuery();
			if(model==null) model=new MyModel();
			model.getRowCount(rsScroll);
			model.setData(rs);
			table.setModel(model);
			table.updateUI();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmtSearchScroll.close();
				pstmtSearch.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}
	
	//전체보기버튼의 DB
	public void total() {
		System.out.println("전체보기");
		try {
			pstmtTotalScroll=con.prepareStatement(sqlTotal,
					ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			pstmtTotal=con.prepareStatement(sqlTotal);
			
			ResultSet rsScroll=pstmtTotalScroll.executeQuery();
			ResultSet rs=pstmtTotal.executeQuery();
			if(model==null) model=new MyModel();
			model.getRowCount(rsScroll);
			model.setData(rs);
			table.setModel(model);
			table.updateUI();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmtTotalScroll.close();
				pstmtTotal.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
		}
	}
	
	//sqlUpdate="update customer set email=?, tel=? where code=? ";
	public void update() {
		String code=txtNo.getText();
		String email=txtEmail.getText();
		String tel=txtTel.getText();
		try {
			pstmt=con.prepareStatement(sqlUpdate);
			pstmt.setString(1, email);
			pstmt.setString(2, tel);
			pstmt.setInt(3, Integer.valueOf(code));
			
			int res=pstmt.executeUpdate();
			if(res==1) System.out.println("수정성공");
			else System.out.println("실패");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
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
			add();   //db연결하여 insert작업이 동작됨
			total();
		} 
		else if(e.getSource()==btnDel) { //삭제버튼 두번
			if(cmd!=DELETE) {
				call(DELETE);
				return;
			}
			frame.setTitle("삭제");
			del();   //db연결하여 삭제작업
			total();
		} 
		else if(e.getSource()==btnSearch) { //검색버튼 두번
			if(cmd!=SEARCH) {
				call(SEARCH);
				return;
			}
			frame.setTitle("검색");
			search();    //db연결하여 이름검색
		} 
		else if(e.getSource()==btnUpdate) {
			if(cmd!=UPDATE) {
				call(UPDATE);
				return;
			}
			frame.setTitle("수정");
			update();
			total();
		}
		else if(e.getSource()==btnTotal) { //전체검색 한번
			call(TOTAL);
			frame.setTitle("전체보기");
			total();
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
	
	public void call(int command) {  //cmd=1,2,3,4,5
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
		case UPDATE:
			txtNo.setEditable(true);
			txtEmail.setEditable(true);
			txtTel.setEditable(true);
			
			btnUpdate.setEnabled(true);  //수정버튼만 켜기
			cmd=UPDATE;
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
