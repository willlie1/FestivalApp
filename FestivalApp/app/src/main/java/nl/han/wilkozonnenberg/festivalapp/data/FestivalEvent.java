package nl.han.wilkozonnenberg.festivalapp.data;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class FestivalEvent implements Serializable {
//    public String age_category;
//    public String artist;
//    public String artist_type;
//    public ArrayList<Category> categories;
//    public String code;
//    public String country;
    public String description;
//    public String description_teaser;


//    public String festival;
//    public String festival_id;
//    public String fringe_first;
    public String genre;
//    public String genre_tags;
    public Image[] images; /* You must register the custom ImagesDeserializer (below) to
                                parse this attribute correctly */
    public double latitude;
    public double longitude;
//    public String non_english;
//    public PerformanceSpace performance_space;
    public ArrayList<Performance> performances;
//    public int performers_number;
//    public String status;

    public String title;
//    public String twitter;
//    public Date updated;
//    public String url;
//    public Venue venue;
//    public String warnings;
//    public String website;


    /**
     * Returns the url of the first image that has type thumb
     */
    public String getThumbUrl() {
        for (Image image : images) {
            if (image.type.equals("thumb")) {
                return image.versions.square.url;
            }
        }
        return "some default url to an image with a red cross indicating that we have no image for you";
    }

    public String getLargeUrl() {
        for (Image image : images) {
            if (image.type.equals("thumb")) {
                return image.versions.large.url;
            }
        }
        return "some default url to an image with a red cross indicating that we have no image for you";
    }

    /** This class doesn't correspond to any key in the JSON of the festival API.
     * Register the ImagesDeserializer as a typeAdapter at the GSONBuilder
     *
     *
     *  Gson gson = new GsonBuilder()
     *        .registerTypeAdapter(FestivalEvent.Image[].class,
     *        new FestivalEvent.ImagesDeserializer())
     *        .create();
     *  
     *  now call  gson.fromJson(the response object with the data, the class you need) to 
     *  obtain the data
     * **/
    public static class Image implements Serializable {
        public Versions versions;
        public String type;

        public Image(String type, Versions versions) {
            this.versions = versions;
            this.type = type;
        }
    }

    public static class Versions implements Serializable{
        @SerializedName("original")
        public ImageVersion large;

        @SerializedName("square-75")
        public ImageVersion square;
    }

    public static class ImageVersion implements Serializable {
        public String url;
    }

    /**
     * The festival api returns the images as a object with hash-keys with each hash-key holding
     * all data (and versions) of a particular image.
     * Because each hash-key is unique we need some custom deserialization to get all images
     * in an array.
     */
    public static class ImagesDeserializer implements JsonDeserializer<Image[]>, Serializable {
        @Override

        public Image[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {

            ArrayList<Image> festivalImagesList = new ArrayList<>();

            /* Iterate over all images (the hashes )in the Json data and put the versions data
            in a new arraylist */
            Iterator<Map.Entry<String, JsonElement>> it = json.getAsJsonObject().entrySet().iterator();
            while(it.hasNext()) {

                /* Grab one image node */
                JsonObject imageJson = it.next().getValue().getAsJsonObject();

                /* Get the type node */
                JsonElement jsonTypeData = imageJson.get("type");
                /* Get the data in the type node. Luckily this data is just a string */
                String imageType = context.deserialize(jsonTypeData, String.class);
                /* Get all versions node*/
                JsonElement jsonVersionsData = imageJson.get("versions");
                /* The data in the versions node is another JSON object with element so we need to traverse
                * these nodes as well. Luckily we can just use the default GSON deserializer providing
                * our Versions class defined above */
                Versions imageVersions =
                        context.deserialize(jsonVersionsData, Versions.class);

                /* Create a new Festival.Image node object and add it to the list of images*/
                Image festivalImage = new Image(imageType, imageVersions);
                festivalImagesList.add(festivalImage);

            }

            return festivalImagesList.toArray(new Image[festivalImagesList.size()]);
        }
    }
}



