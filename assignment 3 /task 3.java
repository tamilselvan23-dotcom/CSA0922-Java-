import java.io.*;
public class Q3_ResourceDemo {
    static void writeReport(String[] files, String output)
            throws IOException {
        try (BufferedWriter writer =
                new BufferedWriter(new FileWriter(output))) {
            for (String file : files) {
                try (BufferedReader reader =
                        new BufferedReader(
                                new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
        try {
            try (PrintWriter a =
                    new PrintWriter("store1.txt")) {
                a.println("Store 1: Rs. 1200");
            }

            try (PrintWriter b =
                    new PrintWriter("store2.txt")) {
                b.println("Store 2: Rs. 1850");
            }

            String[] files = {
                "store1.txt",
                "store2.txt"
            };

            writeReport(files, "daily-report.txt");

            try (BufferedReader reader =
                    new BufferedReader(
                            new FileReader("daily-report.txt"))) {

                System.out.println("Daily sales report:");

                String line;

                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            System.out.println(
                    "All file resources closed automatically.");

        } catch (IOException e) {
            System.out.println(
                    "File error: " + e.getMessage());
        }
    }
}
