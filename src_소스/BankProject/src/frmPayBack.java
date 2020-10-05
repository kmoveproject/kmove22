import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

class frmPayBack extends JInternalFrame implements ActionListener, WindowListener
{
   //=============  데이터베이스 관련 변수들 ===============//
    static Connection conn = null;
    static String strQuery = null;
    static ResultSet rs = null;
    static Statement stmt = null;
	static PreparedStatement pstmt = null;
    final int iColCount = 3;

    //=============  ToolBar에 사용되는 관련 변수들 ===============//
    static JToolBar xToolBar = null;
    static JButton btnSaveItem, btnSearchItem, btnPrintItem, btnCloseWindow;
    JScrollPane scrollpane;

    //=============  화면 하단의 status bar ===============//    
    JLabel lbStatusMessage = null;
    boolean bIsFirst = true;

    // 데이터베이스와 연계하여 사용될 변수들 
    /* class 변수 선언
        strLno : 대출번호, 
        strCno : 대출 고객코드,
        dLamount : 대출상환금액,
        iSerialno : 대출상환 일련번호
    */
    
    String strLno, strCno;
    int iSerialno = 0;
    long lLamount;

    //=============  User Interface ===============//    
    static JLabel label1 = new JLabel("고객번호");
    static JLabel label2 = new JLabel("대출번호");
    static JLabel label3 = new JLabel("상환금액");
    static JLabel label4 = new JLabel("상환잔액");
    static JLabel label5 = new JLabel("대출상환일");
    
    JTextField tf1 = new JTextField();
    JTextField tf2 = new JTextField("0");
    JTextField tf3 = new JTextField("0");
    
    JComboBox  jcb1 = new JComboBox();

	/* 고객거래 자료를 나타낼 JTable과 헤더 & 데이터 배열*/
    JTable jtAccountList = null;
    Object columnName[] = {"일련번호", "상환금액", "상환일"};
    Object dataTable[][] = null;
    
    frmPayBack(String title, Connection conn, JLabel lbMessage) {
        // JInternalFrame의 생성자 호출
        /* 매개변수의 값에 따라 창의 성질 변경 */
        super(title, false, true, true, true);

        // AppFrame클래스에서 database Connection을 받아서 클래스의 connection에
        this.conn = conn;
        this.lbStatusMessage = lbMessage;

        // 화면 하단에 '대출상환 거래' display
        lbStatusMessage.setText("대출상환 거래");

        getContentPane().setLayout(new BorderLayout());

        // Toolbar 초기화                       
        initToolBar();

        // User Interface 초기화        
        initContent();
    }

    /* User Interface 초기화       */              
    private void initContent() {
        JPanel center = new JPanel();
	center.setLayout(null);
 
	label1.setBounds(10, 30, 100, 20);
	label2.setBounds(10, 60, 100, 20);
	label3.setBounds(10, 90, 100, 20);
        label4.setBounds(10,120, 80, 20);

        tf1.setBounds(120, 30, 150, 20);
        jcb1.setBounds(120, 60, 150, 20);
        tf2.setBounds(120, 90, 100, 20);
        tf3.setBounds(120, 120, 100, 20);

        tf3.setEnabled(false);
                       
        center.add(label1);
        center.add(label2);
        center.add(label3);
        center.add(label4);
        
        tf1.addActionListener(this);        
        jcb1.addActionListener(this);
     
	center.add(tf1);
	center.add(tf2);
        center.add(tf3);        
        center.add(jcb1);

        getContentPane().add("Center", center); 
    }

