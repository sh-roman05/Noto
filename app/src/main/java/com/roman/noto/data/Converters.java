package com.roman.noto.data;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Converters {

    /* Date */

    @TypeConverter
    public static Date dateFromTimestamp(Long value) {
        return (value == null) ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return (date == null) ? null : date.getTime();
    }

    /* ArrayList<Integer> */

    @TypeConverter
    public static ArrayList<Integer> stringToArrayList(String value) {
        Type listType = new TypeToken<ArrayList<Integer>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String stringFromArrayList(ArrayList<Integer> list) {
        return new Gson().toJson(list);
    }

    /* Map<Integer, String> */

    @TypeConverter
    public static Map<Integer, String> stringToMap(String value) {
        Type mapType = new TypeToken<Map<Integer, String>>() {}.getType();
        return new Gson().fromJson(value, mapType);
    }

    @TypeConverter
    public static String stringFromMap(Map<Integer, String> map) {
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    /* HashSet<Integer> */

    @TypeConverter
    public static HashSet<Integer> stringToSet(String value) {
        Type setType = new TypeToken<HashSet<Integer>>() {}.getType();
        return new Gson().fromJson(value, setType);
    }

    @TypeConverter
    public static String stringFromSet(HashSet<Integer> set) {
        Gson gson = new Gson();
        return gson.toJson(set);
    }
}
