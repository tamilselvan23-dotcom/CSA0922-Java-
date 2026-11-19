import java.sql.*;
public class SecurityDemo {
    static boolean unsafeLogin(Connection con,
                               String id, String pwd)
            throws SQLException {
        String sql = "SELECT * FROM users WHERE id='" +
                     id + "' AND pwd='" + pwd + "'";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next();
        }
    }
    static boolean safeLogin(Connection con,
                             String id, String pwd)
            throws SQLException {
        String sql =
            "SELECT user_id FROM users WHERE id=? AND pwd=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, pwd);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
    static void validatePass(String line) {
        if (line.isBlank()) {
            System.out.println("Rejected: Empty line");
            return;
        }
        String[] fields = line.split(",", -1);
        if (fields.length != 5) {
            System.out.println("Rejected: Wrong field count");
            return;
        }

        try {
            if (fields[0].isBlank() ||
                fields[1].isBlank() ||
                fields[2].isBlank() ||
                Integer.parseInt(fields[3]) <= 0 ||
                Double.parseDouble(fields[4]) < 0) {
                throw new IllegalArgumentException();
            }

            System.out.println("Valid: " + line);

        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: Invalid numeric or text field");
        }
    }

    public static void main(String[] args) throws Exception {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        try (Connection con =
                DriverManager.getConnection(url, user, password)) {

            String craftedId = "admin' -- ";
            String wrongPassword = "wrong";

            System.out.println("Unsafe login: " +
                unsafeLogin(con, craftedId, wrongPassword));

            System.out.println("Safe login: " +
                safeLogin(con, craftedId, wrongPassword));

            System.out.println("Normal login: " +
                safeLogin(con, "admin", "test123"));
        }

        System.out.println("\nBus Pass Validation:");

        validatePass("P2,192311002,Name");
        validatePass("P3,192311003,Name,abc,3900");
        validatePass("");
        validatePass("P4,192311004,Name,4,notMoney");
        validatePass("P5,192311005,Arun,12,4500");
    }
}
