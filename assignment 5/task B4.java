
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.math.BigDecimal;
import java.util.*;

public class PassMigration {

    static String validate(String[] f) {
        if (f.length != 5)
            return "Incorrect number of fields";

        if (f[0].isBlank() || f[1].isBlank() || f[2].isBlank())
            return "Required field missing";

        try {
            if (Integer.parseInt(f[3]) <= 0)
                return "Invalid route number";

            BigDecimal amount = new BigDecimal(f[4]);
            if (amount.signum() < 0)
                return "Negative amount";

        } catch (NumberFormatException e) {
            return "Invalid numeric value";
        }

        return null;
    }

    static void migrate(Path input, Path rejected,
                        Connection con) throws Exception {

        String sql =
            "INSERT INTO bus_pass " +
            "(pass_id,reg_no,name,route_no,amount) " +
            "VALUES (?,?,?,?,?)";

        int valid = 0;
        int invalid = 0;
        int pending = 0;

        boolean oldAutoCommit = con.getAutoCommit();
        con.setAutoCommit(false);

        try (BufferedReader reader = Files.newBufferedReader(input);
             BufferedWriter errors = Files.newBufferedWriter(rejected);
             PreparedStatement ps = con.prepareStatement(sql)) {

            String line;
            int lineNo = 0;

            while ((line = reader.readLine()) != null) {
                lineNo++;

                String[] fields = line.split(",", -1);
                String reason = validate(fields);

                if (reason != null) {
                    errors.write("Line " + lineNo + ": " +
                        reason + " | " + line);
                    errors.newLine();
                    invalid++;
                    continue;
                }

                ps.setString(1, fields[0]);
                ps.setString(2, fields[1]);
                ps.setString(3, fields[2]);
                ps.setInt(4, Integer.parseInt(fields[3]));
                ps.setBigDecimal(5, new BigDecimal(fields[4]));

                ps.addBatch();
                valid++;
                pending++;

                if (pending == 1000) {
                    ps.executeBatch();
                    pending = 0;
                }
            }

            if (pending > 0)
                ps.executeBatch();

            con.commit();

            System.out.println("Migration successful");
            System.out.println("Valid records: " + valid);
            System.out.println("Rejected records: " + invalid);
            System.out.println("Failure report: " + rejected);

        } catch (Exception e) {
            con.rollback();
            System.out.println("Migration failed and rolled back");
            throw e;

        } finally {
            con.setAutoCommit(oldAutoCommit);
        }
    }

    static void showMetadata(Connection con)
            throws SQLException {

        DatabaseMetaData md = con.getMetaData();

        System.out.println("\nDatabase: " +
            md.getDatabaseProductName());

        System.out.println("Database version: " +
            md.getDatabaseProductVersion());

        System.out.println("Driver: " +
            md.getDriverName());

        System.out.println("Driver version: " +
            md.getDriverVersion());

        try (ResultSet tables = md.getTables(
                null, null, "%", new String[]{"TABLE"})) {

            while (tables.next()) {
                System.out.println("Table: " +
                    tables.getString("TABLE_NAME"));
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Path input = Path.of("passes.csv");
        Path rejected = Path.of("rejected_records.txt");

        if (Files.notExists(input)) {
            Files.write(input, Arrays.asList(
                "P1001,192311001,Arun,12,4500",
                "P1002,DAMAGED",
                "P1003,192311003,Divya,7,3800",
                "P1004,192311004,Ravi,abc,3500",
                "P1005,192311005,Meena,4,4200"
            ));
        }

        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        try (Connection con =
                DriverManager.getConnection(url, user, password)) {

            migrate(input, rejected, con);
            showMetadata(con);
        }
    }
}
