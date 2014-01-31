package thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * The listener thread to listen connection requests from other nodes 
 *
 */
public class LoggerListenerSocketThread extends Thread {
	private ServerSocket server;
	
	public LoggerListenerSocketThread(ServerSocket server){
		this.server = server;
	}

    @Override
    public void run() {
        try {
            while(true) {
                Socket socket = server.accept();
                System.out.println("INFO: connect to " + socket.getRemoteSocketAddress());
                // open a new thread to listen messages coming from the other side
                new LoggerListenThread(socket).start();               
            }
        } catch (IOException e) {
            System.err.println("ERROR: server socket corrupt");
           // e.printStackTrace();
        }
    }

}
