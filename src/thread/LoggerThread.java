package thread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import logging.Logger;
import message.Message;
import message.TimeStampMessage;

public class LoggerThread extends Thread{
	 @Override
    public void run() {
        BufferedReader in = null;
        Logger logger = Logger.getInstance();
        ArrayList<TimeStampMessage> list;
        try {
            
            while (true) {
                // wait user input
                System.out.println("Please enter your scenario \t 1: print out log");
                in = new BufferedReader(new InputStreamReader(System.in));
                String cmdInput = in.readLine();
                // handle with "print out log"
                if (cmdInput.equals("1")) {
                    // get messages
                    list =  (ArrayList<TimeStampMessage>)logger.showMessages();
                    for(int i = 0; i < list.size()-1;i++){
                    	System.out.println(list.get(i).toString());
                    	int ret = list.get(i).compareTo(list.get(i+1));
                    	if(ret == 0)
                    		System.out.println("||");
                    	else System.out.println("->");
                    }
                    System.out.println(list.get(list.size()-1).toString());
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: Reader corrput");
            e.printStackTrace();
        }
    }
}
