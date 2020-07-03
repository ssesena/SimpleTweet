package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    Context context;
    List<Tweet> tweets;
    public static final String TAG = "TweetsAdapter";

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.clearImage();
        holder.resetIcons();
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    //Clean all elements of the recycler
    public void clear(){
        tweets.clear();
        notifyDataSetChanged();
    }

    //Add a list of items
    public void addAll(List<Tweet> tweetList){
        tweets.addAll(tweetList);
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvName;
        TextView tvTimestamp;
        ImageView ivPicture;
        ImageView ivRetweet;
        ImageView ivFavorite;
        TextView tvFavoriteCount;
        TextView tvRetweetCount;

        TwitterClient client;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            client = TwitterApp.getRestClient(context);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvName = itemView.findViewById(R.id.tvName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivPicture = itemView.findViewById(R.id.ivPicture);
            ivRetweet = itemView.findViewById(R.id.ivRetweet);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            tvFavoriteCount = itemView.findViewById(R.id.tvFavoriteCount);
            tvRetweetCount = itemView.findViewById(R.id.tvRetweetCount);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText("@" + tweet.user.screenName);
            Glide.with(context).load(tweet.user.profileImageUrl).circleCrop().into(ivProfileImage);
            tvName.setText(tweet.user.name);
            shortenUsername();
            tvTimestamp.setText("- "+ getRelativeTimeAgo(tweet.createdAt));
            if (tweet.originalTweetLikes != -1){
                tvFavoriteCount.setText(""+tweet.originalTweetLikes);
                Log.i(TAG,"RT"+tweet.originalTweetLikes);
            } else {
                Log.i(TAG,""+tweet.likes);
                tvFavoriteCount.setText(""+tweet.likes);
            }
            tvRetweetCount.setText(""+tweet.retweets);
            if (tweet.pictureUrl != null){
                Glide.with(context).load(tweet.pictureUrl).into(ivPicture);
            }
            if (tweet.favorited){
                ivFavorite.setImageResource(R.drawable.ic_vector_heart);
            }
            if (tweet.retweeted){
                ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
            }
            likeTap(tweet);
            retweetTap(tweet);

        }

        public void likeTap(final Tweet tweet){
            ivFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ivFavorite.getDrawable().getConstantState() == context.getDrawable(R.drawable.ic_vector_heart_stroke).getConstantState()){
                        ivFavorite.setImageResource(R.drawable.ic_vector_heart);
                        client.likeTweet(new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(TAG, "onSuccess for like");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.i(TAG, "onFailure for like");

                            }
                        }, tweet.id);
                        if (tweet.originalTweetLikes != -1){
                            tvFavoriteCount.setText((tweet.originalTweetLikes+1)+"");
                        } else {
                            tvFavoriteCount.setText((tweet.likes+1)+"");
                        }
                    } else{
                        ivFavorite.setImageResource(R.drawable.ic_vector_heart_stroke);
                        client.dislikeTweet(new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(TAG, "onSuccess for dislike");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.i(TAG, "onFailure for dislike");

                            }
                        }, tweet.id);
                        if (tweet.originalTweetLikes != -1){
                            tvFavoriteCount.setText((tweet.originalTweetLikes-1)+"");
                        } else {
                            tvFavoriteCount.setText((tweet.likes-1)+"");
                        }
                    }
                }
            });
        }

        public void retweetTap(final Tweet tweet){
            ivRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ivRetweet.getDrawable().getConstantState() == context.getDrawable(R.drawable.ic_vector_retweet_stroke).getConstantState()){
                        ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
                        client.retweet(new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(TAG, "onSuccess for retweet");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.i(TAG, "onFailure for retweet");

                            }
                        }, tweet.id);
                        tvRetweetCount.setText((tweet.retweets+1)+"");

                    } else {
                        ivRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                        client.unretweet(new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(TAG, "onSuccess for unretweet");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.i(TAG, "onFailure for unretweet");

                            }
                        }, tweet.id);
                        tvRetweetCount.setText((tweet.retweets-1)+"");
                    }
                }
            });

        }

        public void clearImage(){
            Glide.with(context).clear(ivPicture);
        }

        public void resetIcons(){
            ivRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            ivFavorite.setImageResource(R.drawable.ic_vector_heart_stroke);
        }

        public String getRelativeTimeAgo(String rawJsonDate) {
            String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
            SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
            sf.setLenient(true);

            String relativeDate = "";
            try {
                long dateMillis = sf.parse(rawJsonDate).getTime();
                relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return relativeDate;
        }

        public void shortenUsername(){
            String name = (String) tvName.getText();
            String screenName = (String) tvScreenName.getText();
            int nameLength = name.length();
            int screenNameLength = screenName.length();
            if(nameLength > 15){
                tvName.setText(name.substring(0,12)+"...");
            }

        }
    }
}
