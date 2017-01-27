package nl.han.wilkozonnenberg.festivalapp;

import android.content.ContentValues;
import android.provider.SyncStateContract;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nl.han.wilkozonnenberg.festivalapp.data.FestivalEvent;
import nl.han.wilkozonnenberg.festivalapp.data.Performance;

/**
 * Created by Wilko on 08-11-16.
 */



public class Util {

    public static ContentValues objectToContentValues(Object o, Field... ignoredFields) {
        try {
            ContentValues values = new ContentValues();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            //Will ignore any of the fields you pass in here
            List<Field> fieldsToIgnore = Arrays.asList(ignoredFields);


            Field[] fields = o.getClass().getDeclaredFields();
            for(int i = 0; i < fields.length; i ++) {
                Field field = fields[i];
                field.setAccessible(true);

//                if(fieldsToIgnore.contains(field))
//                    continue;

                Object value = field.get(o);
                if(value != null) {
                    //This part just makes sure the content values can handle the field
                    if(value instanceof Double || value instanceof Integer || value instanceof String || value instanceof Boolean
                            || value instanceof Long || value instanceof Float || value instanceof Short)
                        values.put(field.getName(), value.toString());
                    else if (value instanceof Date)
                    values.put(field.getName(), dateFormat.format((Date) value));
                    else if (value instanceof FestivalEvent.Image[]) {
                        values.put("image_large", ((FestivalEvent)o).getLargeUrl().toString());
                        values.put("image_thumb", ((FestivalEvent)o).getThumbUrl().toString());
                    }
                    else if (value instanceof ArrayList){
                        continue;
                    }
                    else
                        throw new IllegalArgumentException("value could not be handled by field: " + value.toString());
                }
//                else
//                    Log.v(" UTIL ","value is null, so we don't include it");
            }

            return values;
        } catch(Exception e) {
            Log.e("error" ,e.getMessage());
            throw new NullPointerException("content values failed to build");
        }
    }

}