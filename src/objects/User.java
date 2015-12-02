package objects;

import networking.Bro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class User  implements Serializable{
    public String uuid;
    public String name;
    public String password;
    public String gcm;
    public ArrayList<Bro> bros;
    public ArrayList<Bro> blocked;

    public User() {

    }
    public User(String uuid, String name, String password, String gcm) {
        this.uuid = uuid;
        this.name = name;
        this.password = password;
        this.gcm = gcm;
    }

    public ArrayList<Bro> getBros() {
        return bros;
    }

    public void addBro(Bro bro) {
        bros.add(bro);
    }

    public void removeBro(Bro bro) {
        for (int i = 0; i < bros.size(); i++) {
            if(bros.get(i) == bro) {
                bros.remove(i);
            }
        }
    }
}
