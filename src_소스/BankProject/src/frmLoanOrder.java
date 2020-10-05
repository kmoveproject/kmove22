import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

class frmLoanOrder extends JInternalFrame implements ActionListener, WindowListener
{
   //=============  데이터베이스 관련 변수들 ===============//
    static Connection conn = null;
    static String strQuery = null;
    static String strTableQuery = null;

    static ResultSet rs = null;
    static Statement stmt = null;
    static PreparedStatement pstmt = null;
    final int iColCount = 5;

    //=============  ToolBar에 사용되는 관련 변수들 ===============//
    JToolBar xToolBar = null;
    JButton btnInsertItem, btnSaveItem, btnPrintItem, btnCloseWindow;

    //=============  화면 하단의 status bar ===============//
    JLabel lbStatusMessage = null;
    boolean bInsertFlag = true;

    // 데이터베이스와 연계하여 사용될 변수들 
    /* class 변수 선언
        strLno : 대출번호, 
        strLname : 대출상품명, 
        strLbcode : 대출지점명, 
        strBcustno : 대출 고객코드,
        dLamount : 대출금액,
        lTerm : 대출기간
    */
    String strLno, strLcno, strLname, strLbcode, strBcustno;
    long lLamount;
    long lTerm, lSeq;

    // 지점코드 기억시킬 배열    
    String sBcode[] = null;
    long lCurrentRow;

    //=============  User Interface ===============//            
    static JLabel label1 = new JLabel("지점");
    static JLabel label2 = new JLabel("고객구분");
    static JLabel label3 = new JLabel("대출번호");
    static JLabel label4 = new JLabel("대출종류");
    static JLabel label5 = new JLabel("대출금액");
    static JLabel label6 = new JLabel("대출기간");
    static JLabel label7 = new JLabel("고객번호");
    static JLabel label8 = new JLabel("개월");    
    static String customer_dist[] = { "개인고객", "기업고객" };
    
    static JTextField tf1 = new JTextField();
    static JTextField tf2 = new JTextField("0");
    static JTextField tf3 = new JTextField("0");
    static JTextField tf4 = new JTextField();    

    JComboBox  jcb1 = new JComboBox();
    JComboBox  jcb2 = new JComboBox();
    JComboBox  jcb3 = new JComboBox();    

    /* 고객거래 자료를 나타낼 JTable과 헤더 & 데이터 배열*/
    JTable jtLoanList = null;
    Object columnName[] = {"대출번호", "대출상품명", "고객번호", "고객명", "대출일"};
    Object dataTable[][] = null;
    
    frmLoanOrder(String title, Connection conn, JLabel lbMessage) {
        // JInternalFrame의 생성자 호출
        /* 매개변수의 값에 따라 창의 성질 변경 */
        super(title, false, true, true, true);

        String query = null;        
        // AppFrame클래스에서 database Connection을 받아서 클래스의 connection에
        this.conn = conn;
        this.lbStatusMessage = lbMessage;

        // 화면 하단에 '대출거래신청' display
        lbStatusMessage.setText("대출거래 신청");

        getContentPane().setLayout(new BorderLayout());

        // Toolbar 초기화        
        initToolBar();

        // User Interface 초기화
        initContent();

        // 고객 대출거래 신청 리스트 JTable에 보여질 내용 select
        initTable();
    
        // 대출계좌번호 생성(지점코드, 개인 or 기업)
        subMakeLoanNumber(jcb1.getSelectedIndex(), jcb2.getSelectedIndex());        
    }
    
