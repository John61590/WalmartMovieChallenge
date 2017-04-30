package com.johnbohne.walmartmoviechallenge.rest.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by john on 4/27/17.
 */

public class Movie implements Parcelable {
    String poster_path;
    int id;
    String release_date;
    String overview;
    String title;
    String original_title;
    String backdrop_path;

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.poster_path);
        dest.writeInt(this.id);
        dest.writeString(this.release_date);
        dest.writeString(this.overview);
        dest.writeString(this.title);
        dest.writeString(this.original_title);
        dest.writeString(this.backdrop_path);
    }

    public Movie() {
    }

    protected Movie(Parcel in) {
        this.poster_path = in.readString();
        this.id = in.readInt();
        this.release_date = in.readString();
        this.overview = in.readString();
        this.title = in.readString();
        this.original_title = in.readString();
        this.backdrop_path = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
