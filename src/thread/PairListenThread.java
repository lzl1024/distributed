package thread;

import java.io.ObjectInputStream;
import java.net.Socket;

import message.Message;
import message.MessagePasser;
import record.Rule;
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
        MessagePasser passer = MessagePasser.getInstance();
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            while(true) {
                Message message = (Message)in.readObject();
                message.set_duplicate(false);
                switch (matchReceiveRule(message)) {
                case DROP:
                    System.out.println("INFO: Drop Message (Receive) " + message);
                    break;
                case DELAY:
                    passer.delayInMsgQueue.add(message);
                    break;
                case DUPLICATE:
                    // no break, because at least one message should be received
                    message.set_duplicate(true);
                default:
                    receiveIn(message, passer);       
                    // receive delayed message
                    synchronized(passer.delayInMsgQueue) {
                        while (!passer.delayInMsgQueue.isEmpty()) {
                            receiveIn(passer.delayInMsgQueue.poll(), passer);
                        }
                    }

                    // receive duplicated message if needed
                    if (message.get_duplicate()) {
                        receiveIn(message, passer);
                    }
                }   
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
    private void receiveIn(Message message, MessagePasser passer) {
        passer.rcvBuffer.offer(message);
    }

    private ACTION matchReceiveRule(Message message) {
        // TODO Auto-generated method stub
    	for (Rule rule : MessagePasser.getInstance().rcvRules){
    		if (rule.isMatch(message)) {
    			return rule.getAction();
    		}
    	}
        return ACTION.DEFAULT;
    }
}