    private void initTable() {
        int lRowCount = 0, i = 0;

        // AppFrame클래스의 createview()에서 생성한 view사용
    	String query = " select A_NO, A_ITEM_NAME, C_NO, C_NAME, A_DATE from v_account_list where A_ITEM_DIST = 'L1' order by A_NO desc ";    

        try {
            
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,  ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(query);
            
            rs.last();
            // 전체 row의 갯수 + 1을 얻어온다.
            lRowCount = rs.getRow() + 1;

            // JTable 생성시 필요한 Object array 생성            
            dataTable = new Object[lRowCount][iColCount];

            // cursor의 위치를 처음이전의 위치로 이동
            rs.beforeFirst();

            // cursor에 있는 데이터를 배열에 저장
            while(rs.next()) {
                for(int j = 0; j < iColCount; j++) {
                    // 각 컬럼의 갯수만큼 반복하여 배열에 저장
                    dataTable[i][j] = rs.getObject(j+1);
                }
                i++;
            }

            // 현재 대출 계좌를 가지고 있는 고객 목록테이블 생성
            jtLoanList = new JTable(dataTable, columnName);

            // 고객목록테이블(JTable)에 스크롤 바 붙여주기
            JScrollPane scrollpane = new JScrollPane(jtLoanList);
            scrollpane.setPreferredSize(new Dimension(0, 200));        
	    getContentPane().add("South", scrollpane); 

            // 사용한 resultSet을 close
            rs.close();

            //  사용한 statement를 close            
            stmt.close();
                  
        } catch (SQLException se) {
            msgBox(se);
            se.printStackTrace();
        }
    }
       
    /* User Interface 초기화       */       
    private void initContent() {
        JPanel center = new JPanel();
	center.setLayout(null);
 
	label1.setBounds(10, 30, 100, 20);
	label2.setBounds(10, 60, 100, 20);
	label3.setBounds(10, 90, 100, 20);
	label4.setBounds(10, 120, 100, 20);
	label5.setBounds(10, 150, 100, 20); 
        label6.setBounds(10, 180, 100, 20);
        label7.setBounds(10, 210, 100, 20);
        label8.setBounds(185, 180, 50, 20);        

        jcb1.setBounds(120, 30, 150, 20);
        jcb2.setBounds(120, 60, 150, 20);
        tf1.setBounds(120, 90, 150, 20);
        jcb3.setBounds(120, 120, 150, 20);       
        tf2.setBounds(120, 150, 100, 20);
        tf3.setBounds(120, 180, 150, 20);
        tf4.setBounds(120, 210, 150, 20);
       
        center.add(label1);
        center.add(label2);
        center.add(label3);
        center.add(label4);
        center.add(label5);
        center.add(label6);
        center.add(label7);
        center.add(label8);

        // 지점코드, 지점명을 가져온다.
	strQuery = "select b_no, b_name from branch";
        
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(strQuery);
            int i=0;

            /*지점이름이 저장되는 인덱스값에 따라 지점코드가 저장 */
            sBcode = new String[10];
            while(rs.next()) {
            	jcb1.addItem(rs.getString("b_name").trim());
                sBcode[i] = rs.getString("b_no").trim();
                i++;
            }
            rs.close();
            stmt.close();
        } catch (SQLException se) {
            msgBox(se);
        }

        // 대출상품 종류 선택 콤보박스
       	for(int i=0; i<customer_dist.length; i++) {
            jcb2.addItem(customer_dist[i]);
        }
        
        jcb1.addActionListener(this);
        jcb2.addActionListener(this);
     
	center.add(tf1);
	center.add(tf2);
        center.add(tf3);
        center.add(tf4);        
        center.add(jcb1);
	center.add(jcb2);
        center.add(jcb3);

