import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;



public class Server extends JFrame implements ActionListener{
	
	static boolean server = false;//GameEnd를 한번만 호출 하기 위한 변수
	
	//버퍼
	private BufferedReader in = null;
	private BufferedWriter out = null;
	private BufferedReader cin = null;
	private BufferedWriter cout = null;
	private BufferedReader sin = null;
	private BufferedWriter sout = null;
	
	//서버 소켓
	private ServerSocket listener = null;//메세지용
	private ServerSocket glistener = null;//최종점수용
	private ServerSocket slistener = null;//현재 점수용
	
	//소켓
	private Socket socket = null;	
	private Socket gsocket = null;
	private Socket ssocket = null;
	
	//메세지용
	private Receiver receiver; // JTextArea를 상속받고 Runnable 인터페이스를 구현한 클래스로서 받은 정보를 담는 객체
	private JTextField sender; // JTextField 객체로서 보내는 정보를 담는 객체
	private JPanel chat;//chat을 담을 JPanel
	
	//테트리스용
	private gameReciever GR;	
	private static int myscore =0;//서버 내에서만 쓸 내 점수 변수
	private JLabel yourScore;
	private Tetris game;
	public static int choiceS= 1;
	
	
	
	
	
	public Server() {
		
		setTitle("Tetris :D");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //프레임 종료 버튼을 클릭하면 프로그램 종료
		Container c = getContentPane();	
		
		c.setLayout(new GridLayout(1,2));
		
		//테트리스 배치
		game = new Tetris();		
		GR = new gameReciever();

		c.add(game.left);
		
		//채팅 배치
		chat = new JPanel();
		chat.setLayout(new BorderLayout());
		
		yourScore = new JLabel();
		yourScore.setText("Opponent's Score: 0");
		
		
		receiver = new Receiver(); // 클라이언트에서 받은 메시지를 출력할 컴퍼넌트
		receiver.setEditable(false); // 편집 불가

		sender = new JTextField();
		sender.addActionListener(this);

		chat.add(yourScore,BorderLayout.NORTH);
		chat.add(new JScrollPane(receiver),BorderLayout.CENTER); // 스크롤바를 위해  ScrollPane 이용
		chat.add(sender,BorderLayout.SOUTH);
		
		c.add(chat);
	
		
		setSize(500, 800); // 프레임 크기 조정
		setVisible(true); // 프레임이 화면에 나타나도록 설정
		
		try {
			setupConnection();
		} catch (IOException e) {
			handleError(e.getMessage());
		}
		
		Thread th = new Thread(receiver); // 상대로부터 메시지 수신을 위한 스레드 생성
		Thread gR = new Thread(GR);
		th.start();
		gR.start();
		
		
	}
	
	private void setupConnection() throws IOException{
		listener = new ServerSocket(9999); //메세지용 서버 소켓 생성
		glistener = new ServerSocket(9998); //최종 점수용 서버 소켓 생성
		slistener = new ServerSocket(9997); //현재 점수용 서버 소켓 생성
		
		receiver.append("상대방을 기다리는 중입니다...\n");
		
		socket = listener.accept(); //클라이언트로부터 연결 요청 대기
		gsocket = glistener.accept(); //최종 점수용
		ssocket = slistener.accept(); //현재 점수용
		
		receiver.append("상대방2이 입장하였습니다.\n"); //연결 완료
		
		int pos = receiver.getText().length(); 
		receiver.setCaretPosition(pos); //caret 포지션을 가장 마지막으로 이동
		
		//메세지용
		in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //클라이언트로부터의 입력 스트림
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //클라이언트로의 출력 스트림
		//최종 점수용
		cin = new BufferedReader(new InputStreamReader(gsocket.getInputStream())); //클라이언트로부터의 입력 스트림
		cout = new BufferedWriter(new OutputStreamWriter(gsocket.getOutputStream())); //클라이언트로의 출력 스트림
		//현재 점수용
		sin = new BufferedReader(new InputStreamReader(ssocket.getInputStream())); //클라이언트로부터의 입력 스트림
		sout = new BufferedWriter(new OutputStreamWriter(ssocket.getOutputStream())); //클라이언트로의 출력 스트림

	}
	
