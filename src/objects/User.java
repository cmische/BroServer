package objects;

import networking.Bro;
import networking.BroLocation;

import javax.xml.stream.Location;
import java.io.Serializable;
import java.util.ArrayList;

public class User  implements Serializable{
    public String uuid;
    public String broName;
    public String password;
    public String gcm;
    public BroLocation location = null;
    public ArrayList<Bro> bros = new ArrayList<Bro>();
    public ArrayList<Bro> blocked = new ArrayList<Bro>();

    public User() {

    }
    public User(String uuid, String name, String password, String gcm) {
        this.uuid = uuid;
        this.broName = name;
        this.password = password;
        this.gcm = gcm;
    }

    public ArrayList<Bro> getBros() {
        return bros;
    }

    public void addBro(Bro bro) {
        if (blocked.contains(bro)) {
            blocked.remove(bro);
        }
        bros.add(bro);
        System.out.println(this.broName + " added " + bro.broName + " as a bro.");
    }

    public void blockBro(Bro bro) {
        if(bros.contains(bro)) {
            bros.remove(bro);
        }
        blocked.add(bro);
    }

    public void removeBro(Bro bro) {
        for (int i = 0; i < bros.size(); i++) {
            if(bros.get(i) == bro) {
                bros.remove(i);
                System.out.println(this.broName + " removed " + bro.broName + " from bros.");
            }
        }
    }

    public BroLocation compareLocation(BroLocation broLocation) {
        double distance = distance(broLocation.latitude, this.location.latitude, broLocation.longitude,
                this.location.longitude);
        if (distance <= 20) {
            return this.location;
        }
        return null;
    }


    //distance equation from stackoverflow
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        return distance;
    }
}
