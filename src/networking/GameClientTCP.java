package networking;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import java.util.Vector;

import a1.MyGame;
import graphicslib3D.Vector3D;
import myGameEngine.GhostAvatar;
import sage.networking.client.GameConnectionClient;
import sage.networking.server.GameConnectionServer;

public class GameClientTCP extends GameConnectionClient{
	
	private MyGame game;
	private UUID id;
	private Vector<GhostAvatar> ghostAvatars;
	
	public GameClientTCP(InetAddress remAddr, int remPort, ProtocolType pType, MyGame game) throws IOException{ 
		super(remAddr, remPort, pType);
		this.game = game;
		this.id = UUID.randomUUID();
		this.ghostAvatars = new Vector<GhostAvatar>();		 
	}
	
	protected void processPacket (Object msg) {// override 
		String message = (String) msg;
		//System.out.println(msg);
		if(msg != null){
			String[] msgTokens = message.split(",");
			 // extract incoming message into substrings. Then process:
			 
			 if(msgTokens[0].compareTo("join") == 0){ // receive “join”
			  // format: join, success or join, failure
				 if(msgTokens[1].compareTo("success") == 0){ 
					 //game.setPlayerName(msgTokens[2]);
					 game.setIsConnected(true);
					 sendCreateMessage(game.getPlayerPosition());
				 }
				 if(msgTokens[1].compareTo("failure") == 0){
					 game.setIsConnected(false);
				 }
			 }
			 if(msgTokens[0].compareTo("bye") == 0){ // receive “bye”
			 // format: bye, remoteId
				 UUID ghostID = UUID.fromString(msgTokens[1]);
				 removeGhostAvatar(ghostID);
			 }
			 if (msgTokens[0].compareTo("dsfr") == 0 ) { // receive “details for”
				 // format: create, remoteId, x,y,z or dsfr, remoteId, x,y,z
				 UUID ghostID = UUID.fromString(msgTokens[1]);
				 // extract ghost x,y,z, position from message, then:
				 Vector3D ghostPosition = new Vector3D(Float.parseFloat(msgTokens[2]),Float.parseFloat(msgTokens[3]),Float.parseFloat(msgTokens[4]));
				 float bottomRotation = Float.parseFloat(msgTokens[5]);
				 float topRotation = Float.parseFloat(msgTokens[6]);
				 createGhostAvatar(ghostID, ghostPosition, topRotation, bottomRotation);
			 }
			 if(msgTokens[0].compareTo("create") == 0) {	// receive “create…”
			 // etc….. 
				 UUID clientID = UUID.fromString(msgTokens[1]);
				 Vector3D ghostPosition = new Vector3D(Float.parseFloat(msgTokens[2]),Float.parseFloat(msgTokens[3]),Float.parseFloat(msgTokens[4]));
				 float bottomRotation = Float.parseFloat(msgTokens[5]);
				 float topRotation = Float.parseFloat(msgTokens[6]);
				 createGhostAvatar(clientID, ghostPosition, topRotation, bottomRotation);
			 }
			 if(msgTokens[0].compareTo("wantsDetails") == 0) {	// receive “wants…”
			 // etc…..
				 UUID remoteID = UUID.fromString(msgTokens[1]);
				try {
					sendDetailsForMessage(remoteID, game.getPlayerPosition());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
			 }
			 if(msgTokens[0].compareTo("move") == 0) {	// receive “move”
			 // etc….. 
				 UUID clientID = UUID.fromString(msgTokens[1]);
				 Vector3D ghostPosition = new Vector3D(Float.parseFloat(msgTokens[2]),Float.parseFloat(msgTokens[3]),Float.parseFloat(msgTokens[4]));
				 float bottomRotation = Float.parseFloat(msgTokens[5]);
				 float topRotation = Float.parseFloat(msgTokens[6]);
				 game.moveGhostPlayer(clientID.toString(), ghostPosition, topRotation, bottomRotation);
			 }
			 
			//Shoot
			if (msgTokens[0].compareTo("shoot") == 0) {
				//format: shoot, clientID
				UUID clientID = UUID.fromString(msgTokens[1]);
				float[] direction= {Float.parseFloat(msgTokens[2]),Float.parseFloat(msgTokens[3]),Float.parseFloat(msgTokens[4])};
				game.shootGhostPlayer(clientID.toString(),direction);
			}
			
			//gotShot
			if(msgTokens[0].compareTo("gotShot") == 0) {
				UUID clientID = UUID.fromString(msgTokens[1]);
				game.gotShot();
			}
		}
	 }
	 
	 private void createGhostAvatar(UUID ghostID, Vector3D ghostPosition, float top, float bot) {
		// TODO Auto-generated method stub
		 removeGhostAvatar(ghostID);
		 ghostAvatars.addElement(new GhostAvatar(ghostID, ghostPosition));
		 //System.out.println("Pos = "+ ghostPosition.toString());
		 game.addGhostPlayer(ghostID.toString(), ghostPosition, top, bot);
	}

	private void removeGhostAvatar(UUID ghostID) {
		// TODO Auto-generated method stub
		 for(int i =0; i < ghostAvatars.size(); i++){
			 if(i < ghostAvatars.size() && ghostAvatars.get(i).getId().equals(ghostID.toString())){
				 game.removeGhostPlayer(ghostAvatars.get(i).getId().toString());
				 ghostAvatars.remove(i);
				 //i--;
			 }
		 }
	}

	public void sendCreateMessage(Vector3D pos) { 
		 // format: (create, localId, x,y,z)
		 try{ 
			 String message = new String("create," + id.toString());
			 message += "," + pos.getX()+"," + pos.getY() + "," + pos.getZ();
			 message += "," + game.getBotRot();
			 message += "," + game.getTopRot();
			 sendPacket(message);
		 }
		  catch (IOException e) { 
			  e.printStackTrace(); 
		  }
	 }
	 
	 public void sendJoinMessage(){ 
		 // format: join, localId
		try{ 
			sendPacket(new String("join," + id.toString()));
		}catch (IOException e) { 
			e.printStackTrace(); 
		}
	 }
	 
	 public void sendByeMessage() throws IOException { 
		 // etc….. 
		 sendPacket(new String("bye," + id.toString()));
	 }
	 
	 public void sendDetailsForMessage(UUID remId, Vector3D pos) throws IOException{ 
		 // etc…..
		 String message = new String("dsfr," + id.toString() + "," +remId.toString());
		 
		 message += "," + pos.getX()+"," + pos.getY() + "," + pos.getZ();
		 message += "," + game.getBotRot();
		 message += "," + game.getTopRot();
		 sendPacket(message);
	 }
	 
	 public void sendMoveMessage(Vector3D pos) throws IOException{
		 // etc….. 
		 String message = new String("move," + id.toString());
		 message += "," + pos.getX()+"," + pos.getY() + "," + pos.getZ();
		 message += "," + game.getTopRot();
		 message += "," + game.getBotRot();
		 sendPacket(message);
		 //sendPacket(new String("move," + id.toString()+","+ pos.getX() +","+ pos.getY() +","+ pos.getZ()));
	 }
	 
	 public void sendShootMessage(float[] view) throws IOException{
		 String message = new String("shoot," + id.toString());
		 message += "," + view[0]+"," + view[1] + "," + view[2];
		 sendPacket(message);
	 }
	 
	 public void sendGotShotMessage() throws IOException{
		 String message = new String("gotShot," + id.toString() + "," + ghostAvatars.get(0).getId().toString());
		 sendPacket(message);
	 }
}
	 
