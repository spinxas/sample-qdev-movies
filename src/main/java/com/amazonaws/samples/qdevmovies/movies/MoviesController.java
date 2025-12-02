package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("allGenres", movieService.getAllGenres());
        model.addAttribute("searchPerformed", false);
        return "movies";
    }

    @GetMapping("/movies/search")
    public String searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) String idParam,
            @RequestParam(value = "genre", required = false) String genre,
            org.springframework.ui.Model model) {
        
        logger.info("Ahoy! Searchin' for movies with parameters - name: '{}', id: '{}', genre: '{}'", 
                   name, idParam, genre);
        
        // Parse ID parameter with proper error handling
        Long id = null;
        String idError = null;
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                id = Long.parseLong(idParam.trim());
                if (id <= 0) {
                    idError = "Arrr! Movie ID must be a positive number, ye scallywag!";
                }
            } catch (NumberFormatException e) {
                idError = "Blimey! That be no proper number for a movie ID, matey!";
                logger.warn("Invalid ID parameter provided: '{}'", idParam);
            }
        }
        
        // Check if we have any search criteria
        boolean hasSearchCriteria = (name != null && !name.trim().isEmpty()) ||
                                   (id != null && id > 0) ||
                                   (genre != null && !genre.trim().isEmpty());
        
        if (!hasSearchCriteria && idError == null) {
            // No search criteria provided - show all movies with a message
            model.addAttribute("movies", movieService.getAllMovies());
            model.addAttribute("searchMessage", "Ahoy! Ye need to provide some search criteria to find yer treasure, matey!");
            model.addAttribute("searchMessageType", "info");
        } else if (idError != null) {
            // ID parsing error - show error message
            model.addAttribute("movies", movieService.getAllMovies());
            model.addAttribute("searchMessage", idError);
            model.addAttribute("searchMessageType", "error");
        } else {
            // Perform the search
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            model.addAttribute("movies", searchResults);
            
            if (searchResults.isEmpty()) {
                model.addAttribute("searchMessage", 
                    "Shiver me timbers! No treasure found matching yer search criteria. " +
                    "Try adjustin' yer search terms, ye landlubber!");
                model.addAttribute("searchMessageType", "warning");
            } else {
                String treasureCount = searchResults.size() == 1 ? "piece of treasure" : "pieces of treasure";
                model.addAttribute("searchMessage", 
                    String.format("Arrr! Found %d %s matching yer search, me hearty!", 
                                searchResults.size(), treasureCount));
                model.addAttribute("searchMessageType", "success");
            }
        }
        
        // Add form data back to the model for persistence
        model.addAttribute("searchName", name != null ? name.trim() : "");
        model.addAttribute("searchId", idParam != null ? idParam.trim() : "");
        model.addAttribute("searchGenre", genre != null ? genre.trim() : "");
        model.addAttribute("allGenres", movieService.getAllGenres());
        model.addAttribute("searchPerformed", true);
        
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }
}