/**
 * Created by sbp5 on 24/10/18.
 */
package Objects;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Iterator;

import CallBack.*;
import Objects.*;
import RemoteObject.*;
import Server.*;

public class User {

    private int id;
    private String name;
    private String password;
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
    public ArrayList<String> getSubscriptions(){
        return this.Subscriptions;
    }
    public boolean addSubscriptions(String subscription){
        try{
            this.Subscriptions.add(subscription);
            return true;
        }catch (Exception e){
            System.out.println("Error al objecte user: "+e.toString());
            return false;
        }
    }
    public boolean deleteSubscriptions(String oldTag) {
        Iterator<String> iter = this.Subscriptions.iterator();
        //We search for the currently user
        while (iter.hasNext()) {
            if (iter.next().equals(oldTag)) {
                iter.remove();
                return true;
            }

        }
        return false;
    }
    public void setName(String name){
        this.name=name;
    }

    public void setPassword(String password){
        this.password=password;
    }

    public void setId (int id) { this.id = id; }
}
