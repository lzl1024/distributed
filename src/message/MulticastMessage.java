package message;

public class MulticastMessage extends TimeStampMessage {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    // new field group name and group seqNumber of this message 
    protected int grpSeqNumber;
    // the real dest node
    protected String groupDest;
    
    public MulticastMessage(String groupdest, String kind, Object data) {
        super(null, kind, data);
        this.groupDest = groupdest;
    }

    public int getGrpSeqNumber() {
        return grpSeqNumber;
    }


    public void setGrpSeqNumber(int grpSeqNumber) {
        this.grpSeqNumber = grpSeqNumber;
    }


    @Override
    public String toString() {
        return "MulticastMessage [" + ", grpSeqNumber="
                + grpSeqNumber + ", timeStamp=" + timeStamp + ", header="
                + header + ", payload=" + payload + ", sendDuplicate="
                + sendDuplicate + "]";
    }

    /**
     * add a seqNumber and send away the message
     */
    public void send() {
        MessagePasser passer = MessagePasser.getInstance();
        // TODO: add seq number
        
        // truly send away the message
        for (String realDest : passer.groupInfo.get(this.getDest())) {
            this.setDest(realDest);
            passer.sendAway(this);
        }
    }
}
