package com.ndrive.codechallengeomdb;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ndrive.codechallengeomdb.application.DialogBuilder;
import com.ndrive.codechallengeomdb.application.request.MovieRequest;
import com.ndrive.codechallengeomdb.model.Movie;

import org.json.JSONObject;

import br.com.forusers.heinscomponents.GlideImageView;

/**
 * A fragment representing a single movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject> {

    //Names of arguments
    public static final String MOVIE_ID = "moveId";
    public static final String MOVIE_TITLE = "moveTitle";
    public static final String MOVIE_YEAR = "moveYear";
    public static final String MOVIE_POSTER = "moveImage";


    private Movie mMovie;
    private MovieRequest mController;

    //Screen components
    private TextView titleView;
    private GlideImageView posterView;
    private TextView yearView;
    private RatingBar ratingBar;
    private TextView plotView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mController = new MovieRequest(getContext());

            //Get the arguments
            if (getArguments().containsKey(MOVIE_ID)) {
                mMovie = new Movie();
                mMovie.setImdbID(getArguments().getString(MOVIE_ID));
                mMovie.setPoster(getArguments().getString(MOVIE_POSTER));
                mMovie.setYear(getArguments().getString(MOVIE_YEAR));
                mMovie.setTitle(getArguments().getString(MOVIE_TITLE));

            }
        } catch (Exception e) {
            DialogBuilder.e(getContext(), e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        try{
            //Screen components references
            this.titleView = (TextView)rootView.findViewById(R.id.movie_title);
            this.posterView = (GlideImageView) rootView.findViewById(R.id.poster);
            this.yearView = (TextView) rootView.findViewById(R.id.movie_year);
            this.ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);
            this.plotView = (TextView) rootView.findViewById(R.id.movie_detail);

            setViews();
        } catch (Exception e) {
            DialogBuilder.e(getContext(), e);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            //Do http request
            if(mMovie.getImdbID() != null) {
                mController.requestMovieDetail(mMovie.getImdbID(), this, this);
            }
        } catch (Exception e) {
            DialogBuilder.e(getContext(), e);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        try {
            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            DialogBuilder.e(getContext(), e);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            mMovie = mController.parseJsonToMovie(response);
            setViews();
        } catch (Exception e) {
            DialogBuilder.e(getContext(), e);
        }
    }

    /**
     * Update views with the Movie data
     */
    private void setViews() {
        try {
            if(mMovie != null) {
                titleView.setText(mMovie.getTitle());

                if(mMovie.getPoster() != null) {
                    posterView.setImageUri(mMovie.getPoster());
                }

                yearView.setText(mMovie.getYear());

                if(mMovie.getImdbRating() != null && !mMovie.getImdbRating().trim().isEmpty()){
                    Double rating = Double.valueOf(mMovie.getImdbRating()) /2.0;
                    ratingBar.setVisibility(View.VISIBLE);
                    ratingBar.setRating(rating.intValue());
                }else{
                    ratingBar.setVisibility(View.GONE);
                }

                if(mMovie.getPlot() != null && !mMovie.getPlot().trim().isEmpty()){
                    plotView.setText(mMovie.getPlot());
                }

                Activity activity = this.getActivity();
                activity.setTitle(mMovie.getTitle());
            }

        } catch (Exception e) {
            DialogBuilder.e(getContext(), e);
        }
    }
}
