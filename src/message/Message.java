package message;

import java.io.Serializable;

public class Message implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Header header = null;
    private Object payload = null;
    private boolean duplicate;
    
    public class Header implements Serializable {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        private int sequenceNumber;
        private String source;
        private String dest;
        private String kind;
        
        public Header(String dest, String kind) {
            this.dest = dest;
            this.kind = kind;
        }

        @Override
        public String toString() {
            return "[sequenceNumber=" + sequenceNumber + ", source="
                    + source + ", dest=" + dest + ", kind=" + kind + "]";
        }   
    }
    
    public Message(String dest, String kind, Object data) {
        this.header = new Header(dest, kind);
        this.payload = data;
        this.duplicate = false;
    }

    public void set_seqNum(int sequenceNumber) {
        this.header.sequenceNumber = sequenceNumber;
    }
    
    public int get_seqNumr() {
        return this.header.sequenceNumber;
    }

    public String get_source() {
        return this.header.source;
    }

    public void set_source(String source) {
        this.header.source = source;
    }

    public String getDest() {
        return header.dest;
    }

    public void setDest(String dest) {
        this.header.dest = dest;
    }

    public String getKind() {
        return header.kind;
    }

    public void setKind(String kind) {
        this.header.kind = kind;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public void set_duplicate(Boolean dupe) {
        this.duplicate = dupe;
    }
    
    public boolean get_duplicate() {
        return this.duplicate;
    }

    @Override
    public String toString() {
        return "[header=" + header + ", payload=" + payload
                + ", duplicate=" + duplicate + "]";
    }
}