    /* 데이터베이스를 Navigate할수 있도록 앞/뒤,처음,끝으로 이동, 
        추가,삭제,저장등의 툴바 */   
    private void initToolBar() {
        xToolBar = new JToolBar(JToolBar.HORIZONTAL);
        xToolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
        
        btnSaveItem      = new JButton(new ImageIcon("./toolbar/save.gif"));
        btnSearchItem      = new JButton(new ImageIcon("./toolbar/search.gif"));
        btnPrintItem      = new JButton(new ImageIcon("./toolbar/print.gif"));
        btnCloseWindow   = new JButton(new ImageIcon("./toolbar/exit.gif"));

		btnSaveItem.setEnabled(false);
        btnPrintItem.setEnabled(false);

        xToolBar.add(btnSaveItem);
        xToolBar.add(btnSearchItem);        
        xToolBar.add(btnPrintItem);
        xToolBar.add(Box.createHorizontalGlue());
        xToolBar.add(btnCloseWindow);

        btnSaveItem.addActionListener(this);
        btnSearchItem.addActionListener(this);        
        btnPrintItem.addActionListener(this);
        btnCloseWindow.addActionListener(this);

        getContentPane().add("North", xToolBar); // 툴바 붙이기
    }        

    /* 툴바의 버튼들의 clicked 이벤트에 따라 호출될 함수정의  */    
    public void actionPerformed(ActionEvent evt) {
        if(evt.getSource() == btnSaveItem) {
      		subSaveItem();
        } else if(evt.getSource() == btnCloseWindow) {
            subCloseWindow();
        } else if(evt.getSource() == btnSearchItem) {
            subSearchItem();
        } else if(evt.getSource() == btnPrintItem) {
            subPrintItem();
        } else if(evt.getSource() == tf1) {
            subQueryLoanNo();
        } else if(evt.getSource() == jcb1) {
        // 대출번호 선택 콤보 박스
//            dataTable((String)jcb1.getSelectedItem(), tf1.getText().trim());
//            subSearchItem();
        }
    }

    /* 화면 resize : datatable이 재생성되어도 화면이 refresh되어야
      정확한 모습이 나타난다. 
      정말 묘한 현상이다. swing에서만 그런지 모르겠다.
    */
    private void resize() {
        Dimension fsize = this.getSize();
        this.setSize(fsize.width - 1, fsize.height - 1);

        fsize = this.getSize();
        this.setSize(fsize.width + 1, fsize.height + 1);        
	}

    /* 사용자에게 알려야 할 메시지 처리  
       overload으로 매개변수에 따라 다른기능을 구현 */
    public void msgBox(String strMsg) {
        lbStatusMessage.setText(strMsg);
    }

    public void msgBox(String strMsg, String title) {
        JOptionPane.showMessageDialog(this, strMsg, title, JOptionPane.ERROR_MESSAGE);
    }
    
    public void msgBox(SQLException se) {
    	lbStatusMessage.setText(se.toString());
    }

    public void msgBox(SQLException se, String title) {
        lbStatusMessage.setText(title + " : " + se.toString());
    }

    // 화면에 있는 User Interface들중에 textField들의 내용을 지운다.
    public void clearItems() {
        tf2.setText("0");
        tf3.setText("0");
        jcb1.setSelectedIndex(0);
    }

    /* 화면의 textField들의 내용을 클래스 변수에  */
    public boolean getItems() {
        strCno = tf1.getText().trim();                 // 고객번호번호
        strLno = (String)jcb1.getSelectedItem();       // 계좌번호
        lLamount = Long.parseLong(tf2.getText().trim()); //상환금액

        // 입력 textField의 값이 누락되었는 확인
        if(lLamount <= 0) { 
            msgBox("상환액을 정확히 입력해주세요",""); 
            return false; 
        }

        // UserInterface로부터 클래스변수로 가져온 값을 statusbar에 display
        lbStatusMessage.setText("[고객번호 : " + strCno + "] [계좌 : " + strLno + "] [상환액 : " + lLamount + "]");
        return true;
    }

