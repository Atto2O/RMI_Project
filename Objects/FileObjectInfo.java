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
    private boolean isPublic;
    private int serverID;
    private String user;

    public FileObjectInfo(FileObject file, ServerInfo server) {
        this.tags = file.getTags();
        this.fileName=file.getFileName();
        this.isPublic = file.getState();
        this.user = file.getUser();
        this.description = file.getDescription();
        this.serverID= server.getId();
    }

    //Getters
    public ArrayList<String> getTags(){
        return this.tags;
    }

    public String getFileName(){
        return this.fileName;
    }

    public int getServerID() {
        return this.serverID;
    }

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

    public void setState(boolean state) {this.isPublic = state;}

    public void setUser(String user) {this.user = user;}

    public void setDescription(String description){
        this.description=description.toString();
    }
}
