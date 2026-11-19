import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
public class Q7_ResultProcessor {
    static class Result {
        final String file;
        final boolean success;
        final int score;
        final String message;
        Result(String file, boolean success,
               int score, String message) {
            this.file = file;
            this.success = success;
            this.score = score;
            this.message = message;
        }
    }
    static class ProcessFile implements Callable<Result> {
        private final Path file;
        ProcessFile(Path file) {
            this.file = file;
        }
        @Override
        public Result call() {
            try {
                String content =
                        Files.readString(file).trim();

                int score = Integer.parseInt(content);

                if (score < 0 || score > 100) {
                    throw new IllegalArgumentException(
                            "Score out of range");
                }

                return new Result(
                        file.getFileName().toString(),
                        true, score, "Processed");

            } catch (Exception e) {
                return new Result(
                        file.getFileName().toString(),
                        false, 0,
                        e.getClass().getSimpleName() +
                        ": " + e.getMessage());
            }
        }
    }

    static void createSampleFiles() throws IOException {

        Path dir = Paths.get("scores");
        Files.createDirectories(dir);

        Files.writeString(
                dir.resolve("student_1.txt"), "85");

        Files.writeString(
                dir.resolve("student_2.txt"), "92");

        Files.writeString(
                dir.resolve("student_3.txt"), "corrupt");

        Files.writeString(
                dir.resolve("student_4.txt"), "76");

        // student_missing.txt is deliberately not created.
    }

    static List<Path> getSampleFiles() {

        Path dir = Paths.get("scores");

        return Arrays.asList(
                dir.resolve("student_1.txt"),
                dir.resolve("student_2.txt"),
                dir.resolve("student_3.txt"),
                dir.resolve("student_missing.txt"),
                dir.resolve("student_4.txt")
        );
    }

    static void process(List<Path> files, int poolSize)
            throws InterruptedException,
                   ExecutionException {

        ExecutorService pool =
                Executors.newFixedThreadPool(poolSize);

        ConcurrentLinkedQueue<Result> results =
                new ConcurrentLinkedQueue<>();

        List<Future<Result>> futures =
                new ArrayList<>();

        long start = System.nanoTime();

        try {
            for (Path file : files) {
                futures.add(
                        pool.submit(new ProcessFile(file)));
            }

            for (Future<Result> future : futures) {
                results.add(future.get());
            }

        } finally {
            pool.shutdown();
        }

        long elapsed = System.nanoTime() - start;

        int success = 0;
        int failure = 0;
        long totalScore = 0;

        System.out.println(
                "\n===== Pool Size: " + poolSize + " =====");

        for (Result r : results) {

            if (r.success) {
                success++;
                totalScore += r.score;

                if (files.size() <= 20)
                    System.out.println(
                            "SUCCESS " + r.file +
                            " score=" + r.score);

            } else {
                failure++;

                System.out.println(
                        "FAILED " + r.file +
                        " reason=" + r.message);
            }
        }

        System.out.println(
                "Success: " + success);

        System.out.println(
                "Failure: " + failure);

        System.out.println(
                "Total Score: " + totalScore);

        System.out.println(
                "Submitted: " + files.size());

        System.out.println(
                "Validation: " +
                (success + failure == files.size()
                        ? "PASSED" : "FAILED"));

        System.out.printf(
                "Elapsed time: %.3f ms%n",
                elapsed / 1e6);
    }

    public static void main(String[] args)
            throws Exception {

        createSampleFiles();

        List<Path> files = getSampleFiles();

        int[] poolSizes = {1, 2, 4, 8};

        for (int size : poolSizes) {
            process(files, size);
        }
    }
}
