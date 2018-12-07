package CallBack;

import java.rmi.*;
import java.rmi.server.*;
import CallBack.*;
import Objects.*;
import RemoteObject.*;
import Server.*;
/**
 * This class implements the remote interface Callback.
 */
public class CallbackImpl extends UnicastRemoteObject implements ClientCallbackInterface
{
    public CallbackImpl () throws RemoteException
    {
        super( );
    }

    //The function called by the Server for each Client
    public String callMe (String message) throws RemoteException
    {
        String returnMessage = "Call back received: " + message;
        System.out.println(returnMessage);

        return returnMessage;
    }
} // end CallbackImplclass