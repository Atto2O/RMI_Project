package Objects;

import java.util.ArrayList;

public class UsersArray {

    private ArrayList<User> users = new ArrayList<>();
    private boolean empty;

    public UsersArray(ArrayList<User> users){
        this.users=users;
    }
    public UsersArray(){}

    public ArrayList<User> getUsers() {
        return this.users;
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public void removeUser(User users)
    {
        this.users.remove(users);
    }

    public int size(){
        return this.users.size();
    }

    public boolean isEmpty(){
        return this.users==null||this.users.size()==0;
    }
}
