package logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import message.Message;
import message.MessagePasser;
import message.TimeStampMessage;

import org.yaml.snakeyaml.Yaml;

import record.Node;
import record.Rule;
import thread.LoggerListenerSocketThread;
import thread.LoggerThread;
import util.Config;
import clock.ClockService;
import clock.ClockService.CLOCK_TYPE;

public class Logger{
	/** Constructor of , parse the configuration file
	 *  and build the initial connection
	 * 
	 * @param configuration_filename
	 * @param local_name
	 */
	// instance to call by other classes
	private static volatile Logger instance = null;

	// node and rules
	public HashMap<String, Node> nodeMap = null;
	public HashMap<String, ObjectOutputStream> outputStreamMap = null;
	public Node myself = null;

	// queue and other data structure useful in communication
	public ConcurrentLinkedQueue<Message> rcvBuffer;
	public ArrayList<Message> receiveList;

	// file
	public long modified = 0;
	public String configFileName = null;
	public String localName = null;
	
	// clock
	public CLOCK_TYPE clockType;
	
	private ServerSocket server;

	/** Constructor of MessagePasser, parse the configuration file
	 *  and build the initial connection
	 * 
	 * @param configuration_filename
	 * @param local_name
	 */
	@SuppressWarnings("unchecked")
	private Logger(String configuration_filename, String local_name) {
		// parse the configuration file
		Yaml yaml = new Yaml();
		InputStream input = null;
		configFileName = configuration_filename;
		localName = local_name;
		try {
			File file = new File(configuration_filename);
			modified = file.lastModified(); //get the last modification time
			input = new FileInputStream(file);
			Map<String,  ArrayList<Map<String, Object>>> map = 
					(Map<String,  ArrayList<Map<String, Object>>>) yaml.load(input);
			nodeMap = Config.parseNodeMap(map.get("Configuration"));
			
			// get clock type
			clockType = CLOCK_TYPE.valueOf(map.get("Clock").get(0).get("Type").toString());
			
		} catch (FileNotFoundException e) {
			System.err.println("ERROR: Cannot find/read the configuration file!");
			e.printStackTrace();
			System.exit(-1);
		}

		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// initiate data structures
	    this.myself = nodeMap.get(local_name);
		outputStreamMap = new HashMap<String, ObjectOutputStream>();
		rcvBuffer = new ConcurrentLinkedQueue<Message>();
		receiveList = new ArrayList<Message>();
	}

	/**
	 * get the singleton instance
	 * @return
	 */
	public static Logger getInstance() {
		return instance;
	}

	/**
	 * Receive message from rcvBuffer
	 * @return
	 */
	public Message receive() {
		return rcvBuffer.poll();
	}

	/**
	 * Show all the message that has been received.
	 * @return ArrayList<Message>
	 */
	public ArrayList<Message> showMessages(){
		while(rcvBuffer.size()>0){
			this.receiveList.add(rcvBuffer.poll());
		}
		Collections.sort(this.receiveList);
		return receiveList;
	}
	/**
	 * Check if the file is modified
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void checkModified () throws IOException { 
		File file = new File(configFileName);
		long lastModified = file.lastModified();
		int closeServerF = 0;
		if (lastModified > modified) {
		    System.out.println("INFO: Configuration file modified! Reload again!");
		    
			modified = lastModified;
			Yaml yaml = new Yaml();
			InputStream input;
			// parse configuration file again
			try {
				input = new FileInputStream(file);
				Map<String,  ArrayList<Map<String, Object>>> map = 
						(Map<String,  ArrayList<Map<String, Object>>>) yaml.load(input);
				HashMap<String, Node> newNodeMap = Config.parseNodeMap(map.get("Configuration"));
				ArrayList<Rule> newSendRules = Config.parseRules(map.get("SendRules"));
				ArrayList<Rule> newRcvRules = Config.parseRules(map.get("ReceiveRules"));
				this.myself = newNodeMap.get(localName);
				outputStreamMap.clear();
				
				closeServerF = nodeMap.get(localName).equals(newNodeMap.get(localName));
				
				input.close();
			} catch (FileNotFoundException e) {
				System.err.println("ERROR: The configuration file has been deleted!");
				e.printStackTrace();
				System.exit(-1);			
			}
		}
	}

	public static void log(Message message){
		MessagePasser passer = MessagePasser.getInstance();
		ObjectOutputStream out;
		String name = "Logger";
		//System.out.println("INFO: before message " + message);
		try {
			// build connection if not
			if (!passer.outputStreamMap.containsKey(name)) {
				Node node = passer.nodeMap.get(name);

				Socket socket = new Socket(node.getIpAddress(), node.getPort());
				out = new ObjectOutputStream(socket.getOutputStream());
				passer.outputStreamMap.put(name, out);

			} else {
				out = passer.outputStreamMap.get(name);
			}

			System.out.println("INFO: send message " + message);
			// send message
			out.writeObject(message);
			out.flush();
			out.reset();
		} catch (IOException e) {
			System.err.println("ERROR: send message error, the other side may be offline " + message);
		}
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println("Usage: configuration_filename local_name");
			System.exit(0);
		}    
		instance = new Logger(args[0], args[1]);
		instance.server = new ServerSocket(instance.myself.getPort());
		// start clock
		ClockService.createClock(instance.clockType);
		// set up listener thread to build connection with other nodes
		new LoggerListenerSocketThread(instance.server).start();
		// set up user thread to receive user input
		new LoggerThread().start();
	}
}
