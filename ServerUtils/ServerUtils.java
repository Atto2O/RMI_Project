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
            System.out.println(files.toString());
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
        System.out.println(users.toString());
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
        System.out.println(ID.id);
        return ID.id;
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
        System.out.println(ID.id);
        return ID.id;
    }

    //Save info to files
    public static void saveFiles(ArrayList<FileObject> files)
        {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(new File("./Server/Backup/Files.json"), files);
            }catch (Exception e){
                System.out.println("Error saveFiles(files) from ServerUtils: " + e.toString());
            }
        }

    public static void saveUsers(ArrayList<User> users)
        {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(new File("./Server/Backup/Users.json"), users);
            }catch (Exception e){
                System.out.println("Error saveUsers(users) from ServerUtils: " + e.toString());
            }
        }

    public static void saveFileID(int lastFileID)
    {
        Id ID = new Id(lastFileID);
        System.out.println(ID.toString());
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
        System.out.println(ID.toString());
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File("./Server/Backup/UserID.json"), ID);
        }catch (Exception e){
            System.out.println("Error saveUserID(id) from ServerUtils: " + e.toString());
        }
    }
}