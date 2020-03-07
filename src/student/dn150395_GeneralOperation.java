package student;

import operations.GeneralOperations;
import student.dijkstra_alg.Dijkstra;
import student.dijkstra_alg.Graph;
import student.dijkstra_alg.Node;

import java.sql.*;
import java.util.Calendar;
import java.util.List;

public class dn150395_GeneralOperation implements GeneralOperations {

	private String connectionUrl;
	public static Calendar current_time;

	public dn150395_GeneralOperation(String url) {
		connectionUrl = url;
	}

	/**
	 * Sets initial time
	 * @param time time
	 */
	@Override
	public void setInitialTime(Calendar time) {
		current_time = time;
	}


	/**
	 * Time to pass in simulation.
	 * @param days number of days that will pass in simulation after this method call
	 * @return current time
	 */
	@Override
	public Calendar time(int days) {
		long offset = days*24;
		offset *= 60;
		offset *= 60;
		offset *= 1000;
		current_time.setTimeInMillis(current_time.getTimeInMillis() + offset);

		String call = "exec dbo.RECEIVE_ORDERS ?";

		try (Connection con = DriverManager.getConnection(connectionUrl);
			 CallableStatement cstmt = con.prepareCall(call);
		) {
			Date date = new Date(current_time.getTimeInMillis());
			cstmt.setDate(1, date);
			boolean b = cstmt.execute(); // update status for received orders

		} catch (SQLException e){
			e.printStackTrace();
		}

		return current_time;
	}

	@Override
	public Calendar getCurrentTime() {

		return current_time;
	}

	@Override
	public void eraseAll() {
		String query1 = "DELETE FROM [Transaction]";
		String query2 = "DELETE FROM Contain";
		String query3 = "DELETE FROM Seller";
		String query31 = "DELETE FROM Profit";
		String query4 = "DELETE FROM [Order]";
		String query5 = "DELETE FROM Buyer";
		String query6 = "DELETE FROM Amount";
		String query7 = "DELETE FROM Article";
		String query8 = "DELETE FROM Shop";
		String query9 = "DELETE FROM Connection";
		String query10 = "DELETE FROM City";
		String query11 = "DELETE FROM [System]";

		try (Connection con = DriverManager.getConnection(connectionUrl);
			 Statement stmt = con.createStatement();
		) {

			stmt.addBatch(query1);
			stmt.addBatch(query2);
			stmt.addBatch(query3);
			stmt.addBatch(query31);
			stmt.addBatch(query4);
			stmt.addBatch(query5);
			stmt.addBatch(query6);
			stmt.addBatch(query7);
			stmt.addBatch(query8);
			stmt.addBatch(query9);
			stmt.addBatch(query10);
			stmt.addBatch(query11);

			int[] n = stmt.executeBatch();
		}

		// Handle any errors that may have occurred.
		catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// Extra operations ********************************************************

	public int createSystem(){
		String insert = "INSERT INTO dbo.[System](Name) values ('Server')";

		try(Connection con = DriverManager.getConnection(connectionUrl);
			Statement stmt = con.createStatement();
		){
			int cnt = stmt.executeUpdate(insert, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next())
				return rs.getInt(1);

		}catch (SQLException e){
			e.printStackTrace();
		}
		return -1;
	}

}
