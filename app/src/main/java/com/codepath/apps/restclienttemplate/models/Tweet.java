package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {
    public String body;
    public String createdAt;
    public User user;
    //public JSONArray media;
    //public JSONObject picture;
    public String pictureUrl;

    // empty constructor for Parceler library
    public Tweet(){

    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        //tweet.media = jsonObject.getJSONArray("media"); tweet.media.length() > 0; getJSONArray("media").length() > 0)
        if (jsonObject.getJSONObject("entities").has("media")){
            //tweet.picture = tweet.media.getJSONObject(0);
            tweet.pictureUrl = jsonObject.getJSONObject("entities").getJSONArray(("media")).getJSONObject(0).getString("media_url_https");
            //tweet.picture.getString("media_url_https");
        }
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length();i++){
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }
}
