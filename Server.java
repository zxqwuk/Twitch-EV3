import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
	
	//Voting
	public Timer timer;
	public int move_forward = 0;
	public int move_left = 0;
	public int move_right = 0;
	public int move_backwards = 0;
	
	public int PORT = 7766;
	public ServerSocket server;
	public Socket client;
	public DataOutputStream dOut;
	public DataInputStream dIn;

	public Server(int PORT) {
		this.PORT = PORT;
		timer = new Timer();
	}
	
	public void run() {
		System.out.println("Waiting for connection");
		connect();
		timer.start();
		System.out.println("Connected");
		
		while(true) {
			while(!server.isClosed()) {
				String input = getInput();
				String[] input_split = input.split(":");
				String username = input_split[0];
				String message = "";
				for(int i=1; i<input_split.length; i++) {
					message += input_split[i];
				}
				message = message.trim();
				System.out.println(username + ": " + message);
				if(username.equals("zxqw")) {
					if(message.equals("start")) {
						timer.setVoting(true);
					}
				}
				if(timer.getVoting()) {
					if(message.equals("!forward")) {
						move_forward++;
					} else if(message.equals("!left")){
						move_left++;
					} else if(message.equals("!right")){
						move_right++;
					} else if(message.equals("!backwards")){
						move_backwards++;
					}
				}
				
				if(timer.getSeconds() >= 30) {
					timer.setVoting(false);
					printResults();
				}
				
			}
			reset();
			try { Thread.sleep(100); } catch(Exception e) {}
		}
	}
	
	public void printResults() {
		int max = 0;
		String direction = "";
		
		int[] moves = new int[4];
		moves[0] = move_forward;
		moves[1] = move_left; 
		moves[2] = move_right; 
		moves[3] = move_backwards;
		
		for(int i=0; i<4; i++) {
			if(moves[i] >= max) {
				max = moves[i];
				if(i == 0) {
					direction = "Forward";
				} else if(i == 1) {
					direction = "Left";
				}  else if(i == 2) {
					direction = "Right";
				}  else if(i == 3) {
					direction = "Backwards";
				} 
			}
		}
		System.out.println("Direction: " + direction);
		System.out.println("Forward: " + move_forward);
		System.out.println("Left: " + move_left);
		System.out.println("Right: " + move_right);
		System.out.println("Backwards: " + move_backwards);
	}
	
	public void connect() {
		while(true) {
			try {
				server = new ServerSocket(PORT);
				client = server.accept(); //This pauses until connection
				OutputStream out = client.getOutputStream();
				InputStream in = client.getInputStream();
				dOut = new DataOutputStream(out);
				dIn = new DataInputStream(in);
				break; //break only if a client successfully connects
			} catch(IOException e) { /* do nothing */ }
			//Delay.msDelay(100);
		}
	}
	
	public void close() {
		try {
			server.close();
			client.close();
			dOut.close();
			dIn.close();
		} catch(IOException e) { /* servers already closed */ }
	}
	
	public void reset() {
		close();
		connect();
	}
	
	// Function that takes a byte stream and returns "string" given 's''t''r''i''n''g''\n'
	public String getInput() {
		String s = "";
		while(true) {
			try {
				int c = dIn.readByte();
				if(c != 10) 
					s += (char) c;
				else break;
			} catch (IOException e) {
				System.out.println("Error: " + e);
				s = "-1";
				break;
			}
		}
		return s;
	}
	
}
