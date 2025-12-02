package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    @DisplayName("Should load movies from JSON file")
    public void testLoadMoviesFromJson() {
        List<Movie> movies = movieService.getAllMovies();
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        assertTrue(movies.size() > 0);
    }

    @Test
    @DisplayName("Should get movie by valid ID")
    public void testGetMovieByValidId() {
        Optional<Movie> movie = movieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals(1L, movie.get().getId());
    }

    @Test
    @DisplayName("Should return empty for invalid ID")
    public void testGetMovieByInvalidId() {
        Optional<Movie> movie = movieService.getMovieById(999L);
        assertFalse(movie.isPresent());
    }

    @Test
    @DisplayName("Should return empty for null ID")
    public void testGetMovieByNullId() {
        Optional<Movie> movie = movieService.getMovieById(null);
        assertFalse(movie.isPresent());
    }

    @Test
    @DisplayName("Should return empty for negative ID")
    public void testGetMovieByNegativeId() {
        Optional<Movie> movie = movieService.getMovieById(-1L);
        assertFalse(movie.isPresent());
    }

    // Search functionality tests

    @Test
    @DisplayName("Should search movies by name (case-insensitive)")
    public void testSearchMoviesByName() {
        List<Movie> results = movieService.searchMovies("prison", null, null);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(m -> m.getMovieName().toLowerCase().contains("prison")));
    }

    @Test
    @DisplayName("Should search movies by name (partial match)")
    public void testSearchMoviesByPartialName() {
        List<Movie> results = movieService.searchMovies("the", null, null);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(m -> m.getMovieName().toLowerCase().contains("the")));
    }

    @Test
    @DisplayName("Should search movies by genre")
    public void testSearchMoviesByGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "drama");
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(m -> m.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    @DisplayName("Should search movies by ID")
    public void testSearchMoviesById() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
    }

    @Test
    @DisplayName("Should search movies by multiple criteria")
    public void testSearchMoviesByMultipleCriteria() {
        List<Movie> results = movieService.searchMovies("the", null, "drama");
        assertNotNull(results);
        assertTrue(results.stream().allMatch(m -> 
            m.getMovieName().toLowerCase().contains("the") && 
            m.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    @DisplayName("Should return empty list when no movies match search criteria")
    public void testSearchMoviesNoMatches() {
        List<Movie> results = movieService.searchMovies("nonexistent", null, null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when searching by invalid ID")
    public void testSearchMoviesByInvalidId() {
        List<Movie> results = movieService.searchMovies(null, 999L, null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should handle null and empty search parameters")
    public void testSearchMoviesWithNullParameters() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        assertNotNull(results);
        assertEquals(movieService.getAllMovies().size(), results.size());
    }

    @Test
    @DisplayName("Should handle empty string search parameters")
    public void testSearchMoviesWithEmptyStrings() {
        List<Movie> results = movieService.searchMovies("", null, "");
        assertNotNull(results);
        assertEquals(movieService.getAllMovies().size(), results.size());
    }

    @Test
    @DisplayName("Should handle whitespace-only search parameters")
    public void testSearchMoviesWithWhitespaceParameters() {
        List<Movie> results = movieService.searchMovies("   ", null, "   ");
        assertNotNull(results);
        assertEquals(movieService.getAllMovies().size(), results.size());
    }

    @Test
    @DisplayName("Should search movies case-insensitively")
    public void testSearchMoviesCaseInsensitive() {
        List<Movie> resultsLower = movieService.searchMovies("prison", null, null);
        List<Movie> resultsUpper = movieService.searchMovies("PRISON", null, null);
        List<Movie> resultsMixed = movieService.searchMovies("PrIsOn", null, null);
        
        assertNotNull(resultsLower);
        assertNotNull(resultsUpper);
        assertNotNull(resultsMixed);
        
        assertEquals(resultsLower.size(), resultsUpper.size());
        assertEquals(resultsLower.size(), resultsMixed.size());
    }

    @Test
    @DisplayName("Should get all unique genres")
    public void testGetAllGenres() {
        List<String> genres = movieService.getAllGenres();
        assertNotNull(genres);
        assertFalse(genres.isEmpty());
        
        // Check that genres are unique
        long uniqueCount = genres.stream().distinct().count();
        assertEquals(genres.size(), uniqueCount);
        
        // Check that genres are sorted
        List<String> sortedGenres = genres.stream().sorted().collect(java.util.stream.Collectors.toList());
        assertEquals(sortedGenres, genres);
    }

    @Test
    @DisplayName("Should search by ID with additional criteria filtering")
    public void testSearchByIdWithAdditionalCriteria() {
        // First, find a movie to test with
        List<Movie> allMovies = movieService.getAllMovies();
        assertFalse(allMovies.isEmpty());
        
        Movie testMovie = allMovies.get(0);
        
        // Search by ID with matching name - should return the movie
        List<Movie> matchingResults = movieService.searchMovies(
            testMovie.getMovieName().substring(0, 3), 
            testMovie.getId(), 
            null);
        assertEquals(1, matchingResults.size());
        assertEquals(testMovie.getId(), matchingResults.get(0).getId());
        
        // Search by ID with non-matching name - should return empty
        List<Movie> nonMatchingResults = movieService.searchMovies(
            "nonexistentmovie", 
            testMovie.getId(), 
            null);
        assertTrue(nonMatchingResults.isEmpty());
    }
}