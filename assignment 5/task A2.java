import java.io.*;
import java.nio.file.*;
public class BufferedCounter {
    static long countUnbuffered(Path file) throws IOException {
        long count = 0;
        int last = -1;
        boolean hasData = false;
        try (FileInputStream in =
                new FileInputStream(file.toFile())) {
            int b;
            while ((b = in.read()) != -1) {
                hasData = true;
                last = b;
                if (b == '\n') count++;
            }
        }
        if (hasData && last != '\n') count++;
        return count;
    }
    static long countBuffered(Path file) throws IOException {
        long count = 0;
        int last = -1;
        boolean hasData = false;
        try (BufferedInputStream in =
                new BufferedInputStream(
                    Files.newInputStream(file), 64 * 1024)) {
            byte[] buffer = new byte[64 * 1024];
            int n;
            while ((n = in.read(buffer)) != -1) {
                for (int i = 0; i < n; i++) {
                    hasData = true;
                    last = buffer[i] & 0xff;
                    if (buffer[i] == '\n') count++;
                }
            }
        }

        if (hasData && last != '\n') count++;
        return count;
    }

    static void generate(Path file, int lines)
            throws IOException {
        try (BufferedWriter writer =
                Files.newBufferedWriter(file)) {
            for (int i = 0; i < lines; i++) {
                writer.write("STUDENT" + i + ",PRESENT");
                writer.newLine();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Path file = Files.createTempFile(
            "attendance", ".txt");

        try {
            generate(file, 100000);

            long start = System.nanoTime();
            long count1 = countUnbuffered(file);
            long end = System.nanoTime();

            double unbufferedMs = (end - start) / 1_000_000.0;

            start = System.nanoTime();
            long count2 = countBuffered(file);
            end = System.nanoTime();

            double bufferedMs = (end - start) / 1_000_000.0;

            System.out.println("File size: " +
                Files.size(file) + " bytes");
            System.out.println("Unbuffered lines: " + count1);
            System.out.println("Buffered lines: " + count2);
            System.out.printf("Unbuffered: %.3f ms%n", unbufferedMs);
            System.out.printf("Buffered: %.3f ms%n", bufferedMs);

        } finally {
            Files.deleteIfExists(file);
        }
    }
}