        getContentPane().add("Center", center); 
    }

    /* 데이터베이스를 Navigate할수 있도록 앞/뒤,처음,끝으로 이동, 
       추가,삭제,저장등의 툴바 */
    private void initToolBar() {
        xToolBar = new JToolBar(JToolBar.HORIZONTAL);
        xToolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
        
        btnInsertItem    = new JButton(new ImageIcon("./toolbar/insert.gif"));
        btnSaveItem      = new JButton(new ImageIcon("./toolbar/save.gif"));
        btnPrintItem      = new JButton(new ImageIcon("./toolbar/print.gif"));
        btnCloseWindow   = new JButton(new ImageIcon("./toolbar/exit.gif"));

        btnPrintItem.setEnabled(false);

        xToolBar.add(btnInsertItem);
        xToolBar.add(btnSaveItem);
        xToolBar.add(btnPrintItem);
        xToolBar.add(Box.createHorizontalGlue());
        xToolBar.add(btnCloseWindow);

        btnInsertItem.addActionListener(this);
        btnSaveItem.addActionListener(this);
        btnPrintItem.addActionListener(this);
        btnCloseWindow.addActionListener(this);

        getContentPane().add("North", xToolBar); // 툴바 붙이기
    }        

    /* 툴바의 버튼들의 clicked 이벤트에 따라 호출될 함수정의  */    	
    public void actionPerformed(ActionEvent evt) {
        if(evt.getSource() == btnSaveItem) {
      	    subSaveItem();
        } else if(evt.getSource() == btnInsertItem) {
            subInsertItem();
        } else if(evt.getSource() == btnPrintItem) {
            subPrintItem();
        } else if(evt.getSource() == btnCloseWindow) {
            subCloseWindow();
        } else if(evt.getSource() == jcb1) {
            subMakeLoanNumber(jcb1.getSelectedIndex(), jcb2.getSelectedIndex());
        } else if(evt.getSource() == jcb2) {
            subMakeLoanNumber(jcb1.getSelectedIndex(), jcb2.getSelectedIndex());
        }
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
        tf4.setText("");        
        jcb1.setSelectedIndex(0);
        jcb2.setSelectedIndex(0);
        jcb3.setSelectedIndex(0);
    }

    /* 화면의 textField들의 내용을 클래스 변수에 저장  */
    public boolean getItems() {
        strLbcode = sBcode[jcb1.getSelectedIndex()];        // 지점코드
        strLno   = tf1.getText().trim();                    // 예금계좌번호
        //strLcno  = (String) jcb2.getSelectedIndex();        // 고객구분        
        strLname = (String) jcb3.getSelectedItem();         // 예금계좌 상품명
        lSeq     = 1;
        lTerm   = Long.parseLong(tf3.getText().trim());     // 계약기간
        lLamount = Long.parseLong(tf2.getText().trim());    // 대출금액
        strBcustno = tf4.getText().trim();                  // 고객번호

	
        /* 데이터의 무결성을 보장위한 데이터 체크 */
        // 대출계좌번호가 8자리 미만. 
        // 즉 자동생성된 계좌번호를 변경했을경우 false리턴
        if (strLno.length() < 8) { msgBox("대출계좌번호가 잘못되었습니다.", ""); return false; }
        
        // 대출금액이 0, 즉 초기치 그대로 일경우 false 리턴
        if (lLamount == 0) { msgBox("대출금액이 누락되었습니다.", ""); return false; }
		
        // 대출기간이 1개월 미만, 즉 초기치 그대로 이거나, 60개월 이상(최대치 초과) 체크
        if (lTerm > 60 || lTerm < 1) { 
        	msgBox("대출기간은 최초 1개월, 최대 60개월입니다.", ""); 
            tf3.setText("0");
            return false; 
        }
        
        // 고객번호가 최소한 12자리 이상이며(사업자번호:12자리)
        // 최대한 14자리(개인고객(주민번호) : 14자리)를 넘길수 없다.
        if (strBcustno.length() < 12 || strBcustno.length() > 14) {
            msgBox("고객번호가 잘못되었습니다. 올바른 고객번호를 입력하시기 바랍니다.", "");
            return false;
        }

        strQuery = "select count(c_no) from customer where c_no = '" + strBcustno + "'";
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(strQuery);
            
            rs.next();
            if (rs.getInt(1) < 1) {
                msgBox("입력하신 고객번호는 고객대장에 없는 고객이거나 잘못된 고객번호입니다.\n고객등록을 먼저 하시기 바랍니다.", "알림");
                return false;
            }
        } catch(SQLException se) {
        	msgBox(se);
        }

        // UserInterface로부터 클래스변수로 가져온 값을 statusbar에 display        
        lbStatusMessage.setText("[strAno : " + strLno + "] [strBcustno : " + strBcustno + "] [strLbcode : " + strLbcode + "] [strLname : " + strLname + "] [lLamount : " + lLamount + "] [lTerm : " + lTerm + "]");
        return true;
    }

    // 클래스 변수에 들어 있는 내용들 database에 반영
    public void subSaveItem() {
        // insert버튼이 눌렸었는지...
        // 즉 새로운 데이터인지, 현재 데이터의 수정인지
        // Insert버튼이 눌렸다면..
        if (bInsertFlag == true) {

            // User Interface의 내용을 클래스 변수에 저장
	    if (getItems()) {
                try {
                    // loan 테이블에 대출계좌 생성 : 대출정보 테이블
                    strQuery = "INSERT INTO account VALUES (?, ?, null, null, sysdate, ?, ?, 'L1', ?, ?, ?)";
                    pstmt = conn.prepareStatement(strQuery);
                    pstmt.setString(1, strLno);
                    pstmt.setLong(2, lSeq);
                    pstmt.setLong(3, lLamount);
                    pstmt.setLong(4, lTerm);
                    pstmt.setString(5, strLname);
                    pstmt.setString(6, strLbcode);
                    pstmt.setString(7, strBcustno);

                    pstmt.executeUpdate();
                    pstmt.close();

                    bInsertFlag = false;
                    lbStatusMessage.setText("insert completed!!");

                    // 정상적으로 데이터 insert가 끝나면 commit
                    conn.commit();
                    
                    initTable();
                    clearItems();
                } catch (SQLException se) {
                    msgBox(se, "Database Insert Into error");
		} //end try~catch
            }//end if(getItems())
        } else {

        // insert 버튼이 눌리지 않았다면
        	if(getItems()) {
	            try {
                    lbStatusMessage.setText("update completed!!");                
	                conn.commit();
		        } catch(SQLException se) {
                    msgBox(se, "입력자료 오류");
	            }	// end try~catch
            } //end if(getItems())
        } // end if(bInsertFlag == true)
    }	// end subSaveItem()
	
    // Insert버튼이 눌렸을 경우 처리
    public void subInsertItem() {
	clearItems();
        bInsertFlag = true;
    }

    /* 지점변경, 고객구분 변경에 따라 대출번호 생성 method*/
    public void subMakeLoanNumber(int bcodeIndex, int distIndex) {
        String c_dist = null, l_no = null, sTemp = null;

        // 고객구분코드를 가져온다.        
        if (distIndex == 0) {
	    c_dist = "P";
            sTemp = "00";
        } else if(distIndex == 1) {
            c_dist = "E";
            sTemp = "11";
        }

        /* 대출계좌번호 생성 : L P 100 001
            L : loan(대출)
            P : 개인 , E : 기업고객
            100 : 지점코드..
            001 : 일련번호...
        */
        l_no = "L" + c_dist + sBcode[bcodeIndex];
        strQuery = "select to_char(nvl(max(substr(a_no, 6,3)) + 1, 1), '099') from account where a_no like '" + l_no + "%' and A_ITEM_DIST = 'L1'";
        //msgBox(strQuery);

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(strQuery);

            rs.next();
            l_no = l_no + rs.getString(1).trim();
            tf1.setText(l_no);
            //msgBox(l_no);
            rs.close();
			
            // 대출 가능 종류를 SELECT 고객 구분에 따라서.
            strQuery = "select item_name from item where item_dist = 'L1' and item_c_dist = '" + sTemp +"'";
            rs = stmt.executeQuery(strQuery);
			
            msgBox(strQuery);

            // 대출계좌 종류 combo box 초기화
            // combo box의 item들을 모두 삭제후 다시 insert하기 위해
            jcb3.removeAllItems();
            
            while(rs.next()) {
                jcb3.addItem(rs.getString(1).trim());
            }
            rs.close();
            stmt.close();
        } catch (SQLException se) {
            msgBox(se);
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