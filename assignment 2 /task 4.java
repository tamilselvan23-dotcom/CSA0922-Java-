import java.util.*;
public class PharmacyBenchmark {
    static volatile long checksum;
    static Map<String, Integer> createHashMap(int n) {
        Map<String, Integer> map = new HashMap<>();
        fill(map, n);
        return map;
    }
    static Map<String, Integer> createTreeMap(int n) {
        Map<String, Integer> map = new TreeMap<>();
        fill(map, n);
        return map;
    }
    static Map<String, Integer> createLinkedMap(int n) {
        Map<String, Integer> map = new LinkedHashMap<>();
        fill(map, n);
        return map;
    }
    static void fill(Map<String, Integer> map, int n) {
        for (int i = n - 1; i >= 0; i--) {
            map.put("MED" + i, (i % 100) + 1);
        }
    }
    static double lookupTime(Map<String, Integer> map,
                             int n) {
        long start = System.nanoTime();
        long total = 0;

        for (int i = 0; i < 100000; i++) {
            String key = "MED" + (i % n);
            Integer stock = map.get(key);

            if (stock != null)
                total += stock;
        }

        checksum = total;
        return (System.nanoTime() - start) / 1e6;
    }

    static double iterationTime(Map<String, Integer> map) {
        long start = System.nanoTime();
        long total = 0;

        for (Map.Entry<String, Integer> entry
                : map.entrySet()) {
            total += entry.getValue();
        }

        checksum = total;
        return (System.nanoTime() - start) / 1e6;
    }

    static void report(String name,
                       Map<String, Integer> map,
                       int n) {
        double lookup = 0;
        double iteration = 0;

        for (int i = 0; i < 5; i++) {
            lookup += lookupTime(map, n);
            iteration += iterationTime(map);
        }

        System.out.printf(
                "%-15s Lookup: %.3f ms Iteration: %.3f ms%n",
                name, lookup / 5, iteration / 5);
    }

    static void test(int n) {
        System.out.println("\nInventory size: " + n);

        report("HashMap", createHashMap(n), n);
        report("TreeMap", createTreeMap(n), n);
        report("LinkedHashMap", createLinkedMap(n), n);
    }

    public static void main(String[] args) {
        test(100);
        test(10000);
        test(50000);

        Map<String, Integer> inventory =
                new LinkedHashMap<>();

        inventory.put("MED003", 50);
        inventory.put("MED001", 20);
        inventory.put("MED002", 35);

        System.out.println("\nInsertion-order report:");
        System.out.println(inventory);

        Map<String, Integer> sorted =
                new TreeMap<>(inventory);

        System.out.println("\nCode-sorted report:");
        System.out.println(sorted);

        List<String> keys =
                new ArrayList<>(inventory.keySet());

        Collections.sort(keys);

        System.out.println("\nSorted HashMap alternative:");
        for (String key : keys)
            System.out.println(key + " = "
                    + inventory.get(key));
    }
}
