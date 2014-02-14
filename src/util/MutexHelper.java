package util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    public static CS_STATUS csStatus;
    public static ConcurrentLinkedQueue<MulticastMessage> voteQueue;
    public static boolean voted;
    public static HashSet<String> getVoted;
    
    public static void init(){
        // initiate CS information
        csStatus = CS_STATUS.NOT_IN_CS;
        voteQueue = new ConcurrentLinkedQueue<MulticastMessage>();
        voted = false;
        getVoted = new HashSet<String>();
    }

    /**
     * Handle message according to its type
     * @param message
     */
    public static void handleMutexMsg(MulticastMessage message) {
        MSG_TYPE type = MSG_TYPE.valueOf(message.getKind());
        //MessagePasser passer = MessagePasser.getInstance();
        
        switch(type) {
        case REQUEST:
            break;
        case RELEASE:
            break;
        case VOTE:
        }
        
    }
    
}
