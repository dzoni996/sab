import operations.*;
import student.*;
import tests.TestHandler;
import tests.TestRunner;


public class StudentMain {

    public static void main(String[] args) {

        String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=SAB2019;user=sa;password=1";

        dn150395_GeneralOperation genOper = new dn150395_GeneralOperation(connectionUrl);
        int sys_id = genOper.createSystem();

        // Change this for your implementation (points will be negative if interfaces are not implemented).

        ArticleOperations articleOperations = new dn150395_ArticleOperation(connectionUrl);
        BuyerOperations buyerOperations = new dn150395_BuyerOperation(connectionUrl);
        CityOperations cityOperations = new dn150395_CityOperation(connectionUrl);
        GeneralOperations generalOperations = new dn150395_GeneralOperation(connectionUrl);
        OrderOperations orderOperations = new dn150395_OrderOperation(connectionUrl, sys_id);
        ShopOperations shopOperations = new dn150395_ShopOperation(connectionUrl);
        TransactionOperations transactionOperations = new dn150395_TransactionOperation(connectionUrl);

        TestHandler.createInstance(
                articleOperations,
                buyerOperations,
                cityOperations,
                generalOperations,
                orderOperations,
                shopOperations,
                transactionOperations
        );

        TestRunner.runTests();
    }
}