    // 클래스 변수에 들어 있는 내용들 database에 반영   
    public void subSaveItem() {
	if(getItems()) {
            long lTemp = 0;
            String openDate = "";
            long a_term = 0;
            String a_item_dist = "";
            String a_item_name = "";
            String a_b_no = "";            
	    try {
                // 통장에서 누적 b_serial_no의 맨 마지막 값 + 1 을 가져온다.                
                //strQuery = "select max(b_serial_no)+1 from borrow where b_cust_no = ? and b_loan_no = ?";
                strQuery = "select a_open_date, a_term, a_item_dist, a_item_name, a_b_no, max(a_serial_no)+1, sum(nvl(a_amount,0)) ";
                strQuery += " from account where a_c_no = ? and a_no = ? ";
                strQuery += " group by a_open_date, a_term, a_item_dist, a_item_name, a_b_no ";                
                pstmt = conn.prepareStatement(strQuery);
                pstmt.setString(1, strCno);
                pstmt.setString(2, strLno);
                rs = pstmt.executeQuery();

                /* Result Set의 반환 행수가 1개 밖에 없다.  
                   why? max(d_serial_no) 따라서 while문장이 필요없다. */
                rs.next();  
                openDate = rs.getString(1);
                a_term = rs.getLong(2);
                a_item_dist = rs.getString(3);
                a_item_name = rs.getString(4);
                a_b_no = rs.getString(5);
                iSerialno = rs.getInt(6);
                //lTemp = Long.parseLong(tf3.getText().trim());

                // 상환 잔액 : ltemp
                lTemp = Long.parseLong(tf3.getText().trim());

                if(lLamount > lTemp) {
                    msgBox("상환잔액을 초과 합니다. 현재 상환잔액은 : " + lTemp + "입니다", "");
                    return;
                }
                rs.close();
                pstmt.close();

                // borrow테이블에 대출상환 내역을 기록
                strQuery = "INSERT INTO account select ?, ?, sysdate, ?, a.a_open_date, a.a_total_amount, a.a_term, a.a_item_dist, a.a_item_name, a.a_b_no, ? from account a where a.a_no = '" + strLno + "' and a.a_c_no = '" + strCno + "' and a_serial_no = 1 ";
	        pstmt = conn.prepareStatement(strQuery);
                pstmt.setString(1, strLno);
                pstmt.setLong(2, iSerialno);
                pstmt.setLong(3, lLamount);
                pstmt.setString(4, strCno);   
                
                pstmt.executeUpdate();             

                lbStatusMessage.setText("insert completed!!");
                conn.commit();  // db에 commit;
                pstmt.close();
                clearItems();   // 저장한후 화면clear
                dataTable((String)jcb1.getSelectedItem(), tf1.getText().trim());
	    } catch (SQLException se) {
	        msgBox(se, "Database Insert Into error");
                se.printStackTrace();
	    } //end try~catch
        }
	}

    // search버튼이 눌렸을 경우 처리
    public void subSearchItem() {
        dataTable((String)jcb1.getSelectedItem(), tf1.getText().trim());
    }

