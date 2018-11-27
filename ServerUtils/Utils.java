package ServerUtils;

import java.util.ArrayList;
import RemoteObject.*;

public static class ServerUtils(){
//Get info from files

    public static ArrayList<FileObject> getFiles() throws ClassNotFoundException{
        ArrayList<FileObject> files = new ArrayList<>();
        try
        {
            JsonReader reader = new JsonReader(new FileReader("./Files.json"));
            reader.beginArray();
            while (reader.hasNext()) {
                FileObject value = new ObjectMapper().readValue(reader.nextString(), FileObject.class);
                files.add(value);
            }
            reader.endArray();
            reader.close();

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
        try
        {
            JsonReader reader = new JsonReader(new FileReader("./Users.json"));
            reader.beginArray();
            while (reader.hasNext()) {
                User value = new ObjectMapper().readValue(reader.nextString(), User.class);
                users.add(value);
            }
            reader.endArray();
            reader.close();

        }
        catch(Exception e)
        {
            System.out.println("Error getUsers() from ServerUtils: " + e.toString());
        }
        return users;
    }

    public static int getFileID()
    {
        int id = -1;
        try{
            InputStream inJson=int.getResourceAsStream("./FileID.json");
            Id ID = new ObjectMapper().readValue(inJson,Id.class);
            id = ID.id;
        }catch(Exception e){
            System.out.println("Error getFileID from ServerUtils: " + e.toStrint());
        }
        return id;
    }

    public static int getUserID()
    {
        int id = -1;
        try{
            InputStream inJson=int.getResourceAsStream("./UserID.json");
            Id ID = new ObjectMapper().readValue(inJson,Id.class);
            id = ID.id;
        }catch(Exception e){
            System.out.println("Error getUserID from ServerUtils: " + e.toStrint());
        }
        return id;
    }


    //Save info to files
    public static void saveFiles(ArrayList<FileObject> files)
        {

        }

    public static void saveUsers(ArrayList<User> users)
        {

        }

    public static void saveFileID(int lastFileID)
        {

        }

    public static void saveUserID(int lastUserID)
        {


        }
}

public class Id(){
    int id;
    public Id(int id){
        this.id = id;
    }
}
