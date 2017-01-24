package com.ndrive.codechallengeomdb;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ndrive.codechallengeomdb.application.request.MovieRequest;
import com.ndrive.codechallengeomdb.model.Movie;

import org.hamcrest.core.Is;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
@Config( constants = BuildConfig.class, packageName = "com.ndrive.codechallengeomdb", manifest = "src/main/AndroidManifest.xml", sdk = 19)
public class MovieRequestUnitTest {

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }


    @Test
    public void requestMoviesTest() throws Exception {
        System.out.println("Requesting movie list");
        final MovieRequest movieController = new MovieRequest(RuntimeEnvironment.application);

        final List<Movie> movieList = new ArrayList<>();

        movieController.requestMovieList("a",new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    movieList.addAll(movieController.parseJsonToMovieList(response));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });

        //Thread sleep is necessary to asynchronous task with volley
        Thread.sleep(3000);
        ShadowApplication.runBackgroundTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        Thread.sleep(3000);

        System.out.print("Movies found: "+movieList.size());
        assertThat(movieList.isEmpty(), Is.is(false));
    }

    @Test
    public void requestMovieDetailTest() throws Exception {
        String movieId = "tt0076759";
        System.out.println("Requesting movie details: "+movieId);
        final MovieRequest movieController = new MovieRequest(RuntimeEnvironment.application);

        movieController.requestMovieDetail(movieId,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Movie movie = movieController.parseJsonToMovie(response);
                    System.out.println("Movie plot requested: "+movie.getPlot());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });

        //Thread sleep is necessary to asynchronous task with volley
        Thread.sleep(3000);
        ShadowApplication.runBackgroundTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        Thread.sleep(3000);
    }
}