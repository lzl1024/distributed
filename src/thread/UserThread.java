package thread;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import message.Message;
import message.MessagePasser;

/**
 * 
 * Thread to listen user input 
 *
 */
public class UserThread extends Thread {

    @Override
    public void run() {
        BufferedReader in = null;
        MessagePasser passer = MessagePasser.getInstance();
        try {
            while (true) {
                // wait user input
                System.out.println("Please enter your scenario \t 1: Send, 2: Receive");
                in = new BufferedReader(new InputStreamReader(System.in));
                String cmdInput = in.readLine();
                // handle with "send"
                if (cmdInput.equals("1")) {
                    System.out.println("Please enter your dest:");
                    String dest = in.readLine();
                    System.out.println("Please enter the kind:");
                    String kind = in.readLine();
                    System.out.println("Please enter the data:");
                    String data = in.readLine();
                    Message msg = new Message(dest, kind, data);
                    msg.set_source(passer.myself.getName());
                    passer.send(msg);
                } else if (cmdInput.equals("2")) {
                    System.out.println("Receive Messages :" + passer.receive());
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: Reader corrput");
            e.printStackTrace();
        }
    }
}
