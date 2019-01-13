/**
 * Created by sbp5 on 24/10/18.
 */
package Objects;

import java.util.ArrayList;
import java.util.Iterator;

public class User {

    private int id;
    private String name;
    private String password;
    private ArrayList<String> subscriptions = new ArrayList<>();

    public User(String name,String password, int id) {
        this.name=name;
        this.password=password;
        this.id = id;
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
        return this.subscriptions;
    }
    public boolean addSubscriptions(String subscription){
        try{
            this.subscriptions.add(subscription);
            return true;
        }catch (Exception e){
            System.out.println("Error al objecte user: "+e.toString());
            return false;
        }
    }
    public boolean deleteSubscriptions(String oldTag) {
        Iterator<String> iter = this.subscriptions.iterator();
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
