package com.ndrive.codechallengeomdb.application.request;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ndrive.codechallengeomdb.R;
import com.ndrive.codechallengeomdb.model.Movie;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MovieRequest {

    private final Context context;

    public MovieRequest(Context context) {
        this.context = context;
    }

    /**
     * Do a http request to get a movie list
     */
    public void requestMovieList(String query, Response.Listener<JSONObject> successListener,Response.ErrorListener errorListener) throws IOException, JSONException {
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = getURL(R.string.url_title_query, query);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                successListener,
                errorListener);

        queue.add(jsonRequest);
    }

    /**
     * Parse JsonObject to a movie list
     */
    public List<Movie> parseJsonToMovieList(JSONObject response) throws JSONException {
        if(!response.has("Error")) {
            String jsonArray = response.getString("Search");

            Type listType = new TypeToken<ArrayList<Movie>>() {}.getType();

            return new Gson().fromJson(jsonArray, listType);
        }else{
            Log.w(getClass().getSimpleName(), response.getString("Error"));
            return new ArrayList<>();
        }
    }

    /**
     * Request movie detail from remote server
     */
    public void requestMovieDetail(String movieId, Response.Listener<JSONObject> successListener,Response.ErrorListener errorListener) throws IOException, JSONException {
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = getURL(R.string.url_id_query, movieId);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                successListener,
                errorListener);

        queue.add(jsonRequest);
    }

    /**
     * Parse JsonObject to a Movie object
     * @param response
     * @return
     * @throws Exception
     */
    public Movie parseJsonToMovie(JSONObject response) throws Exception {
        if(!response.has("Error")) {
            return new Gson().fromJson(response.toString(), Movie.class);
        }else{
            throw new Exception(response.getString("Error"));
        }
    }

    /**
     * Get service url
     */
    String getURL(@StringRes int resource, String parameter){
        return context.getString(resource, context.getString(R.string.url), parameter);
    }
}
