package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static fr.isen.java2.db.daos.DataSourceFactory.getDataSource;
import fr.isen.java2.db.entities.Genre;

public class GenreDao {

	public List<Genre> listGenres() {
		List<Genre> genres = new ArrayList<>();
		String query = "SELECT * FROM genre";
		try (Connection connection= getDataSource().getConnection()){	
			try (PreparedStatement statement = connection.prepareStatement(query)){
					ResultSet resultSet = statement.executeQuery();
					
					
					while (resultSet.next()) {
						Genre genre = new Genre();
				        genre.setId(resultSet.getInt("idgenre"));
				        genre.setName(resultSet.getString("name"));
				        genres.add(genre);
					}
			
			}
		
		    } catch (SQLException e) {
		        throw new RuntimeException("Erreur lors de la récupération des genres", e);
		    }
		    return genres;
	}

		
	

	public Genre getGenre(String name)  {
        String query = "SELECT * FROM genre WHERE name = ?";
        Genre genre = null;
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    genre = new Genre();
                    genre.setId(resultSet.getInt("idgenre"));
                    genre.setName(resultSet.getString("name"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du genre", e);
        }
        return genre;
    }
		

	public void addGenre(String name) {
	    String query = "INSERT INTO genre(name) VALUES(?)";

	    try (Connection connection = getDataSource().getConnection()) {
	        try (PreparedStatement statement = connection.prepareStatement(query)) {
	            statement.setString(1, name);
	            statement.executeUpdate();
	        }
	    } catch (SQLException e) {
	        throw new RuntimeException("Erreur lors de l'ajout du genre", e);
	    }
	}

}
