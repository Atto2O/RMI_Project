package ServerUtils;

import ServerUtils.WS_manager.DataManager;

public class ServerInfo {
    private int id;
    private int port;
    private String ip;

    public ServerInfo(int port, String address){
        this.id = ServerUtils.getServerConfig();
        this.port = port;
        this.ip = address;
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

    public String getIp() {
        return this.ip;
    }

    public void saveInfo(){
        ServerUtils.saveServerInfo(this);
    }
}
