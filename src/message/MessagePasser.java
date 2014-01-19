package message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
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
    
    // node and rules
    public HashMap<String, Node> nodeMap = null;
    public HashMap<String, ObjectOutputStream> outputStreamMap = null;
    public ArrayList<Rule> sendRules = null;
    public ArrayList<Rule> rcvRules = null;
    public Node myself = null;
    
    // queue and other data structure useful in communication
    public ConcurrentLinkedQueue<Message> delayInMsgQueue;
    public ConcurrentLinkedQueue<Message> delayOutMsgQueue;
    public ConcurrentLinkedQueue<Message> rcvBuffer;
    
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
        
        // initiate data structures
        outputStreamMap = new HashMap<String, ObjectOutputStream>();
        delayInMsgQueue = new ConcurrentLinkedQueue<Message>();
        delayOutMsgQueue = new ConcurrentLinkedQueue<Message>();
        rcvBuffer = new ConcurrentLinkedQueue<Message>();
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
            System.out.println("INFO: Drop Message (Send) " + message);
            break;
        case DELAY:
            delayOutMsgQueue.add(message);
            break;
        case DUPLICATE:
            // no break, because at least one message should be sent
            message.set_duplicate(true);
        default:
            sendAway(message);       
            // send delayed message
            synchronized(delayOutMsgQueue) {
                while (!delayOutMsgQueue.isEmpty()) {
                    sendAway(delayOutMsgQueue.poll());
                }
            }
            // send duplicated message if needed
            if (message.get_duplicate()) {
                sendAway(message);
            }
        }
    }
    
    
    /**
     * Send away message to specific destination
     * @param message
     */
    @SuppressWarnings("resource")
    private void sendAway(Message message) {
        ObjectOutputStream out;
        
        try {
            // build connection if not
            if (!outputStreamMap.containsKey(message.getDest())) {
                Node node = nodeMap.get(message.getDest());
                
                Socket socket = new Socket(node.getIpAddress(), node.getPort());
                out = new ObjectOutputStream(socket.getOutputStream());
                outputStreamMap.put(message.getDest(), out);
                
            } else {
                out = outputStreamMap.get(message.getDest());
            }
            
            // send message
            out.writeObject(message);
            out.flush();
            out.reset();
            System.out.println("INFO: send message " + message);
            
        } catch (IOException e) {
            System.err.println("ERROR: send message error, the other side may be offline " + message);
        }
    }

    /**
     * Judge if match one send rule
     * @param message
     * @return return the action which is needed to be taken
     */
    private ACTION matchSendRule(Message message) {
        // TODO Auto-generated method stub
        // you may want to create Nth list or map for matching
    	for (Rule rule : sendRules){
    		if (rule.isMatch(message)) {
    			return rule.getAction();
    		}
    	}
        return ACTION.DEFAULT;
    }

    /**
     * Receive message from rcvBuffer
     * @return
     */
    public ArrayList<Message> receive() {
        ArrayList<Message> receiveList = new ArrayList<Message>();
        synchronized (rcvBuffer) {
            while (!rcvBuffer.isEmpty()) {
                receiveList.add(rcvBuffer.poll());
            }
        }
        return receiveList;
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
    