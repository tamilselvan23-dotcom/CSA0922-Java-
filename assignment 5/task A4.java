import java.io.*;
import java.sql.Connection;
import java.util.*;
class Resident implements Serializable {
    private static final long serialVersionUID = 1L;
    String name;
    String registerNumber;
    String phoneNumber;
    transient String password;
    transient Connection connection;
    Resident(String name, String reg, String password) {
        this.name = name;
        this.registerNumber = reg;
        this.password = password;
    }
    public String toString() {
        return name + " | " + registerNumber +
               " | Password after load: " + password;
    }
}
class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    int roomNumber;
    List<Resident> residents = new ArrayList<>();
    Room(int number) {
        roomNumber = number;
    }
}
class Hostel implements Serializable {
    private static final long serialVersionUID = 1L;
    String hostelName;
    List<Room> rooms = new ArrayList<>();
    Hostel(String name) {
        hostelName = name;
    }
}

public class HostelSerialization {
    public static void main(String[] args) throws Exception {
        Hostel hostel = new Hostel("SIMATS Hostel");

        Room room = new Room(101);
        room.residents.add(
            new Resident("Arun", "REG001", "demoPassword"));
        room.residents.add(
            new Resident("Divya", "REG002", "demoPassword2"));

        hostel.rooms.add(room);

        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("hostel.dat"))) {
            out.writeObject(hostel);
        }

        System.out.println("Hostel serialized successfully");

        // Read only files created by this trusted application.
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream("hostel.dat"))) {

            Hostel restored = (Hostel) in.readObject();

            System.out.println("Hostel: " + restored.hostelName);

            for (Room r : restored.rooms) {
                System.out.println("Room: " + r.roomNumber);

                for (Resident resident : r.residents) {
                    System.out.println(resident);
                }
            }
        }
    }
}
