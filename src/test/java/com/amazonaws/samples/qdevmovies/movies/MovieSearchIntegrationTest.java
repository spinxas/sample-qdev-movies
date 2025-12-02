package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the complete movie search workflow
 * Ahoy! These tests be checkin' that our treasure huntin' works from bow to stern!
 */
public class MovieSearchIntegrationTest {

    private MoviesController controller;
    private MovieService movieService;
    private ReviewService reviewService;
    private Model model;

    @BeforeEach
    public void setUp() {
        // Use real services for integration testing
        movieService = new MovieService();
        reviewService = new ReviewService();
        controller = new MoviesController();
        model = new ExtendedModelMap();

        // Inject services using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(controller, movieService);

            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(controller, reviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject services", e);
        }
    }

    @Test
    @DisplayName("Should load movies from JSON and display all movies")
    public void testFullMovieLoadingWorkflow() {
        String result = controller.getMovies(model);
        assertEquals("movies", result);

        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(12, movies.size()); // Should load all 12 movies from JSON

        // Verify some known movies are loaded
        assertTrue(movies.stream().anyMatch(m -> m.getMovieName().equals("The Prison Escape")));
        assertTrue(movies.stream().anyMatch(m -> m.getMovieName().equals("The Family Boss")));
        assertTrue(movies.stream().anyMatch(m -> m.getMovieName().equals("The Masked Hero")));
    }

    @Test
    @DisplayName("Should search for movies by name with real data")
    public void testRealMovieSearchByName() {
        String result = controller.searchMovies("prison", null, null, model);
        assertEquals("movies", result);

        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("The Prison Escape", movies.get(0).getMovieName());
        assertEquals("success", model.getAttribute("searchMessageType"));
    }

    @Test
    @DisplayName("Should search for movies by genre with real data")
    public void testRealMovieSearchByGenre() {
        String result = controller.searchMovies(null, null, "drama", model);
        assertEquals("movies", result);

        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertTrue(movies.size() > 0);
        
        // All results should contain "drama" in genre (case-insensitive)
        assertTrue(movies.stream().allMatch(m -> 
            m.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    @DisplayName("Should search for movies by ID with real data")
    public void testRealMovieSearchById() {
        String result = controller.searchMovies(null, "1", null, model);
        assertEquals("movies", result);

        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals(1L, movies.get(0).getId());
        assertEquals("The Prison Escape", movies.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should handle combined search criteria with real data")
    public void testRealMovieSearchCombined() {
        String result = controller.searchMovies("the", null, "action", model);
        assertEquals("movies", result);

        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        
        // Should find movies that contain "the" in name AND "action" in genre
        assertTrue(movies.stream().allMatch(m -> 
            m.getMovieName().toLowerCase().contains("the") && 
            m.getGenre().toLowerCase().contains("action")));
    }

    @Test
    @DisplayName("Should return empty results for non-existent movie")
    public void testRealMovieSearchNoResults() {
        String result = controller.searchMovies("nonexistentmovie", null, null, model);
        assertEquals("movies", result);

        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertTrue(movies.isEmpty());
        assertEquals("warning", model.getAttribute("searchMessageType"));
        
        String message = (String) model.getAttribute("searchMessage");
        assertTrue(message.contains("Shiver me timbers!"));
    }

    @Test
    @DisplayName("Should load and display all available genres")
    public void testRealGenreLoading() {
        List<String> genres = movieService.getAllGenres();
        assertNotNull(genres);
        assertTrue(genres.size() > 0);
        
        // Check for some expected genres from the JSON data
        assertTrue(genres.contains("Drama"));
        assertTrue(genres.contains("Crime/Drama"));
        assertTrue(genres.contains("Action/Crime"));
        
        // Verify genres are unique and sorted
        long uniqueCount = genres.stream().distinct().count();
        assertEquals(genres.size(), uniqueCount);
        
        List<String> sortedGenres = genres.stream().sorted().collect(java.util.stream.Collectors.toList());
        assertEquals(sortedGenres, genres);
    }

    @Test
    @DisplayName("Should handle case-insensitive search with real data")
    public void testRealMovieSearchCaseInsensitive() {
        // Test different case variations
        String result1 = controller.searchMovies("PRISON", null, null, model);
        String result2 = controller.searchMovies("prison", null, null, model);
        String result3 = controller.searchMovies("PrIsOn", null, null, model);
        
        assertEquals("movies", result1);
        assertEquals("movies", result2);
        assertEquals("movies", result3);
        
        // All should return the same results
        @SuppressWarnings("unchecked")
        List<Movie> movies1 = (List<Movie>) model.getAttribute("movies");
        
        model = new ExtendedModelMap();
        controller.searchMovies("prison", null, null, model);
        @SuppressWarnings("unchecked")
        List<Movie> movies2 = (List<Movie>) model.getAttribute("movies");
        
        model = new ExtendedModelMap();
        controller.searchMovies("PrIsOn", null, null, model);
        @SuppressWarnings("unchecked")
        List<Movie> movies3 = (List<Movie>) model.getAttribute("movies");
        
        assertEquals(movies1.size(), movies2.size());
        assertEquals(movies1.size(), movies3.size());
        assertEquals(1, movies1.size()); // Should find "The Prison Escape"
    }

    @Test
    @DisplayName("Should preserve search form values after search")
    public void testSearchFormValuePersistence() {
        String result = controller.searchMovies("test search", "5", "drama", model);
        assertEquals("movies", result);
        
        assertEquals("test search", model.getAttribute("searchName"));
        assertEquals("5", model.getAttribute("searchId"));
        assertEquals("drama", model.getAttribute("searchGenre"));
        assertTrue(model.containsAttribute("allGenres"));
        assertEquals(true, model.getAttribute("searchPerformed"));
    }

    @Test
    @DisplayName("Should handle partial genre matching with real data")
    public void testRealPartialGenreMatching() {
        // Search for "sci" should match "Action/Sci-Fi" and "Adventure/Sci-Fi"
        String result = controller.searchMovies(null, null, "sci", model);
        assertEquals("movies", result);

        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertTrue(movies.size() > 0);
        
        // All results should have "sci" in their genre
        assertTrue(movies.stream().allMatch(m -> 
            m.getGenre().toLowerCase().contains("sci")));
    }

    @Test
    @DisplayName("Should validate movie details integration")
    public void testMovieDetailsIntegration() {
        // First search for a movie
        controller.searchMovies("prison", null, null, model);
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertFalse(movies.isEmpty());
        
        Movie foundMovie = movies.get(0);
        
        // Then get details for that movie
        Model detailsModel = new ExtendedModelMap();
        String result = controller.getMovieDetails(foundMovie.getId(), detailsModel);
        assertEquals("movie-details", result);
        
        Movie detailsMovie = (Movie) detailsModel.getAttribute("movie");
        assertNotNull(detailsMovie);
        assertEquals(foundMovie.getId(), detailsMovie.getId());
        assertEquals(foundMovie.getMovieName(), detailsMovie.getMovieName());
    }
}