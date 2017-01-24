package com.ndrive.codechallengeomdb.adapter;

import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ndrive.codechallengeomdb.R;
import com.ndrive.codechallengeomdb.model.Movie;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.forusers.heinscomponents.GlideImageView;

/**
 * Adapter to show a movie list
 */
public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

    /**
     * Current year to compare with movie year
     */
    private String currentYear;

    /**
     * Data model
     */
    private List<Movie> mValues;

    /**
     * Delegates the item listener
     */
    private OnclickMovieListener mListener;

    public interface OnclickMovieListener{
        void onClickMovie(View view, Movie movie);
    }

    public MovieListAdapter() {
        this.currentYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());
    }

    public MovieListAdapter(List<Movie> mValues, OnclickMovieListener mListener) {
        this();
        this.mValues = mValues;
        this.mListener = mListener;
    }

    public void changeData(List<Movie> movies){
        mValues = movies;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(holder.mItem.getTitle());
        holder.mYearView.setText(holder.mItem.getYear());
        holder.mPosterView.setImageUri(holder.mItem.getPoster());

        //Event propagation
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onClickMovie(v, holder.mItem);
                }
            }
        });

        //The year should be highlighted (red and bold) when it matches the current year
        if(holder.mItem.getYear().equalsIgnoreCase(currentYear)){
            holder.mYearView.setTextColor(
                    ResourcesCompat.getColor(holder.mView.getContext().getResources(),
                            R.color.red_900, null));
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mTitleView;
        final TextView mYearView;
        final GlideImageView mPosterView;
        Movie mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.title);
            mYearView = (TextView) view.findViewById(R.id.year);
            mPosterView= (GlideImageView) view.findViewById(R.id.posterGlideImageView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mYearView.getText() + "'";
        }
    }
}
