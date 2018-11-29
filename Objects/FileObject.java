/**
 * Created by sbp5 on 24/10/18.
 */
package Objects;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.registry.Registry;
import CallBack.*;
import Objects.*;
import RemoteObject.*;
import Server.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class FileObject {

    private String title;
    private ArrayList<String> description;
    private String fileName;
    private int id;
    private Type type;
    private boolean isPublic;
    private byte[] file;
    private String user;

    public FileObject(String title,ArrayList<String> description, String fileName, Type type, byte[] file, boolean publicFile, String user) {
        this.title=title;
        this.description=description;
        this.fileName=fileName;
        this.type = type;
        this.file = file;
        this.isPublic = publicFile;
        this.user = user;
    }

    public FileObject()
    {
        this.id = -1;
    }

    //Getters
    public String getTitle(){
        return this.title;
    }

    public ArrayList<String> getDescription(){
        return this.description;
    }

    public String getFileName(){
        return this.fileName;
    }

    public Type getType() { return this.type; }

    public byte[] getFile() {return this.file;}

    public boolean getState() {return this.isPublic;}

    public int getId() {return this.id;}

    public String getUser(){return this.user;}

    //Setters
    public void setId(int id){this.id=id;}

    public void setTitle(String title){
        this.title=title;
    }

    public void setDescription(List<String> description){
        this.description = new ArrayList<>();
        for (String str : description) {
            this.description.add(str);
        }
    }
    public void addDescription(String descr)
    {
        this.description.add(descr);
    }
    public void deleteDescription(String descr)
    {
        this.description.remove(descr);
    }

    public void setFileName(String fileName){
        this.fileName=fileName;
    }

    public void setType(Type type) {this.type = type;}

    public void setFile(byte[] file) {this.file = file;}

    public void setState(boolean state) {this.isPublic = state;}

    public void setUser(String user) {this.user = user;}
}
