package thread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import logging.Logger;
import message.MessagePasser;
import message.TimeStampMessage;
import message.Message;
import clock.ClockService;

public class LoggerThread extends Thread{
	 @Override
    public void run() {
        BufferedReader in = null;
        Logger logger = Logger.getInstance();
        ArrayList<Message> list;
        try {
            
            while (true) {
                // wait user input
                System.out.println("Please enter your scenario \t 1: print out log");
                in = new BufferedReader(new InputStreamReader(System.in));
                String cmdInput = in.readLine();
                // handle with "print out log"
                if (cmdInput.equals("1")) {
                    // get messages
                    list = (ArrayList<Message>)logger.showMessages();
                    for(Message m: list){
                    	if(m instanceof TimeStampMessage)
                    	System.out.println(m.toString());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: Reader corrput");
            e.printStackTrace();
        }
    }
}
