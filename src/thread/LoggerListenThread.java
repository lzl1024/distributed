package thread;

import java.io.ObjectInputStream;
import java.net.Socket;

import logging.Logger;
import message.Message;


/**
 * 
 * Thread to listen to particular node (socket)
 *
 */
public class LoggerListenThread extends Thread {
    private Socket socket = null;
    
    public LoggerListenThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        Logger logger = Logger.getInstance();
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            while(true) {
                Message message = (Message)in.readObject();            
                receiveIn(message, logger);
            }
        } catch (Exception e) {
            System.err.println("ERROR: PairListenThread corrupt");
            e.printStackTrace();
        }
    }

    /**
     * Add message to receive buffer
     * @param message
     * @param passer
     */
    private void receiveIn(Message message, Logger logger) {
        logger.rcvBuffer.offer(message);
    }
}