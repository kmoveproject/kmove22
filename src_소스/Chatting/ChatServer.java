import java.net.*;
import java.io.*;
import java.util.*;

public class ChatServer
{
  Vector clientVector= new Vector();
  int clientNum= 0;

  public void broadcast(String msg) throws IOException
  {
    synchronized(clientVector){
      for(int i=0; i<clientVector.size(); i++){   //접속한 클라이언트수만큼
    	ChatThread client= (ChatThread) clientVector.elementAt(i); 
    	   //모든 클라이언트에게 메시지 보냄
        synchronized(client){
          client.sendMessage(msg);
        }
      }
    }
  }

  public void removeClient(ChatThread client)
  {
    synchronized(clientVector){
      clientVector.removeElement(client);
      client= null;
      System.gc();  //가비지 콜렉션
    }
  }

  public void addClient(ChatThread client)
  {
    synchronized(clientVector){
      clientVector.addElement(client);
    }
  }

  public static void main(String[] args)
  {
    ServerSocket myServerSocket= null;

    ChatServer myServer= new ChatServer();        //1)현재 ChatServer자체 생성하여 --> myServer객체명으로 사용

    try{
      myServerSocket= new ServerSocket(2587);    //2)ServerSocket(2587)로 생성하여 -->myServerSocket객체명으로 사용
    }catch(IOException e){
      System.out.println(e.toString());
      System.exit(-1);
    }
    
    System.out.println("[서버 대기 상태] "+ myServerSocket);  
       //3)콘솔에 [서버 대기 상태] ServerSocket[addr=0.0.0.0/0.0.0.0,port=0,localport=2587] 
    
//--------------------------------------------------------------
    try{
      while(true){   //서버는 무한루프 돌기
        ChatThread client= new ChatThread(myServer, myServerSocket.accept()); //★
        client.start();  //ClatTread클래스의 run()메소드 실행함      //★
        myServer.addClient(client);  //내자신의 addClient 메소드 실행 //★

        myServer.clientNum++;  //내자신의 clientNum 1씩증가 //★
        System.out.println("[현재 접속자수] "+ myServer.clientNum +"명");
      }
    }catch(IOException e){
      System.out.println(e.toString());
    }
  }
}
