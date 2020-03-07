package student;

import operations.TransactionOperations;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class dn150395_TransactionOperation implements TransactionOperations {

	private String connectionUrl;

	public dn150395_TransactionOperation(String url) {
		connectionUrl = url;
	}

	@Override
	public BigDecimal getBuyerTransactionsAmmount(int buyerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getShopTransactionsAmmount(int shopId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getTransationsForBuyer(int buyerId) {

		String query = "SELECT Id FROM [Transaction] WHERE IdBuyer = "+buyerId;

		try(Connection con = DriverManager.getConnection(connectionUrl);
			Statement stmt = con.createStatement();
		){
			ResultSet rs = stmt.executeQuery(query);
			List<Integer> list = new LinkedList<>();
			if (rs.next()) {
				do {
					list.add(rs.getInt("Id"));
				} while (rs.next());
				return list;
			}

		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public int getTransactionForBuyersOrder(int orderId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTransactionForShopAndOrder(int orderId, int shopId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Integer> getTransationsForShop(int shopId) {

		String query = "SELECT Id FROM [Transaction] WHERE IdShop = "+shopId;

		try(Connection con = DriverManager.getConnection(connectionUrl);
			Statement stmt = con.createStatement();
		){
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				List<Integer> list = new LinkedList<>();
				do {
					list.add(rs.getInt(1));
				} while (rs.next());
				return list;
			}


		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Calendar getTimeOfExecution(int transactionId) {

		String query = "SELECT ExecutionTime FROM [Transaction] WHERE Id = "+transactionId;

		try(Connection con = DriverManager.getConnection(connectionUrl);
			Statement stmt = con.createStatement();
		){
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				Calendar ret = Calendar.getInstance();
				ret.setTimeInMillis(rs.getDate(1).getTime());
				return ret;
			}

		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public BigDecimal getAmmountThatBuyerPayedForOrder(int orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getAmmountThatShopRecievedForOrder(int shopId, int orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getTransactionAmount(int transactionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getSystemProfit() {

		String query = "SELECT SUM(Amount) FROM dbo.Profit";

		try(Connection con = DriverManager.getConnection(connectionUrl);
			Statement stmt = con.createStatement();
		){
			ResultSet rs = stmt.executeQuery(query);
			Calendar time = null;
			if (rs.next()) {
				BigDecimal amount = rs.getBigDecimal(1);
				return amount;
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

}
