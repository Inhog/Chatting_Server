package Server;
import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import com.mysql.*;
 
public class Server {

	ArrayList<Guest> list;

	HashMap<String, ArrayList<Guest>> map;

	HashMap<String, String> room_N_P; // �� ���� : ��� 
	
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

			Guest g = new Guest(this, s, connection); //�� ������

			g.start();
		}
	}

	void removeRoom(String rn){ // �濡 ����� ������ �� ����
		if(map.get(rn).size()==0){
			map.remove(rn);
		}
	}

	void removeGuest_FromRoom(String rn , Guest guest) throws Exception{ // �濡�� ����� ���� ..  

		map.get(rn).remove(guest);
	
		broadcastRoom(rn, "OUT|"+guest.id);
		
		makeGuestlist_Of_Room(rn);
	}

	int addGuest_ToRoom(String rn, String pw, Guest guest) { // �� ����
		
		if(map.get(rn) != null && room_N_P.get(rn).equals(pw))
		{
			ArrayList<Guest> list2 = map.get(rn);

			list2.add(guest);

			System.out.print("����" + rn + " ,");

			System.out.println("����� :" + list2.size());
			return 1;
		}
		else
			return 0;
	}

	int MakeRoom(String roomname, String roompw, Guest guest) { // �� ����

		if(map.get(roomname) != null) //�̹� ���� ������ ���� �� ����.
			return 0;
		
		else
		{
			ArrayList<Guest> arraylist2 = new ArrayList<Guest>();
			arraylist2.add(guest);		
			map.put(roomname, arraylist2);
			room_N_P.put(roomname, roompw);		
			System.out.println("�����ȹ� :" + roomname);
			System.out.println("����ڼ� :" + arraylist2.size());
		}
		
		return 1;
		
	}

	void broadcastRoom(String rn, String msg) throws Exception { // �ش�� �޽��� �Ѹ���

		ArrayList<Guest> list2 = map.get(rn); // arrayList�� ��ϵǾ��ִ� �Խ�Ʈ��

		for (Guest g : list2) {

			g.sendMsg(msg); // �̸�-guestlistRoom/�̸�/�̸�2     �̸�2-guestlistRoom/�̸�/�̸�2

		}

	}

	void makeGuestlist_Of_Room(String rn) throws Exception { // �濡 ������ ����� ����Ʈ ����

		ArrayList<Guest> list2 = map.get(rn);

		StringBuffer buffer = new StringBuffer("GuestList"); //guestlistRoom/�̸�/�̸�2

		peoplelist=list2;

		for (Guest g : list2) {
			buffer.append("|" + g.id);
		}
		broadcastRoom(rn, buffer.toString()); // �ٹ� , guestlistRoom|�̸�|�̸�2
	}

	/*
	void addGuest(Guest g) {

		list.add(g);

		System.out.println("�����ڼ�:" + list.size());

	}
	
	void makeRoomlist() throws Exception {

		Set<String> roomlist = map.keySet();

		StringBuffer buffer = new StringBuffer("roomlist/");

		for (String t : roomlist) {

			buffer.append(t +"/"); /////����� ǥ���ؾ��Ҳ�������

		}

		broadcast(buffer.toString());

		Roomnumber(roomlist);

	}

	void Roomnumber(Set<String> roomlist) throws Exception{

		

		StringBuffer buffer2 = new StringBuffer("roomnum/"); //�濡 ����� 

		for(String t : roomlist){

			buffer2.append(map.get(t).size()+"/");

		}

		broadcast(buffer2.toString());

	}
	
	public void talkMsg(String talk, String talk2, String talk3) {

		// talk ������

		// 2 ������

		// 3 �Ҹ�

		for (Guest g : list) {

			if (g.id.equals(talk2)) {

				try {

					g.sendMsg("�ӼӸ�/" + talk + "&" + talk2 + "&" + talk3);

				} catch (Exception e) {

					System.out.println("�Խ�Ʈ���� �Ӹ������ٰ� ����" + e.getMessage());

				}

			}

		}
	}

	void removeGuest(Guest g) {

		list.remove(g);

		System.out.println("�����ڼ�:" + list.size()); //1��

	}

	void broadcast(String msg) throws Exception {

		try {

			for (Guest g : list) { //msg/[�̸�]�ȳ�Ƽ��� ����Դϴ�.

				g.sendMsg(msg);

			}

		} catch (Exception e) {

			System.out.println(e.getMessage());

		}

	}
	

	void makeGuestlist() throws Exception { // guestlist/ȫ�浿/��浿/�̱浿/
		
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