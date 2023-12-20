import java.awt.*;


import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;



public class Client extends JFrame implements ActionListener {
   
   static boolean client = false;

   //버퍼
   private BufferedReader in = null;
   private  BufferedWriter out = null;
   private BufferedReader sin = null;
   private BufferedWriter sout = null;
   private BufferedReader cin = null;
   private BufferedWriter cout = null;
   //소켓
   private Socket socket = null;
   private Socket gsocket = null;
   private Socket usocket = null;
   //메세지용
   private JPanel chat;
   private Receiver receiver = null; // JTextArea를 상속받고 Runnable 인터페이스를 구현한 클래스로서 받은 정보를 담는 객체
   private JTextField sender = null; // JTextField 객체로서 보내는 정보를 담는 객체
   private gameReciever GR;
   //게임용
   private static int myscore =0;//이클래스만 쓰는 나의 점수
   private JLabel yourScore;//너의 점수를 담을 레이블
   private Tetris game;
   private int cha;//점수 차
   private int Uscore;//너의 점수
  
   
   public Client() {
      setTitle("Tetris :D");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //프레임 종료 버튼을 클릭하면 프로그램 종료
      Container c = getContentPane();   
      
      c.setLayout(new GridLayout(1,2));
      
     
      
      //테트리스 배치
      game = new Tetris();
      GR = new gameReciever();
      c.add(game.leftPanel);
      
      //채팅 배치
      chat = new JPanel();
      chat.setLayout(new BorderLayout());
      receiver = new Receiver(); // 클라이언트에서 받은 메시지를 출력할 컴퍼넌트
      receiver.setEditable(false); // 편집 불가
      
      yourScore = new JLabel();
      yourScore.setText("Opponent's Score: 0");

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
   
   private void setupConnection() throws IOException {
      socket = new Socket("localhost", 9999); // 메세지용 클라이언트 소켓 생성
      gsocket = new Socket("localhost", 9998); // 게임용 클라이언트 소켓 생성
      usocket = new Socket("localhost", 9997); // 게임용 클라이언트 소켓 생성

      receiver.append("상대방1이 입장하였습니다.\n");//연결됨
      
      int pos = receiver.getText().length();
      receiver.setCaretPosition(pos); // caret 포지션을 가장 마지막으로 이동
      
      in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 메세지용
      out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));// 메세지용
      
      sin = new BufferedReader(new InputStreamReader(usocket.getInputStream())); //최종점수용
      sout = new BufferedWriter(new OutputStreamWriter(usocket.getOutputStream())); //클라이언트로의 출력 스트림
      
      cin = new BufferedReader(new InputStreamReader(gsocket.getInputStream())); //최종점수용
      cout = new BufferedWriter(new OutputStreamWriter(gsocket.getOutputStream())); //최종점수용

      
   }
   private static void handleError(String string) {
      System.out.println(string);
      System.exit(1);
   }
   //메세지 스레드
   private class Receiver extends JTextArea implements Runnable {
      @Override
      public void run() {
         String msg = null;
         while (true) {
            try {
 
               msg = in.readLine(); // 상대로부터 한 행의 문자열 받기
            } catch (IOException e) {
               handleError(e.getMessage());
            } 
            this.append("\n상대방1 : " + msg); // 받은 문자열을 JTextArea에 출력
            int pos = this.getText().length();
            this.setCaretPosition(pos); // caret(커서)을 가장 마지막으로 이동
         }
      }
   }
   @Override
   public void actionPerformed(ActionEvent e) { // JTextField에 <Enter> 키 처리
      if (e.getSource() == sender) {
         String msg = sender.getText(); // 텍스트 필드에 사용자가 입력한 문자열
         try {
            out.write(msg+"\n"); // 문자열 전송
            out.flush();
            
            receiver.append("\n상대방2 : " + msg); // JTextArea에 출력
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
            game.start();         
            int score = -1;
            while (true) {
               
                  try {
                     sendScoreToServer();
                  } catch (IOException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }                  
                  
               try {               
                  score =Integer.parseInt(cin.readLine());
                  Uscore =Integer.parseInt(sin.readLine());
                  yourScore.setText("Opponent's Score: "+Uscore);
                  System.out.println(score);
                  if(score == -1) {
//                     
                  }
                  else{
                     String winner = "";
                     cha = 0;
                     if(score > myscore) {
                        winner = "상대편";
                        cha = score - myscore;
                     }else if(score < myscore) {
                        winner = "당신";
                        cha = myscore - score;
                     }else {
                        winner= "동점";
                     }
                     if(!client) {
                     GameEnd(winner, cha);
                     }
                  }
                  
               } catch (IOException e) {
                  handleError(e.getMessage());
               }             
         
         }      
   }
   }
   
    private void GameEnd(String winner, int cha) throws IOException {
    	client = true;
        if (winner.equals("동점")) {
            //JOptionPane.showMessageDialog(null, "게임 종료!\n동점입니다!\n5초 뒤 다시 재시작 합니다.", "user2", JOptionPane.ERROR_MESSAGE);
        	JOptionPane.showMessageDialog(null, "게임 종료!\n동점입니다!\n", "user2", JOptionPane.ERROR_MESSAGE);
        } else {
//            JOptionPane.showMessageDialog(null, "게임 종료!\n" +cha+"점차로 "+ winner + "이 이겼습니다!\n5초 뒤 다시 재시작 합니다.", "user2", JOptionPane.ERROR_MESSAGE);
        	JOptionPane.showMessageDialog(null, "게임 종료!\n" +cha+"점차로 "+ winner + "이 이겼습니다!\n", "user2", JOptionPane.ERROR_MESSAGE);
        }

//        try {
//        	sendScoreToServer();
//            Thread.sleep(5000); // 5초 대기
//        } catch (InterruptedException e) {
//            handleError(e.getMessage());
//        }
//
//        // 게임 재시작 로직 추가
//        game.restartGame();
    }
   
  //테트리스 전역변수가 바뀌면 바뀐 값을 계속 상대방한테 보냄 
   public void sendScoreToServer() throws IOException {
      
      if(Tetris.gameEnd) {
          myscore = Tetris.Currentscore;//테트리스 게임이 끝나면
          cout.write(myscore + "\n");//최종 점수를 보내라
          cout.flush();
       }else {
          cout.write("-1"+ "\n");
             cout.flush();
       }
      sout.write(myscore+"\n");
      sout.flush();
   }
   public static void main(String[] args) {
      new Client();
   }
      
  }
