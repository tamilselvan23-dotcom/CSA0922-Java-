public class Q4_ThreadDemo {
    static class Station extends Thread {
        private final int scripts;
        Station(String name, int scripts) {
            super(name);
            this.scripts = scripts;
        }
        @Override
        public void run() {
            try {
                int stationNumber =
                        getName().charAt(getName().length() - 1) - '0';
                Thread.sleep(150 + stationNumber * 80);
                System.out.println(
                        getName() + " scanned " +
                        scripts + " scripts");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(
                        getName() + " interrupted");
            }
        }
    }
    public static void main(String[] args)
            throws InterruptedException {
        Station[] stations = {
            new Station("Station-1", 25),
            new Station("Station-2", 30),
            new Station("Station-3", 20),
            new Station("Station-4", 25)
        };

        System.out.println("Before starting:");

        for (Station s : stations) {
            System.out.println(
                    s.getName() + ": " + s.getState());
        }

        for (Station s : stations)
            s.start();

        System.out.println("\nAfter starting:");

        for (Station s : stations) {
            System.out.println(
                    s.getName() + " alive: " + s.isAlive());
        }

        for (Station s : stations)
            s.join();

        int total = 0;

        for (Station s : stations) {
            total += s.scripts;
        }

        System.out.println(
                "\nAll stations completed.");
        System.out.println(
                "Total scanned: " + total);

        for (Station s : stations) {
            System.out.println(
                    s.getName() + ": " + s.getState());
        }
    }
}
