package ServerUtils;

import Objects.FileObject;
import Objects.Type;
import Objects.User;
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

    public static ArrayList<FileObject> getFiles() {
        ArrayList<FileObject> files = new ArrayList<>();
        FileObject file = new FileObject();
        try
        {
            InputStream is = new FileInputStream("./Server/Backup/Files.json");
            String jsonTxt = is.toString();
            System.out.println(jsonTxt);
           /* if(jsonTxt.equals("'{}'")){
                return files;
            }else
            {
                JSONArray baseJSONResponse = new JSONArray(jsonTxt);

                for (int i = 0; i < baseJSONResponse.length(); i++) {
                    JSONObject fileObj = baseJSONResponse.getJSONObject(i);

                    file.setTitle(fileObj.getString("title"));

                    JSONArray array = fileObj.getJSONArray("description");
                    List<Object> objects = array.toList();
                    List<String> strings = new ArrayList<>(objects.size());
                    for (Object object : objects) {
                        strings.add(object.toString());
                    }
                    file.setDescription(strings);

                    file.setFileName(fileObj.getString("fileName"));

                    file.setId(fileObj.getInt("id"));

                    file.setType((Type)fileObj.get("type"));

                    file.setState(fileObj.getBoolean("isPublic"));

                    file.setFile((byte[])fileObj.get("file"));
                    files.add(file);
                }

            }*/
        }
        catch(Exception e)
        {
            System.out.println("Error getFiles() from ServerUtils: " + e.toString());
        }
        return files;
    }

    public static ArrayList<User> getUsers()
    {
        ArrayList<User> users = new ArrayList<>();
        User user = new User();
        try
        {
            InputStream is = new FileInputStream("./Server/Backup/Files.json");
            String jsonTxt = is.toString();
            JSONArray baseJSONResponse = new JSONArray(jsonTxt);

            for (int i = 0; i < baseJSONResponse.length(); i++) {
                JSONObject fileObj = baseJSONResponse.getJSONObject(i);

                user.setId(fileObj.getInt("id"));

                user.setName(fileObj.getString("name"));

                user.setName(fileObj.getString("password"));

                users.add(user);
            }
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
            InputStream is = new FileInputStream("./Server/Backup/FileID.json");
            String jsonTxt = is.toString();
            JSONObject jsonObject = new JSONObject(jsonTxt);
            ID.id =jsonObject.getInt("id");
        }catch(Exception e){
            System.out.println("Error getFileID from ServerUtils: " + e.toString());
        }
        return ID.id;
    }

    public static int getUserID()
    {
        Id ID = new Id();
        try{
            InputStream is = new FileInputStream("./Server/Backup/UserID.json");
            String jsonTxt = is.toString();
            JSONObject jsonObject = new JSONObject(jsonTxt);
            ID.id =jsonObject.getInt("id");
        }catch(Exception e){
            System.out.println("Error getUserID from ServerUtils: " + e.toString());
        }
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

    public static void saveFileID(int lastFileID) throws IOException {
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