package student;

import operations.ShopOperations;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class dn150395_ShopOperation implements ShopOperations {

	private String connectionUrl;

	public dn150395_ShopOperation(String url) {
		connectionUrl = url;
	}

	@Override
	public int createShop(String name, String cityName) {
		String query2 = "SELECT * FROM dbo.City WHERE Name = '" + cityName + "'";
		String query = "INSERT INTO dbo.Shop(Name, Discount, IdCity) VALUES (?,?,?)";

		try (Connection con = DriverManager.getConnection(connectionUrl);
			 PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			 Statement stmt2 = con.createStatement();
		) {
			ResultSet rs = stmt2.executeQuery(query2);

			if (rs.next()) {
				stmt.setString(1, "'" + name + "'");
				stmt.setInt(2,0);
				int idcity = rs.getInt("Id");
				stmt.setString(3,"" + idcity);
				int n = stmt.executeUpdate();

				if (n == 0)
					return -1;
				else {
					int key = -1;
					try (ResultSet genKeys = stmt.getGeneratedKeys();) {
						if (genKeys.next())
							key = genKeys.getInt(1);
					} catch (Exception e) {
						return -1;
					}
					return key;
				}
			} else
				return -1;

		}
		// Handle any errors that may have occurred.
		catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public int setCity(int shopId, String cityName) {
		String query2 = "SELECT * FROM dbo.City WHERE Name = '" + cityName + "'";
		String query = "UPDATE Shop SET IdCity = ? WHERE Id = " + shopId;

		try (Connection con = DriverManager.getConnection(connectionUrl);
			 PreparedStatement stmt = con.prepareStatement(query);
			 Statement stmt2 = con.createStatement();
		) {
			ResultSet rs = stmt2.executeQuery(query2);

			if (rs.next()) {
				int idcity = rs.getInt("Id");
				stmt.setString(1,"" + idcity);
				int n = stmt.executeUpdate();

				if (n == 1)
					return 1;
			}
			else
				return -1;

		}
		// Handle any errors that may have occurred.
		catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public int getCity(int shopId) {
		String query  = "SELECT IdCity FROM dbo.Shop WHERE Id = ?";

		try (Connection con = DriverManager.getConnection(connectionUrl);
				PreparedStatement stmt = con.prepareStatement(query);
			){
			stmt.setInt(1,shopId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()){
				return rs.getInt("IdCity");
			}

		} catch (SQLException e){
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public int setDiscount(int shopId, int discountPercentage) {

		if (discountPercentage < 0 || discountPercentage > 100) return -1;

		String query  = "UPDATE dbo.Shop SET Discount = ? WHERE Id = ?";

		try (Connection con = DriverManager.getConnection(connectionUrl);
			 PreparedStatement stmt = con.prepareStatement(query);
		){
			stmt.setInt(1,discountPercentage);
			stmt.setInt(2,shopId);
			int rs = stmt.executeUpdate();
			if (rs == 1){
				return 1;
			}

		} catch (SQLException e){
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public int increaseArticleCount(int articleId, int increment) {

		String query  = "UPDATE dbo.Amount SET Amount = Amount + ? WHERE IdArticle = ?";
		String query2 = "SELECT Amount FROM dbo.Amount WHERE IdArticle = " + articleId;

		try (Connection con = DriverManager.getConnection(connectionUrl);
			 PreparedStatement stmt = con.prepareStatement(query);
			 	Statement stmt2 = con.createStatement();
		){
			stmt.setInt(1,increment);
			stmt.setInt(2,articleId);
			int rs = stmt.executeUpdate();
			if (rs > 0){
				ResultSet rs2 = stmt2.executeQuery(query2);
				if (rs2.next())
					return rs2.getInt("Amount");
			}

		} catch (SQLException e){
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public int getArticleCount(int articleId) {

		String query = "SELECT Amount FROM dbo.Amount WHERE IdArticle = ?";

		try (Connection con = DriverManager.getConnection(connectionUrl);
			 PreparedStatement stmt = con.prepareStatement(query);
		){
			stmt.setInt(1,articleId);
			//stmt.setInt(2,shopId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()){
				return rs.getInt("Amount");
			}

		} catch (SQLException e){
			e.printStackTrace();
		}
		return -1;

	}

	@Override
	public List<Integer> getArticles(int shopId) {

		String query = "SELECT IdArticle FROM dbo.Amount WHERE IdShop = ?";

		try (Connection con = DriverManager.getConnection(connectionUrl);
			 PreparedStatement stmt = con.prepareStatement(query);
		){
			stmt.setInt(1,shopId);
			ResultSet rs = stmt.executeQuery();
			List<Integer> list = new LinkedList<>();

			while (rs.next()){
				list.add(rs.getInt("IdArticle"));
			}
			return list;

		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int getDiscount(int shopId) {

		String query = "SELECT Discount FROM dbo.Shop WHERE Id = ?";

		try (Connection con = DriverManager.getConnection(connectionUrl);
			 PreparedStatement stmt = con.prepareStatement(query);
		){
			stmt.setInt(1,shopId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()){
				return rs.getInt("Discount");
			}

		} catch (SQLException e){
			e.printStackTrace();
		}
		return -1;
	}

}
