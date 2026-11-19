import java.util.*;
abstract class Vehicle {
    private final String regNo;
    protected Vehicle(String r) {
        if (r == null || r.isBlank())
            throw new IllegalArgumentException("Invalid registration");
        regNo = r.trim().toUpperCase();
    }
    public String getRegNo() {
        return regNo;
    }
    protected abstract double ratePerHour();
    public double calculateFee(long minutes) {
        if (minutes < 0)
            throw new IllegalArgumentException("Negative duration");
        long hours = minutes == 0 ? 0 : (minutes + 59) / 60;
        return hours * ratePerHour();
    }
}
class TwoWheeler extends Vehicle {
    TwoWheeler(String r) {
        super(r);
    }
    @Override
    protected double ratePerHour() {
        return 10;
    }
}
class Car extends Vehicle {
    Car(String r) {
        super(r);
    }
    @Override
    protected double ratePerHour() {
        return 30;
    }
}

class ParkingTicket {

    private static long nextId = 1;

    private final long id;
    private final Vehicle vehicle;
    private final int floor;
    private final int slot;
    private final long entryMinute;

    ParkingTicket(Vehicle v, int f, int s, long time) {
        id = nextId++;
        vehicle = Objects.requireNonNull(v);
        floor = f;
        slot = s;
        entryMinute = time;
    }

    long getId() {
        return id;
    }

    Vehicle vehicle() {
        return vehicle;
    }

    int floor() {
        return floor;
    }

    int slot() {
        return slot;
    }

    long entryMinute() {
        return entryMinute;
    }
}

class SmartParking {

    private final Vehicle[][] slots;

    private final Map<String, ParkingTicket> active =
        new HashMap<>();

    SmartParking(int[] capacities) {

        slots = new Vehicle[capacities.length][];

        for (int f = 0; f < capacities.length; f++) {
            if (capacities[f] < 0)
                throw new IllegalArgumentException("Negative capacity");

            slots[f] = new Vehicle[capacities[f]];
        }
    }

    ParkingTicket enter(Vehicle v, long now) {

        if (active.containsKey(v.getRegNo()))
            throw new IllegalArgumentException("Already parked");

        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {

                if (slots[f][s] == null) {

                    slots[f][s] = v;

                    ParkingTicket t =
                        new ParkingTicket(v, f, s, now);

                    active.put(v.getRegNo(), t);

                    return t;
                }
            }
        }

        return null;
    }

    double exit(String reg, long now) {

        if (reg == null || reg.isBlank())
            throw new IllegalArgumentException("Invalid registration");

        ParkingTicket t =
            active.get(reg.trim().toUpperCase());

        if (t == null)
            throw new IllegalArgumentException("No active ticket");

        long duration = now - t.entryMinute();

        if (duration < 0)
            throw new IllegalArgumentException("Invalid exit time");

        double fee = t.vehicle().calculateFee(duration);

        slots[t.floor()][t.slot()] = null;

        active.remove(reg.trim().toUpperCase());

        return fee;
    }

    void displayOccupancy() {

        for (int f = 0; f < slots.length; f++) {

            System.out.println("Floor " + f);

            for (int s = 0; s < slots[f].length; s++) {
                System.out.println(
                    "Slot " + s + ": " +
                    (slots[f][s] == null
                        ? "Available"
                        : slots[f][s].getRegNo()));
            }
        }
    }
}

public class SmartParkingDemo {

    public static void main(String[] args) {

        SmartParking parking =
            new SmartParking(new int[]{2, 1});

        ParkingTicket t1 =
            parking.enter(new TwoWheeler("TN01A1"), 0);

        ParkingTicket t2 =
            parking.enter(new Car("TN02B2"), 0);

        ParkingTicket t3 =
            parking.enter(new Car("TN03C3"), 0);

        System.out.println("Ticket ID: " + t1.getId());
        System.out.println("Ticket ID: " + t2.getId());
        System.out.println("Ticket ID: " + t3.getId());

        System.out.println(
            "Two-wheeler fee: Rs " +
            parking.exit("TN01A1", 61));

        System.out.println(
            "Car fee: Rs " +
            parking.exit("TN02B2", 130));

        System.out.println(
            "Car 2 fee: Rs " +
            parking.exit("TN03C3", 60));

        System.out.println("\nFinal Occupancy:");

        parking.displayOccupancy();
    }
}
