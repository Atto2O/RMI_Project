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
    public

    public boolean publicFile;

    public FileObject(String title,String description, String fileName) {
        this.title=title;
        this.description=description;
        this.fileName=fileName;
    }


    public String getTitle(){
        return this.title;
    }

    public ArrayList<String> getDescription(){
        return this.description;
    }

    public String getFileName(){
        return this.fileName;
    }


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

}
