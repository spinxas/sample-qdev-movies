package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Search for movies based on multiple criteria with pirate-themed logging
     * Ahoy! This method be searchin' through our treasure chest of movies!
     * 
     * @param name Movie name to search for (partial match, case-insensitive)
     * @param id Specific movie ID to find
     * @param genre Genre to filter by (partial match, case-insensitive)
     * @return List of movies matching the search criteria
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Ahoy! Searchin' for treasure with criteria - name: '{}', id: '{}', genre: '{}'", 
                   name, id, genre);
        
        List<Movie> results = new ArrayList<>();
        
        // If searchin' by ID specifically, try to find that exact treasure
        if (id != null && id > 0) {
            Optional<Movie> movieById = getMovieById(id);
            if (movieById.isPresent()) {
                Movie movie = movieById.get();
                // Check if this treasure also matches other criteria, matey!
                if (matchesSearchCriteria(movie, name, genre)) {
                    results.add(movie);
                    logger.info("Arrr! Found treasure by ID: '{}'", movie.getMovieName());
                }
            }
            return results;
        }
        
        // Search through all movies in our treasure chest
        for (Movie movie : movies) {
            if (matchesSearchCriteria(movie, name, genre)) {
                results.add(movie);
            }
        }
        
        logger.info("Shiver me timbers! Found {} pieces of treasure matching yer search!", results.size());
        return results;
    }

    /**
     * Check if a movie matches the search criteria
     * This be our trusty compass for findin' the right treasure!
     */
    private boolean matchesSearchCriteria(Movie movie, String name, String genre) {
        // Check name criteria (partial match, case-insensitive)
        if (name != null && !name.trim().isEmpty()) {
            String searchName = name.trim().toLowerCase();
            String movieName = movie.getMovieName().toLowerCase();
            if (!movieName.contains(searchName)) {
                return false;
            }
        }
        
        // Check genre criteria (partial match, case-insensitive)
        if (genre != null && !genre.trim().isEmpty()) {
            String searchGenre = genre.trim().toLowerCase();
            String movieGenre = movie.getGenre().toLowerCase();
            if (!movieGenre.contains(searchGenre)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Get all unique genres from our treasure chest
     * Useful for helpin' landlubbers know what treasures we have!
     */
    public List<String> getAllGenres() {
        return movies.stream()
                .map(Movie::getGenre)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
