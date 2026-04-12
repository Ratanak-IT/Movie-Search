package service;

import model.Movie;
import model.MovieResponse;

public interface MovieService {
    MovieResponse searchMovies(String query, int page);
    Movie getMovieDetail(int movieId);
    String getTrailerUrl(int movieId);
}
