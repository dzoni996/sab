package student;

import operations.ArticleOperations;

import java.sql.*;

public class dn150395_ArticleOperation implements ArticleOperations {

	private String connectionUrl;

	public dn150395_ArticleOperation(String url) {
		connectionUrl = url;
	}

	@Override
	public int createArticle(int shopId, String articleName, int articlePrice) {

		String query = "INSERT INTO dbo.Article(Name) VALUES ('" + articleName + "')";
		String query2 = "INSERT INTO dbo.Amount(IdShop, IdArticle, Amount, Price) VALUES (?,?,0,?)";
		String query3 = "SELECT Id FROM dbo.Article WHERE Name = '" + articleName + "'";

		try (Connection con = DriverManager.getConnection(connectionUrl);
			 Statement stmt = con.createStatement();
			 PreparedStatement ps = con.prepareStatement(query2);
		){
			int n = stmt.executeUpdate(query);
			if (n == 1){
				ResultSet rs = stmt.executeQuery(query3);
				if (rs.next()){
					ps.setInt(1,shopId);
					int artId = rs.getInt("Id");
					ps.setInt(2,artId);
					ps.setInt(3,articlePrice);
					int n2 = ps.executeUpdate();
					if (n2 > 0)
						return artId;
				}
			}

		} catch (SQLException e){
			e.printStackTrace();
		}
		return -1;
	}

}
