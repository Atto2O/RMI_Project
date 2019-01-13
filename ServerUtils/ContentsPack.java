package ServerUtils;

public class ContentsPack {
    private int serverID;
    private int id;


    public ContentsPack(int serverID, int id) {
        this.serverID = serverID;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getServerID() {
        return serverID;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setServerID(int serverID) {
        this.serverID = serverID;
    }
}
