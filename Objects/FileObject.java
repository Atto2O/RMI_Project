/**
 * Created by sbp5 on 24/10/18.
 */
package Objects;

import java.util.ArrayList;
import java.util.List;

public class FileObject implements java.io.Serializable{

    private ArrayList<String> tags;
    private String description ;//= new char[200];
    private String fileName;
    private int id;
    private Type type;
    private boolean isPublic;
    private byte[] file;
    private String user;

    public FileObject(ArrayList<String> tags, String fileName, Type type, byte[] file, boolean publicFile, String user, String description) {
        this.tags = tags;
        this.fileName=fileName;
        this.type = type;
        this.file = file;
        this.isPublic = publicFile;
        this.user = user;
        this.description = description;
    }

    public FileObject()
    {
        this.id = -1;
    }

    //Getters
    public ArrayList<String> getTags(){
        return this.tags;
    }

    public String getFileName(){
        return this.fileName;
    }

    public Type getType() { return this.type; }

    public byte[] getFile() {return this.file;}

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
    public void addDescription(String descr)
    {
        this.tags.add(descr);
    }
    public void deleteDescription(String descr)
    {
        this.tags.remove(descr);
    }

    public void setFileName(String fileName){
        this.fileName=fileName;
    }

    public void setType(Type type) {this.type = type;}

    public void setFile(byte[] file) {this.file = file;}

    public void setState(boolean state) {this.isPublic = state;}

    public void setUser(String user) {this.user = user;}

    public void setDescription(char[] description){
        this.description=description.toString();
    }
}
