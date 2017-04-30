package com.johnbohne.walmartmoviechallenge.rest.model;

import java.util.List;

/**
 * Created by john on 4/27/17.
 */

public class MovieResponse {
    int page;
    int total_results;
    int total_pages;
    List<Movie> results;
    String errors; //maybe need to make own object?

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }
}
