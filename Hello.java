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

public interface Hello extends Remote{
    public String sayHello() throws java.rmi.RemoteException;

    public String saveFile(byte[] myByteArray, String filename)  throws java.rmi.RemoteException;

    public String searchTitle(String file) throws java.rmi.RemoteException;

}
