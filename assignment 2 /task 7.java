import java.util.*;
public class PlaylistRedesign {
    static volatile int checksum;
    static long testArrayList(int n) {
        List<Integer> list = new ArrayList<>();
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            list.add(0, i);
        }
        long elapsed = System.nanoTime() - start;
        checksum = list.size();
        return elapsed;
    }
    static long testArrayDeque(int n) {
        Deque<Integer> deque = new ArrayDeque<>();
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            deque.addFirst(i);
        }
        long elapsed = System.nanoTime() - start;
        checksum = deque.size();

        return elapsed;
    }

    static void benchmark(int n) {

        int repeats = 5;

        long arrayTotal = 0;
        long dequeTotal = 0;

        // Warm-up
        testArrayList(Math.min(n, 1000));
        testArrayDeque(Math.min(n, 1000));

        for (int i = 0; i < repeats; i++) {

            // Alternate test order for fairness
            if (i % 2 == 0) {
                arrayTotal += testArrayList(n);
                dequeTotal += testArrayDeque(n);
            } else {
                dequeTotal += testArrayDeque(n);
                arrayTotal += testArrayList(n);
            }
        }

        double arrayAverage =
                arrayTotal / (double) repeats / 1e6;

        double dequeAverage =
                dequeTotal / (double) repeats / 1e6;

        double improvement =
                arrayAverage / dequeAverage;

        System.out.println("\nNumber of songs: " + n);

        System.out.printf(
                "ArrayList front insert: %.3f ms%n",
                arrayAverage);

        System.out.printf(
                "ArrayDeque addFirst:    %.3f ms%n",
                dequeAverage);

        System.out.printf(
                "Improvement ratio: %.2fx%n",
                improvement);
    }

    static class PlaylistController {

        private final Deque<String> upNext =
                new ArrayDeque<>();

        void addNext(String song) {
            upNext.addFirst(song);
        }

        void addLast(String song) {
            upNext.addLast(song);
        }

        String playNext() {
            return upNext.pollFirst();
        }

        void display() {
            System.out.println("Up Next: " + upNext);
        }
    }

    public static void main(String[] args) {

        System.out.println(
                "Playlist Performance Redesign");

        benchmark(1000);
        benchmark(10000);
        benchmark(50000);

        System.out.println("\nPlaylist Controller Demo");

        PlaylistController playlist =
                new PlaylistController();

        playlist.addLast("Song A");
        playlist.addLast("Song B");
        playlist.addNext("Priority Song");

        playlist.display();

        System.out.println(
                "Now playing: " + playlist.playNext());

        playlist.display();
    }
}
