/**
 * Created by sbp5 on 24/10/18.
 */
package RemoteObject;

import java.rmi.*;
import java.rmi.server.*;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import CallBack.*;
import Objects.*;
import RemoteObject.*;
import Server.*;
import java.util.ArrayList;

public interface Garage extends Remote{

    public boolean user_signup(String NewNomUsuari, String contrasenya)  throws RemoteException;
    public boolean user_login (String NomUsuari, String contrasenya)  throws RemoteException;
    public String uploadFile(byte[] myByteArray, String filename)  throws java.rmi.RemoteException;
    public  ArrayList<FileObject> searchFile (String keyText) throws java.rmi.RemoteException;
    public byte[] downloadFile (String title) throws java.rmi.RemoteException;
    public void addCallback (ClientCallbackInterface callbackObject) throws java.rmi.RemoteException;
    public void deleteCallback (ClientCallbackInterface callbackObject) throws java.rmi.RemoteException;

}
