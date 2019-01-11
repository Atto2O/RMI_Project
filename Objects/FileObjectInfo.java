/**
 * Created by sbp5 on 24/10/18.
 */
package Objects;

import ServerUtils.ServerInfo;

import java.util.ArrayList;
import java.util.List;

public class FileObjectInfo implements java.io.Serializable{

    private ArrayList<String> tags;
    private String description ;
    private String fileName;
    private int id;
    private Type type;
    private boolean isPublic;
    private ServerInfo server;
    private String user;

    public FileObjectInfo(FileObject file, ServerInfo server) {
        this.tags = file.getTags();
        this.fileName=file.getFileName();
        this.type = file.getType();
        this.isPublic = file.getState();
        this.user = file.getUser();
        this.description = file.getDescription();
        this.server = server;
    }

    //Getters
    public ArrayList<String> getTags(){
        return this.tags;
    }

    public String getFileName(){
        return this.fileName;
    }

    public Type getType() { return this.type; }

    public ServerInfo getServer() {return this.server;}

    public boolean getState() {return this.isPublic;}

    public int getId() {return this.id;}

    public String getUser(){return this.user;}

    public String getDescription(){
        return this.description;
    }

    //Setters
    public void setId(int id){this.id=id;}

    public void setTags(List<String> tags){
        this.tags = new ArrayList<>();
        for (String str : tags) {
            this.tags.add(str);
        }
    }

    public void setFileName(String fileName){
        this.fileName=fileName;
    }

    public void setType(Type type) {this.type = type;}

    public void setFile(ServerInfo server) {this.server = server;}

    public void setState(boolean state) {this.isPublic = state;}

    public void setUser(String user) {this.user = user;}

    public void setDescription(String description){
        this.description=description.toString();
    }
}
