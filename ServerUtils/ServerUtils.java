package ServerUtils;

import Objects.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;

public class ServerUtils{
//Get info from files
    private static ServerInfo serverInfo;

    public static ServerInfo getServerInfo(){
        return ServerUtils.serverInfo;
    }

    public static int getServerConfig(){
        Id ID = new Id();
        try{
            ObjectMapper mapper = new ObjectMapper();
            ID = mapper.readValue(new File("./Server/Backup/Config.json"), Id.class);
        }catch(Exception e){
            System.out.println("Error getServerConfig() from ServerUtils: " + e.toString());
        }
        if(ID==null){ID=new Id();}
        return ID.getId();
    }

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

    public static int getFileID()
    {
        Id ID = new Id();
        try{
            ObjectMapper mapper = new ObjectMapper();
            ID = mapper.readValue(new File("Server/Backup/Files.json"), Id.class);
        }catch(Exception e){
            System.out.println("Error getFileID() from ServerUtils: " + e.toString());
        }
        if(ID==null){ID=new Id();}
        return ID.getId();
    }

    public static void saveServerInfo(ServerInfo toSave){
        ServerUtils.serverInfo = toSave;
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

    public static void saveFileID(int lastFileID)
    {
        Id ID = new Id(lastFileID);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File("Server/Backup/Files.json"), ID);
        }catch (Exception e){
            System.out.println("Error saveFileID(id) from ServerUtils: " + e.toString());
        }
    }

    public static void saveServerConfig(int id){
        Id ID = new Id(id);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File("./Server/Backup/Config.json"), ID);
        }catch (Exception e){
            System.out.println("Error saveServerConfig(id) from ServerUtils: " + e.toString());
        }
    }
}