package thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import message.MessagePasser;

/**
 * 
 * The listener thread to listen connection requests from other nodes 
 *
 */
public class ListenerThread extends Thread {
	private ServerSocket server;
	
	public ListenerThread(ServerSocket server){
		this.server = server;
	}
    @SuppressWarnings("resource")
    @Override
    public void run() {
        MessagePasser passer = MessagePasser.getInstance();
        try {
            //ServerSocket server = new ServerSocket(passer.myself.getPort());
            
            while(true) {
                Socket socket = server.accept();
                System.out.println("INFO: connect to " + socket.getRemoteSocketAddress());
                // open a new thread to listen messages coming from the other side
                new PairListenThread(socket).start();               
            }
        } catch (IOException e) {
            System.err.println("ERROR: server socket corrupt");
           // e.printStackTrace();
        }
    }

}
