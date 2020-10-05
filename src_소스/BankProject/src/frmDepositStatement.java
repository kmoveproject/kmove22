import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

class frmDepositStatement extends JInternalFrame implements ActionListener, WindowListener
{
   //=============  데이터베이스 관련 변수들 ===============//
    static Connection conn = null;
    static String strQuery = null;
    static String strTableQuery = null;
    static ResultSet rs = null;
    static Statement stmt = null;
	static PreparedStatement pstmt = null;
    final int iColCount = 3;

    //=============  ToolBar에 사용되는 관련 변수들 ===============//
    static JToolBar xToolBar = null;
    static JButton btnSearchItem, btnPrintItem, btnCloseWindow;
    JScrollPane scrollpane;

    //=============  화면 하단의 status bar ===============//
    JLabel lbStatusMessage = null;
    boolean bIsFirst = true;

    // 데이터베이스와 연계하여 사용될 변수들 
    String strAno, strCno;
    int iSerialno = 0, iInout = 1;

    //=============  User Interface ===============//            
    static JLabel label1 = new JLabel("고객번호");
    static JLabel label2 = new JLabel("계좌번호");
    static JLabel label3 = new JLabel("잔액");
    
    JTextField tf1 = new JTextField();
    JTextField tf2 = new JTextField();

    JComboBox  jcb1 = new JComboBox();

    JTable jtAccountList = null;
    Object columnName[] = {"일련번호", "거래금액", "거래일"};
    Object dataTable[][] = null;
    
    frmDepositStatement(String title, Connection conn, JLabel lbMessage) {
        // JInternalFrame의 생성자 호출
        /* 매개변수의 값에 따라 창의 성질 변경 */
        super(title, false, true, true, true);

        // AppFrame클래스에서 database Connection을 받아서 클래스의 connection에
        this.conn = conn;
        this.lbStatusMessage = lbMessage;

        // 화면 하단에 '예금거래 내역조회' display                
        lbStatusMessage.setText("예금거래 내역 조회");

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

        tf1.setBounds(120, 30, 150, 20);
        jcb1.setBounds(120, 60, 150, 20);
        tf2.setBounds(120, 90, 100, 20);
       
        center.add(label1);
        center.add(label2);
        center.add(label3);


		tf1.addActionListener(this);        
        jcb1.addActionListener(this);
     
		center.add(tf1);
		center.add(tf2);
        center.add(jcb1);

        getContentPane().add("Center", center); 
    }

    /* 데이터베이스를 Navigate할수 있도록 앞/뒤,처음,끝으로 이동, 
       추가,삭제,저장등의 툴바 */
    private void initToolBar() {
        xToolBar = new JToolBar(JToolBar.HORIZONTAL);
        xToolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
        
        btnSearchItem      = new JButton(new ImageIcon("./toolbar/search.gif"));
        btnPrintItem      = new JButton(new ImageIcon("./toolbar/print.gif"));
        btnCloseWindow   = new JButton(new ImageIcon("./toolbar/exit.gif"));

        btnPrintItem.setEnabled(false);

        xToolBar.add(btnSearchItem);
        xToolBar.add(btnPrintItem);        
        xToolBar.add(Box.createHorizontalGlue());
        xToolBar.add(btnCloseWindow);

        btnSearchItem.addActionListener(this);        
        btnPrintItem.addActionListener(this);
        btnCloseWindow.addActionListener(this);

        getContentPane().add("North", xToolBar); // 툴바 붙이기
    }        

    /* 툴바의 버튼들의 clicked 이벤트에 따라 호출될 함수정의  */	
    public void actionPerformed(ActionEvent evt) {
        if(evt.getSource() == btnCloseWindow) {
            subCloseWindow();
        } else if(evt.getSource() == btnSearchItem) {
          dataTable((String)jcb1.getSelectedItem(), tf1.getText().trim());
        } else if(evt.getSource() == btnPrintItem) {
            subPrintItem();
        } else if(evt.getSource() == tf1) {
            subQueryAccountNo();
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
        tf1.setText("");
        tf2.setText("0");
        jcb1.setSelectedIndex(0);
    }
	
    // 입력받은 고객번호와 계좌번호를 이용하여 거래내역 조회
    public void subSearchItem() {
        dataTable((String)jcb1.getSelectedItem(), tf1.getText().trim());
    }

	
    // 입력받은 고객번호를 이용하여 소유하고있는 계좌번호 조회
	public void subQueryAccountNo() {
		String strTemp = null;
		String strCustno = tf1.getText().trim();
        strQuery =  "select A_NO, MAX(A_SERIAL_NO) from account where a_c_no =  '" + strCustno + "' group by A_NO";
    
        //msgBox(strQuery);
        
        // 이전에 있던 계좌번호들을 clear
        jcb1.removeAllItems();    

        try {
            stmt = conn.createStatement();
        	rs = stmt.executeQuery(strQuery);

            // 예금계좌번호들을 콤보박스에 additem			        	
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

    /* 고객번호 입력 -> 조회버튼 -> 계좌번호 선택 후에
      JTable로 datatable을 생성하여 화면 하단에 display 
    */
    public void dataTable(String strAccountno, String strCustno) {
        // 창을 띄운후 처음 datatable일 생성될때에는 content panel에서
        // 제거할 필요가 없다.
        if (!bIsFirst) {
            getContentPane().remove(scrollpane);
        }
            
        int lRowCount = 0, i = 0;

        // 해당고객번호, 해당 계좌번호를 만족하는 거래 내역을 select		
        strQuery = "select a_serial_no, to_char(a_amount, '9,999,999,999'), to_char(a_date, 'yyyy-mm-dd') from account where a_no = '" + strAccountno + "' and a_c_no = '" + strCustno +"'";
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
            iSerialno = rs.getInt(1);

            // JTable 생성한다.            
            jtAccountList = new JTable(dataTable, columnName);

            // scroll bar를 생성            
            scrollpane = new JScrollPane(jtAccountList);
            scrollpane.setPreferredSize(new Dimension(0, 200));        
            getContentPane().add("South", scrollpane); 

            rs.close();
            stmt.close();

            // 계좌 잔액 select
            strQuery = "select to_char(sum(nvl(a_amount,0)), '9,999,999,999') from account where a_no = ? and a_c_no = ? ";
            
            pstmt = conn.prepareStatement(strQuery);
            pstmt.setString(1, strAccountno);
            pstmt.setString(2, strCustno);
            rs = pstmt.executeQuery();
            
            rs.next();

            // 화면에 setting
            tf2.setText((String)rs.getString(1));

            bIsFirst = false;

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