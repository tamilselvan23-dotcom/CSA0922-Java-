import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
public class BusPassManager {
    static final Path FILE = Path.of("buspasses.csv");
    static final String HEADER =
        "pass_id,reg_no,name,route_no,boarding_stop,valid_until,amount_paid";
    static String csv(String s) {
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
    static List<String> parseCSV(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (quoted && i + 1 < line.length()
                        && line.charAt(i + 1) == '"') {
                    value.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (c == ',' && !quoted) {
                fields.add(value.toString());
                value.setLength(0);
            } else {
                value.append(c);
            }
        }
        if (quoted)
            throw new IllegalArgumentException("Unclosed quote");

        fields.add(value.toString());
        return fields;
    }

    static void initialize() throws IOException {
        if (Files.notExists(FILE)) {
            Files.writeString(FILE, HEADER + "\n",
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE_NEW);
        }
    }

    static void validate(List<String> f) {
        if (f.size() != 7)
            throw new IllegalArgumentException("Expected 7 fields");

        for (int i = 0; i < 7; i++) {
            if (f.get(i).isBlank())
                throw new IllegalArgumentException("Empty field");
        }

        if (Integer.parseInt(f.get(3)) <= 0)
            throw new IllegalArgumentException("Invalid route");

        LocalDate.parse(f.get(5));

        double amount = Double.parseDouble(f.get(6));
        if (!Double.isFinite(amount) || amount < 0)
            throw new IllegalArgumentException("Invalid amount");
    }

    static List<String> find(String passId) throws IOException {
        initialize();

        try (BufferedReader reader =
                Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            String line;
            int lineNo = 0;

            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (lineNo == 1 || line.isBlank()) continue;

                try {
                    List<String> fields = parseCSV(line);
                    validate(fields);

                    if (fields.get(0).equals(passId))
                        return fields;

                } catch (RuntimeException e) {
                    System.err.println(
                        "Skipping damaged line " + lineNo);
                }
            }
        }
        return null;
    }

    static void add(Scanner sc) throws IOException {
        System.out.print("Pass ID: ");
        String id = sc.nextLine().trim();

        if (id.isEmpty() || find(id) != null) {
            System.out.println("Invalid or duplicate Pass ID");
            return;
        }

        System.out.print("Register Number: ");
        String reg = sc.nextLine().trim();

        System.out.print("Student Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Route Number: ");
        String route = sc.nextLine().trim();

        System.out.print("Boarding Stop: ");
        String stop = sc.nextLine().trim();

        System.out.print("Valid Until (yyyy-MM-dd): ");
        String date = sc.nextLine().trim();

        System.out.print("Amount Paid: ");
        String amount = sc.nextLine().trim();

        List<String> fields = Arrays.asList(
            id, reg, name, route, stop, date, amount);

        try {
            validate(fields);
        } catch (RuntimeException e) {
            System.out.println("Invalid input: " + e.getMessage());
            return;
        }

        List<String> escaped = new ArrayList<>();
        for (String f : fields)
            escaped.add(csv(f));

        try (BufferedWriter writer =
                Files.newBufferedWriter(FILE,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND)) {
            writer.write(String.join(",", escaped));
            writer.newLine();
        }

        System.out.println("Bus pass added successfully");
    }

    static void listRoute(int route) throws IOException {
        initialize();

        try (BufferedReader reader =
                Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            String line;
            boolean found = false;
            int lineNo = 0;

            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (lineNo == 1 || line.isBlank()) continue;

                try {
                    List<String> f = parseCSV(line);
                    validate(f);

                    if (Integer.parseInt(f.get(3)) == route) {
                        System.out.println(f);
                        found = true;
                    }
                } catch (RuntimeException e) {
                    System.err.println("Skipping line " + lineNo);
                }
            }

            if (!found)
                System.out.println("No passes found on route " + route);
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        initialize();

        while (true) {
            System.out.println("\n1. Add Pass");
            System.out.println("2. Find Pass");
            System.out.println("3. List by Route");
            System.out.println("4. Exit");
            System.out.print("Choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    add(sc);
                    break;
                case "2":
                    System.out.print("Enter Pass ID: ");
                    List<String> pass = find(sc.nextLine());
                    System.out.println(
                        pass == null ? "Pass not found" : pass);
                    break;
                case "3":
                    System.out.print("Enter Route: ");
                    try {
                        listRoute(Integer.parseInt(sc.nextLine()));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid route number");
                    }
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}