    /* 고객번호를 입력한후 검색버튼을 눌렀을경우 
       해당 고객번호를 대출거래 테이블에서 
       대출계좌번호만 select하여 화면 대출계좌번호 콤보박스에 additem
    */
    public void subQueryLoanNo() {
		String strTemp = null;
		String strCustno = tf1.getText().trim();
        strQuery =  "select A_NO, MAX(A_SERIAL_NO) from account where a_c_no =  '" + strCustno + "' and a_item_dist = 'L1' group by A_NO";
        
        // 이전에 있던 계좌번호들을 clear
        jcb1.removeAllItems();    

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(strQuery);

            // 대출계좌번호들을 콤보박스에 additem          
            while(rs.next()) {
                strTemp = rs.getString(1).trim();
                jcb1.addItem(strTemp);
	    }
    	    rs.close();
            stmt.close();
        } catch (SQLException se) {
            msgBox(se);
        }
    }

    /* 고객번호 입력 -> 조회버튼 -> 대출계좌번호 선택 후에
      JTable로 datatable을 생성하여 화면 하단에 display 
    */
    public void dataTable(String strLoanno, String strCustno) {
        // save(저장)버튼을 활성화 시킨다.
        btnSaveItem.setEnabled(true);

        // 처음에 화면 실행했을때는 bIsFirst 값이 true, 
        // 다음 수행시부터는 bIsFirst 값이 false
        // 값을 갖는다. 따라서 두번째 수행될때부터는 계속수행된다.
        if (!bIsFirst) {
            getContentPane().remove(scrollpane);
        }
            
        int lRowCount = 0, i = 0;
		
        // 대출금 상환 내역을 리스트
        strQuery = "select a_serial_no, nvl(a_amount,0), a_date from account where a_no = '" + strLoanno + "' and a_c_no = '" + strCustno +"'";

	dataTable = null;        
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,  ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(strQuery);
            
            rs.last();
            lRowCount = rs.getRow() + 1;
            
            // Object배열 선언
            dataTable = new Object[lRowCount][iColCount];

            rs.beforeFirst();
            while(rs.next()) {
                for(int j = 0; j < iColCount; j++) {
                    // Object 배열에 데이터 저장.
                    dataTable[i][j] = rs.getObject(j+1);
                }
                i++;
            }
			rs.last();
            
            // 맨 마지막의 serial번호를 가져온다. 
            // 해당 계좌의 다음 거래 serial번호를 생성하기 위해
            //iSerialno = rs.getInt(1);

            // JTable 생성한다.            
            jtAccountList = new JTable(dataTable, columnName);

            // scroll bar를 생성
            scrollpane = new JScrollPane(jtAccountList);
            scrollpane.setPreferredSize(new Dimension(0, 200));        
            getContentPane().add("South", scrollpane); 

            rs.close();
            stmt.close();
            bIsFirst = false;
			
            long lSumRepAmount, lLoanAmount, lRemainAmount;

            // 상환 총액 구하기.
            strQuery = "select sum(nvl(a_amount,0)) from account where a_no = ? and a_c_no = ? ";
            pstmt = conn.prepareStatement(strQuery);
            pstmt.setString(1, strLoanno);
            pstmt.setString(2, strCustno);            
            rs = pstmt.executeQuery();
            rs.next();
            lSumRepAmount = rs.getLong(1);

            rs.close();
            pstmt.close();

            // 대출금액 조회
            strQuery = "select a_total_amount from account where a_no = ? and a_amount is null and rownum = 1 ";
            pstmt = conn.prepareStatement(strQuery);
            pstmt.setString(1, strLoanno);
            rs = pstmt.executeQuery();
            rs.next();
            lLoanAmount = rs.getLong(1);
            
            lRemainAmount = lLoanAmount - lSumRepAmount;

            // 상환 완료 된 경우
            if (lRemainAmount == 0) {
                btnSaveItem.setEnabled(false);
            }
			
            // 상환해야할 잔액이 남아 있는 경우
            if (lRemainAmount > 0) {
                tf3.setText("" + lRemainAmount);
                btnSaveItem.setEnabled(true);
            }

            rs.close();
            pstmt.close();                
            // 화면 refresh : 편법
	        resize();            
        } catch (SQLException se) {
            msgBox(se);
            se.printStackTrace();
        }
    }

    // 인쇄버튼이 눌렸을 경우 처리
   public void subPrintItem() 
    {
        // 인쇄모듈 구성
    }

    // 닫기버튼이 눌렸을 경우 처리
    public void subCloseWindow() {
        try {
            if (pstmt != null) pstmt.close();
            if (stmt != null) stmt.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }

        setVisible(false);
        dispose();
    }

    //  WindowListener를 implements 했을 경우 모든 Method를 overriding해야 한다.
    public void windowOpened(WindowEvent evt) {}
  	public void windowClosed(WindowEvent evt) {}

    public void windowClosing(WindowEvent evt) {
		subCloseWindow();
	}        

    public void windowIconified(WindowEvent evt) {}
  	public void windowDeiconified(WindowEvent evt) {}
  	public void windowActivated(WindowEvent evt) {}
  	public void windowDeactivated(WindowEvent evt) {}
}