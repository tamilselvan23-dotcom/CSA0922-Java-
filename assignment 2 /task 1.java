public class TicketBenchmark {
    static volatile long checksum;
    static String usingString(int fields) {
        String ticket = "";
        for (int i = 0; i < fields; i++) {
            ticket = ticket + "Field" + i + ":Value" + i + ";";
        }
        return ticket;
    }
    static String usingBuilder(int fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields; i++) {
            sb.append("Field").append(i)
              .append(":Value").append(i).append(";");
        }
        return sb.toString();
    }
    static String usingBuffer(int fields) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < fields; i++) {
            sb.append("Field").append(i)
              .append(":Value").append(i).append(";");
        }
        return sb.toString();
    }
    static double measure(int method, int tickets,
                          int fields) {
        long total = 0;
        for (int r = 0; r < 5; r++) {
            long start = System.nanoTime();
            long length = 0;

            for (int i = 0; i < tickets; i++) {
                String result;

                if (method == 1)
                    result = usingString(fields);
                else if (method == 2)
                    result = usingBuilder(fields);
                else
                    result = usingBuffer(fields);

                length += result.length();
            }

            checksum = length;
            total += System.nanoTime() - start;
        }

        return total / 5.0 / 1_000_000;
    }

    static void test(String name, int tickets,
                     int fields) {
        System.out.println("\n" + name);
        System.out.println("Tickets: " + tickets
                           + ", Fields: " + fields);

        for (int method = 1; method <= 3; method++)
            measure(method, Math.min(tickets, 100),
                    fields);

        System.out.printf("String       : %.3f ms%n",
                measure(1, tickets, fields));
        System.out.printf("StringBuilder: %.3f ms%n",
                measure(2, tickets, fields));
        System.out.printf("StringBuffer : %.3f ms%n",
                measure(3, tickets, fields));
    }

    public static void main(String[] args) {
        test("Workload A", 1, 6);
        test("Workload B", 10000, 50);
        test("Workload C", 1000, 100);
    }
}
