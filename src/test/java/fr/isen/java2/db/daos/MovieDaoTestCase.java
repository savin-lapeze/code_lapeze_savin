package fr.isen.java2.db.daos;
 
 
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.Assert.fail;
import static org.assertj.core.api.Assertions.assertThat;
 
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
 
import org.junit.Before;
import org.junit.Test;
 
import fr.isen.java2.db.entities.Movie;
 
public class MovieDaoTestCase {
	
	private MovieDao movieDao = new MovieDao();
	
	@Before
	public void initDb() throws Exception {
		Connection connection = DataSourceFactory.getDataSource().getConnection();
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS genre (idgenre INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , name VARCHAR(50) NOT NULL);");
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS movie (\r\n"
				+ "  idmovie INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + "  title VARCHAR(100) NOT NULL,\r\n"
				+ "  release_date DATETIME NULL,\r\n" + "  genre_id INT NOT NULL,\r\n" + "  duration INT NULL,\r\n"
				+ "  director VARCHAR(100) NOT NULL,\r\n" + "  summary MEDIUMTEXT NULL,\r\n"
				+ "  CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genre (idgenre));");
		stmt.executeUpdate("DELETE FROM movie");
		stmt.executeUpdate("DELETE FROM genre");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (1,'Drama')");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (2,'Comedy')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (1, 'Title 1', '2015-11-26 12:00:00.000', 1, 120, 'director 1', 'summary of the first movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (2, 'My Title 2', '2015-11-14 12:00:00.000', 2, 114, 'director 2', 'summary of the second movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (3, 'Third title', '2015-12-12 12:00:00.000', 2, 176, 'director 3', 'summary of the third movie')");
		stmt.close();
		connection.close();
	}
	
	 @Test
	 public void shouldListMovies() {
		    // WHEN
		   List<Movie> movies = movieDao.listMovies();

	        // THEN
	        assertThat(movies).hasSize(3);
	        assertThat(movies).extracting("id", "title", "releaseDate", "genre", "duration", "director", "summary")
	                .containsOnly(
	                        tuple(1, "Title 1", LocalDate.of(2015,11,26), 1, 120, "director 1", "summary of the first movie"),
	                        tuple(2, "My Title 2", LocalDate.of(2015,11,14), 2, 114, "director 2", "summary of the second movie"),
	                        tuple(3, "Third title", LocalDate.of(2015, 12, 12), 2, 176, "director 3", "summary of the third movie")
	                );
	    }
	

	 @Test
	 public void shouldListMoviesByGenre() {
	    

	        // WHEN
	        List<Movie> movies = movieDao.listMoviesByGenre("Comedy");

	        // THEN
	        assertThat(movies).hasSize(2);
	        assertThat(movies).extracting("id", "title", "releaseDate", "genre", "duration", "director", "summary")
	                .containsOnly(
	                        tuple(2, "My Title 2", LocalDate.of(2015, 11, 14), 2, 114, "director 2", "summary of the second movie"),
	                        tuple(3, "Third title", LocalDate.of(2015, 12, 12), 2, 176, "director 3", "summary of the third movie")
	                );
	    }
	
	 
	 @Test
	 public void shouldAddMovie() {
	     // WHEN
	     Movie newMovie = new Movie(null, "New Movie", LocalDate.of(2022, 2, 5), 1, 120, "New Director", "Summary of the new movie");
	     Movie addedMovie = movieDao.addMovie(newMovie);

	     // THEN
	     try (Connection connection = DataSourceFactory.getDataSource().getConnection();
	          Statement statement = connection.createStatement();
	          ResultSet resultSet = statement.executeQuery("SELECT * FROM movie WHERE idmovie=" + addedMovie.getId())) {

	         assertThat(resultSet.next()).isTrue();
	         assertThat(resultSet.getString("title")).isEqualTo("New Movie");
	         assertThat(resultSet.getDate("release_date").toLocalDate()).isEqualTo(LocalDate.of(2022, 2, 5));
	         assertThat(resultSet.getInt("genre_id")).isEqualTo(1); // Assurez-vous d'ajuster l'ID du genre correctement
	         assertThat(resultSet.getInt("duration")).isEqualTo(120);
	         assertThat(resultSet.getString("director")).isEqualTo("New Director");
	         assertThat(resultSet.getString("summary")).isEqualTo("Summary of the new movie");

	         assertThat(resultSet.next()).isFalse();
	     } catch (SQLException e) {
	         // Gérez l'exception ici si nécessaire
	         e.printStackTrace();
	     }
	 }
	 }

