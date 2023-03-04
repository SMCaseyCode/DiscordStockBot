package SMCaseyCode;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.historical.trade.Trade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static SMCaseyCode.ProtectedData.URL;

public class DatabaseManager {

    AlpacaManager api = new AlpacaManager();

    // POSSIBLE OPTIMIZATION
    //TODO: Change DB To have current pricing of each symbol update once/per min. See Details Below:
    // API supports 200 API calls per minute on free tier... uh oh. Solution? Here it is:
    // 1. Place ALL symbols that are held by users into a DB.
    // 2. Update first 200 price minute 1
    // 3. Update second set of 200 price minute 2
    // 4. Repeat until DB is fulfilled.
    // Pros:
    // "Fast" updates, users cannot rate limit bot
    // Cons:
    // After 200 x 15, may as well use a free unlimited call API w/ 15 minute delay. <-- unlikely to reach that point
    // Would have to add a SECOND API for price check command

    // ^ Spamming API did not rate limit. Will keep current structure for now ^

    public static Connection connect() throws SQLException {
        String url = URL.getContent();
        return DriverManager.getConnection(url);
    }

    public void checkUser(String userID) {
        try (Connection conn = connect()){

            String selectQuery = "select userID from users where userID=?";
            PreparedStatement ps = conn.prepareStatement(selectQuery);

            ps.setString(1, userID);

            ResultSet rs = ps.executeQuery();


                if (!Objects.equals(rs.getString("userID"), userID)){
                    String insertQuery = "insert into users(userID, userWallet) values (?,?)";
                    ps = conn.prepareStatement(insertQuery);

                    ps.setString(1, userID);
                    ps.setDouble(2, 10000.00);

                    ps.executeUpdate();
                }

            ps.close();

        } catch (SQLException e){
            System.out.println("SQL CHECK USER ERROR: " + e);
        }
    }

    public boolean resetAccount(String userID, OptionMapping confirmation) {
        try (Connection conn = connect()){

            String updateQuery = "update users set userWallet = 10000.00 where userID =?";
            PreparedStatement ps = conn.prepareStatement(updateQuery);

            String deleteQuery = "delete from portfolio where userID =?";
            PreparedStatement ps2 = conn.prepareStatement(deleteQuery);

            ps.setString(1, userID);
            ps2.setString(1, userID);

            if (confirmation.getAsString().equalsIgnoreCase("confirm")) {
                ps2.executeUpdate();
                ps.executeUpdate();
                return true;
            }

            ps.close();

            return false;

        } catch (SQLException e) {
            System.out.println("SQL RESET USER ERROR: " + e);
            return false;
        }
    }

    public int buySymbol(String userID, Integer qty, String symbol) {
        try (Connection conn = connect()){
            Trade trade = api.alpacaGetTrade(symbol);

            if (trade != null){
                double totalStockPrice = trade.getP() * qty;

                String selectQuery = "select userWallet from users where userID=?";
                PreparedStatement ps = conn.prepareStatement(selectQuery);

                ps.setString(1, userID);
                ResultSet rs = ps.executeQuery();

                if (rs.getDouble("userWallet") >= totalStockPrice){
                    int owned = checkOwnership(userID, symbol);

                    if (owned > 0){
                        String totalSpentQuery = "select totalSpent, quantity from portfolio where userID=? and symbol =?";
                        ps = conn.prepareStatement(totalSpentQuery);
                        ps.setString(1, userID);
                        ps.setString(2, symbol);
                        rs = ps.executeQuery();
                        double totalSpent = rs.getDouble("totalSpent") + totalStockPrice;
                        int updatedQuantity = rs.getInt("quantity") + qty;

                        String updateQuery = "update portfolio set quantity =?, totalSpent = (totalSpent + ?), avgCost =? where userID =? and symbol=?";
                        ps = conn.prepareStatement(updateQuery);
                        ps.setInt(1, (owned + qty));
                        ps.setDouble(2, totalStockPrice);
                        ps.setDouble(3, totalSpent/updatedQuantity);
                        ps.setString(4, userID);
                        ps.setString(5, symbol);
                        ps.executeUpdate();
                        ps.close();
                    }else {
                        String insertQuery = "insert into portfolio(userID, symbol, quantity, totalSpent, avgCost) values (?,?,?,?,?)";
                        ps = conn.prepareStatement(insertQuery);
                        ps.setString(1, userID);
                        ps.setString(2, symbol);
                        ps.setInt(3, qty);
                        ps.setDouble(4, totalStockPrice);
                        ps.setDouble(5, totalStockPrice/qty);
                        ps.executeUpdate();
                        ps.close();
                    }

                    String updateQuery = "update users set userWallet = (userWallet - ?) where userID=?";
                    ps = conn.prepareStatement(updateQuery);

                    ps.setDouble(1, totalStockPrice);
                    ps.setString(2, userID);
                    ps.executeUpdate();
                    ps.close();

                    return qty + owned;
                }else {
                    return -1;
                }

            }else {
                return -69;
            }

        }catch (SQLException e){
            System.out.println("SQL BUY ERROR: " + e);
            return -2;
        }
    }

