public class DataRepresentation {
    public static void main(String[] args) {
        int floor = 2;
        int slot = 5;
        String registration = "TN01AB1111";
        long duration = 61;
        double fee = 30.0;
        String[][] parkingSlots = {
            {"Car1", "Car2"},
            {"Bike1", null},
            {"Car3", null}
        };
        int hours = duration == 0
                ? 0 : (int)((duration + 59) / 60);
        double totalFee = hours * fee;
        System.out.println("Floor: " + floor);
        System.out.println("Slot: " + slot);
        System.out.println("Registration: " + registration);
        System.out.println("Duration: " + duration);
        System.out.println("Billable Hours: " + hours);
        System.out.println("Total Fee: Rs." + totalFee);
        System.out.println("Parking Slots:");
        for (int i = 0; i < parkingSlots.length; i++) {
            for (int j = 0; j < parkingSlots[i].length; j++) {
                System.out.print(parkingSlots[i][j] + " ");
            }
            System.out.println();
        }
    }
}
