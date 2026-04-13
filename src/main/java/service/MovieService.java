package service;

import model.Movie;
import model.MovieResponse;

public interface MovieService {
    MovieResponse searchMovies(String query, int page);
    Movie getMovieDetail(int movieId);
    String getTrailerUrl(int movieId);
    MovieResponse getPopularMovies(int page);
    MovieResponse getTopRatedMovies(int page);
    MovieResponse getNowPlayingMovies(int page);
    MovieResponse getUpcomingMovies(int page);
}