    public int sellSymbol(String userID, String symbol, int qty){

        try (Connection conn = connect()){

            Trade trade = api.alpacaGetTrade(symbol);

            if (trade != null){
                double totalSale = trade.getP() * qty;

                String selectQuery = "select quantity, totalSpent from portfolio where userID =? and symbol =?";
                PreparedStatement ps = conn.prepareStatement(selectQuery);

                ps.setString(1, userID);
                ps.setString(2, symbol);

                ResultSet rs = ps.executeQuery();
                int updatedQuantity = rs.getInt("quantity");
                double updatedCost = rs.getDouble("totalSpent") - totalSale;

                if (rs.getInt("quantity") >= qty){

                    if (rs.getInt("quantity") - qty == 0){
                        String deleteQuery = "delete from portfolio where userID=? and symbol=?";
                        ps = conn.prepareStatement(deleteQuery);

                        ps.setString(1, userID);
                        ps.setString(2, symbol);

                        ps.executeUpdate();
                    }else {
                        String updateQuery = "update portfolio set quantity = (quantity - ?), totalSpent = (totalSpent - ?), avgCost =? where userID=? and symbol=?";
                        ps = conn.prepareStatement(updateQuery);

                        ps.setInt(1, qty);
                        ps.setDouble(2, totalSale);
                        ps.setDouble(3, updatedCost/updatedQuantity);
                        ps.setString(4, userID);
                        ps.setString(5, symbol);

                        ps.executeUpdate();
                    }



                    String updateQuery = "update users set userWallet = (userWallet + ?) where userID=?";
                    ps = conn.prepareStatement(updateQuery);

                    ps.setDouble(1, totalSale);
                    ps.setString(2, userID);
                    ps.executeUpdate();
                    ps.close();

                    return qty;

                } else {
                    return 0;
                }
            }else {
                //if trade = null
                return -2;
            }

        }catch (SQLException e){
            System.out.println("SELL SYMBOL SQL ERROR: " + e);
            return -3;
        }
    }

    private int checkOwnership(String userID, String symbol) {
        try (Connection conn = connect()){

            String selectQuery = "select quantity from portfolio where userID=? and symbol=?";
            PreparedStatement ps = conn.prepareStatement(selectQuery);
            ps.setString(1, userID);
            ps.setString(2, symbol);

            ResultSet rs = ps.executeQuery();

            int qty = rs.getInt("quantity");
            ps.close();

            return Math.max(qty, 0);

        }catch (SQLException e){
            System.out.println("CHECK OWNERSHIP SQL ERROR: " + e);
            return 0;
        }
    }

    public double checkWallet(String userID){
        try (Connection conn = connect()){

            String selectQuery = "select userWallet from users where userID=?";
            PreparedStatement ps = conn.prepareStatement(selectQuery);

            ps.setString(1, userID);

            ResultSet rs = ps.executeQuery();
            double balance = rs.getDouble("userWallet");
            ps.close();
            return balance;

        }catch (SQLException e){
            System.out.println("SQL CHECK WALLET ERROR: " + e);
            return -1;
        }
    }

    public List<String> viewPortfolio(String userID){
        List<String> positions = new ArrayList<>();

        try (Connection conn = connect()){

            String selectQuery = "select symbol, quantity, totalSpent from portfolio where userID=?";
            PreparedStatement ps = conn.prepareStatement(selectQuery);
            ps.setString(1, userID);

            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                positions.add(rs.getString(1));
                positions.add(rs.getString(2));
                positions.add(String.valueOf(rs.getDouble("totalSpent")));
            }
            ps.close();

            return positions;

        }catch (SQLException e){
            System.out.println("VIEW PORTFOLIO SQL ERROR: " + e);
            return null;
        }


    }

}
