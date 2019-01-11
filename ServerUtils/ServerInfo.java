package ServerUtils;

import ServerUtils.ServerUtils;

public class ServerInfo {
    private int port;
    private String address;

    public ServerInfo(int port, String address){
        this.port = port;
        this.address = address;
    }

    public ServerInfo(){}

    public int getPort() {
        return this.port;
    }

    public String getAddress() {
        return this.address;
    }

    public void saveInfo(){
        ServerUtils.saveServerInfo(this);
    }
}
