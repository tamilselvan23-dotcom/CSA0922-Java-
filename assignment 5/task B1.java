import java.io.*;
import java.nio.file.*;
import java.util.*;
public class PassStore {
    private final Path file;
    public PassStore(Path file) {
        this.file = file;
    }
    public void add(String passId, String regNo,
                    String name, int route, double amount)
            throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(
                file, StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            w.write(String.join(",",
                passId, regNo, name,
                String.valueOf(route),
                String.valueOf(amount)));
            w.newLine();
        }
    }
    public String[] find(String passId) throws IOException {
        try (BufferedReader r = Files.newBufferedReader(file)) {
            String line;
            int lineNo = 0;
            while ((line = r.readLine()) != null) {
                lineNo++;
                String[] fields = line.split(",", -1);
                if (fields.length != 5) {
                    System.out.println(
                        "Skipped damaged row " + lineNo + ": " + line);
                    continue;
                }
                try {
                    if (fields[0].isBlank() ||
                        fields[1].isBlank() ||
                        fields[2].isBlank() ||
                        Integer.parseInt(fields[3]) <= 0 ||
                        !Double.isFinite(
                            Double.parseDouble(fields[4])) ||
                        Double.parseDouble(fields[4]) < 0) {

                        throw new IllegalArgumentException();
                    }

                } catch (IllegalArgumentException e) {
                    System.out.println(
                        "Skipped invalid row " + lineNo + ": " + line);
                    continue;
                }

                if (fields[0].equals(passId))
                    return fields;
            }
        }

        return null;
    }

    public static void main(String[] args) throws Exception {
        Path f = Files.createTempFile("passes", ".csv");

        try {
            PassStore store = new PassStore(f);

            store.add("P1001", "192311001", "Arun", 12, 4500);

            Files.writeString(f,
                "P1002,DAMAGED LINE\n",
                StandardOpenOption.APPEND);

            store.add("P1003", "192311003", "Divya", 7, 3800);

            System.out.println("Find P1003 = " +
                Arrays.toString(store.find("P1003")));

            System.out.println("Find P1002 = " +
                Arrays.toString(store.find("P1002")));

            System.out.println("Find P9999 = " +
                Arrays.toString(store.find("P9999")));

        } finally {
            Files.deleteIfExists(f);
        }
    }
}
