package com.johnbohne.walmartmoviechallenge;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.johnbohne.walmartmoviechallenge.rest.model.Movie;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {

    public Movie mMovie;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey("movie")) {

            mMovie = getArguments().getParcelable("movie");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);




        if (mMovie != null) {

            //setting title here to deal with orientation changes

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mMovie.getTitle());
            }
            TextView overviewTextView = (TextView) rootView.findViewById(R.id.description_text);
            String overview = mMovie.getOverview();
            if (!TextUtils.isEmpty(overview)) {
               overviewTextView.setText(overview);
            } else {
                overviewTextView.setVisibility(View.GONE);
            }

            RelativeLayout dateLayout = (RelativeLayout) rootView.findViewById(R.id.date_layout);
            String releaseDate = mMovie.getRelease_date();
            if (!TextUtils.isEmpty(releaseDate)) {
                TextView dateText = (TextView) rootView.findViewById(R.id.date_text);
                dateText.setText(releaseDate);
            } else {
                dateLayout.setVisibility(View.GONE);
            }
        }

        return rootView;
    }
}
