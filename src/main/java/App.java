import model.Movie;
import model.MovieResponse;
import service.MovieService;
import service.MovieServiceImpl;
import utils.TableRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static utils.TableRenderer.printAsciiArt;

public class App {

    private static final MovieService movieService = new MovieServiceImpl();
    private static final Scanner scanner = new Scanner(System.in);

    private static String currentQuery    = "";
    private static int    currentPage     = 1;
    private static int    totalPages      = 1;
    private static int    totalResults    = 0;
    private static MovieResponse currentResponse = null;
    private static String currentMode     = "";

    public static void main(String[] args) {
        printAsciiArt();
        mainMenu();
    }

    private static void mainMenu() {
        while (true) {
            TableRenderer.tableMenu();
//            System.out.print("[-] Choose an option: ");
            String op = scanner.nextLine().trim().toLowerCase();

            switch (op) {
                case "1" -> {
                    currentMode = "search";
                    System.out.print("[-] Enter movie title: ");
                    String query = scanner.nextLine().trim();
                    if (query.isEmpty()) continue;
                    currentQuery = query;
                    currentPage  = 1;
                    fetchAndDisplay();
                    paginationLoop();
                }
                case "2" -> {
                    currentMode = "popular";
                    currentQuery = "Popular Movies";
                    currentPage  = 1;
                    fetchAndDisplay();
                    paginationLoop();
                }
                case "3" -> {
                    currentMode = "top_rated";
                    currentQuery = "Top Rated Movies";
                    currentPage  = 1;
                    fetchAndDisplay();
                    paginationLoop();
                }
                case "4" -> {
                    currentMode = "now_playing";
                    currentQuery = "Now Playing";
                    currentPage  = 1;
                    fetchAndDisplay();
                    paginationLoop();
                }
                case "5" -> {
                    currentMode = "upcoming";
                    currentQuery = "Upcoming Movies";
                    currentPage  = 1;
                    fetchAndDisplay();
                    paginationLoop();
                }
                case "e" -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("[!] Invalid option. Please try again.");
            }
        }
    }

    private static void fetchAndDisplay() {
        System.out.println("\nLoading...");
        currentResponse = switch (currentMode) {
            case "search"      -> movieService.searchMovies(currentQuery, currentPage);
            case "popular"     -> movieService.getPopularMovies(currentPage);
            case "top_rated"   -> movieService.getTopRatedMovies(currentPage);
            case "now_playing" -> movieService.getNowPlayingMovies(currentPage);
            case "upcoming"    -> movieService.getUpcomingMovies(currentPage);
            default            -> movieService.getPopularMovies(currentPage);
        };

        totalPages   = currentResponse.getTotalPages()   != null ? currentResponse.getTotalPages()   : 1;
        totalResults = currentResponse.getTotalResults() != null ? currentResponse.getTotalResults() : 0;

        List<String> trailerUrls = new ArrayList<>();
        if (currentResponse.getResults() != null) {
            for (Movie m : currentResponse.getResults()) {
                trailerUrls.add(m.getId() != null ? movieService.getTrailerUrl(m.getId()) : null);
            }
        }

        TableRenderer.displayMovieTableWithTrailers(currentResponse, currentQuery, trailerUrls);
        System.out.printf("Page %d / %d  |  Total results: %d  |  Category: %s%n",
                currentPage, totalPages, totalResults, currentQuery);
    }

    private static void paginationLoop() {
        while (true) {
            System.out.println("""
                    
                    [n]   Next page
                    [p]   Previous page
                    [g]   Go to page
                    [md]  Movie Detail
                    [b]   Back to menu
                    [e]   Exit""");
            System.out.print("[-] Choose an option: ");
            String op = scanner.nextLine().trim().toLowerCase();

            switch (op) {
                case "n" -> {
                    if (currentPage < totalPages) {
                        currentPage++;
                        fetchAndDisplay();
                    } else {
                        System.out.printf("[!] Already on last page (%d).%n", totalPages);
                    }
                }
                case "p" -> {
                    if (currentPage > 1) {
                        currentPage--;
                        fetchAndDisplay();
                    } else {
                        System.out.println("[!] Already on first page.");
                    }
                }
                case "g" -> {
                    System.out.printf("[!] Enter page number (1 - %d): ", totalPages);
                    String input = scanner.nextLine().trim();
                    try {
                        int target = Integer.parseInt(input);
                        if (target < 1 || target > totalPages) {
                            System.out.printf("[!] Page must be between 1 and %d.%n", totalPages);
                        } else {
                            currentPage = target;
                            fetchAndDisplay();
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("[!] Invalid page number.");
                    }
                }
                case "md" -> {
                    System.out.print("[!] Enter movie ID: ");
                    String input = scanner.nextLine().trim();
                    try {
                        int movieId = Integer.parseInt(input);
                        showMovieDetail(movieId);
                        TableRenderer.displayMovieTableWithTrailers(
                                currentResponse, currentQuery, fetchTrailers(currentResponse));
                        System.out.printf("Page %d / %d  |  Total results: %d  |  Category: %s%n",
                                currentPage, totalPages, totalResults, currentQuery);
                    } catch (NumberFormatException e) {
                        System.out.println("[!] Invalid movie ID.");
                    }
                }
                case "b" -> { return; }
                case "e" -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("[!] Unknown option. Please try again.");
            }
        }
    }

    private static void showMovieDetail(int movieId) {
        System.out.println("\nFetching movie detail...");
        try {
            Movie detail      = movieService.getMovieDetail(movieId);
            String trailerUrl = movieService.getTrailerUrl(movieId);
            TableRenderer.displayMovieDetail(detail, trailerUrl);
        } catch (Exception e) {
            System.out.println("[!] Could not fetch detail: " + e.getMessage());
        }
    }

    private static List<String> fetchTrailers(MovieResponse response) {
        List<String> trailerUrls = new ArrayList<>();
        if (response != null && response.getResults() != null) {
            for (Movie m : response.getResults()) {
                trailerUrls.add(m.getId() != null ? movieService.getTrailerUrl(m.getId()) : null);
            }
        }
        return trailerUrls;
    }


}
