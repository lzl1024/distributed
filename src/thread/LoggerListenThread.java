package thread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import logging.Logger;
import message.Message;
import message.MessagePasser;
import message.TimeStampMessage;
import record.Rule;
import record.Rule.ACTION;
import clock.ClockService;


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
                
                // update time if message is timestamp message
                if (message instanceof TimeStampMessage) {
                    ClockService.getInstance().updateLocalTime((TimeStampMessage)message);
                }
                
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