package thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import message.MessagePasser;
import message.MulticastMessage;
import message.TimeStampMessage;
import util.Config.CS_STATUS;
import util.MutexHelper;
import clock.ClockService;

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
                System.out
                        .println("Please enter your scenario \t 1: Send, 2: Receive, 3: Local Time, 4: Send with Log, " +
                        		"5: Receive with Log, 6: Multicast Send, 7: Request, 8: Release");
                in = new BufferedReader(new InputStreamReader(System.in));
                String cmdInput = in.readLine();
                // handle with "send"
                if (cmdInput.equals("1")) {
                    sendMessage(passer, in, false);
                } else if (cmdInput.equals("2")) {
                    System.out.println("Receive Message : " + passer.receive(false));
                } else if (cmdInput.equals("3")) {
                    System.out.println("Local Time : "
                            + ClockService.getInstance().getTime());
                } else if (cmdInput.equals("4")) {
                    sendMessage(passer, in, true);
                } else if (cmdInput.equals("5")) {
                    passer.receive(true);
                } else if (cmdInput.equals("6")) {
                    System.out.println("Please enter your dest group:");
                    String dest = in.readLine();
                    while (!passer.groupInfo.containsKey(dest)) {
                        System.out
                                .println("Your Dest has not been registered, enter again:");
                        dest = in.readLine();
                    }
                    System.out.println("Please enter the kind:");
                    String kind = in.readLine();
                    System.out.println("Please enter the data:");
                    String data = in.readLine();
                    new MulticastMessage(dest, kind, data).send();
                } else if (cmdInput.equals("7")) {
                    if (MutexHelper.csStatus == CS_STATUS.IN_CS) {
                        System.out.println("You have already in CS!");
                    } else {
                        // send request message to its voting group
                        new MulticastMessage(passer.localName, "REQUEST", "request a CS").send();
                    }
                } else if (cmdInput.equals("8")) {
                    if (MutexHelper.csStatus == CS_STATUS.NOT_IN_CS) {
                        System.out.println("You are not in CS!");
                    } else {
                        // send request message to its voting group
                        new MulticastMessage(passer.localName, "RELEASE", "release CS").send();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: Reader corrput");
            e.printStackTrace();
        }
    }

    /**
     * Send a message with logger it or not
     * 
     * @param passer
     * @param in
     * @throws IOException
     */
    private void sendMessage(MessagePasser passer, BufferedReader in,
            boolean isLog) throws IOException {
        System.out.println("Please enter your dest:");
        String dest = in.readLine();
        while (!passer.nodeMap.containsKey(dest)) {
            System.out
                    .println("Your Dest has not been registered, enter again:");
            dest = in.readLine();
        }

        System.out.println("Please enter the kind:");
        String kind = in.readLine();
        System.out.println("Please enter the data:");
        String data = in.readLine();

        // create and send message
        TimeStampMessage msg = new TimeStampMessage(dest, kind, data);
        msg.set_source(passer.myself.getName());
        passer.send(msg, isLog);
    }
}
