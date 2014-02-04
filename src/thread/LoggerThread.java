package thread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import clock.ClockService.CLOCK_TYPE;

import logging.Logger;
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
                    if (list == null || list.size() < 1) {
                        continue;
                    }
                    
                    for(int i = 0; i < list.size()-1;i++){
                    	System.out.println(list.get(i));
                    	if (Logger.getInstance().clockType == CLOCK_TYPE.VECTOR) {
                    	    compareWithPeers(i, list);
                    	}                 	
                    	int ret = list.get(i).compareTo(list.get(i+1));
                    	if(ret == 0) {
                    		System.out.println("||");
                    	} else {
                    	    System.out.println("->");
                    	}
                    }
                    System.out.println(list.get(list.size()-1));
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: Reader corrput");
            e.printStackTrace();
        }
    }

	 /**
	  * compare with other message in the list
	  * @param index
	  * @param timeStampMessage
	  */
    private void compareWithPeers(int index, ArrayList<TimeStampMessage> list) {
        TimeStampMessage target = list.get(index);
        
        for (int i = index+1; i < list.size(); i++) {
            int ret = target.compareTo(list.get(i));
            if (ret == 0) {
                System.out.print("  ||  ");
            } else {
                System.out.print("  ->  ");
            }
            System.out.print(list.get(i).getTimeStamp());
        }
        System.out.println();
    }
}
