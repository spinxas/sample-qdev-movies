# Movie Service - Spring Boot Demo Application

A simple movie catalog web application built with Spring Boot, demonstrating Java application development best practices with a fun pirate theme! 🏴‍☠️

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **🏴‍☠️ Pirate-Themed Movie Search**: Hunt for treasure (movies) using our advanced search functionality!
  - Search by movie name (partial matching, case-insensitive)
  - Search by movie ID (1-12)
  - Filter by genre with dropdown selection
  - Combine multiple search criteria for precise treasure hunting
  - Pirate-themed error messages and success notifications
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds, smooth animations, and pirate-themed search interface

## Technology Stack

- **Java 8**
- **Spring Boot 2.0.5**
- **Maven** for dependency management
- **Thymeleaf** for server-side templating
- **Log4j 2.20.0**
- **JUnit 5.8.2**

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List**: http://localhost:8080/movies
- **Movie Search**: Use the pirate-themed search form on the movies page
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller for movie endpoints
│   │       │   ├── MovieService.java         # Business logic with search functionality
│   │       │   ├── Movie.java                # Movie data model
│   │       │   └── Review.java               # Review data model
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie data (12 movies)
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       └── templates/
│           ├── movies.html                   # Movie list with search form
│           └── movie-details.html            # Movie details page
└── test/                                     # Comprehensive unit tests
    └── java/
        └── com/amazonaws/samples/qdevmovies/movies/
            ├── MovieServiceTest.java         # Service layer tests
            └── MoviesControllerTest.java     # Controller tests with search functionality
```

## API Endpoints

### Get All Movies
```
GET /movies
```
Returns an HTML page displaying all movies with ratings, basic information, and the pirate-themed search form.

**Features:**
- Displays all 12 movies in a responsive grid
- Includes search form for filtering movies
- Shows movie icons, ratings, and basic details

### 🏴‍☠️ Search Movies (New!)
```
GET /movies/search
```
Ahoy! Search for treasure (movies) using various criteria, matey!

**Query Parameters:**
- `name` (optional): Movie name to search for (partial match, case-insensitive)
- `id` (optional): Specific movie ID to find (1-12)
- `genre` (optional): Genre to filter by (partial match, case-insensitive)

**Examples:**
```
# Search by movie name
http://localhost:8080/movies/search?name=prison

# Search by movie ID
http://localhost:8080/movies/search?id=1

# Search by genre
http://localhost:8080/movies/search?genre=drama

# Combine multiple criteria
http://localhost:8080/movies/search?name=the&genre=action

# Search with case-insensitive matching
http://localhost:8080/movies/search?name=PRISON&genre=DRAMA
```

**Response Features:**
- Returns HTML page with filtered movie results
- Pirate-themed success/error messages
- Form persistence (search values remain after submission)
- Empty result handling with helpful pirate messages
- Input validation with friendly error messages

**Edge Cases Handled:**
- Invalid movie IDs with pirate-themed error messages
- Empty search criteria (shows all movies with info message)
- No matching results (shows "no treasure found" message)
- Malformed parameters (graceful error handling)

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

## Search Functionality Details

### 🏴‍☠️ Pirate-Themed Search Features

**Search Capabilities:**
- **Name Search**: Partial, case-insensitive matching (e.g., "prison" matches "The Prison Escape")
- **ID Search**: Exact movie ID lookup with validation
- **Genre Search**: Partial, case-insensitive genre filtering (e.g., "drama" matches "Crime/Drama")
- **Combined Search**: Use multiple criteria simultaneously for precise results

**User Experience:**
- Pirate-themed interface with treasure chest styling
- Real-time form validation
- Persistent search values after submission
- Clear success/error messaging in pirate language
- Mobile-responsive design

**Error Handling:**
- Invalid ID: "Blimey! That be no proper number for a movie ID, matey!"
- Negative ID: "Arrr! Movie ID must be a positive number, ye scallywag!"
- No criteria: "Ahoy! Ye need to provide some search criteria to find yer treasure, matey!"
- No results: "Shiver me timbers! No treasure found matching yer search criteria."

### Available Genres
- Action/Crime
- Action/Sci-Fi
- Adventure/Fantasy
- Adventure/Sci-Fi
- Crime/Drama
- Drama
- Drama/History
- Drama/Romance
- Drama/Thriller

## Testing

Run the comprehensive test suite:

```bash
# Run all tests
mvn test

# Run specific test classes
mvn test -Dtest=MovieServiceTest
mvn test -Dtest=MoviesControllerTest
```

**Test Coverage:**
- **MovieServiceTest**: Tests all search functionality, edge cases, and data loading
- **MoviesControllerTest**: Tests all endpoints, search parameters, and error handling
- **Integration Tests**: End-to-end testing of search workflows

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

1. Check that movies.json is properly loaded
2. Verify search parameters are correctly formatted
3. Check application logs for pirate-themed debug messages

## Contributing

This project is designed as a demonstration application. Feel free to:
- Add more movies to the catalog
- Enhance the UI/UX with more pirate themes
- Add new search features (director search, year range, rating filters)
- Improve the responsive design
- Add more pirate language elements! 🏴‍☠️

## Recent Updates

### Version 1.1.0 - Pirate Search Feature 🏴‍☠️
- Added comprehensive movie search functionality
- Implemented pirate-themed UI and messaging
- Added search by name, ID, and genre
- Enhanced error handling with pirate language
- Added comprehensive unit tests
- Updated documentation with search API details

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

*Arrr! May ye find all the treasure ye seek in our movie collection, me hearty!* 🏴‍☠️
