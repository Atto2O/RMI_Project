package CallBack;

import java.rmi.*;
import CallBack.*;
import Objects.*;
import RemoteObject.*;
import Server.*;


public interface ClientCallbackInterface extends java.rmi.Remote{
    // This remote method is invoked by a callback
    // server to make a callback to an client which
    // implements this interface.
    // @param message - a string containing information for the
    //                  client to process upon being called back.

    public String callMe(String message)
            throws java.rmi.RemoteException;

} // end interface
