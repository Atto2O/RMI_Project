package ServerUtils.WS_manager;

import CallBack.CallbackImpl;
import Client.Client;
import Objects.FileObject;
import Objects.FileObjectInfo;
import Objects.User;
import RemoteObject.Garage;
import RemoteObject.GarageImp;
import ServerUtils.ServerInfo;
import ServerUtils.ServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.util.*;

import ServerUtils.ContentsPack;

import static java.lang.Integer.parseInt;


public class DataManager {

    public static URL url;
    public static final String testURL ="/contents";
    public static final String filesURL = "/contents";
    public static final String usersURL = "/users";
    public static final String serversURL = "/servers";
    public static String url_address; //= "http://82c8190f.ngrok.io/api/resources";

    public static void setURL_String(String ngrok){
        DataManager.url_address = "http://" + ngrok + ".ngrok.io/api/resources";
    }

    public static boolean test(){ // https://stackoverflow.com/questions/12916169/how-to-consume-rest-in-java
        try {
            String test_URL = DataManager.url_address + DataManager.testURL + "/test";
            DataManager.url = new URL(test_URL);
            HttpURLConnection conn = (HttpURLConnection) DataManager.url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if(conn.getResponseCode() != 200){
                throw new RuntimeException("Failed : HTTP Error code : " + conn.getResponseCode());
            }
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String output;
            while ((output = br.readLine()) != null){
                System.out.println(output);
            }
            conn.disconnect();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    ///////  TO WEB SERVICE  /////////////////////////////////
    //region<FILES>
    public static int filePOST(FileObject file){
        FileObjectInfo file_toUpload = new FileObjectInfo(file, ServerUtils.getServerInfo());
        //put this info to Web Service DB
        int id = 0;
        return id;
    }

    public static void filePUT(FileObject file){

    }

    public static void fileDELETE(int id){

    }

    public ArrayList<FileObject> fileGET_Array(String description){
        ArrayList<FileObject> files = new ArrayList<>();

        ArrayList<ContentsPack> packs = new ArrayList<>();
        Map<Integer, ArrayList<Integer>> servers = new HashMap<Integer, ArrayList<Integer>>();
        ArrayList<Integer> servers_to_search = new ArrayList<>();
        ArrayList<ServerInfo> servers_to_access = new ArrayList<ServerInfo>();
        try {
            String test_URL = DataManager.url_address + DataManager.filesURL + "/like?word="+description;
            DataManager.url = new URL(test_URL);
            HttpURLConnection conn = (HttpURLConnection) DataManager.url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if(conn.getResponseCode() != 200){
                throw new RuntimeException("Failed : HTTP Error code : " + conn.getResponseCode());
            }
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String output;
            while ((output = br.readLine()) != null){
                System.out.println(output);
                JSONArray jsonArray = new JSONArray(output);
                ObjectMapper mapper = new ObjectMapper();
                for (int i=0; i<jsonArray.length(); i++) {
                    packs.add(mapper.readValue(jsonArray.getString(i), ContentsPack.class));
                }
                for (ContentsPack c:packs) {
                    if (!servers.containsKey(c.getServerID())){
                        servers.put(c.getServerID(), new ArrayList<>(c.getId()));
                    }else{
                        ArrayList<Integer> value = servers.get(c.getServerID());
                        value.add(c.getId());
                        servers.replace(c.getServerID(), value);
                    }
                }
                servers_to_search.addAll(servers.keySet());
                servers_to_access = DataManager.serverGET_byID(servers_to_search);
                for (ServerInfo s:servers_to_access) {
                    ArrayList values =new ArrayList();
                    values.addAll(servers.get(s.getId()));
                    files.addAll(DataManager.getFileFromServer(values, s));
                }
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }
    //endregion

    //region<USERS>
    public static User userGET(String name){
        User user = new User();
        try {
            String userGET_URL = DataManager.url_address + DataManager.usersURL + "/name?name=" + name;
            DataManager.url = new URL(userGET_URL);
            HttpURLConnection conn = (HttpURLConnection) DataManager.url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if(conn.getResponseCode() != 200){
                throw new RuntimeException("Failed : HTTP Error code : " + conn.getResponseCode());
            }
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String output;
            while ((output = br.readLine()) != null){
                System.out.println(output);
                ObjectMapper mapper = new ObjectMapper();
                user = mapper.readValue(output, User.class);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public static int userPOST(String name, String password){
        int id = 0;
        try {
            String userPOST_URL = DataManager.url_address + DataManager.usersURL + "/new";
            DataManager.url = new URL(userPOST_URL);
            HttpURLConnection conn = (HttpURLConnection) DataManager.url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            String input = "{\"name\":\""+name+"\",\"password\":\""+password+"\",\"subscriptions\":[]}";
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();
            if(conn.getResponseCode() != 200){
                throw new RuntimeException("Failed : HTTP Error code : " + conn.getResponseCode());
            }
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String output;
            System.out.println("NEW SERVER ID:");
            while ((output = br.readLine()) != null){
                System.out.println(output);
                id = parseInt(output);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public static void userPUT(User user){
        try {
            String userPUT_URL = DataManager.url_address + DataManager.usersURL + "/modify";
            DataManager.url = new URL(userPUT_URL);
            HttpURLConnection conn = (HttpURLConnection) DataManager.url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            String input = "{\"id\":\""+user.getId()+"\",\"name\":\""+user.getName()+"\",\"password\":\""+user.getPassword()+"\",\"subscriptions\":"+user.getSubscriptions()+"}";
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();
            if(conn.getResponseCode() != 200){
                throw new RuntimeException("Failed : HTTP Error code : " + conn.getResponseCode());
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region<SERVER>
    public static int serverPOST(ServerInfo server){
        int id = 0;
        try {
            String serverPOST_URL = DataManager.url_address + DataManager.serversURL + "/new";
            DataManager.url = new URL(serverPOST_URL);
            HttpURLConnection conn = (HttpURLConnection) DataManager.url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            String input = "{\"port\":\""+server.getPort()+"\",\"ip\":\""+server.getAddress()+"\"}";
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();
            if(conn.getResponseCode() != 200){
                throw new RuntimeException("Failed : HTTP Error code : " + conn.getResponseCode());
            }
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String output;
            System.out.println("NEW SERVER ID:");
            while ((output = br.readLine()) != null){
                System.out.println(output);
                id = parseInt(output);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public static void serverPUT(ServerInfo server){
        try {
            String serverPUT_URL = DataManager.url_address + DataManager.serversURL + "/modify";
            DataManager.url = new URL(serverPUT_URL);
            HttpURLConnection conn = (HttpURLConnection) DataManager.url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            String input = "{\"id\":\""+server.getId()+"\",\"port\":\""+server.getPort()+"\",\"ip\":\""+server.getAddress()+"\"}";
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();
            if(conn.getResponseCode() != 200){
                throw new RuntimeException("Failed : HTTP Error code : " + conn.getResponseCode());
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ServerInfo> serverGET_byID(ArrayList<Integer> ids){
        ArrayList<ServerInfo> servers = new ArrayList<ServerInfo>();
        try {
            String serverGET_URL = DataManager.url_address + DataManager.serversURL + "/byID";
            DataManager.url = new URL(serverGET_URL);
            HttpURLConnection conn = (HttpURLConnection) DataManager.url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            String input = "{\"list\":"+ids+"}";
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();
            if(conn.getResponseCode() != 200){
                throw new RuntimeException("Failed : HTTP Error code : " + conn.getResponseCode());
            }
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String output;
            while ((output = br.readLine()) != null){
                System.out.println(output);
                JSONArray jsonArray = new JSONArray(output);
                ObjectMapper mapper = new ObjectMapper();
                for (int i=0; i<jsonArray.length(); i++) {
                    servers.add(mapper.readValue(jsonArray.getString(i), ServerInfo.class));
                }
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return servers;
    }



    public static ArrayList<ServerInfo> serverGET_all(){
        ArrayList<ServerInfo> servers = new ArrayList<ServerInfo>();
        try {
            String serverGET_URL = DataManager.url_address + DataManager.serversURL + "/all";
            DataManager.url = new URL(serverGET_URL);
            HttpURLConnection conn = (HttpURLConnection) DataManager.url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if(conn.getResponseCode() != 200){
                throw new RuntimeException("Failed : HTTP Error code : " + conn.getResponseCode());
            }
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String output;
            while ((output = br.readLine()) != null){
                System.out.println(output);
                JSONArray jsonArray = new JSONArray(output);
                ObjectMapper mapper = new ObjectMapper();
                for (int i=0; i<jsonArray.length(); i++) {
                    servers.add(mapper.readValue(jsonArray.getString(i), ServerInfo.class));
                }
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return servers;
    }
    //endregion
    //////////////////////////////////////////////////////////


    ///////  TO OTHER SERVERS ////////////////////////////////
    public static ArrayList<FileObject> getFileFromServer(ArrayList<Integer> ids, ServerInfo serverInfo){
        String registryURL = "rmi://"+ serverInfo.getId() +":" + serverInfo.getPort() + "/some";
        try {
            GarageImp h = (GarageImp) Naming.lookup(registryURL);
            return h.getFileObjects(ids);
        }catch (Exception e) {
            System.out.println("Access to Server "+ serverInfo.getId()+" dennied. (IP: "+serverInfo.getAddress()+"\tPORT: "+serverInfo.getPort()+")\nUnable to get files from this Server.");
        }
        return null;
    }
    //////////////////////////////////////////////////////////
}