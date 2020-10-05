import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

class frmDepositKind extends JInternalFrame implements ActionListener, WindowListener
{
   //=============  데이터베이스 관련 변수들 ===============//
    static Connection conn = null;
    static String strQuery = "select item_name, item_limit, item_dist, item_c_dist, term1, rate1, term2, rate2, term3, rate3 from item where ITEM_DIST = 'A0'";    
    static ResultSet rs = null;
    static Statement stmt = null;

    //=============  ToolBar에 사용되는 관련 변수들 ===============//
    JToolBar xToolBar;
    JButton btnMoveFirst, btnMovePrev, btnMoveNext, btnMoveLast;
    JButton btnInsertItem, btnDeleteItem, btnSaveItem, btnPrintItem, btnCloseWindow;

    //=============  화면 하단의 status bar ===============//
    JLabel lbStatusMessage;

    // 데이터베이스와 연계하여 사용될 변수들 (상품명, 상품구분, 고객구분)
    String strDkname, strDkdist, strDkcdist;
    long  lDklimit;
    int iDkterm1, iDkterm2, iDkterm3;
    float fDkrate1,  fDkrate2,  fDkrate3; 

     // cursor의 현재 위치    
    long lCurrentRow;

    // 추가버튼이 누려진후에 저장버튼이 눌렸는지 체크
    // true: insert -> save, false : 내용 수정후 -> save
    boolean bInsertFlag = false;
    
    //=============  User Interface ===============//
    static JLabel label1 = new JLabel("예금상품명");
    static JLabel label2 = new JLabel("예금한도");
    static JLabel label3 = new JLabel("상품구분");
    static JLabel label4 = new JLabel("고객구분");    
    static JLabel label5 = new JLabel("기간1");
    static JLabel label6 = new JLabel("이율1");
    static JLabel label7 = new JLabel("기간2");
    static JLabel label8 = new JLabel("이율2");
    static JLabel label9 = new JLabel("기간3");
    static JLabel label10 = new JLabel("이율3");

    static String customer_dist[] = { "개인고객", "기업고객" };
    static String item_dist[] = { "예금", "대출" };
    
    static JTextField tf1 = new JTextField();
    static JTextField tf2 = new JTextField();
    static JTextField tf3 = new JTextField();
    static JTextField tf4 = new JTextField();
    static JTextField tf5 = new JTextField();
    static JTextField tf6 = new JTextField();
    static JTextField tf7 = new JTextField();
    static JTextField tf8 = new JTextField();

    JComboBox  jcb1 = new JComboBox();
    JComboBox  jcb2 = new JComboBox();    
 
    frmDepositKind(String title, Connection conn, JLabel lbMessage) {
        // JInternalFrame의 생성자 호출
        /* 매개변수의 값에 따라 창의 성질 변경 */
        super(title, true, true, true, true);

        // AppFrame클래스에서 database Connection을 받아서 클래스의 connection에
        this.conn = conn;
        this.lbStatusMessage = lbMessage;
        

        // 화면 하단에 '예금상품 관리' display        
        lbStatusMessage.setText("예금상품 관리");

        getContentPane().setLayout(new BorderLayout());

        // Toolbar 초기화		
        initToolBar();

        getContentPane().add("North", xToolBar); // 툴바 붙이기

        // User Interface 초기화
        initContent();

        // 기본적으로 사용될 resultSet을 데이터베이스에서 Query
        // "select dk_name, dk_limit, dk_dishi, dk_term1, dk_rate1, 
        //  dk_term2, dk_rate2, dk_term3, dk_rate3 from deposit_kind";
        initResultSet(strQuery);
        subMoveFirst();
    }

