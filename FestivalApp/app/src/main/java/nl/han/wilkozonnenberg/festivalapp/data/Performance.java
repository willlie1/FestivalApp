package nl.han.wilkozonnenberg.festivalapp.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Wilko on 28-09-16.
 */

public class Performance implements Serializable {
//    public Boolean concession;
//    public Boolean concession_additional;
//    public Boolean concession_family;
    public String id;
    public Date end;
    public double price;
    public Date start;
    public String title;

}
