package ServerUtils;

import Objects.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ServerUtils{
//Get info from files

    public static FilesArray getFiles() {
        FilesArray files = new FilesArray();
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            files = mapper.readValue(new File("./Server/Backup/Files.json"), FilesArray.class);
        }
        catch(Exception e)
        {
            System.out.println("Error getFiles() from ServerUtils: " + e.toString());
        }
        return files;
    }

    public static UsersArray getUsers()
    {
        UsersArray users = new UsersArray();
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            users = mapper.readValue(new File("./Server/Backup/Users.json"), UsersArray.class);
        }
        catch(Exception e)
        {
            System.out.println("Error getUsers() from ServerUtils: " + e.toString());
        }
        return users;
    }

    public static int getFileID()
    {
        Id ID = new Id();
        try{
            ObjectMapper mapper = new ObjectMapper();
            ID = mapper.readValue(new File("./Server/Backup/FileID.json"), Id.class);
        }catch(Exception e){
            System.out.println("Error getFileID from ServerUtils: " + e.toString());
        }
        if(ID==null){ID=new Id();}
        return ID.getId();
    }

    public static int getUserID()
    {
        Id ID = new Id();
        try{
            ObjectMapper mapper = new ObjectMapper();
            ID = mapper.readValue(new File("./Server/Backup/UserID.json"), Id.class);
        }catch(Exception e){
            System.out.println("Error getUserID from ServerUtils: " + e.toString());
        }
        if(ID==null){ID=new Id();}
        return ID.getId();
    }

    //Save info to files
    public static void saveFiles(ArrayList<FileObject> files)
        {
            FilesArray f = new FilesArray(files);
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(new File("./Server/Backup/Files.json"), f);
            }catch (Exception e){
                System.out.println("Error saveFiles(files) from ServerUtils: " + e.toString());
            }
        }

    public static void saveUsers(ArrayList<User> users)
        {
            UsersArray u = new UsersArray(users);
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(new File("./Server/Backup/Users.json"), u);
            }catch (Exception e){
                System.out.println("Error saveUsers(users) from ServerUtils: " + e.toString());
            }
        }

    public static void saveFileID(int lastFileID)
    {
        Id ID = new Id(lastFileID);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File("./Server/Backup/FileID.json"), ID);
        }catch (Exception e){
            System.out.println("Error saveFileID(id) from ServerUtils: " + e.toString());
        }
    }

    public static void saveUserID(int lastUserID)
    {
        Id ID = new Id(lastUserID);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File("./Server/Backup/UserID.json"), ID);
        }catch (Exception e){
            System.out.println("Error saveUserID(id) from ServerUtils: " + e.toString());
        }
    }
}