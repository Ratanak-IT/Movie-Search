package utils;

import model.Genre;
import model.Movie;
import model.MovieResponse;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.util.List;
import java.util.stream.Collectors;

public class TableRenderer {

    private static final CellStyle CENTER = new CellStyle(CellStyle.HorizontalAlign.CENTER);
    private static final CellStyle LEFT   = new CellStyle(CellStyle.HorizontalAlign.LEFT);

    public static void displayMovieTable(MovieResponse movieResponse, String query) {
        int totalResults = movieResponse.getTotalResults() != null ? movieResponse.getTotalResults() : 0;

        System.out.printf("%nTotal results: %d  |  Query: \"%s\"%n%n", totalResults, query);

        Table table = new Table(5, BorderStyle.UNICODE_ROUND_BOX_WIDE, ShownBorders.ALL);

        String[] headers = {"ID", "Title", "Release", "Rating", "Trailer"};
        for (String h : headers) {
            table.addCell(h, CENTER);
        }

        List<Movie> movies = movieResponse.getResults();
        if (movies == null || movies.isEmpty()) {
            table.addCell("No results found.", CENTER, 5);
        } else {
            for (Movie m : movies) {
                table.addCell(m.getId() != null ? m.getId().toString() : "-", CENTER);
                table.addCell(safe(m.getTitle()));
                table.addCell(safe(m.getReleaseDate()));
                table.addCell(m.getVoteAverage() != null
                        ? String.format("%.1f", m.getVoteAverage()) : "N/A", CENTER);
                table.addCell("N/A", CENTER);
            }
        }

        System.out.println(table.render());
    }

    public static void displayMovieTableWithTrailers(MovieResponse movieResponse,
                                                     String query,
                                                     List<String> trailerUrls) {
        int totalResults = movieResponse.getTotalResults() != null ? movieResponse.getTotalResults() : 0;

        System.out.printf("%nTotal results: %d  |  Query: \"%s\"%n%n", totalResults, query);

        Table table = new Table(5, BorderStyle.UNICODE_ROUND_BOX_WIDE, ShownBorders.ALL);

        String[] headers = {"ID", "Title", "Release", "Rating", "Trailer"};
        for (String h : headers) {
            table.addCell(h, CENTER);
        }

        List<Movie> movies = movieResponse.getResults();
        if (movies == null || movies.isEmpty()) {
            table.addCell("No results found.", CENTER, 5);
        } else {
            for (int i = 0; i < movies.size(); i++) {
                Movie m = movies.get(i);
                String trailer = (trailerUrls != null && i < trailerUrls.size() && trailerUrls.get(i) != null)
                        ? trailerUrls.get(i) : "N/A";

                table.addCell(m.getId() != null ? m.getId().toString() : "-", CENTER);
                table.addCell(safe(m.getTitle()));
                table.addCell(safe(m.getReleaseDate()));
                table.addCell(m.getVoteAverage() != null
                        ? String.format("%.1f", m.getVoteAverage()) : "N/A", CENTER);
                table.addCell(trailer, LEFT);
            }
        }

        System.out.println(table.render());
    }

    public static void displayMovieDetail(Movie movie, String trailerUrl) {
        Table table = new Table(4, BorderStyle.UNICODE_ROUND_BOX_WIDE, ShownBorders.ALL);

        String headerText = "  MOVIE DETAIL  ";
        table.addCell(headerText, CENTER, 4);

        addDetailRow(table, "Title",    safe(movie.getTitle()));
        addDetailRow(table, "Release",  safe(movie.getReleaseDate()));
        addDetailRow(table, "Rating",   movie.getVoteAverage() != null
                ? String.format("%.1f / 10", movie.getVoteAverage()) : "N/A");
        addDetailRow(table, "Runtime",  movie.getRuntime() != null
                ? movie.getRuntime() + " min" : "N/A");
        addDetailRow(table, "Budget",   movie.getBudget() != null && movie.getBudget() > 0
                ? String.format("$%,d", movie.getBudget()) : "N/A");

        String genres = "N/A";
        if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
            genres = movie.getGenres().stream()
                    .map(Genre::getName)
                    .collect(Collectors.joining(", "));
        }
        addDetailRow(table, "Genres", genres);

        String origins = "N/A";
        if (movie.getOriginCountry() != null && !movie.getOriginCountry().isEmpty()) {
            origins = String.join(", ", movie.getOriginCountry());
        }
        addDetailRow(table, "Origin", origins);

        // dak trailer
        addDetailRow(table, "Trailer", trailerUrl != null ? trailerUrl : "N/A");

        System.out.println(table.render());
    }


    private static void addDetailRow(Table table, String label, String value) {
        table.addCell(" " + label, CENTER);
        table.addCell(" " + value, LEFT, 3);
    }

    private static String safe(String s) {
        return s != null ? s : "N/A";
    }
}
