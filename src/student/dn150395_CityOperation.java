package student;

import operations.CityOperations;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class dn150395_CityOperation implements CityOperations {
	
	private String connectionUrl;

	public dn150395_CityOperation(String url) {
        connectionUrl = url;
	}


	@Override
	public int createCity(String name) {	

	    String query = "INSERT INTO dbo.City(Name) VALUES (?)";
        String query2 = "SELECT Id FROM dbo.City WHERE Name = '" + name + "'";

		try (Connection con = DriverManager.getConnection(connectionUrl);
			 PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			 Statement stmt2 = con.createStatement();
		){
		    ResultSet rset = stmt2.executeQuery(query2);
		    if (rset.next() == false) {
                stmt.setString(1, name);
                int n = stmt.executeUpdate();
                if (n == 1) {
                    int key = -1;
                    try (ResultSet genKeys = stmt.getGeneratedKeys();) {
                        if (genKeys.next())
                            key = genKeys.getInt(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return key;
                }
            }
		} catch (SQLException e){
			e.printStackTrace();
		}
		return -1;
        	
	}

	
	@Override
	public List<Integer> getCities() {
		try (Connection con = DriverManager.getConnection(connectionUrl);
			 Statement stmt = con.createStatement();) {

			String SQL = "SELECT Id FROM dbo.City";
			ResultSet rs = stmt.executeQuery(SQL);

			List<Integer> retIds = new LinkedList<>();
			while (rs.next()) {
				//System.out.println(rs.getInt(1) + " " + rs.getString("Name"));
				retIds.add(new Integer(rs.getInt(1)));
			}

			stmt.close();

			return retIds;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int connectCities(int cityId1, int cityId2, int distance) {
//		String query = "IF NOT EXISTS (SELECT * FROM Connection WHERE (Id1 = " + cityId1 + " and Id2 = " + cityId2 + ")" +
//				" OR (Id1 = " + cityId2 + " and Id2 = " + cityId1 + ")) "
//				+ "INSERT INTO dbo.Connection(Id1, Id2, Distance) VALUES (" + cityId1 + "," + cityId2 + "," + distance + ")";
        String query = "INSERT INTO dbo.Connection(Id1, Id2, Distance) VALUES (" + cityId1 + "," + cityId2 + "," + distance + ")";
		String query2 = "SELECT * FROM Connection WHERE (Id1 = " + cityId1 + " and Id2 = " + cityId2 + ")" +
		" OR (Id1 = " + cityId2 + " and Id2 = " + cityId1 + ")";
		try (Connection con = DriverManager.getConnection(connectionUrl);
			 PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			 Statement stmt2 = con.createStatement();
		) {

            ResultSet rs = stmt2.executeQuery(query2);

            if (rs.next() == false) {
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
	public List<Integer> getConnectedCities(int cityId) {
		String query1 = "SELECT Id1 FROM Connection where Id2 = '" + cityId + "'";
		String query2 = "SELECT Id2 FROM Connection where Id1 = '" + cityId + "'";

		try (Connection con = DriverManager.getConnection(connectionUrl);
			 Statement stmt = con.createStatement();
		) {
			List<Integer> retList = new LinkedList<>();
			ResultSet rs = stmt.executeQuery(query1);
			while (rs.next())
				retList.add(rs.getInt("Id1"));
			rs = stmt.executeQuery(query2);
			while (rs.next())
				retList.add(rs.getInt("Id2"));

			return retList;
		}
		// Handle any errors that may have occurred.
		catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Integer> getShops(int cityId) {
		String query = "SELECT Id FROM Shop WHERE IdCity = '" + cityId + "'";

		try (Connection con = DriverManager.getConnection(connectionUrl);
			 Statement stmt = con.createStatement();
		) {
			LinkedList<Integer> list = new LinkedList<>();

			ResultSet rs = stmt.executeQuery(query);

			while (rs.next())
				list.add(rs.getInt(1));

				return list;
		}

		// Handle any errors that may have occurred.
		catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
