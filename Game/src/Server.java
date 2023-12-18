import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Server extends JFrame implements ActionListener{
	private BufferedReader in = null;
	private BufferedWriter out = null;
	private ServerSocket listener = null;
	private Socket socket = null;
	private Receiver receiver;
	private JTextField sender;
	
	public Server() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //프레임 종료 버튼을 클릭하면 프로그램 종료
		Container c = getContentPane();
		
		
	}
	
	private void setupConnection() throws IOException{
		listener = new ServerSocket(9999); //서버 소켓 생성
		socket = listener.accept(); //클라이언트로부터 연결 요청 대기
		receiver.append("클라이언트로부터 연결 완료"); //이부분 아마 바꿔야할듯
		int pos = receiver.getText().length(); // 이부분도
		receiver.setCaretPosition(pos); //caret 포지션을 가장 마지막으로 이동
		
		in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //클라이언트로부터의 입력 스트림
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //클라이언트로의 출력 스트림
	}
	
	private static void handleError(String string) {
		System.out.println(string);
		System.exit(1);
	}
	
	private class Receiver extends JTextArea implements Runnable{
		public void run() {
			
		}
	}
	
}
