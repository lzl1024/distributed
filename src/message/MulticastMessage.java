package message;

public class MulticastMessage extends TimeStampMessage {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    // new field group name and group seqNumber of this message 
    protected int grpSeqNumber;
    
    public MulticastMessage(String dest, String kind, Object data, int seqNumber) {
        super(dest, kind, data);
        this.grpSeqNumber = seqNumber;
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
}
