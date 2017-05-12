package networking;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import sage.networking.server.GameConnectionServer;
import sage.networking.server.IClientInfo;

public class GameServerTCP extends GameConnectionServer<UUID>{
	private int clientNum;
	
	public static void main(String[] args) {
		try {
			//Integer.parseInt(args[1])
			//having trouble here giving cmd line arguments
		GameServerTCP testTCPServer = new GameServerTCP(Integer.parseInt(args[0]));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	public GameServerTCP(int localPort) throws IOException {
		super(localPort, ProtocolType.TCP);
		InetAddress ip = InetAddress.getLocalHost();
		String hostname = ip.getHostName();
        System.out.println("Server started on ip:   " + ip.getHostAddress());
		System.out.println("Server started on port: " + localPort);
		clientNum = 0;
	}
	
	public void acceptClient(IClientInfo ci, Object o) {
		String message = (String) o;
		String[] messageTokens = message.split(",");
		System.out.println("Accepted Client");
		if (messageTokens.length > 0) {
			if (messageTokens[0].compareTo("join") == 0) {
				//format: join, localid
				UUID clientID = UUID.fromString(messageTokens[1]);
				addClient(ci, clientID);
				String message1 = new String("join,success");
				try {
					sendPacket(message1, clientID);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				String message1 = new String("join,failure");
			}
		}
	}
	
	public void processPacket(Object o, InetAddress senderIP, int sndPort) {
		String message = (String) o;
		String[] msgTokens = message.split(",");
		
		if (msgTokens.length > 0) {
			//create message
			if (msgTokens[0].compareTo("create") == 0) {
				//format: create,localid,x,y,z
				UUID Sender = UUID.fromString(msgTokens[1]);
				String[] pos = {msgTokens[2],msgTokens[3], msgTokens[4]};
				float bottomRotation = Float.parseFloat(msgTokens[5]);
				float topRotation = Float.parseFloat(msgTokens[6]);
				sendCreateMessages(Sender, pos, topRotation, bottomRotation);
				sendWantsDetailsMessages(Sender);
			}
			//bye message
			if (msgTokens[0].compareTo("bye") == 0) {
				//format: bye,localid
				UUID Sender = UUID.fromString(msgTokens[1]);
				sendByeMessages(Sender);
				removeClient(Sender);
				System.out.println("Client Left: " + Sender);
			}
			
			//detailsfor message
			if (msgTokens[0].compareTo("dsfr") == 0) {
				//format: dsfr,localid,remoteid,x,y,z
				UUID Sender = UUID.fromString(msgTokens[1]);
				UUID remoteID = UUID.fromString(msgTokens[2]);
				//pos[x,y,z]
				String[] pos = new String[3];
				pos[0] = msgTokens[3];
				pos[1] = msgTokens[4];
				pos[2] = msgTokens[5];
				float bottomRotation = Float.parseFloat(msgTokens[6]);
				float topRotation = Float.parseFloat(msgTokens[7]);
				sndDetailsMsg(Sender, remoteID, pos, topRotation, bottomRotation);
			}
			
			//move message
			if (msgTokens[0].compareTo("move") == 0) {
				//format: move,clientID,x,y,z
				UUID clientID = UUID.fromString(msgTokens[1]);
				//pos[x,y,z]
				String[] pos = new String[3];
				pos[0] = msgTokens[2];
				pos[1] = msgTokens[3];
				pos[2] = msgTokens[4];
				float bottomRotation = Float.parseFloat(msgTokens[5]);
				float topRotation = Float.parseFloat(msgTokens[6]);
				sendMoveMessages(clientID, pos, topRotation, bottomRotation);
			}
			
			//rotate message
			if (msgTokens[0].compareTo("rotate") == 0) {
				//format: rotate,clientID,x,y,z
				UUID clientID = UUID.fromString(msgTokens[1]);
				//pos[x,y,z]
				String[] pos = new String[3];
				pos[0] = msgTokens[2];
				pos[1] = msgTokens[3];
				pos[2] = msgTokens[4];
				float bottomRotation = Float.parseFloat(msgTokens[5]);
				float topRotation = Float.parseFloat(msgTokens[6]);
				sendRotateMessages(clientID, pos, topRotation, bottomRotation);
			}
			
			//wantsDetails message
			if (msgTokens[0].compareTo("wantsDetails") == 0) {
				//format: wantsDetails,clientID
				UUID clientID = UUID.fromString(msgTokens[1]);
				sendWantsDetailsMessages(clientID);
			}
			
			//Shoot
			if (msgTokens[0].compareTo("shoot") == 0) {
				//format: shoot,clientID
				UUID clientID = UUID.fromString(msgTokens[1]);
				String[] view = new String[3];
				view[0] = msgTokens[2];
				view[1] = msgTokens[3];
				view[2] = msgTokens[4];
				sendShootMessages(clientID, view);
			}
			
			if (msgTokens[0].compareTo("gotShot") == 0) {
				//format: gotShot, Sender, remoteID
				UUID Sender = UUID.fromString(msgTokens[1]);
				UUID remoteID = UUID.fromString(msgTokens[2]);
				sendGotShotMessage(Sender, remoteID);
			}
		}
	}
	
	private void sendByeMessages(UUID clientID) {
		try {
			//format: bye,clientID
			String message = new String("bye," + clientID.toString());
			clientNum--;
			forwardPacketToAll(message, clientID);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendWantsDetailsMessages(UUID clientID){
		//format: wantsDetails,clientID
		try {
			String message = new String("wantsDetails," + clientID.toString());
			forwardPacketToAll(message, clientID);
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void sendCreateMessages(UUID clientID, String[] position, float topRotation, float botRotation) {
		//format: create,remoteID,x,y,z
		try {
			String message = new String("create," + clientID.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			message += "," + topRotation;
			message += "," + botRotation;
			forwardPacketToAll(message, clientID);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sndDetailsMsg(UUID clientID, UUID remoteID, String[] position, float topRotation, float botRotation) {
		//format: dsfr,clientID,x,y,z
		//clientID is FROM, remoteId is TO
		try {
			String message = new String("dsfr," + clientID.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			message += "," + topRotation;
			message += "," + botRotation;
			sendPacket(message, remoteID);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMoveMessages(UUID clientID, String[] position, float topRotation, float botRotation) {
		//format: move,clientID,x,y,z
		try {
			String message = new String("move," + clientID.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			message += "," + topRotation;
			message += "," + botRotation;
			forwardPacketToAll(message, clientID);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendRotateMessages(UUID clientID, String[] position, float topRotation, float botRotation) {
		//format: rotate, clientID, u,v,n
		try {
			String message = new String("rotate," + clientID.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			message += "," + topRotation;
			message += "," + botRotation;
			forwardPacketToAll(message, clientID);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendShootMessages(UUID clientID, String[] view){
		try {
			String message = new String("shoot," + clientID.toString());
			message += "," + view[0];
			message += "," + view[1];
			message += "," + view[2];
			forwardPacketToAll(message, clientID);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendGotShotMessage(UUID clientID, UUID remoteID){
		try{
			String message = new String("gotShot," + clientID.toString());
			sendPacket(message, remoteID);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
