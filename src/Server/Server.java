package Server;
import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import com.mysql.*;
 
public class Server {

	ArrayList<Guest> list;

	HashMap<String, ArrayList<Guest>> map;

	HashMap<String, String> room_N_P; // 방 제목 : 비번 
	
	ArrayList<Guest> arraylist;

	ArrayList<Guest> peoplelist;

	Connection connection = null;
	
	void initNet() throws Exception {
		
		Class.forName("com.mysql.jdbc.Driver");
		
	 	connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/members" , "root", "920321");
	 	
		map = new HashMap<String, ArrayList<Guest>>();
		
		room_N_P = new HashMap<String, String>();
		
		arraylist = new ArrayList<Guest>();

		list = new ArrayList<Guest>();

		ServerSocket ss = new ServerSocket(10001);

		while (true) {
			Socket s = ss.accept();

			Guest g = new Guest(this, s, connection); //내 아이피

			g.start();
		}
	}

	void removeRoom(String rn){ // 방에 사람이 없으면 방 삭제
		if(map.get(rn).size()==0){
			map.remove(rn);
		}
	}

	void removeGuest_FromRoom(String rn , Guest guest) throws Exception{ // 방에서 사람이 나감 ..  

		map.get(rn).remove(guest);
	
		broadcastRoom(rn, "OUT|"+guest.id);
		
		makeGuestlist_Of_Room(rn);
	}

	int addGuest_ToRoom(String rn, String pw, Guest guest) { // 방 접속
		
		if(map.get(rn) != null && room_N_P.get(rn).equals(pw))
		{
			ArrayList<Guest> list2 = map.get(rn);

			list2.add(guest);

			System.out.print("방제" + rn + " ,");

			System.out.println("사람수 :" + list2.size());
			return 1;
		}
		else
			return 0;
	}

	int MakeRoom(String roomname, String roompw, Guest guest) { // 방 생성

		if(map.get(roomname) != null) //이미 방이 있으면 만들 수 없음.
			return 0;
		
		else
		{
			ArrayList<Guest> arraylist2 = new ArrayList<Guest>();
			arraylist2.add(guest);		
			map.put(roomname, arraylist2);
			room_N_P.put(roomname, roompw);		
			System.out.println("개설된방 :" + roomname);
			System.out.println("사용자수 :" + arraylist2.size());
		}
		
		return 1;
		
	}

	void broadcastRoom(String rn, String msg) throws Exception { // 해당방 메시지 뿌리기

		ArrayList<Guest> list2 = map.get(rn); // arrayList에 등록되어있는 게스트들

		for (Guest g : list2) {

			g.sendMsg(msg); // 이름-guestlistRoom/이름/이름2     이름2-guestlistRoom/이름/이름2

		}

	}

	void makeGuestlist_Of_Room(String rn) throws Exception { // 방에 접속한 사람들 리스트 전송

		ArrayList<Guest> list2 = map.get(rn);

		StringBuffer buffer = new StringBuffer("GuestList"); //guestlistRoom/이름/이름2

		peoplelist=list2;

		for (Guest g : list2) {
			buffer.append("|" + g.id);
		}
		broadcastRoom(rn, buffer.toString()); // 다방 , guestlistRoom|이름|이름2
	}

	/*
	void addGuest(Guest g) {

		list.add(g);

		System.out.println("접속자수:" + list.size());

	}
	
	void makeRoomlist() throws Exception {

		Set<String> roomlist = map.keySet();

		StringBuffer buffer = new StringBuffer("roomlist/");

		for (String t : roomlist) {

			buffer.append(t +"/"); /////사람수 표시해야할꺼같은데

		}

		broadcast(buffer.toString());

		Roomnumber(roomlist);

	}

	void Roomnumber(Set<String> roomlist) throws Exception{

		

		StringBuffer buffer2 = new StringBuffer("roomnum/"); //방에 사람수 

		for(String t : roomlist){

			buffer2.append(map.get(t).size()+"/");

		}

		broadcast(buffer2.toString());

	}
	
	public void talkMsg(String talk, String talk2, String talk3) {

		// talk 보낸놈

		// 2 받을놈

		// 3 할말

		for (Guest g : list) {

			if (g.id.equals(talk2)) {

				try {

					g.sendMsg("귓속말/" + talk + "&" + talk2 + "&" + talk3);

				} catch (Exception e) {

					System.out.println("게스트에서 귓말보내다가 에러" + e.getMessage());

				}

			}

		}
	}

	void removeGuest(Guest g) {

		list.remove(g);

		System.out.println("접속자수:" + list.size()); //1명

	}

	void broadcast(String msg) throws Exception {

		try {

			for (Guest g : list) { //msg/[이름]안녀아세요 사람입니다.

				g.sendMsg(msg);

			}

		} catch (Exception e) {

			System.out.println(e.getMessage());

		}

	}
	

	void makeGuestlist() throws Exception { // guestlist/홍길동/김길동/이길동/
		
		StringBuffer buffer = new StringBuffer("guestlist/"); //guestlist/

		for (Guest g : list) {

			buffer.append(g.id + "/");

		}

		broadcast(buffer.toString());

	}
	
	*/
   public static void main(String[] args) throws Exception{
	   Server server = new Server();
	  server.initNet();
   }
}