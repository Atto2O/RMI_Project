/**
 * Created by sbp5 on 24/10/18.
 */
import java.rmi.*;
import java.rmi.server.*;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class HelloImpl extends UnicastRemoteObject implements Hello {

    public HelloImpl() throws RemoteException
    {
        super ();
    }

    @Override
    public String sayHello() throws RemoteException {
        return "Hello, Paquito!";
    }

    /*@Override
    public int printNumber(float f) throws RemoteException {
        return (int) f;
    }*/
    
    @Override
    public String saveFile(byte[] myByteArray, String filename) throws RemoteException {
        try (FileOutputStream fos = new FileOutputStream("./garage/"+filename)) {
            System.out.println(fos);
            fos.write(myByteArray);
            //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
        }catch(Exception e){
            System.out.println("malemnte a anat \n");
            return "erro al guardar";
        }
        return "Saved!!";
    }

    @Override
    public String searchTitle(String file) throws RemoteException {

        return "We fins it: "+file+" !!";
    }

}
