package record;

public class Node {
    private String name;
    private String ipAddress;
    private int port;
    
    /**
     * Constructor of node
     * @param name
     * @param ipAddress
     * @param port
     */
    public Node(String name, String ipAddress, int port) {
        super();
        this.name = name;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getName() {
        return name;
    }
    public void setAlice(String name) {
        this.name = name;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    
    @Override
    public String toString() {
        return "[name=" + name + ", ipAddress=" + ipAddress + ", port="
                + port + "]";
    }
}
