package ServerUtils.WS_manager;

import Objects.FileObject;
import Objects.FileObjectInfo;
import ServerUtils.ServerInfo;
import ServerUtils.ServerUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DataManager {
    public static ServerInfo serverInfo = ServerUtils.getServerInfo();

    public static URL url;
    public static final String url_address = "http://172.16.130.82:8080/Web-serviceWeb/rest/content";

    public static void test(){ // https://stackoverflow.com/questions/12916169/how-to-consume-rest-in-java
        try {
            String testURL = DataManager.url_address + "/test";
            DataManager.url = new URL(testURL);
            HttpURLConnection conn = (HttpURLConnection) DataManager.url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if(conn.getResponseCode() != 200){
                throw new RuntimeException("Failed : HTTP Error code : " + conn.getResponseCode());
            }
            System.out.println("Before print");
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String output;
            while ((output = br.readLine()) != null){
                System.out.println(output);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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