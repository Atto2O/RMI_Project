/**
 * Created by sbp5 on 24/10/18.
 */
package Objects;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import CallBack.*;
import Objects.*;
import RemoteObject.*;
import Server.*;

public class User {

    private int id;
    private String name;
    private String password;
    private int callbackid=-1;
    private ArrayList<String> Subscriptions= new ArrayList<>();

    public User(String name,String password, int id) {
        this.name=name;
        this.password=password;
        this.id = id;
        //this.Subscriptions = new ArrayList<>();
    }

    public User(){}

    public String getName(){
        return this.name;
    }

    public String getPassword(){
        return this.password;
    }

    public int getId() { return this.id; }
    public int getCallbackid() { return this.callbackid; }
    public ArrayList<String> getSubscriptions(){
        return this.Subscriptions;
    }
    public void addSubscriptions(String subscription){
        try{
            this.Subscriptions.add(subscription);
        }catch (Exception e){
            System.out.println("Error al objecte user: "+e.toString());
        }


    }
    public void setName(String name){
        this.name=name;
    }

    public void setPassword(String password){
        this.password=password;
    }

    public void setId (int id) { this.id = id; }
    public void setCallbackid (int callbackid) { this.callbackid = callbackid; }

}
