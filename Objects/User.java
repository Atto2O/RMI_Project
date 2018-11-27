/**
 * Created by sbp5 on 24/10/18.
 */
package Objects;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.registry.Registry;
import CallBack.*;
import Objects.*;
import RemoteObject.*;
import Server.*;

public class User {

    public String name;
    public String password;
    public int id;

    public User(String name,String password, int id) {
        this.name=name;
        this.password=password;
    }

    public String getName(){
        return this.name;
    }

    public String getPassword(){
        return this.password;
    }

    public int getId() { return this.id; }


    public void setName(String name){
        this.name=name;
    }

    public void setPassword(String password){
        this.password=password;
    }

    public void setId (int id) { this.id = id; }
}
