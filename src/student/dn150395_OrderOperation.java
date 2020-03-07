package student;

import operations.OrderOperations;
import student.dijkstra_alg.Dijkstra;
import student.dijkstra_alg.Graph;
import student.dijkstra_alg.Node;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class dn150395_OrderOperation implements OrderOperations {

	private String connectionUrl;
	private int sys_id;

	public dn150395_OrderOperation(String url, int sys_id) {
		this.sys_id = sys_id;
		connectionUrl = url;
	}

	/**
	 * Adds article to order.
	 * It adds articles only if there are enough of them in shop.
	 * If article is in order already, it only increases count.
	 * @param orderId order id
	 * @param articleId article id
	 * @param count number of articles to be added
	 * @return item id (item contains information about number of article instances in particular order), -1 if failure
	 */
	@Override
	public int addArticle(int orderId, int articleId, int count) {
        // CREATE PROCEDURE addArticle (@articleId int, @shopId int, @cnt int, @orderId int, @result int OUTPUT}
		String Call = "exec dbo.addArticle ?, ?, ?, ?, ?";
        String query1 = "SELECT IdShop FROM Amount WHERE IdArticle = "+articleId;
        String query2 = "SELECT Id FROM Contain WHERE IdArticle = "+articleId+" AND IdOrder = "+orderId;

		try (Connection con = DriverManager.getConnection(connectionUrl);
             CallableStatement call = con.prepareCall(Call);
            Statement stmt = con.createStatement();
		){
		    int shopId = -1;
            ResultSet rs = stmt.executeQuery(query1);
            if (rs.next())
                shopId = rs.getInt("IdShop");
            else
                return -1;

            call.setInt(1,articleId);
            call.setInt(2,shopId);
            call.setInt(3,count);
            call.setInt(4,orderId);
            call.registerOutParameter(5, Types.INTEGER);

			call.execute();

			int n = call.getInt(5);

			if (n == 0){ // success
				int key = -1;
				ResultSet genKeys = stmt.executeQuery(query2);
				if (genKeys.next())
					key = genKeys.getInt(1);
				return key;
			}

		} catch (SQLException e){
			e.printStackTrace();
		}
		return -1;
	}


	/**
	 * Removes article from order.
	 * @param orderId order id
	 * @param articleId article id
	 * @return 1 if success, -1 if failure
	 */
	@Override
	public int removeArticle(int orderId, int articleId) {

		String query = "DELETE FROM Contain WHERE IdOrder = ? AND IdArticle = ?";
		String update = "UPDATE Amount SET Amount = Amount + (SELECT Count FROM [Contain] WHERE IdArticle = ? AND IdOrder = ?) " +
				"WHERE IdArticle = ?";

		try (Connection con = DriverManager.getConnection(connectionUrl);
			 PreparedStatement ps1 = con.prepareStatement(update);
			 PreparedStatement stmt = con.prepareStatement(query);
		){
			ps1.setInt(1,articleId);
			ps1.setInt(2,orderId);
			ps1.setInt(3,articleId);
			int n1 = ps1.executeUpdate();

			stmt.setInt(1,orderId);
			stmt.setInt(2,articleId);
			int n2 = stmt.executeUpdate();

			if (n1 == 1 && n2 == 1)
				return 1;

		} catch (SQLException e){
			e.printStackTrace();
		}

		return -1;
	}


	/**
	 * Get all items for order.
	 * @param orderId order's id
	 * @return list of item ids for an order
	 */
	@Override
	public List<Integer> getItems(int orderId) {

		String query = "SELECT Id FROM Contain WHERE IdOrder = "+orderId;

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

	/**
	 * Sends order to the system. Order will be immediately sent.
	 * @param orderId oreder id
	 * @return 1 if success, -1 if failure
	 */
	@Override
	public int completeOrder(int orderId) {

		String query = "UPDATE [Order] SET [STATUS] = 'sent', SentTime = ? WHERE Id = ?";
		String call = "exec dbo.SP_FINAL_PRICE ?";
		String update = "UPDATE [Order] SET ReceivedTime = DATEADD(day, ?, SentTime), IdCityNearest = ?, " +
				"ReceivedTimeNearest = DATEADD(day, ?, SentTime) WHERE Id = "+orderId;

		try (Connection con = DriverManager.getConnection(connectionUrl);
			 PreparedStatement pstmt = con.prepareStatement(query);
			 PreparedStatement pstmt2 = con.prepareStatement(update);
			 CallableStatement cstmt = con.prepareCall(call);
			 Statement stmt = con.createStatement();
		) {

			// CALCULATING PRICES ****************************************************
			cstmt.setInt(1, orderId); // all prices are calculated here
			boolean b = cstmt.execute();

			// UPDATE STATUS AND SENT TIME *******************************************
			pstmt.setDate(1, new Date(dn150395_GeneralOperation.current_time.getTimeInMillis()));
			pstmt.setInt(2, orderId);
			int cnt = pstmt.executeUpdate();

			// CALCULATING TIME FOR ORDER TO ARRIVE **********************************

			// 1. Buyer city, where order will go
			int destCity = -1;
			ResultSet rs2 = stmt.executeQuery(
					"SELECT IdCity FROM Buyer WHERE Id = (SELECT IdBuyer FROM [Order] WHERE Id = "+orderId+")");
			if (rs2.next()) destCity = rs2.getInt("IdCity");
			else System.out.println("ERROR: No city specify in IdCity from Buyer of Order!");

			// 2. Grapf with all cities
			String query2 = "SELECT Id1, Id2, Distance FROM Connection ORDER BY Id1 ASC, Id2 ASC";
			ResultSet rs = stmt.executeQuery(query2);
			List<Node> connected = new LinkedList<>();
			connected.clear();
			while (rs.next()){
				int id1 = rs.getInt("Id1");
				int id2 = rs.getInt("Id2");
				int dist = rs.getInt("Distance");
				Node node1 = Dijkstra.getNodeByName(connected, ""+id1);
				Node node2 = Dijkstra.getNodeByName(connected, ""+id2);
				if (node1 == null) node1 = new Node(""+id1);
				if (node2 == null) node2 = new Node(""+id2);
				node1.addDestination(node2, dist);
				node2.addDestination(node1, dist);
				connected.add(node1);
				connected.add(node2);
			}
			Node source = Dijkstra.getNodeByName(connected, ""+destCity);
			if (source == null) System.out.println("ERROR: source node is null - not in graph!");
			Graph graph = Dijkstra.getShortestPath(connected, source);
			// check ***************************************************************
//			for (Node n:graph.getNodes())
//				System.out.println(n.getName() + " " + n.getDistance());
			// *********************************************************************

			// 3. Orders cities to list and find nearest city to desination
			List<Node> sources = new LinkedList<>();
			ResultSet rs3 = stmt.executeQuery(
					"SELECT s.IdCity FROM Shop s, Seller sel WHERE s.Id = sel.IdShop AND sel.IdOrder = "+orderId);
			int shortestCity = -1;
			int distance = Integer.MAX_VALUE;
			while (rs3.next()){
				int temp_id = rs3.getInt(1);
				if (temp_id == destCity) continue; // should NOT happen!
				int temp = Dijkstra.getShortestPathDistance(graph,""+temp_id);
				if (temp < distance){
					shortestCity = temp_id;
					distance = temp;
				}
				if (Dijkstra.getNodeByName(sources, ""+temp_id) == null)
					sources.add(Dijkstra.getNodeByName (connected,""+temp_id));
			}

			// 5. Gathering final duration
			Graph graph2 = Dijkstra.getShortestPath(sources, Dijkstra.getNodeByName(graph, ""+shortestCity));
			int max_distance = 0;
			for (Node node : graph2.getNodes()){
				if (node.getDistance() > max_distance)
					max_distance = node.getDistance();
			}

			int final_distance = distance + max_distance; // sum of days needed for order arrival

			// 6. Update ReceivedTime
//			int n = stmt.executeUpdate(
//					"UPDATE [Order] SET ReceivedTime = DATEADD(day, "+final_distance+", SentTime) WHERE Id = "+orderId);


			// 4. Update nearest city and its distance from destination, where all articles come
//			int offset = distance * 24 * 60 * 60 * 1000;
//			Date date = new Date(dn150395_GeneralOperation.current_time.getTimeInMillis() + offset);
//			pstmt2.setInt(1, shortestCity);
//			pstmt2.setDate(2, date);
//			n = pstmt2.executeUpdate();
			// DATEADD(day, "+distance+", SentTime)


			// UPDATING RECEIVE TIME AND NEAREST CITY INFO
			pstmt2.setInt(1,final_distance);
			pstmt2.setInt(2,shortestCity);
			pstmt2.setInt(3,distance);
			int nn = pstmt2.executeUpdate();

			// **********************************************************

			if (pstmt2.getUpdateCount() == 1) {
				return 1;
			}

		} catch (SQLException e){
			e.printStackTrace();
		}

		return -1;
	}

	/**
	 * Gets calculated final price after all discounts.
	 * @param orderId order id
	 * @return final price. Sum that buyer have to pay. -1 if failure or if order is not completed
	 */
	@Override
	public BigDecimal getFinalPrice(int orderId) {

		String query = "SELECT FinalPrice FROM dbo.[Order] WHERE Id = "+orderId;

		try(Connection con = DriverManager.getConnection(connectionUrl);
			Statement stmt = con.createStatement();
		){
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next())
				return rs.getBigDecimal(1);
		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * Gets calculated discount for the order
	 * @param orderId order id
	 * @return total discount, -1 if failure or if order is not completed
	 */
	@Override
	public BigDecimal getDiscountSum(int orderId) {

		String query = "SELECT DiscountSum FROM dbo.[Order] WHERE Id = "+orderId;

		try(Connection con = DriverManager.getConnection(connectionUrl);
			Statement stmt = con.createStatement();
		){
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next())
				return rs.getBigDecimal(1);
		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets state of the order.
	 * @param orderId order's id
	 * @return state of the order
	 */
	@Override
	public String getState(int orderId) {

		String query = "SELECT [Status] FROM dbo.[Order] WHERE Id = "+orderId;

		try(Connection con = DriverManager.getConnection(connectionUrl);
			Statement stmt = con.createStatement();
		){
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				String st = rs.getString(1);
				return st;
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets order's sending time
	 * @param orderId order's id
	 * @return order's sending time, null if failure
	 */
	@Override
	public Calendar getSentTime(int orderId) {

		String query = "SELECT SentTime FROM dbo.[Order] WHERE Id = "+orderId;

		try(Connection con = DriverManager.getConnection(connectionUrl);
			Statement stmt = con.createStatement();
		){
			ResultSet rs = stmt.executeQuery(query);
			Calendar time = null;
			if (rs.next()) {
				Date date = rs.getDate(1);
				if (date != null) {
					time.setTimeInMillis(date.getTime());
					return time;
				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets time when order arrived to buyer's city.
	 * @param orderId order id
	 * @return order's recieve time, null if failure
	 */
	@Override
	public Calendar getRecievedTime(int orderId) {

		String query = "SELECT ReceivedTime FROM dbo.[Order] WHERE Id = "+orderId;

		try(Connection con = DriverManager.getConnection(connectionUrl);
			Statement stmt = con.createStatement();
		){
			ResultSet rs = stmt.executeQuery(query);
			Calendar time = null;
			if (rs.next())
				time.setTimeInMillis(rs.getDate(1).getTime());
			return time;
		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets buyer.
	 * @param orderId order's id
	 * @return buyer's id
	 */
	@Override
	public int getBuyer(int orderId) {

		String query = "SELECT IdBuyer FROM dbo.[Order] WHERE Id = "+orderId;

		try(Connection con = DriverManager.getConnection(connectionUrl);
			Statement stmt = con.createStatement();
		){
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next())
				return rs.getInt("IdBuyer");
		} catch (SQLException e){
			e.printStackTrace();
		}
		return -1;
	}


	/**
	 * Gets location for an order.
	 * If order is assembled and order is moving from city C1 to city C2 then location of an order is city C1.
	 * If order is not yet assembled then location of the order is location of the shop (associated with order) that is closest to buyer's city.
	 * If order is in state "created" then location is -1.
	 * @param orderId order's id
	 * @return id of city, -1 if failure
	 */
	@Override
	public int getLocation(int orderId) {

		String query = "SELECT [Status] FROM dbo.[Order] WHERE Id = "+orderId;
		String query2 = "SELECT IdCity FROM dbo.[Order] WHERE Id = "+orderId;

		try(Connection con = DriverManager.getConnection(connectionUrl);
			Statement stmt = con.createStatement();
		){
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				if (rs.getString(1).equals("created"))
					return -1;
				else {
					ResultSet rs2 = stmt.executeQuery(query2);
					if (rs2.next())
						return rs2.getInt(1);
				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return -1;
	}

}