	private static void handleError(String string) {
		System.out.println(string);
		System.exit(1);
	}
	//메세지
	private class Receiver extends JTextArea implements Runnable{
		public void run() {
			String msg = null;
			while (true) {
				try {
					msg = in.readLine(); // 상대로부터 한 행의 문자열 받기
				} catch (IOException e) {
					handleError(e.getMessage());
				} 
				this.append("\n  상대방2 : " + msg); // 받은 문자열을 JTextArea에 출력
				int pos = this.getText().length();
				this.setCaretPosition(pos); // caret 포지션을 가장 마지막으로 이동
			}
		}
		
	}
	 @Override
	   public void actionPerformed(ActionEvent e) {
	      // TODO Auto-generated method stub
	      if (e.getSource() == sender) {
	         String msg = sender.getText(); // 텍스트 필드에서 문자열 얻어옴
	         try {
	            out.write(msg+"\n"); // 문자열 전송
	            out.flush();
	            
	            receiver.append("\n서버 : " + msg);// JTextArea에 출력
	            int pos = receiver.getText().length();
	            receiver.setCaretPosition(pos); // caret 포지션을 가장 마지막으로 이동
	            sender.setText(null); // 입력창의 문자열 지움
	         } catch (IOException e1) {
	            handleError(e1.getMessage());
	         } 
	      }
	      
	   }
	//게임 스레드
	private class gameReciever implements Runnable{
		public void run() {
			int score = -1;
			int currentScore = 0;
			game.start();// 게임 시작
			server = false;
			while (true) {
				
					try {
						sendScoreToClient();//테트리스쪽에서 보내는 상대편 점수를 지속적으로 확인하는 함수
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				try {					
					score =Integer.parseInt(cin.readLine());//받고 int형으로 타입 변환
					currentScore =Integer.parseInt(sin.readLine());//받고 int형으로 타입 변환
					yourScore.setText("Opponent's Score: "+ currentScore);
					if(score == -1) {//-1이면 아무것도 안함
//						
					}
					else {//점수가 있으면 내 점수와 비교해서 JOptionPane 출력
						String winner = "";
						int cha = 0;
						if(score > myscore) {
							winner = "상대편";
							cha=score-myscore;
						}else if(score < myscore) {
							winner = "당신";
							cha = myscore - score;
						}else {
							winner= "동점";
						}
						if(!server) {
						GameEnd(winner, cha);
						}
					
					}
					
				} catch (IOException e) {
					handleError(e.getMessage());
				} 		
			}
				}}
	
	
	// 값을 받아서 알맞은 JOptionPane 호출하는 함수
	 private void GameEnd(String winner, int cha) {
		 server = true;
	        if(winner == "동점") {
	        	choiceS = JOptionPane.showConfirmDialog(null, "게임 오버!\n 동점입니다\n 재시작 하시겠습니까?", "user1", JOptionPane.YES_NO_OPTION);

	        } else {
		        
		        choiceS = JOptionPane.showConfirmDialog(null, "게임 오버!\n"+cha+"차로 "+winner+"이 이겼습니다!", "user1", JOptionPane.YES_NO_OPTION);
	        }

	        
	 }

	 ////테트리스쪽에서 보내는 상대편 점수를 지속적으로 확인하는 함수
	 public void sendScoreToClient() throws IOException {
		 
		 myscore = Tetris.Currentscore;
		 if(Tetris.gameEnd) {//테트리스 게임이 끝나면 
		 	cout.write(myscore + "\n");//최종 점수를 보내라
		    cout.flush();
		 }else {
			 cout.write("-1\n");//게임이 진행중이면 -1를 보내라
			 cout.flush();
		 }	 
		 sout.write(myscore + "\n");//걍 보내라
		 sout.flush();
	}

	

	public static void main(String[] args) {
		new Server();
	}
	
}

