class Vehicle {
    private String regNo;
    Vehicle(String regNo) {
        if (regNo == null || regNo.isBlank())
            throw new IllegalArgumentException("Invalid registration");
        this.regNo = regNo.trim().toUpperCase();
    }
    public String getRegNo() {
        return regNo;
    }
}
class ParkingTicket {
    private static int nextId = 1;
    private final int ticketId;
    private final Vehicle vehicle;
    private final int floor;
    private final int slot;
    ParkingTicket(Vehicle vehicle, int floor, int slot) {
        this.ticketId = nextId++;
        this.vehicle = vehicle;
        this.floor = floor;
        this.slot = slot;
    }
    public void display() {
        System.out.println("Ticket ID: " + ticketId);
        System.out.println("Vehicle: " + vehicle.getRegNo());
        System.out.println("Floor: " + floor);
        System.out.println("Slot: " + slot);
    }
}
public class ParkingClasses {
    public static void main(String[] args) {
        Vehicle v = new Vehicle("TN01A1");
        ParkingTicket ticket =
            new ParkingTicket(v, 1, 2);
        ticket.display();
    }
}
