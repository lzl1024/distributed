package message;

import java.io.IOException;
import java.util.HashMap;

import record.MultiMsgId;

public class MulticastMessage extends TimeStampMessage {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // new field group name and group seqNumber of this message
    protected HashMap<String, Integer> grpSeqVector;
    // the real dest node
    protected String groupDest;

    public MulticastMessage(String groupdest, String kind, Object data) {
        super(null, kind, data);
        this.groupDest = groupdest;
    }

    @SuppressWarnings("unchecked")
    public MulticastMessage(MulticastMessage msg) {
        super(msg);
        this.groupDest = msg.groupDest;
        this.grpSeqVector = (HashMap<String, Integer>) msg.grpSeqVector.clone();
    }

    public HashMap<String, Integer> getGrpSeqVector() {
        return grpSeqVector;
    }

    public void setGrpSeqVector(HashMap<String, Integer> grpSeqVector) {
        this.grpSeqVector = grpSeqVector;
    }

    public String getGroupDest() {
        return groupDest;
    }

    public void setGroupDest(String groupDest) {
        this.groupDest = groupDest;
    }

    @Override
    public String toString() {
        return "MulticastMessage [" + "grpSeqNumber=" + grpSeqVector
                + ", timeStamp=" + timeStamp + ", header=" + header
                + ", payload=" + payload + ", sendDuplicate=" + sendDuplicate
                + "]";
    }

    /**
     * add a seqNumber and send away the message
     * 
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public void send() throws IOException {
        MessagePasser passer = MessagePasser.getInstance();
        this.set_source(passer.myself.getName());

        // add sequence number
        HashMap<String, Integer> seqVector = passer.seqNumVector
                .get(this.groupDest);

        // update vector and put into send archive
        int updateSeqNum = seqVector.get(passer.localName) + 1;
        seqVector.put(passer.localName, updateSeqNum);
        passer.seqNumVector.put(this.groupDest, seqVector);
        this.grpSeqVector = (HashMap<String, Integer>) seqVector.clone();
        passer.msgArchive.put(new MultiMsgId(this.groupDest, updateSeqNum),
                this);

        // send away the message
        System.out.println("Send away multicast message: " + this);
        for (String realDest : passer.groupInfo.get(this.getGroupDest())) {
            if (!realDest.equals(passer.localName)) {
                MulticastMessage msg = new MulticastMessage(this);
                msg.setDest(realDest);
                // send as the normal timestamp message
                passer.send(msg, false);
            }
        }
    }
}
