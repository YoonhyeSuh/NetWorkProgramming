import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;



public class Server extends JFrame implements ActionListener{
   
   static boolean server = false;
   
   
   private BufferedReader in = null;
   private BufferedWriter out = null;
   private BufferedReader cin = null;
   private BufferedWriter cout = null;
   private ServerSocket listener = null;
   private Socket socket = null;
   private ServerSocket glistener = null;
   private Socket gsocket = null;
   private Receiver receiver; // JTextArea를 상속받고 Runnable 인터페이스를 구현한 클래스로서 받은 정보를 담는 객체
   private gameReciever GR;
   private JTextField sender; // JTextField 객체로서 보내는 정보를 담는 객체
   private static int myscore =0;
   private JLabel yourScore;
   private Tetris game;
   
   
   private JPanel chat;
   
   public Server() {
      
      setTitle("Tetris :D");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //프레임 종료 버튼을 클릭하면 프로그램 종료
      Container c = getContentPane();   
      
      c.setLayout(new GridLayout(1,2));
      
      game = new Tetris();
      
      
      //테트리스 배치
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
   
      GR = new gameReciever();
      
      Thread th = new Thread(receiver); // 상대로부터 메시지 수신을 위한 스레드 생성
      Thread gR = new Thread(GR);
      th.start();
      gR.start();
      
      
   }
   
   private void setupConnection() throws IOException{
      listener = new ServerSocket(9999); //서버 소켓 생성
      glistener = new ServerSocket(9998); //서버 소켓 생성
      receiver.append("상대방을 기다리는 중입니다...\n");
      socket = listener.accept(); //클라이언트로부터 연결 요청 대기
      gsocket = glistener.accept(); //클라이언트로부터 연결 요청 대기
      receiver.append("상대방2이 입장하였습니다.\n"); 
      int pos = receiver.getText().length(); // 이부분도
      receiver.setCaretPosition(pos); //caret 포지션을 가장 마지막으로 이동
      
      in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //클라이언트로부터의 입력 스트림
      out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //클라이언트로의 출력 스트림
   
      
      cin = new BufferedReader(new InputStreamReader(gsocket.getInputStream())); //클라이언트로부터의 입력 스트림
      cout = new BufferedWriter(new OutputStreamWriter(gsocket.getOutputStream())); //클라이언트로의 출력 스트림
      

   }
   
   private static void handleError(String string) {
      System.out.println(string);
      System.exit(1);
   }
   
   private class Receiver extends JTextArea implements Runnable{
      public void run() {
            String msg = null;
            while (true) {
               try {
                  msg = in.readLine(); // 상대로부터 한 행의 문자열 받기
               } catch (IOException e) {
                  handleError(e.getMessage());
               } 
               this.append("\n상대방2 : " + msg); // 받은 문자열을 JTextArea에 출력
               int pos = this.getText().length();
               this.setCaretPosition(pos); // caret 포지션을 가장 마지막으로 이동
            }
         }   
   }
   //게임 스레드
   private class gameReciever implements Runnable{
      public void run() {
                  
         int score = -1;
         game.start();
         server = false;
         while (true) {
            
               try {
                  sendScoreToClient();
               } catch (IOException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
               
               
            try {               
               score =Integer.parseInt(cin.readLine());
               if(score == -1) {
//                  yourScore.setText("Opponent's Score: "+-(score));
               }
               else if(score >= 0) {            
                  String winner = "";
                  if(score > myscore) {
                     winner = "상대편";
                  }else if(score < myscore) {
                     winner = "당신";
                  }else {
                     winner= "동점";
                  }
                  if(!server) {
                  GameEnd(winner);
                  }
               
               }
               
            } catch (IOException e) {
               handleError(e.getMessage());
            }          
            
         }
      }
      }
   
   
   
    private void GameEnd(String winner) {
       server = true;
        if (winner.equals("동점")) {
            JOptionPane.showMessageDialog(null, "게임 종료!\n동점입니다!\n5초 뒤 다시 재시작 합니다.", "상대방1", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "게임 종료!\n" + winner + "이 이겼습니다!\n5초 뒤 다시 재시작 합니다.", "상대방1", JOptionPane.ERROR_MESSAGE);
        }

        try {
            sendScoreToClient(); // 서버에서 클라이언트에게 스코어 전송
            Thread.sleep(5000); // 5초 대기
        } catch (IOException | InterruptedException e) {
            handleError(e.getMessage());
        }

        // 게임 재시작 로직 추가
        game.restartGame();
    }

   

   @Override
      public void actionPerformed(ActionEvent e) {
         // TODO Auto-generated method stub
         if (e.getSource() == sender) {
            String msg = sender.getText(); // 텍스트 필드에서 문자열 얻어옴
            try {
               out.write(msg+"\n"); // 문자열 전송
               out.flush();
               
               receiver.append("\n상대방1 : " + msg);// JTextArea에 출력
               int pos = receiver.getText().length();
               receiver.setCaretPosition(pos); // caret 포지션을 가장 마지막으로 이동
               sender.setText(null); // 입력창의 문자열 지움
            } catch (IOException e1) {
               handleError(e1.getMessage());
            } 
         }
         
      }
    
    public void sendScoreToClient() throws IOException {
       
       myscore = Tetris.Currentscore;
       if(Tetris.gameEnd) {
          cout.write(myscore + "\n");
          cout.flush();
       }else {
          cout.write("-1\n");
          cout.flush();
       }
   }

   

   public static void main(String[] args) {
      new Server();
   }
   
}