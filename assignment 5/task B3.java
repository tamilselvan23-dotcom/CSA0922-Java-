
import java.sql.*;
import java.util.*;

public class PassLoader {

    static List<String[]> generate(int count) {
        List<String[]> passes = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            passes.add(new String[]{
                "P" + i,
                "REG" + i,
                "Student" + i,
                String.valueOf(i % 20 + 1),
                "4500.00"
            });
        }

        return passes;
    }

    static void clear(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.executeUpdate("DELETE FROM bus_pass");
        }
    }

    static int usingStatement(Connection con,
                              List<String[]> passes)
            throws SQLException {

        int count = 0;

        try (Statement st = con.createStatement()) {
            for (String[] p : passes) {
                // Generated, trusted data only.
                String sql =
                    "INSERT INTO bus_pass VALUES ('" +
                    p[0] + "','" + p[1] + "','" + p[2] +
                    "'," + p[3] + "," + p[4] + ")";

                count += st.executeUpdate(sql);
            }
        }

        return count;
    }

    static int usingPrepared(Connection con,
                             List<String[]> passes)
            throws SQLException {

        String sql =
            "INSERT INTO bus_pass VALUES (?,?,?,?,?)";
        int count = 0;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (String[] p : passes) {
                bind(ps, p);
                count += ps.executeUpdate();
            }
        }

        return count;
    }

    static void bind(PreparedStatement ps, String[] p)
            throws SQLException {

        ps.setString(1, p[0]);
        ps.setString(2, p[1]);
        ps.setString(3, p[2]);
        ps.setInt(4, Integer.parseInt(p[3]));
        ps.setBigDecimal(5,
            new java.math.BigDecimal(p[4]));
    }

    public static int loadAll(Connection con,
                               List<String[]> passes)
            throws SQLException {

        String sql =
            "INSERT INTO bus_pass VALUES (?,?,?,?,?)";

        boolean oldAutoCommit = con.getAutoCommit();
        con.setAutoCommit(false);

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int count = 0;

            for (String[] p : passes) {
                bind(ps, p);
                ps.addBatch();
                count++;

                if (count % 1000 == 0)
                    ps.executeBatch();
            }

            ps.executeBatch();
            con.commit();
            return count;

        } catch (SQLException | RuntimeException e) {
            con.rollback();
            throw e;

        } finally {
            con.setAutoCommit(oldAutoCommit);
        }
    }

    static void metadata(Connection con) throws SQLException {
        try (PreparedStatement ps =
                con.prepareStatement("SELECT * FROM bus_pass");
             ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData md = rs.getMetaData();

            for (int i = 1; i <= md.getColumnCount(); i++) {
                System.out.println(
                    md.getColumnName(i) + " : " +
                    md.getColumnTypeName(i));
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        int[] sizes = {10, 1000, 10000, 100000};

        try (Connection con =
                DriverManager.getConnection(url, user, password)) {

            metadata(con);

            for (int size : sizes) {
                List<String[]> passes = generate(size);
                System.out.println("\nRecords: " + size);

                clear(con);
                long start = System.nanoTime();
                usingStatement(con, passes);
                System.out.printf("Statement: %.2f ms%n",
                    (System.nanoTime() - start) / 1e6);

                clear(con);
                start = System.nanoTime();
                usingPrepared(con, passes);
                System.out.printf("PreparedStatement: %.2f ms%n",
                    (System.nanoTime() - start) / 1e6);

                clear(con);
                start = System.nanoTime();
                loadAll(con, passes);
                System.out.printf("Batch: %.2f ms%n",
                    (System.nanoTime() - start) / 1e6);

                try (Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery(
                         "SELECT COUNT(*) FROM bus_pass")) {
                    rs.next();
                    System.out.println("Rows inserted: " +
                        rs.getInt(1));
                }
            }
        }
    }
}
