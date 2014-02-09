package thread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import logging.Logger;
import message.Message;
import message.Message.Type;
import message.MessagePasser;
import message.MulticastMessage;
import message.TimeStampMessage;
import record.MultiMsgId;
import record.Rule;
import record.Rule.ACTION;
import clock.ClockService;

/**
 * 
 * Thread to listen to particular node (socket)
 * 
 */
public class PairListenThread extends Thread {
    private Socket socket = null;

    public PairListenThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        MessagePasser passer = MessagePasser.getInstance();
        try {
            ObjectInputStream in = new ObjectInputStream(
                    socket.getInputStream());
            while (true) {
                Message message = (Message) in.readObject();

                // match and handle receive rules
                switch (matchReceiveRule(message, passer)) {
                case DROP:
                    System.out.println("INFO: Drop Message (Receive) "
                            + message);
                    Logger.log(Type.SEVERE, message);
                    break;
                case DELAY:
                    passer.delayInMsgQueue.add(message);
                    break;
                case DUPLICATE:
                    // no break, because at least one message should be received
                    message.set_rcvDuplicate(true);
                default:
                    receiveIn(message, passer);
                    // receive delayed message
                    synchronized (passer.delayInMsgQueue) {
                        while (!passer.delayInMsgQueue.isEmpty()) {
                            Message msg = passer.delayInMsgQueue.poll();
                            receiveIn(msg, passer);
                            Logger.log(Type.SEVERE, msg);
                        }
                    }

                    // receive duplicated message if needed
                    if (message.get_rcvDuplicate()) {
                        receiveIn(message, passer);
                        Logger.log(Type.SEVERE, message);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: PairListenThread corrupt");
            e.printStackTrace();
        }
    }

    /**
     * Add message to receive buffer
     * 
     * @param message
     * @param passer
     */
    private void receiveIn(Message message, MessagePasser passer) {
        // update time if message is timestamp message
        if (message instanceof TimeStampMessage) {
            ClockService.getInstance().updateLocalTime(
                    (TimeStampMessage) message);
        }

        if (message instanceof MulticastMessage) {
            // check multicast message's logic
            MulticastMessage multiMsg = (MulticastMessage) message;

            try {
                if (multiMsg.getKind().equals("NACK")) {
                    // get a nack, find the messages and send back
                    @SuppressWarnings("unchecked")
                    ArrayList<MultiMsgId> idList = (ArrayList<MultiMsgId>) multiMsg
                            .getPayload();
                    System.out.println("Get NACK, need ID: " + idList);

                    for (MultiMsgId id : idList) {
                        MulticastMessage msg = passer.msgArchive.get(id);
                        msg.setDest(multiMsg.get_source());
                        msg.setKind("NACK_ACK");
                        System.out.println("Send NACK_ACK Message:");
                        passer.send(msg, false);
                    }
                } else {
                    System.out.println("Get MulticastMessage: " + multiMsg);
                    // update hold back queue
                    ArrayList<MulticastMessage> groupHBqueue = passer.holdBackQueue
                            .get(multiMsg.getGroupDest());
                    // get the default message, check if it is the expect
                    // message, if match, put into deliver queue
                    int expectNum;
                    if ((expectNum = checkExpected(multiMsg, passer)) == 1) {
                        passer.rcvBuffer.offer(multiMsg);

                        int updatedSeq = sumMap(multiMsg.getGrpSeqVector());
                        Collections.sort(groupHBqueue);

                        @SuppressWarnings("unchecked")
                        HashMap<String, Integer> seqVector = (HashMap<String, Integer>) multiMsg
                                .getGrpSeqVector().clone();

                        boolean findFlag = true;
                        int k = groupHBqueue.size();
                        while (!groupHBqueue.isEmpty() && findFlag) {
                            findFlag = false;

                            // find if the seq+1 is in the hold back queue
                            for (int i = 0; i < k; i++) {
                                if (sumMap(groupHBqueue.get(i)
                                        .getGrpSeqVector()) == updatedSeq + 1) {
                                    findFlag = true;
                                    MulticastMessage tmp = groupHBqueue
                                            .remove(i);
                                    passer.rcvBuffer.offer(tmp);

                                    // update local expect seq number
                                    for (Entry<String, Integer> entry : tmp
                                            .getGrpSeqVector().entrySet()) {
                                        seqVector.put(entry.getKey(), Math.max(
                                                entry.getValue(),
                                                seqVector.get(entry.getKey())));
                                    }

                                    // update iterator
                                    i--;
                                    k--;
                                }
                            }

                            if (findFlag) {
                                updatedSeq++;
                            }
                        }

                        passer.seqNumVector.put(multiMsg.getGroupDest(),
                                seqVector);
                    } else if (expectNum < 0) {
                        // not expected, add to hold back queue
                        groupHBqueue.add(multiMsg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Multicast Message type wrong!");
            }
        } else {
            passer.rcvBuffer.offer(message);
        }
    }

    /**
     * Get the sum of the vector
     * @param grpSeqVector
     * @return
     */
    private int sumMap(HashMap<String, Integer> vector) {
        int retValue = 0;
        for (int val : vector.values()) {
            retValue += val;
        }
        return retValue;
    }

    /**
     * check if the rcv vector if expected vector. If not, send NACK and set
     * message into holdback queue
     * 
     * @param multiMsg
     * @param passer
     * @return
     * @throws IOException
     */
    private int checkExpected(MulticastMessage multiMsg, MessagePasser passer)
            throws IOException {
        int returnFlag = 1;
        int smallFlag = 1;
        HashMap<String, Integer> localVector = passer.seqNumVector.get(multiMsg
                .getGroupDest());
        String senderName = multiMsg.get_source();

        // compare the source number in the vector with the local
        if (multiMsg.getGrpSeqVector().get(senderName) < localVector
                .get(senderName) + 1) {
            smallFlag = 0;
            // find miss, send NACK
        } else if (multiMsg.getGrpSeqVector().get(senderName) > localVector
                .get(senderName) + 1) {
            returnFlag = -1;
            // send NACK
            ArrayList<MultiMsgId> data = new ArrayList<MultiMsgId>();
            for (int i = localVector.get(senderName) + 1; i < multiMsg
                    .getGrpSeqVector().get(senderName); i++) {
                data.add(new MultiMsgId(multiMsg.getGroupDest(), i));
            }
            MulticastMessage msg = new MulticastMessage(null, "NACK", data);
            msg.setDest(multiMsg.get_source());
            msg.set_source(passer.localName);
            System.out.println("Send NACK Message:");
            passer.send(msg, false);
        }

        // compare the self-store vector with the message vector
        for (Entry<String, Integer> entry : passer.seqNumVector.get(
                multiMsg.getGroupDest()).entrySet()) {
            if (passer.groupInfo.containsKey(entry.getKey())
                    && !entry.getKey().equals(senderName)) {
                if (multiMsg.getGrpSeqVector().get(entry.getKey()) < entry
                        .getValue()) {
                    smallFlag = 0;
                } else if (multiMsg.getGrpSeqVector().get(entry.getKey()) > entry
                        .getValue()) {
                    returnFlag = -1;
                    // send NACK
                    ArrayList<MultiMsgId> data = new ArrayList<MultiMsgId>();
                    for (int i = entry.getValue(); i < multiMsg
                            .getGrpSeqVector().get(entry.getKey()); i++) {
                        data.add(new MultiMsgId(multiMsg.getGroupDest(), i));
                    }
                    MulticastMessage msg = new MulticastMessage(null, "NACK",
                            data);
                    msg.setDest(entry.getKey());
                    msg.set_source(passer.localName);
                    System.out.println("Send NACK Message:");
                    passer.send(msg, false);
                }
            }
        }

        return returnFlag == 1 ? smallFlag : returnFlag;
    }

    /**
     * Check if the rule is matched
     * 
     * @param message
     * @param passer
     * @return
     * @throws IOException
     */
    private ACTION matchReceiveRule(Message message, MessagePasser passer)
            throws IOException {
        passer.checkModified();
        for (Rule rule : passer.rcvRules) {
            if (rule.isMatch(message)) {
                return rule.getAction();
            }
        }
        return ACTION.DEFAULT;
    }
}
