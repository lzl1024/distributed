package message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.yaml.snakeyaml.Yaml;

import record.Node;
import record.Rule;
import record.Rule.ACTION;
import thread.ListenerThread;
import thread.UserThread;
import util.Config;

public class MessagePasser {
    // instance to call by other classes
    private static volatile MessagePasser instance = null;
    public HashMap<String, Node> nodeMap = null;
    public ArrayList<Rule> sendRules = null;
    public ArrayList<Rule> rcvRules = null;
    public Node myself = null;
    
    // set up an atomic counter for message id
    private AtomicInteger IDcounter;
    
    /** Constructor of MessagePasser, parse the configuration file
     *  and build the initial connection
     * 
     * @param configuration_filename
     * @param local_name
     */
    @SuppressWarnings("unchecked")
    private MessagePasser(String configuration_filename, String local_name) {
        // parse the configuration file
        Yaml yaml = new Yaml();
        InputStream input = null;
        try {
            input = new FileInputStream(new File(configuration_filename));
            Map<String,  ArrayList<Map<String, Object>>> map = 
                    (Map<String,  ArrayList<Map<String, Object>>>) yaml.load(input);
            nodeMap = Config.parseNodeMap(map.get("Configuration"));
            sendRules = Config.parseRules(map.get("SendRules"));
            rcvRules = Config.parseRules(map.get("ReceiveRules"));
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: Cannot find the configuration file!");
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.myself = nodeMap.get(local_name);
        IDcounter = new AtomicInteger(0);
    }
    
    /**
     * get the singleton instance
     * @return
     */
    public static MessagePasser getInstance() {
        return instance;
    }
    
    /**
     * Send the message to the other end
     * @param message
     */
    public void send(Message message) {
        message.set_seqNum(IDcounter.incrementAndGet());
        switch (matchSendRule(message)) {
        case DROP:
            break;
        case DELAY:
            break;
        case DUPLICATE:
            // no break, because at least on message should be sent
            message.set_duplicate(true);
        default:
            sendAway(message);       
            // TODO send delayed message
            
            // send duplicated message if needed
            if (message.get_duplicate()) {
                sendAway(message);
            }
        }
    }
    
    
    private void sendAway(Message message) {
        // TODO Auto-generated method stub
        
    }

    /**
     * Judge if match one send rule
     * @param message
     * @return return the action which is needed to be taken
     */
    private ACTION matchSendRule(Message message) {
        // TODO Auto-generated method stub
        return ACTION.DEFAULT;
    }

    public ArrayList<Message> receive() {
        // TODO
        return null;
    }
    
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 2) {
            System.out.println("Usage: configuration_filename local_name");
            System.exit(0);
        }    
        instance = new MessagePasser(args[0], args[1]);
        
        // set up listener thread to build connection with other nodes
        new ListenerThread().start();
        // set up user thread to receive user input
        new UserThread().start();
    }
}
    