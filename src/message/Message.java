package message;

import java.io.Serializable;

public class Message implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Header header = null;
    private Object payload = null;
    
    public class Header implements Serializable {
        /**
         * 
         */
        private static final long serialVersionUID = 2L;
        public int id;
        public String src;
        public String dest;
        public String kind;
        
        public Header(String src, String dest, String kind) {
            this.src = src;
            this.dest = dest;
            this.kind = kind;
        }

        @Override
        public String toString() {
            return "[id=" + id + ", src=" + src + ", dest=" + dest
                    + ", kind=" + kind + "]";
        }
        
    }
    
    public Message(String src, String dest, String kind, Object data) {
        this.header = new Header(src, dest, kind);
        this.payload = data;
    }

    public void set_id(int id) {
        this.header.id = id;
    }
    
    public int get_id() {
        return this.header.id;
    }

    public String getSrc() {
        return this.header.src;
    }

    public void setSrc(String src) {
        this.header.src = src;
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

    @Override
    public String toString() {
        return "[header=" + header + ", payload=" + payload + "]";
    }

}