class Ticket {
    private static int nextTicketNumber = 1;
    private final int ticketNumber;
    private String vehicleNumber;
    Ticket(String vehicleNumber) {
        this.ticketNumber = nextTicketNumber++;
        this.vehicleNumber = vehicleNumber;
    }
    void display() {
        System.out.println("Ticket Number: " + ticketNumber);
        System.out.println("Vehicle Number: " + vehicleNumber);
    }
    static void displayNextNumber() {
        System.out.println(
            "Next Ticket Number: " + nextTicketNumber);
    }
}
public class ParkingTicketDemo {
    public static void main(String[] args) {
        Ticket t1 = new Ticket("TN01A1");
        Ticket t2 = new Ticket("TN02B2");
        Ticket t3 = new Ticket("TN03C3");
        t1.display();
        t2.display();
        t3.display();
        Ticket.displayNextNumber();
    }
}
