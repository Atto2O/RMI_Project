package ServerUtils;

import ServerUtils.WS_manager.DataManager;

public class ServerInfo {
    private int id;
    private int port;
    private String address;

    public ServerInfo(int port, String address){
        this.id = ServerUtils.getServerConfig();
        this.port = port;
        this.address = address;
        if(this.id == -1 || this.id == 0){
            this.id = DataManager.serverPOST(this);
            ServerUtils.saveServerConfig(this.id);
        }else{
            DataManager.serverPUT(this);
        }
    }

    public ServerInfo(){}

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

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
