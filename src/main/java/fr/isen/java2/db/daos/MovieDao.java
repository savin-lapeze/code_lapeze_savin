package fr.isen.java2.db.daos;
 
import static fr.isen.java2.db.daos.DataSourceFactory.getDataSource;
 
 
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
 
import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;
 
public class MovieDao {
	
	
    
	public List<Movie> listMovies() {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre";
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                Movie movie = new Movie();
                movie.setId(resultSet.getInt("idmovie"));
                movie.setTitle(resultSet.getString("title"));
                movie.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
                movie.setGenre(new Genre (resultSet.getInt("genre_id"),resultSet.getString("name")).getId());
                movie.setDuration(resultSet.getInt("duration"));
                movie.setDirector(resultSet.getString("director"));
                movie.setSummary(resultSet.getString("summary"));
                
                
                
                
                movies.add(movie);
            }
            return movies;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des genres", e);
        }
        
    }
	
	public List<Movie> listMoviesByGenre(String genreName) {
	    List<Movie> movies = new ArrayList<>();
	    String query = "SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre WHERE genre.name = ?";
	    
	    try (Connection connection = getDataSource().getConnection();
	         PreparedStatement statement = connection.prepareStatement(query)) {
	        
	        statement.setString(1, genreName);
	        
	        try (ResultSet resultSet = statement.executeQuery()) {
	            while (resultSet.next()) {
	                Movie movie = new Movie();
	                movie.setId(resultSet.getInt("idmovie"));
	                movie.setTitle(resultSet.getString("title"));
	                movie.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
	                
	                // Utilisation du constructeur de Genre avec l'ID et le nom
	                Genre genre = new Genre(resultSet.getInt("genre_id"), resultSet.getString("name"));
	                movie.setGenre(genre.getId());
	                
	                movie.setDuration(resultSet.getInt("duration"));
	                movie.setDirector(resultSet.getString("director"));
	                movie.setSummary(resultSet.getString("summary"));
	                
	                movies.add(movie);
	            }
	        }
	        
	        return movies;
	    } catch (SQLException e) {
	        throw new RuntimeException("Erreur lors de la récupération des films par genre", e);
	    }
	}


	public Movie addMovie(Movie movie) {
	    String query = "INSERT INTO movie(title, release_date, genre_id, duration, director, summary) VALUES (?, ?, ?, ?, ?, ?)";
	    
	    try (Connection connection = getDataSource().getConnection();
	         PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
	        
	        statement.setString(1, movie.getTitle());
	        statement.setDate(2, Date.valueOf(movie.getReleaseDate()));
	        statement.setInt(3, movie.getGenre());
	        statement.setInt(4, movie.getDuration());
	        statement.setString(5, movie.getDirector());
	        statement.setString(6, movie.getSummary());

	        int affectedRows = statement.executeUpdate();

	        if (affectedRows == 0) {
	            throw new SQLException("Échec de l'ajout du film, aucune ligne affectée.");
	        }

	        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                movie.setId(generatedKeys.getInt(1));
	            } else {
	                throw new SQLException("Échec de la récupération de l'ID généré du film.");
	            }
	        }

	        return movie;
	    } catch (SQLException e) {
	        throw new RuntimeException("Erreur lors de l'ajout du film", e);
	    }
	}

}
