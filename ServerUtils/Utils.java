package ServerUtils;
import Objects.FileObject;
import Objects.User;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.json.*;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ServerUtils{
//Get info from files

    public ArrayList<FileObject> getFiles() throws ClassNotFoundException{
        ArrayList<FileObject> files = new ArrayList<>();
        FileObject file = new FileObject();
        try
        {
            InputStream is = new FileInputStream("./Files.json");
            String jsonTxt = is.toString();
            JSONArray baseJSONResponse = new JSONArray(jsonTxt);

            for (int i = 0; i < baseJSONResponse.length(); i++) {
                JSONObject fileObj = baseJSONResponse.getJSONObject(i);

                file.setTitle(fileObj.getString("title"));

                JSONArray array = fileObj.getJSONArray("description");

                List<String> asd = array.toList();
                file.setDescription();

                CurStudentObj.setmStudentID(StudentObj.getString("StudentID"));
                CurStudentObj.setmName(StudentObj.getString("Name"));
                CurStudentObj.setmGender(StudentObj.getString("Gender").charAt(0));
                String TempDOB = (StudentObj.getString("DOB"));
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MM dd HH:mm:ss z yyyy");
                CurStudentObj.setmNICN(StudentObj.getString("NICN"));
                String TempDOJ = (StudentObj.getString("DOJ"));
                CurStudentObj.setmGender(StudentObj.getString("Gender").charAt(0));
                JSONArray array = StudentObj.getJSONArray("Module");

            }
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
            InputStream inJson=Id.getResourceAsStream("./FileID.json");
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

class Id{
    int id;
    public Id(int id){
        this.id = id;
    }
}
