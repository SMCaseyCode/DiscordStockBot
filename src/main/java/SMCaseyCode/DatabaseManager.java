package SMCaseyCode;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.historical.trade.Trade;

import java.sql.*;
import java.util.Objects;

import static SMCaseyCode.ProtectedData.URL;

public class DatabaseManager {

    AlpacaManager api = new AlpacaManager();

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

            while (rs.next()){
                if (Objects.equals(rs.getString("userID"), userID)){
                    break;
                }else {
                    String insertQuery = "insert into users(userID, userWallet) values (?,?)";
                    ps = conn.prepareStatement(insertQuery);

                    ps.setString(1, userID);
                    ps.setDouble(2, 10000.00);

                    ps.executeUpdate();
                }
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
                        String updateQuery = "update portfolio set quantity =? where userID =? and symbol=?";
                        ps = conn.prepareStatement(updateQuery);
                        ps.setInt(1, (owned + qty));
                        ps.setString(2, userID);
                        ps.setString(3, symbol);
                        ps.executeUpdate();
                        ps.close();
                    }else {
                        String insertQuery = "insert into portfolio(userID, symbol, quantity) values (?,?,?)";
                        ps = conn.prepareStatement(insertQuery);
                        ps.setString(1, userID);
                        ps.setString(2, symbol);
                        ps.setInt(3, qty);
                        ps.executeUpdate();
                        ps.close();
                    }

                    String updateQuery = "update users set userWallet = (userWallet - ?)";
                    ps = conn.prepareStatement(updateQuery);

                    ps.setDouble(1, totalStockPrice);
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

}
