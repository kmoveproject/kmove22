import javax.swing.JOptionPane;

public class MsgBox {
	private MsgBox(){}
	
	public static void alert(String str){
		JOptionPane.showMessageDialog(null, str);
	}
	
	public static int confirm(String str){
		int check = JOptionPane.showConfirmDialog(null, str, "»Æ¿Œ", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		return check;
	}
}