package thread;

import java.io.ObjectInputStream;
import java.net.Socket;

import message.Message;
import record.Rule.ACTION;

/**
 * 
 * Thread to listen to particular node (socket)
 *
 */
public class PairListenThread extends Thread {
    private Socket socket = null;
    
    public PairListenThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            while(true) {
                Message message = (Message)in.readObject();
                switch (matchReceiveRule(message)) {
                case DROP:
                    break;
                case DELAY:
                    break;
                case DUPLICATE:
                    // no break, because at least on message should be received
                    message.set_duplicate(true);
                default:
                    receiveIn(message);       
                    // TODO receive delayed message
                    
                    // receive duplicated message if needed
                    if (message.get_duplicate()) {
                        receiveIn(message);
                    }
                }   
            }
        } catch (Exception e) {
            System.err.println("ERROR: PairListenThread corrupt");
            e.printStackTrace();
        }
    }

    private void receiveIn(Message message) {
        // TODO Auto-generated method stub
        
    }

    private ACTION matchReceiveRule(Message message) {
        // TODO Auto-generated method stub
        return ACTION.DEFAULT;
    }
}
