import java.sql.*;
import java.math.BigDecimal;
public class WalletTransfer {
    static void transfer(Connection con, int from, int to,
                         BigDecimal amount) throws SQLException {
        if (from == to || amount.signum() <= 0)
            throw new IllegalArgumentException("Invalid transfer");
        boolean oldAutoCommit = con.getAutoCommit();
        con.setAutoCommit(false);
        try {
            String debit =
                "UPDATE wallet SET balance=balance-? " +
                "WHERE wallet_id=? AND balance>=?";
            String credit =
                "UPDATE wallet SET balance=balance+? " +
                "WHERE wallet_id=?";
            try (PreparedStatement ps = con.prepareStatement(debit)) {
                ps.setBigDecimal(1, amount);
                ps.setInt(2, from);
                ps.setBigDecimal(3, amount);
                if (ps.executeUpdate() != 1)
                    throw new SQLException(
                        "Insufficient balance or invalid source");
            }
            try (PreparedStatement ps = con.prepareStatement(credit)) {
                ps.setBigDecimal(1, amount);
                ps.setInt(2, to);
                if (ps.executeUpdate() != 1)
                    throw new SQLException(
                        "Destination wallet not found");
            }
            con.commit();
            System.out.println("Transfer successful: Rs." + amount);
        } catch (SQLException | RuntimeException e) {
            con.rollback();
            System.out.println("Transaction rolled back");
            throw e;

        } finally {
            con.setAutoCommit(oldAutoCommit);
        }
    }

    public static void main(String[] args) throws Exception {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        try (Connection con =
                DriverManager.getConnection(url, user, password)) {

            transfer(con, 1, 2, new BigDecimal("500.00"));

            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM wallet ORDER BY wallet_id");
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    System.out.println(
                        rs.getString("wallet_name") + ": Rs." +
                        rs.getBigDecimal("balance"));
                }
            }
        }
    }
}