    /* 기본적으로 사용될 resultSet을 데이터베이스에서 Query    */        
    public void initResultSet(String strQuery) 
    {
        try {
            // update 가능하면서, 다른 사용자의 데이터 변경을 감지        
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,  ResultSet.CONCUR_UPDATABLE);            
            rs = stmt.executeQuery(strQuery);
        } catch (SQLException se) {
            msgBox(se.toString(), "SQLException");
        }
    }

    /* User Interface 초기화       */
    private void initContent() {
       JPanel north = new JPanel();
       north.setLayout(null);
 
       label1.setBounds(10, 30, 100, 20);
       label2.setBounds(10, 60, 100, 20);
       label3.setBounds(10, 90, 100, 20);
       label4.setBounds(10, 120, 100, 20);       
       label5.setBounds(10, 150, 100, 20); 
       label6.setBounds(10, 180, 100, 20);
       label7.setBounds(10, 210, 100, 20);
       label8.setBounds(10, 240, 100, 20);
       label9.setBounds(10, 270, 100, 20);
       label10.setBounds(10, 300, 100, 20);       

       tf1.setBounds(120, 30, 150, 20);
       tf2.setBounds(120, 60, 150, 20);
       jcb1.setBounds(120, 90, 100, 20);
       jcb2.setBounds(120, 120, 100, 20);       
       tf3.setBounds(120, 150, 150, 20);
       tf4.setBounds(120, 180, 150, 20);
       tf5.setBounds(120, 210, 150, 20);
       tf6.setBounds(120, 240, 150, 20);
       tf7.setBounds(120, 270, 150, 20);
       tf8.setBounds(120, 300, 150, 20);

       north.add(label1);
       north.add(label2);
       north.add(label3);
       north.add(label4);
       north.add(label5);
       north.add(label6);
       north.add(label7);
       north.add(label8);
       north.add(label9);
       north.add(label10);

       for(int i=0; i<customer_dist.length; i++) {
           jcb1.addItem(customer_dist[i]);
 	   }
 	   
       for(int i=0; i<item_dist.length; i++) {
           jcb2.addItem(item_dist[i]);
 	   } 	   
     
       jcb2.disable();
     
       north.add(tf1);
       north.add(tf2);
       north.add(tf3);
       north.add(jcb1);
       north.add(jcb2);
       north.add(tf4);
       north.add(tf5);
       north.add(tf6);
       north.add(tf7);
       north.add(tf8);

       getContentPane().add("Center", north); 
    }

    /* 데이터베이스를 Navigate할수 있도록 앞/뒤,처음,끝으로 이동, 
       추가,삭제,저장등의 툴바 */
    private void initToolBar() {
        xToolBar = new JToolBar(JToolBar.HORIZONTAL);
        xToolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
        
        btnMoveFirst     = new JButton(new ImageIcon("./toolbar/first.gif"));
        btnMovePrev      = new JButton(new ImageIcon("./toolbar/prev.gif"));
        btnMoveNext      = new JButton(new ImageIcon("./toolbar/next.gif"));
        btnMoveLast      = new JButton(new ImageIcon("./toolbar/last.gif"));
        btnInsertItem    = new JButton(new ImageIcon("./toolbar/insert.gif"));
        btnDeleteItem    = new JButton(new ImageIcon("./toolbar/delete.gif"));        
        btnSaveItem      = new JButton(new ImageIcon("./toolbar/save.gif"));
        btnPrintItem      = new JButton(new ImageIcon("./toolbar/print.gif"));
        btnCloseWindow   = new JButton(new ImageIcon("./toolbar/exit.gif"));

        btnPrintItem.setEnabled(false);
        
        xToolBar.add(btnMoveFirst);
        xToolBar.add(btnMovePrev);
        xToolBar.add(btnMoveNext);
        xToolBar.add(btnMoveLast);
        xToolBar.addSeparator();
        xToolBar.add(btnInsertItem);
        xToolBar.add(btnDeleteItem);
        xToolBar.add(btnSaveItem);
        xToolBar.add(btnPrintItem);                
        xToolBar.add(Box.createHorizontalGlue());
        xToolBar.add(btnCloseWindow);

        btnMoveFirst.addActionListener(this);
        btnMovePrev.addActionListener(this);
        btnMoveNext.addActionListener(this);
        btnMoveLast.addActionListener(this);    
        btnInsertItem.addActionListener(this);
        btnDeleteItem.addActionListener(this);
        btnSaveItem.addActionListener(this);
        btnPrintItem.addActionListener(this);
        btnCloseWindow.addActionListener(this);
    }        

    /* 툴바의 버튼들의 clicked 이벤트에 따라 호출될 함수정의  */	
    public void actionPerformed(ActionEvent evt) {
        if(evt.getSource() == btnSaveItem) {
      		subSaveItem();
        } else if(evt.getSource() == btnInsertItem) {
            subInsertItem();
        } else if(evt.getSource() == btnDeleteItem) {
        	subDeleteItem();
        } else if(evt.getSource() == btnMoveFirst) {
            subMoveFirst();
        } else if(evt.getSource() == btnMovePrev) {
        	subMovePrev();
        } else if(evt.getSource() == btnMoveNext) {
        	subMoveNext();
        } else if(evt.getSource() == btnMoveLast) {
        	subMoveLast();
        } else if(evt.getSource() == btnPrintItem) {
            subPrintItem();
        } else if(evt.getSource() == btnCloseWindow) {
        	subCloseWindow();
        }
    }

    /* 사용자에게 알려야 할 메시지 처리  
       overload으로 매개변수에 따라 다른기능을 구현 */
    public void msgBox(String strMsg) {
        JOptionPane.showMessageDialog(this, strMsg, "알림", JOptionPane.INFORMATION_MESSAGE);
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
          tf1.setText("");      // 예금상품명
          tf2.setText("0");     // 예금한도
          tf3.setText("0");     // 기간1
          tf4.setText("0.0");   // 이율1
          tf5.setText("0");     // 기간2
          tf6.setText("0.0");   // 이율2
          tf7.setText("0");     // 기간3
          tf8.setText("0.0");   // 이율3
          jcb1.setSelectedIndex(0); // 고객구분
          jcb2.setSelectedIndex(0); // 상품구분
    }

    /* 화면의 textField들에 현재 cursor의 내용을 setting */
    public void setItems() {
        try {
            // cursor에서 클래스 변수로 자료 이동        
            strDkname  = rs.getString(1).trim();    // 예금상품명
            lDklimit = rs.getLong(2);             // 예금한도액
            strDkdist = rs.getString(3).trim();    // 상품구분 A0, L1
            strDkcdist = rs.getString(4).trim();    // 고객구분 00 개인, 11 기업
            iDkterm1 = rs.getInt(5);         		// 기간 1(개월)
            fDkrate1 = rs.getFloat(6);       		// 이율 1
            iDkterm2 = rs.getInt(7);         		// 기간 2(개월)
            fDkrate2 = rs.getFloat(8);       		// 이율 2
            iDkterm3 = rs.getInt(9);         		// 기간 3(개월)
            fDkrate3 = rs.getFloat(10);       		// 이율 3
                        
            lCurrentRow = rs.getRow();

            // 클래스 변수에서 화면 Interface로 자료 복사
            tf1.setText(strDkname);
            tf2.setText("" + lDklimit);
            tf3.setText("" + iDkterm1);
            tf4.setText("" + fDkrate1);
            tf5.setText("" + iDkterm2);
            tf6.setText("" + fDkrate2);
            tf7.setText("" + iDkterm3);
            tf8.setText("" + fDkrate3);
        
            if (strDkcdist.equals("00")) {
                jcb1.setSelectedIndex(0);
            } else if (strDkcdist.equals("11")) {
                jcb1.setSelectedIndex(1);
            }
          
            jcb2.setSelectedIndex(0);
           
        } catch(SQLException se) {
             msgBox(se, "setitems()");
        }
	}

    /* 화면의 textField들의 내용을 클래스 변수에  */
    public boolean getItems() {
    	int intIndex;
    	int intIndex2;

        strDkname   = tf1.getText().trim();    				// 예금상품명
        lDklimit    = Long.parseLong(tf2.getText().trim()); 	// 예금한도액
        iDkterm1    = Integer.parseInt(tf3.getText().trim());   	// 기간 1(개월)
        fDkrate1    = Float.parseFloat(tf4.getText().trim());   	// 이율 1
        iDkterm2    = Integer.parseInt(tf5.getText().trim());   	// 기간 2(개월)
        fDkrate2    = Float.parseFloat(tf6.getText().trim());   	// 이율 2
        iDkterm3    = Integer.parseInt(tf7.getText().trim());   	// 기간 3(개월)
        fDkrate3    = Float.parseFloat(tf8.getText().trim());   	// 이율 3

        intIndex = jcb1.getSelectedIndex();

        // 입력 textField의 값이 누락되었는 확인
        if (strDkname == null) { msgBox("예금상품명이 누락되었습니다."); return false; }
        if (iDkterm1 > 60 ) { msgBox("가능 기간은 60개월 미만입니다."); return false; }
        if (iDkterm2 > 60 ) { msgBox("가능 기간은 60개월 미만입니다."); return false; }
        if (iDkterm3 > 60 ) { msgBox("가능 기간은 60개월 미만입니다."); return false; }
                    
        // 고객구분
        switch(intIndex) {
            case 0 : strDkcdist = "00";
            case 1 : strDkcdist = "11";
        }
        
	   strDkdist = "A0";
        return true;
    }

    // cursor의 위치를 처음으로 이동
    public void subMoveFirst() {
        try {
            if(!rs.isFirst()) {
                rs.first();
                setItems();
            }
        } catch(SQLException se) {
             msgBox(se);
        }
    }

    // cursor의 위치를 현재에서 이전으로 이동
    public void subMovePrev() {
        try {
            if(!rs.isFirst()) {
                rs.previous();
                setItems();
            }
        } catch(SQLException se) {
             msgBox(se);
        }
    }

    // cursor의 위치를 현재에서 다음으로 이동
    public void subMoveNext() {
        try {
            if(!rs.isLast()) {
                rs.next();
                setItems();
            }
        } catch(SQLException se) {
             msgBox(se);
        }
    }

    // cursor의 위치를 마지막으로 이동
    public void subMoveLast() {
    	try {
	        if(!rs.isLast()) {
	            rs.last();
                setItems();
    	    }
        } catch(SQLException se) {
             msgBox(se);
        }
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

                    // cursor의 위치를 insert buffer로 이동
		            rs.moveToInsertRow();

                    // cursor의 내용을 클래스 변수로 setting
                    rs.updateString(1, strDkname.trim());
                    rs.updateLong(2, lDklimit);
                    rs.updateString(3, strDkdist.trim());
                    rs.updateString(4, strDkcdist.trim());
                    rs.updateInt(5, iDkterm1);
                    rs.updateFloat(6, fDkrate1);
                    rs.updateInt(7, iDkterm2);
                    rs.updateFloat(8, fDkrate2);
                    rs.updateInt(9, iDkterm3);
                    rs.updateFloat(10, fDkrate3);
                    
                    // setting된 insert buffer내용을 데이터베이스에 반영
                    rs.insertRow();

	                bInsertFlag = false;

                    lbStatusMessage.setText("insert completed!!");

                    // 데이터 베이스를 commit
                    conn.commit();
                    rs.moveToCurrentRow();
		        } catch (SQLException se) {
                    msgBox(se, "Database Insert Into error");
		        } //end try~catch
            }//end if(getItems())
        } else {
        // insert 버튼이 눌리지 않았다면
        	if(getItems()) {
	            try {
                    // 클래스 변수의 내용을 cursor에 반영
                    rs.updateString(1, strDkname.trim());
                    rs.updateLong(2, lDklimit);
                    rs.updateString(3, strDkdist.trim());
                    rs.updateString(4, strDkcdist.trim());
                    rs.updateInt(5, iDkterm1);
                    rs.updateFloat(6, fDkrate1);
                    rs.updateInt(7, iDkterm2);
                    rs.updateFloat(8, fDkrate2);
                    rs.updateInt(9, iDkterm3);
                    rs.updateFloat(10, fDkrate3);

                    // cursor의 변경사항을 데이터베이스에 반영
                    rs.updateRow();

                    lbStatusMessage.setText("update completed!!");                
                    //데이터베이스 commit
	                conn.commit();
		        } catch(SQLException se) {
                    msgBox(se, "입력자료 오류");
	                System.out.println("hi");
	            }	// end try~catch
            } //end if(getItems())
        } // end if(bInsertFlag == true)
    }	// end subSaveItem()
	
    // Insert버튼이 눌렸을 경우 처리
    public void subInsertItem() {
		clearItems();
        bInsertFlag = true;
    }

    // Delete버튼이 눌렸을 경우 처리
    public void subDeleteItem() {
		int rtnValue;

        //삭제 확인 메시지 창을 띄운다.
	    rtnValue = JOptionPane.showConfirmDialog(null, "현재 데이터를 삭제 합니다.", "확인", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

		try {
            // 삭제 확인 버튼이 눌린경우
            if (rtnValue == 0) {
                // 현재 커서의 위치에 있는 데이터를 삭제
                rs.deleteRow();

                // 다음행으로 이동                
                subMoveNext();
                                
                lbStatusMessage.setText("delete completed!!");
                // 데이터 베이스 commit
                conn.commit();
            }
        } catch (SQLException se) {
            msgBox(se, "subDelteItem()");
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
            stmt.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }

        setVisible(false);
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