import java.util.*;
public class Triage {
    static class Patient {
        final String name;
        final int severity;
        final long arrival;
        Patient(String name, int severity, long arrival) {
            this.name = name;
            this.severity = severity;
            this.arrival = arrival;
        }
        @Override
        public String toString() {
            return name + "(S" + severity + ")";
        }
    }
    static class TriageSystem {
        private long counter = 0;
        private final PriorityQueue<Patient> queue =
                new PriorityQueue<>(
                        Comparator.comparingInt(
                                (Patient p) -> p.severity)
                                .thenComparingLong(
                                        p -> p.arrival)
                );
        void admit(String name, int severity) {
            if (severity < 1 || severity > 5) {
                throw new IllegalArgumentException(
                        "Severity must be 1 to 5");
            }
            queue.add(new Patient(
                    name, severity, counter++));
        }

        Patient next() {
            return queue.poll();
        }

        boolean isEmpty() {
            return queue.isEmpty();
        }

        int waitingCount() {
            return queue.size();
        }
    }

    public static void main(String[] args) {

        TriageSystem hospital = new TriageSystem();

        hospital.admit("John", 3);
        hospital.admit("Meena", 1);
        hospital.admit("Asha", 5);
        hospital.admit("Kumar", 1);
        hospital.admit("Ravi", 3);
        hospital.admit("Lata", 2);

        System.out.println("Hospital Triage System");
        System.out.println("----------------------");

        System.out.println("Patients waiting: "
                + hospital.waitingCount());

        System.out.println("\nTreatment order:");

        int count = 1;

        while (!hospital.isEmpty()) {
            Patient patient = hospital.next();

            System.out.println(
                    count + ". " + patient);
            count++;
        }
    }
}
