package com.johnbohne.walmartmoviechallenge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.johnbohne.walmartmoviechallenge.rest.api.MovieService;
import com.johnbohne.walmartmoviechallenge.rest.model.Movie;
import com.johnbohne.walmartmoviechallenge.rest.model.MovieResponse;
import com.rockerhieu.rvadapter.endless.EndlessRecyclerViewAdapter;


import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private EndlessRecyclerViewAdapter mEndlessRecyclerViewAdapter;

    private Context mContext;
    private RecyclerView mRecyclerView;
    private int mCurrentPage;
    private TextView mEmptyTextView;
    private SimpleItemRecyclerViewAdapter mSimpleItemRecyclerViewAdapter;
    Parcelable mRecyclerViewState;
    MovieService mMovieServiceAPI;

    String mQuery;
    int mTotalPages;

    Call<MovieResponse> mCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        mContext = this;

        mRecyclerView = (RecyclerView) findViewById(R.id.item_list);
        mEmptyTextView = (TextView) findViewById(R.id.empty_view);

        assert mRecyclerView != null;
        int spanCount;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && !mTwoPane) {
            spanCount = 3;
        } else {
            spanCount = 2;
        }
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, spanCount));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));


        if (savedInstanceState != null) {
            List<Movie> movies = savedInstanceState.getParcelableArrayList("data");
            mTotalPages = savedInstanceState.getInt("total_pages");
            mCurrentPage = savedInstanceState.getInt("current_page");
            boolean resultsFound = savedInstanceState.getBoolean("results_found");
            if (!resultsFound) {
                mEmptyTextView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
            mQuery = savedInstanceState.getString("query");
            if (movies != null && movies.size() > 0) {
                //restore movie list state
                mSimpleItemRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter(movies);
                mEndlessRecyclerViewAdapter = new EndlessRecyclerViewAdapter(mContext,
                        mSimpleItemRecyclerViewAdapter,
                        new EndlessRecyclerViewAdapterLoadMoreListener()
                );
                mRecyclerView.setAdapter(mEndlessRecyclerViewAdapter);
                mRecyclerViewState = savedInstanceState.getParcelable("save_state");
                mRecyclerView.getLayoutManager().onRestoreInstanceState(mRecyclerViewState);
            }


        }





        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();





        // prepare call in Retrofit 2.0
        mMovieServiceAPI = retrofit.create(MovieService.class);


    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCall != null) {
            if (mCall.isExecuted()) {
                //An attempt will be made to cancel in-flight calls, and
                // if the call has not yet been executed it never will be.
                mCall.cancel();
            }
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRecyclerView != null) {
            outState.putParcelable("save_state", mRecyclerView.getLayoutManager().onSaveInstanceState());
            if (mRecyclerView.getVisibility() == View.GONE) {
                outState.putBoolean("results_found", false);
            } else {
                outState.putBoolean("results_found", true);
            }
        }
        if (mSimpleItemRecyclerViewAdapter != null) {
            List<Movie> movies = mSimpleItemRecyclerViewAdapter.getMovies();
            if (movies != null) {
                outState.putParcelableArrayList("data", (ArrayList<? extends Parcelable>) movies);
            }
        }
        outState.putInt("current_page", mCurrentPage);
        outState.putInt("total_pages", mTotalPages);
        outState.putString("query", mQuery);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search);

        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
                    LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_layout, null);
                    final TextInputEditText editText = (TextInputEditText) linearLayout.findViewById(R.id.movie_search_edit_text);
                    builder.setPositiveButton(getResources().getString(R.string.submit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mCurrentPage = 1;
                            mQuery = editText.getText().toString();
                            mCall = mMovieServiceAPI.searchMovie(mQuery, mCurrentPage);
                            //asynchronous call
                            mCall.enqueue(new Callback<MovieResponse>() {
                                @Override
                                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                                    if (response.isSuccessful()) {
                                        MovieResponse movieResponse = response.body();
                                        List<Movie> movieResults = movieResponse.getResults();
                                        if (movieResults.size() == 0) {
                                            //display empty view
                                            mRecyclerView.setVisibility(View.GONE);
                                            mEmptyTextView.setVisibility(View.VISIBLE);
                                            mTotalPages = 0;
                                            mQuery = "";
                                            mCurrentPage = 0;
                                            mSimpleItemRecyclerViewAdapter = null;
                                            return;
                                        }
                                        mRecyclerView.setVisibility(View.VISIBLE);
                                        mEmptyTextView.setVisibility(View.GONE);
                                        mTotalPages = movieResponse.getTotal_pages();
                                        mSimpleItemRecyclerViewAdapter =
                                                new SimpleItemRecyclerViewAdapter(movieResults);

                                        mEndlessRecyclerViewAdapter = new EndlessRecyclerViewAdapter(mContext,
                                                mSimpleItemRecyclerViewAdapter,
                                                new EndlessRecyclerViewAdapterLoadMoreListener()
                                        );
                                        mRecyclerView.setAdapter(mEndlessRecyclerViewAdapter);


                                    }
                                }

                                @Override
                                public void onFailure(Call<MovieResponse> call, Throwable t) {
                                    t.printStackTrace();
                                }
                            });
                        }
                    });
                    builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setView(linearLayout);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
            }
        });
        return true;

    }
    public class EndlessRecyclerViewAdapterLoadMoreListener implements com.rockerhieu.rvadapter.endless.
            EndlessRecyclerViewAdapter.RequestToLoadMoreListener {


        @Override
        public void onLoadMoreRequested() {
            mCurrentPage++;
            if (mCurrentPage < mTotalPages) {
                mCall = mMovieServiceAPI.searchMovie(mQuery, mCurrentPage);
                mCall.enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful()) {
                            MovieResponse movieResponse = response.body();
                            List<Movie> movieResults = movieResponse.getResults();
                            if (movieResults.size() != 0) {
                                mSimpleItemRecyclerViewAdapter.appendItems(movieResults);
                                mEndlessRecyclerViewAdapter.onDataReady(true);
                            } else {
                                mEndlessRecyclerViewAdapter.onDataReady(false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {

                    }
                });
            } else {
                mEndlessRecyclerViewAdapter.onDataReady(false);
            }
        }
    }



    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Movie> mMovies;

        public SimpleItemRecyclerViewAdapter(List<Movie> movies) {
            mMovies = movies;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mMovies.get(position);
            String posterPath = holder.mItem.getPoster_path();

            if (!TextUtils.isEmpty(posterPath)) {
                Glide.with(mContext)
                        .load(IMAGE_BASE_URL + holder.mItem.getPoster_path())
                        .placeholder(R.drawable.primary_placeholder_drawable)
                        .override(96, 96).centerCrop().into(holder.mMovieImageView);
            } else {
                //only show no image if there isn't actually an image of course
                holder.mMovieImageView.setImageDrawable(ContextCompat.getDrawable(mContext,
                        R.drawable.movie_no_image));
            }
            String title = holder.mItem.getTitle();
            holder.mTitleTextView.setText(title);
            holder.mReleaseDateTextView.setText(holder.mItem.getRelease_date());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putParcelable("movie", holder.mItem);
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra("movie", holder.mItem);

                        context.startActivity(intent);
                    }
                }
            });
        }
        public void appendItems(List<Movie> items) {
            int count = getItemCount();
            mMovies.addAll(items);
            notifyItemRangeInserted(count, items.size());
        }

        public List<Movie> getMovies() {
            return mMovies;
        }

        @Override
        public int getItemCount() {
            return mMovies.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mMovieImageView;
            public final TextView mTitleTextView;
            public final TextView mReleaseDateTextView;
            public Movie mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mMovieImageView = (ImageView) view.findViewById(R.id.movie_image_view);
                mTitleTextView = (TextView) view.findViewById(R.id.title);
                mReleaseDateTextView = (TextView) view.findViewById(R.id.release_date);
            }
        }
    }
}
