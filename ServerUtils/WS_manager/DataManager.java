package ServerUtils.WS_manager;

import Objects.FileObject;
import Objects.FileObjectInfo;
import Objects.FilesArray;
import ServerUtils.ServerInfo;
import ServerUtils.ServerUtils;

import java.util.ArrayList;

public class DataManager {
    public static ServerInfo serverInfo = ServerUtils.getServerInfo();

    ///////  TO WEB SERVICE  /////////////////////////////////
    public static int putFileToWS(FileObject file){
        FileObjectInfo file_toUpload = new FileObjectInfo(file, DataManager.serverInfo);
        //put this info to Web Service DB
        int id = 0;
        return id;
    }

    public static void deleteFileFromWS(int id){
    }

    public void getFilesFromWS(String description){


    }
    //////////////////////////////////////////////////////////


    ///////  TO OTHER SERVERS ////////////////////////////////
    public FileObject getFileFromServer(int id, ServerInfo serverInfo){
        //use the server info to search an specific FileObject
        return new FileObject();
    }
    //////////////////////////////////////////////////////////

}
