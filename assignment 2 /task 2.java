import java.util.*;
public class PlaylistBenchmark {
    static volatile long sink;
    static void test(int n) {
        List<Integer> array = new ArrayList<>();
        List<Integer> linked = new LinkedList<>();
        long start = System.nanoTime();
        for (int i = 0; i < n; i++)
            array.add(i);
        long arrayAppend = System.nanoTime() - start;
        start = System.nanoTime();
        for (int i = 0; i < n; i++)
            linked.add(i);
        long linkedAppend = System.nanoTime() - start;
        array.clear();
        linked.clear();
        start = System.nanoTime();
        for (int i = 0; i < n; i++)
            array.add(0, i);
        long arrayFront = System.nanoTime() - start;
        start = System.nanoTime();
        for (int i = 0; i < n; i++)
            linked.add(0, i);
        long linkedFront = System.nanoTime() - start;
        long sum = 0;
        start = System.nanoTime();
        for (int i = 0; i < n; i++)
            sum += array.get(i);
        long arrayRead = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < n; i++)
            sum += linked.get(i);
        long linkedRead = System.nanoTime() - start;
        start = System.nanoTime();
        for (int x : array)
            sum += x;
        long arrayIteration = System.nanoTime() - start;
        start = System.nanoTime();
        for (int x : linked)
            sum += x;
        long linkedIteration = System.nanoTime() - start;
        sink = sum;
        System.out.println("\nSize: " + n);
        System.out.println("Operation          ArrayList(ms) LinkedList(ms)");
        print("Append", arrayAppend, linkedAppend);
        print("Front insertion", arrayFront, linkedFront);
        print("Indexed read", arrayRead, linkedRead);
        print("Iteration", arrayIteration, linkedIteration);
    }

    static void print(String operation, long a, long b) {
        System.out.printf("%-18s %12.3f %14.3f%n",
                operation, a / 1e6, b / 1e6);
    }

    public static void main(String[] args) {
        test(1000);
        test(10000);
        test(50000);

        Deque<String> upNext = new ArrayDeque<>();

        upNext.addLast("Song A");
        upNext.addLast("Song B");
        upNext.addFirst("Priority Song");

        System.out.println("\nUp Next: " + upNext);
        System.out.println("Playing: " + upNext.pollFirst());
        System.out.println("Remaining: " + upNext);
    }
}
