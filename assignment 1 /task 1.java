class Vehicle {
    String registrationNo;
    String vehicleType;

    Vehicle(String reg, String type) {
        registrationNo = reg;
        vehicleType = type;
    }
}
class ParkingSlot {
    int slotNumber;
    boolean occupied;
    ParkingSlot(int number) {
        slotNumber = number;
        occupied = false;
    }
}
class ParkingTicket {
    int ticketId;
    String registrationNo;
    double fee;
    ParkingTicket(int id, String reg, double fee) {
        ticketId = id;
        registrationNo = reg;
        this.fee = fee;
    }
}
class ParkingFacility {
    String facilityName;
    ParkingFacility(String name) {
        facilityName = name;
    }
}
public class ParkingObjects {
    public static void main(String[] args) {
        Vehicle v = new Vehicle("TN01A1", "Car");
        ParkingSlot s = new ParkingSlot(101);
        ParkingTicket t = new ParkingTicket(1, v.registrationNo, 90);
        ParkingFacility p = new ParkingFacility("Campus Parking");

        System.out.println(p.facilityName);
        System.out.println(v.registrationNo);
        System.out.println(v.vehicleType);
        System.out.println("Slot: " + s.slotNumber);
        System.out.println("Ticket: " + t.ticketId);
        System.out.println("Fee: Rs." + t.fee);
    }
}
