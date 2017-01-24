package com.ndrive.codechallengeomdb;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ndrive.codechallengeomdb.adapter.MovieListAdapter;
import com.ndrive.codechallengeomdb.application.DialogBuilder;
import com.ndrive.codechallengeomdb.application.request.MovieRequest;
import com.ndrive.codechallengeomdb.model.Movie;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.forusers.heinscomponents.GlideImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * An activity representing a list of movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity implements MovieListAdapter.OnclickMovieListener, Response.Listener<JSONObject>, Response.ErrorListener {

    private static final String SEARCH_KEY = "search_key";
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private MovieListAdapter adapter;
    private List<Movie> movieList;
    private MovieRequest mController;

    private GlideImageView posterImageView;
    private String lastQuery="a";//initial value
    private SearchView searchView;
    private SharedPreferences mSharedPreferences;

    Subject<String> mQueryObservabla = PublishSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_movie_list);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            toolbar.setTitle(getTitle());

            View recyclerView = findViewById(R.id.movie_list);

            initData();
            setupRecyclerView((RecyclerView) recyclerView);

            if (findViewById(R.id.movie_detail_container) != null) {
                // The detail container view will be present only in the
                // large-screen layouts (res/values-w900dp).
                // If this view is present, then the
                // activity should be in two-pane mode.
                mTwoPane = true;
            }

            if (savedInstanceState != null) {
                lastQuery = savedInstanceState.getString(SEARCH_KEY);
            }

            mSharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

            lastQuery = mSharedPreferences.getString(SEARCH_KEY, null);

        } catch (Exception e) {
            DialogBuilder.e(this, e);
        }
    }

    private void initData() {
        mController = new MovieRequest(this);
        movieList = new ArrayList<>();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new MovieListAdapter(movieList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClickMovie(View view, Movie movie) {
        try {
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putString(MovieDetailFragment.MOVIE_ID, movie.getImdbID());
                arguments.putString(MovieDetailFragment.MOVIE_POSTER, movie.getPoster());
                arguments.putString(MovieDetailFragment.MOVIE_TITLE, movie.getTitle());
                arguments.putString(MovieDetailFragment.MOVIE_YEAR, movie.getYear());

                MovieDetailFragment fragment = new MovieDetailFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment)
                        .commit();
            } else {
                Intent intent = new Intent(this, MovieDetailActivity.class);
                intent.putExtra(MovieDetailFragment.MOVIE_ID, movie.getImdbID());
                intent.putExtra(MovieDetailFragment.MOVIE_POSTER, movie.getPoster());
                intent.putExtra(MovieDetailFragment.MOVIE_TITLE, movie.getTitle());
                intent.putExtra(MovieDetailFragment.MOVIE_YEAR, movie.getYear());

                startActivity(intent);
            }
        } catch (Exception e) {
            DialogBuilder.e(this, e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_list_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo( searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                mQueryObservabla.onComplete();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mQueryObservabla.onNext(newText);

                return true;
            }
        });

        mQueryObservabla
                .debounce(350, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String query) throws Exception {
                        lastQuery = query;
                        mController.requestMovieList(query, MovieListActivity.this, MovieListActivity.this);
                    }
                });

        final MenuItem menuItem = menu.findItem(R.id.search);
        if(lastQuery != null && !lastQuery.trim().isEmpty()){
            MenuItemCompat.expandActionView(menuItem);
            searchView.setQuery(lastQuery, false);
            searchView.clearFocus();
            mQueryObservabla.onNext(lastQuery);
        }
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                MenuItemCompat.collapseActionView(menuItem);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            movieList = mController.parseJsonToMovieList(response);
            adapter.changeData(movieList);
        } catch (Exception e) {
            DialogBuilder.e(this, e);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        try {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            DialogBuilder.e(this, e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            lastQuery = searchView.getQuery().toString();

            SharedPreferences.Editor ed = mSharedPreferences.edit();
            ed.putString(SEARCH_KEY, lastQuery);
            ed.apply();

        } catch (Exception e) {
            DialogBuilder.e(this, e);
        }
    }
}
