package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services
        mockMovieService = new MovieService() {
            @Override
            public List<Movie> getAllMovies() {
                return Arrays.asList(
                    new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                    new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0),
                    new Movie(3L, "The Adventure", "Adventure Director", 2021, "Adventure", "Adventure description", 130, 4.2)
                );
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                if (id == 1L) {
                    return Optional.of(new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5));
                } else if (id == 2L) {
                    return Optional.of(new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0));
                }
                return Optional.empty();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                List<Movie> allMovies = getAllMovies();
                List<Movie> results = new ArrayList<>();
                
                // Simple mock search logic
                for (Movie movie : allMovies) {
                    boolean matches = true;
                    
                    if (name != null && !name.trim().isEmpty()) {
                        matches = movie.getMovieName().toLowerCase().contains(name.toLowerCase());
                    }
                    
                    if (id != null && matches) {
                        matches = movie.getId().equals(id);
                    }
                    
                    if (genre != null && !genre.trim().isEmpty() && matches) {
                        matches = movie.getGenre().toLowerCase().contains(genre.toLowerCase());
                    }
                    
                    if (matches) {
                        results.add(movie);
                    }
                }
                
                return results;
            }
            
            @Override
            public List<String> getAllGenres() {
                return Arrays.asList("Action", "Adventure", "Drama");
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    @DisplayName("Should return movies view for getMovies")
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        assertNotNull(result);
        assertEquals("movies", result);
        
        // Verify model attributes
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("allGenres"));
        assertTrue(model.containsAttribute("searchPerformed"));
        assertEquals(false, model.getAttribute("searchPerformed"));
    }

    @Test
    @DisplayName("Should return movie details view for valid movie ID")
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
        
        assertTrue(model.containsAttribute("movie"));
        assertTrue(model.containsAttribute("movieIcon"));
        assertTrue(model.containsAttribute("allReviews"));
    }

    @Test
    @DisplayName("Should return error view for invalid movie ID")
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
        
        assertTrue(model.containsAttribute("title"));
        assertTrue(model.containsAttribute("message"));
    }

    // Search functionality tests

    @Test
    @DisplayName("Should search movies by name")
    public void testSearchMoviesByName() {
        String result = moviesController.searchMovies("test", null, null, model);
        assertEquals("movies", result);
        
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("searchMessage"));
        assertTrue(model.containsAttribute("searchMessageType"));
        assertTrue(model.containsAttribute("searchPerformed"));
        assertEquals(true, model.getAttribute("searchPerformed"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should search movies by ID")
    public void testSearchMoviesById() {
        String result = moviesController.searchMovies(null, "2", null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals(2L, movies.get(0).getId());
    }

    @Test
    @DisplayName("Should search movies by genre")
    public void testSearchMoviesByGenre() {
        String result = moviesController.searchMovies(null, null, "action", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Action", movies.get(0).getGenre());
    }

    @Test
    @DisplayName("Should handle invalid ID parameter")
    public void testSearchMoviesWithInvalidId() {
        String result = moviesController.searchMovies(null, "invalid", null, model);
        assertEquals("movies", result);
        
        assertTrue(model.containsAttribute("searchMessage"));
        assertEquals("error", model.getAttribute("searchMessageType"));
        
        String message = (String) model.getAttribute("searchMessage");
        assertTrue(message.contains("Blimey!"));
    }

    @Test
    @DisplayName("Should handle negative ID parameter")
    public void testSearchMoviesWithNegativeId() {
        String result = moviesController.searchMovies(null, "-1", null, model);
        assertEquals("movies", result);
        
        assertTrue(model.containsAttribute("searchMessage"));
        assertEquals("error", model.getAttribute("searchMessageType"));
        
        String message = (String) model.getAttribute("searchMessage");
        assertTrue(message.contains("Arrr!"));
    }

    @Test
    @DisplayName("Should handle no search criteria")
    public void testSearchMoviesWithNoCriteria() {
        String result = moviesController.searchMovies(null, null, null, model);
        assertEquals("movies", result);
        
        assertTrue(model.containsAttribute("searchMessage"));
        assertEquals("info", model.getAttribute("searchMessageType"));
        
        String message = (String) model.getAttribute("searchMessage");
        assertTrue(message.contains("Ahoy!"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(3, movies.size()); // Should return all movies
    }

    @Test
    @DisplayName("Should handle empty search results")
    public void testSearchMoviesWithNoResults() {
        String result = moviesController.searchMovies("nonexistent", null, null, model);
        assertEquals("movies", result);
        
        assertTrue(model.containsAttribute("searchMessage"));
        assertEquals("warning", model.getAttribute("searchMessageType"));
        
        String message = (String) model.getAttribute("searchMessage");
        assertTrue(message.contains("Shiver me timbers!"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertTrue(movies.isEmpty());
    }

    @Test
    @DisplayName("Should preserve search form values")
    public void testSearchMoviesPreservesFormValues() {
        String result = moviesController.searchMovies("test movie", "1", "drama", model);
        assertEquals("movies", result);
        
        assertEquals("test movie", model.getAttribute("searchName"));
        assertEquals("1", model.getAttribute("searchId"));
        assertEquals("drama", model.getAttribute("searchGenre"));
    }

    @Test
    @DisplayName("Should handle empty string parameters")
    public void testSearchMoviesWithEmptyStrings() {
        String result = moviesController.searchMovies("", "", "", model);
        assertEquals("movies", result);
        
        assertTrue(model.containsAttribute("searchMessage"));
        assertEquals("info", model.getAttribute("searchMessageType"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(3, movies.size()); // Should return all movies
    }

    @Test
    @DisplayName("Should display success message for single result")
    public void testSearchMoviesSuccessMessageSingle() {
        String result = moviesController.searchMovies("test", null, null, model);
        assertEquals("movies", result);
        
        String message = (String) model.getAttribute("searchMessage");
        assertTrue(message.contains("1 piece of treasure"));
        assertEquals("success", model.getAttribute("searchMessageType"));
    }

    @Test
    @DisplayName("Should include all genres in model")
    public void testSearchMoviesIncludesAllGenres() {
        String result = moviesController.searchMovies("test", null, null, model);
        assertEquals("movies", result);
        
        assertTrue(model.containsAttribute("allGenres"));
        @SuppressWarnings("unchecked")
        List<String> genres = (List<String>) model.getAttribute("allGenres");
        assertEquals(3, genres.size());
        assertTrue(genres.contains("Drama"));
        assertTrue(genres.contains("Action"));
        assertTrue(genres.contains("Adventure"));
    }

    @Test
    @DisplayName("Should integrate with existing movie service")
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        
        Optional<Movie> movie = mockMovieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals("Test Movie", movie.get().getMovieName());
    }
}
