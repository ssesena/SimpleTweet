package com.codepath.apps.restclienttemplate.models;

import android.widget.ImageView;

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
    public long id;
    public int likes;
    //public JSONArray media;
    //public JSONObject picture;
    public String pictureUrl;
    public int retweets;
    public int originalTweetLikes = -1;
    public Boolean favorited;
    public Boolean retweeted;

    // empty constructor for Parceler library
    public Tweet(){

    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.id = jsonObject.getLong("id");
        tweet.likes = jsonObject.getInt("favorite_count");
        tweet.retweets = jsonObject.getInt("retweet_count");
        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        if (jsonObject.has("retweeted_status")) {
            tweet.originalTweetLikes = jsonObject.getJSONObject("retweeted_status").getInt("favorite_count");
        }
        if (jsonObject.getJSONObject("entities").has("media")){
            tweet.pictureUrl = jsonObject.getJSONObject("entities").getJSONArray(("media")).getJSONObject(0).getString("media_url_https");
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
