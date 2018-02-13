package Server;
import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import com.mysql.*;
class Guest extends Thread {

	String id;

	Server server;

	Socket socket;

	BufferedReader br;

	PrintWriter pw;

	Connection connection;

	Guest(Server server, Socket socket, Connection connection) throws Exception {

		this.server = server;

		this.socket = socket;

		this.connection = connection;
		
		InetAddress inetaddr = socket.getInetAddress();

        System.out.println(inetaddr.getHostAddress()+ " 로부터 접속했습니다.");
        
		InputStream is = socket.getInputStream();

		InputStreamReader isr = new InputStreamReader(is, "UTF-8");

		br = new BufferedReader(isr);

		OutputStream os = socket.getOutputStream();

		pw = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
	}



	public void run() {

		try {
			String line;
			while((line = br.readLine())!= null) {

				System.out.println(line+"읽음");

				String array[] = line.split("\\|");

				switch (array[0]) {

				case "signUP": // [1] : ID, [2] : PW
					if(ID_check(array[1], array[2]) == 1)
					{
						pw.println("CAN");
						pw.flush();
					}
					else
					{
						pw.println("CANNOT");
						pw.flush();
					}
					
					break;
					
					
				case "logIn": // [1] : ID , [2] : PW
					if(LOG_in(array[1], array[2]) == 1)
					{
						pw.println("CAN");
						pw.flush();
						this.id = array[1];  
					}
					else
					{
						pw.println("CANNOT");
						pw.flush();
					}
					
					break;
									

				case "msg": //[1] : 방제, [2] : 내용

					server.broadcastRoom(array[1], "msg|"+this.id+"|"+array[2]);

					break;	
					

				case "MakeRoom" : // [1] : 방제 , [2] : 비번

					if(server.MakeRoom(array[1],array[2], this) == 0)
					{
						pw.println("CANNOT");
						pw.flush();
					}
					else
					{						
						pw.println("CAN");
						
						pw.flush();
						
						server.makeGuestlist_Of_Room(array[1]);

					}
					
					break;

				
				case "JoinRoom" : //[1] : 방제 , [2] : 비번
					
					if(server.addGuest_ToRoom(array[1], array[2], this) == 1) // 그 방에 자기를 넣음
					{
						pw.println("CAN");
						pw.flush();
						server.makeGuestlist_Of_Room(array[1]); // 방에 참여한 접속자리스트 생성
					} 
					else
					{
						pw.println("CANNOT");
						pw.flush();
					}

					break;

					

				case "OutRoom" : // [1] : 방제
					
					server.removeGuest_FromRoom(array[1], this);
					
					server.removeRoom(array[1]);
					
					pw.println("Bye");
					
					pw.flush();
					
					break;

				}

			}
			pw.close();
			br.close();
			connection.close();
			socket.close();
			
		} catch (Exception e) {
			System.out.println(id+"가 접속을 종료함");
		}
	}

	private int ID_check(String input_ID, String input_PW)
	{
			int res = 0;
		   try 
		   {
				PreparedStatement pstmt = connection.prepareStatement("select * from member where ID = ?");
				pstmt.setString(1, input_ID);
				ResultSet rs = pstmt.executeQuery();
				if(rs.next())
					res = 0;
				
				else
				{
					pstmt = connection.prepareStatement("insert into member values(?, ?)");
					pstmt.setString(1, input_ID);
					pstmt.setString(2, input_PW);
					pstmt.execute();
					res = 1;
				}
				
				rs.close();
				pstmt.close();
				
		   } catch (SQLException e) {
			e.printStackTrace();
		   }
		   return res;
	  }
	   
	   private int LOG_in(String input_ID, String input_PW)
	   {
		   int res = 0;
		   try 
		   {
				PreparedStatement pstmt = connection.prepareStatement("select * from member where ID = ? and PW = ?");
				pstmt.setString(1, input_ID);
				pstmt.setString(2, input_PW);
				ResultSet rs = pstmt.executeQuery();
				if(rs.next())
					res = 1;
				else
					res = 0;
				
				rs.close();
				pstmt.close();
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		   return res;
	   }

	   public void sendMsg(String msg) throws Exception {
	
			pw.println(msg);
	
			pw.flush();
	
		}

}
