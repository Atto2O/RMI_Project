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

public class FileObject {

    public String title;
    public ArrayList<String> description;
    public String fileName;
    public int id;
    public Type type;
    public boolean isPublic;
    public byte[] file;

    public FileObject(String title,ArrayList<String> description, String fileName, Type type, byte[] file, boolean publicFile) {
        this.title=title;
        this.description=description;
        this.fileName=fileName;
        this.type = type;
        this.file = file;
        this.isPublic = publicFile;
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

    public Type getType() { return type; }

    public byte[] getFile() {return file;}

    //Setters
    public void setTitle(String title){
        this.title=title;
    }

    public void setDescription(ArrayList<String> description){
        this.description=description;
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
}
