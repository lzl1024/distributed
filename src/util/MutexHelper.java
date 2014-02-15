package util;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import message.MessagePasser;
import message.MulticastMessage;
import util.Config.CS_STATUS;

/**
 * 
 * Helper class to handle the message related with mutual exclusion
 *
 */
public class MutexHelper {
    public static final HashSet<String> CSMsgType = new HashSet<String>(Arrays.asList("REQUEST","VOTE","RELEASE")); 
    
    private enum MSG_TYPE {
        REQUEST, VOTE, RELEASE
    }
    
    // CS information
    private static CS_STATUS csStatus;
    private static ConcurrentLinkedQueue<MulticastMessage> voteQueue;
    private static boolean voted;
    private static HashSet<String> getVoted;
    private static int numofGrpMember;
    private static MessagePasser passer;
    
    public static void init(){
        // initiate CS information
        csStatus = CS_STATUS.NOT_IN_CS;
        voteQueue = new ConcurrentLinkedQueue<MulticastMessage>();
        voted = false;
        getVoted = new HashSet<String>();
        passer = MessagePasser.getInstance();
        numofGrpMember = passer.groupInfo.get(passer.localName).size();
    }

    /**
     * Handle message according to its type
     * @param message
     * @throws IOException 
     */
    public static void handleMutexMsg(MulticastMessage message) throws IOException {
        MSG_TYPE type = MSG_TYPE.valueOf(message.getKind());
          
        switch(type) {
        case REQUEST:
            if (csStatus == CS_STATUS.IN_CS || true == voted) {
                voteQueue.offer(message);
            } else {
                // send back vote message
                voted = true;
                MulticastMessage msg = new MulticastMessage(null, "VOTE", "vote");
                msg.setDest(message.get_source());
                msg.set_source(passer.localName);
                passer.send(msg, false);
            }
            break;
        case VOTE:
            getVoted.add(message.get_source());
            // if get all of the vote, get into the CS
            if (getVoted.size() == numofGrpMember) {
                System.out.println("INFO: " + passer.localName + " get into the critical section!");
                csStatus = CS_STATUS.IN_CS;
            }
        case RELEASE:
            if (voteQueue.isEmpty()) {
                voted = false;
            } else {
                // vote the head of the queue
                voted = true;
                String dest = voteQueue.poll().get_source();
                MulticastMessage msg = new MulticastMessage(null, "VOTE", "vote");
                msg.setDest(dest);
                msg.set_source(passer.localName);
                passer.send(msg, false);
            }
            break;
        }  
    }  
}
