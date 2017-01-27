package nl.han.wilkozonnenberg.festivalapp.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Wilko on 28-09-16.
 */

public class Category implements Serializable {
    public ArrayList<String> strand_titles;
    public ArrayList<String> subjects;
    public ArrayList<Keywords> Keywords;
}
