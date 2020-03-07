package student;

import operations.BuyerOperations;

import java.math.BigDecimal;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class dn150395_BuyerOperation implements BuyerOperations {

	private String connectionUrl;

	public dn150395_BuyerOperation(String url) {
		connectionUrl = url;
	}

	@Override
	public int createBuyer(String name, int cityId) {

		String query = "INSERT INTO dbo.Buyer(Name, Credit, IdCity) VALUES (?,0,?)";

		try (Connection con = DriverManager.getConnection(connectionUrl);
			 PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		){
			stmt.setString(1, name);
			stmt.setInt(2,cityId);
			int n = stmt.executeUpdate();
			if (n == 1){
				int key = -1;
				try (ResultSet genKeys = stmt.getGeneratedKeys();) {
					if (genKeys.next())
						key = genKeys.getInt(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return key;
			}

		} catch (SQLException e){
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public int setCity(int buyerId, int cityId) {
		String query = "UPDATE dbo.Buyer SET IdCity = " + cityId + " WHERE Id = "+buyerId;

		try(Connection con = DriverManager.getConnection(connectionUrl);
			Statement stmt = con.createStatement();
		){
			int n = stmt.executeUpdate(query);
			if (n == 1)
				return 1;
		} catch (SQLException e){
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public int getCity(int buyerId) {
		String query = "SELECT IdCity FROM dbo.Buyer WHERE Id = "+buyerId;

		try(Connection con = DriverManager.getConnection(connectionUrl);
			Statement stmt = con.createStatement();
		){
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next())
				return rs.getInt("IdCity");
		} catch (SQLException e){
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public BigDecimal increaseCredit(int buyerId, BigDecimal credit) {
		String query = "UPDATE dbo.Buyer SET Credit = Credit + " + credit + " WHERE Id = "+buyerId;
		String query2 = "SELECT Credit FROM dbo.Buyer WHERE Id = "+buyerId;

		try(Connection con = DriverManager.getConnection(connectionUrl);
			Statement stmt = con.createStatement();
		){
			int n = stmt.executeUpdate(query);
			if (n == 1) {
				ResultSet rs = stmt.executeQuery(query2);
				if (rs.next()) {
					BigDecimal ret = rs.getBigDecimal("Credit");
					return ret;

				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int createOrder(int buyerId) {

		String query = "INSERT INTO [Order](Status, IdBuyer) VALUES (?,?)";

		try (Connection con = DriverManager.getConnection(connectionUrl);
			 PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		){
			stmt.setString(1,"created");
			stmt.setInt(2,buyerId);
			int n = stmt.executeUpdate();
			if (n == 1){
				int key = -1;
				try (ResultSet genKeys = stmt.getGeneratedKeys();) {
					if (genKeys.next())
						key = genKeys.getInt(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return key;
			}

		} catch (SQLException e){
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public List<Integer> getOrders(int buyerId) {

		String query = "SELECT Id FROM [Order] WHERE IdBuyer = "+buyerId;

		try(Connection con = DriverManager.getConnection(connectionUrl);
			Statement stmt = con.createStatement();
		){
			ResultSet rs = stmt.executeQuery(query);
			List<Integer> list = new LinkedList<>();
			while (rs.next())
				list.add(rs.getInt("Id"));
			return list;

		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public BigDecimal getCredit(int buyerId) {

		String query = "SELECT Credit FROM dbo.Buyer WHERE Id = "+buyerId;

		try(Connection con = DriverManager.getConnection(connectionUrl);
			Statement stmt = con.createStatement();
		){
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				BigDecimal ret = rs.getBigDecimal("Credit").setScale(3);
				return ret;
			}

		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

}
