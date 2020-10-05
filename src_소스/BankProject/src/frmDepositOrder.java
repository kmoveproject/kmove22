import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

class frmDepositOrder extends JInternalFrame implements ActionListener, WindowListener
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
    JButton btnInsertItem, btnSaveItem,  btnPrintItem, btnCloseWindow;

    //=============  화면 하단의 status bar ===============//
    JLabel lbStatusMessage = null;
    boolean bInsertFlag = true;

    // 데이터베이스와 연계하여 사용될 변수들 
    String strAno, strAcno, strAname, strAbcode, strDcustno;
    long lTerm, lSeq;
    
    // 지점코드 기억시킬 배열
    String sBcode[] = null;
    long lCurrentRow;

    //=============  User Interface ===============//            
    static JLabel label1 = new JLabel("지점");
    static JLabel label2 = new JLabel("고객구분");
    static JLabel label3 = new JLabel("계좌번호");
    static JLabel label4 = new JLabel("예금종류");
    static JLabel label5 = new JLabel("계약기간");
    static JLabel label6 = new JLabel("고객번호");
    static JLabel label7 = new JLabel("개월");
    static String customer_dist[] = { "개인고객", "기업고객" };
    
    static JTextField tf1 = new JTextField();
    static JTextField tf2 = new JTextField("0");
    static JTextField tf3 = new JTextField();

    JComboBox  jcb1 = new JComboBox();
    JComboBox  jcb2 = new JComboBox();
    JComboBox  jcb3 = new JComboBox();    
    JTable jtAccoutList = null;

    Object columnName[] = {"계좌번호", "예금상품명", "고객번호", "고객명", "개설일"};
    Object dataTable[][] = null;
    
    frmDepositOrder(String title, Connection conn, JLabel lbMessage) {
        // JInternalFrame의 생성자 호출
        /* 매개변수의 값에 따라 창의 성질 변경 */
        super(title, false, true, true, true);

        String query = null;
        // AppFrame클래스에서 database Connection을 받아서 클래스의 connection에
        this.conn = conn;
        this.lbStatusMessage = lbMessage;

        // 화면 하단에 '예금거래신청' display                
        lbStatusMessage.setText("예금거래 신청");

        getContentPane().setLayout(new BorderLayout());

        // Toolbar 초기화
        initToolBar();

        // User Interface 초기화
        initContent();

        // 고객 예금거래 신청 리스트 JTable에 보여질 내용 select
        initTable();
        // 예금계좌번호 생성(지점코드, 개인 or 기업)
        subMakeAccountNumber(jcb1.getSelectedIndex(), jcb2.getSelectedIndex());        
    }
    
    private void initTable() {
        int lRowCount = 0, i = 0;
        // AppFrame클래스의 createview()에서 생성한 view사용
        String query =" select A_NO, A_ITEM_NAME, C_NO, C_NAME, A_DATE from v_account_list where A_ITEM_DIST = 'A0' order by A_NO desc";        

        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,  ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(query);
            
            // 전체 row의 갯수+1을 얻어온다.
            rs.last();
            lRowCount = rs.getRow() + 1; // 현재cursor의 위치
            
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
			
            // 현재 예금 계좌를 가지고 있는 고객 목록테이블 생성
	        jtAccoutList = new JTable(dataTable, columnName);

            // 고객목록테이블(JTable)에 스크롤 바 붙여주기
    	       JScrollPane scrollpane = new JScrollPane(jtAccoutList);
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
        label7.setBounds(185, 150, 70, 20);

        jcb1.setBounds(120, 30, 150, 20);
        jcb2.setBounds(120, 60, 150, 20);
        tf1.setBounds(120, 90, 150, 20);
        jcb3.setBounds(120, 120, 150, 20);       
        tf2.setBounds(120, 150, 100, 20);
        tf3.setBounds(120, 180, 150, 20);       
       
        center.add(label1);
        center.add(label2);
        center.add(label3);
        center.add(label4);
        center.add(label5);
        center.add(label6);
        center.add(label7);

        // 지점코드, 지점명을 가져온다.
	strQuery = "select b_no, b_name from branch";
        
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(strQuery);
            int i=0;

            /* 지점이름이 저장되는 인덱스값에 따라 지점코드가 저장 */
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

        // 고객구분 선택 콤보박스
        for(int i=0; i<customer_dist.length; i++) {
            jcb2.addItem(customer_dist[i]);
        }
        
        jcb1.addActionListener(this);
        jcb2.addActionListener(this);
     
	center.add(tf1);
	center.add(tf2);
        center.add(tf3);
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
        } else if(evt.getSource() == btnCloseWindow) {
        	subCloseWindow();
        } else if(evt.getSource() == btnPrintItem) {
            subPrintItem();
        } else if(evt.getSource() == jcb1) {
            subMakeAccountNumber(jcb1.getSelectedIndex(), jcb2.getSelectedIndex());
        } else if(evt.getSource() == jcb2) {
            subMakeAccountNumber(jcb1.getSelectedIndex(), jcb2.getSelectedIndex());
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
        tf3.setText("");
        jcb1.setSelectedIndex(0);
        jcb2.setSelectedIndex(0);
        jcb3.setSelectedIndex(0);
    }

    /* 화면의 textField들의 내용을 클래스 변수에 저장  */
    public boolean getItems() {
        strAbcode = sBcode[jcb1.getSelectedIndex()];        // 지점코드
        strAno   = tf1.getText().trim();                    // 예금계좌번호
        //intIndex = (String) jcb2.getSelectedIndex();        // 고객구분
        strAname = (String) jcb3.getSelectedItem();         // 예금계좌 상품명
        lSeq     = 1;
        lTerm    = Long.parseLong(tf2.getText().trim());     // 계약기간
        strDcustno = tf3.getText().trim();                  // 고객번호

        // 입력 textField의 값이 누락되었는 확인
        if (strAno.length() < 8) { msgBox("예금계좌번호가 잘못되었습니다."); return false; }
        if (strDcustno.equals("")) { msgBox("고객번호가 입력되지 않았습니다."); return false; }

        // 입력된 고객번호가 고객테이블에 존재하는 고객인지 확인
        strQuery = "select count(c_no) from customer where c_no = '" + strDcustno + "'";
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(strQuery);
            
            rs.next();
            if (rs.getInt(1) < 1) {
                msgBox("입력하신 고객번호는 고객대장에 없는 고객이거나 잘못된 고객번호입니다.", "알림");
                return false;
            }
        } catch(SQLException se) {
        	msgBox(se);
        }

        // UserInterface로부터 클래스변수로 가져온 값을 statusbar에 display
        lbStatusMessage.setText("[strAno : " + strAno + "] [strDcustno : " + strDcustno + "] [strAbcode : " + strAbcode + "] [strAname : " + strAname + "] [lTerm : " + lTerm + "]");
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
                    // 계좌테이블에 계좌데이터 insert
                    strQuery = "INSERT INTO account VALUES (?, ?, null, null, sysdate, null, ?, 'A0', ?, ?, ?)";
                    
                    pstmt = conn.prepareStatement(strQuery);
                    pstmt.setString(1, strAno);
                    pstmt.setLong(2, lSeq);
                    pstmt.setLong(3, lTerm);
                    pstmt.setString(4, strAname);
                    pstmt.setString(5, strAbcode);
		    pstmt.setString(6, strDcustno);
                    // 계좌테이블에 데이터가 정상적으로 insert되었으면
                    // deposit테이블에 거래데이터 insert
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

    // 지점변경, 고객구분 변경에 따라 계좌번호 생성
    public void subMakeAccountNumber(int bcodeIndex, int distIndex) {
        String c_dist = null, a_no = null, sTemp = null;
        
        // 고객구분코드를 가져온다.
        if (distIndex == 0) {
	    c_dist = "P";
            sTemp = "00";
        } else if(distIndex == 1) {
            c_dist = "E";
            sTemp = "11";
        }

        /* 계좌번호 생성 : A P 100 001
            A : account(예금)
            P : 개인 , E : 기업고객
            100 : 지점코드..
            001 : 일련번호...
        */
        a_no = "A" + c_dist + sBcode[bcodeIndex];

        /* 계좌번호 max값 가져오기. 
            to_char(11, '09999')하면 '00011' 나온다.
            substr("100001", 5, 2) 하면 '01' 나온다.
            nvl : 지점이 최초로 생성된 코드라면.. max값이 null이 들어온다.
        */
        strQuery = "select to_char(nvl(max(substr(a_no, 6,3)) + 1, 1), '099') from account where a_no like '" + a_no + "%' and A_ITEM_DIST = 'A0'";
        //msgBox(strQuery);

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(strQuery);

            rs.next();
            a_no = a_no + rs.getString(1).trim();
            tf1.setText(a_no);
            //msgBox(a_no);
            rs.close();

            // sTemp값 : 개인(00), 기업(11)에 따라 예금종류를 가져온다.
            strQuery = "select item_name from item where item_dist = 'A0' and item_c_dist = '" + sTemp +"'";
            rs = stmt.executeQuery(strQuery);

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