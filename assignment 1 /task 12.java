import java.time.*;
import java.util.*;
interface PricingRule {
    double fee(long minutes, LocalDateTime entry,
               LocalDateTime exit);
}
class HourlyPricingRule implements PricingRule {
    private final double rate;
    HourlyPricingRule(double rate) {
        if (rate < 0)
            throw new IllegalArgumentException("Negative rate");
        this.rate = rate;
    }
    public double fee(long minutes, LocalDateTime entry,
                      LocalDateTime exit) {
        if (minutes < 0)
            throw new IllegalArgumentException("Invalid duration");
        long hours = minutes == 0 ? 0 : (minutes + 59) / 60;
        return hours * rate;
    }
}
class FlatFeePricingRule implements PricingRule {
    private final double amount;
    FlatFeePricingRule(double amount) {
        if (amount < 0)
            throw new IllegalArgumentException("Negative fee");
        this.amount = amount;
    }
    public double fee(long minutes, LocalDateTime entry,
                      LocalDateTime exit) {
        if (minutes < 0)
            throw new IllegalArgumentException("Invalid duration");
        return minutes == 0 ? 0 : amount;
    }
}
class DeliveryVanPricingRule implements PricingRule {

    private final double carRate;

    DeliveryVanPricingRule(double carRate) {
        if (carRate < 0)
            throw new IllegalArgumentException("Invalid rate");

        this.carRate = carRate;
    }

    public double fee(long minutes, LocalDateTime entry,
                      LocalDateTime exit) {

        if (minutes < 0)
            throw new IllegalArgumentException("Invalid duration");

        if (minutes == 0)
            return 0;

        boolean daytime =
            !entry.toLocalTime().isBefore(LocalTime.of(9, 0))
            && entry.toLocalTime().isBefore(LocalTime.of(17, 0));

        long hours = (minutes + 59) / 60;

        return hours * carRate * (daytime ? 2 : 1);
    }
}

class OneDayPass {

    private final String registration;
    private final LocalDate validDate;

    OneDayPass(String reg, LocalDate date) {

        if (reg == null || reg.isBlank())
            throw new IllegalArgumentException("Invalid registration");

        registration = reg.trim().toUpperCase();
        validDate = Objects.requireNonNull(date);
    }

    boolean isValid(String reg, LocalDate date) {

        return reg != null
            && registration.equals(reg.trim().toUpperCase())
            && validDate.equals(date);
    }
}

class ParkingVehicle {

    private final String regNo;
    private final PricingRule pricingRule;

    ParkingVehicle(String regNo, PricingRule rule) {

        if (regNo == null || regNo.isBlank())
            throw new IllegalArgumentException("Invalid registration");

        this.regNo = regNo.trim().toUpperCase();
        this.pricingRule = Objects.requireNonNull(rule);
    }

    public String getRegNo() {
        return regNo;
    }

    public double calculateFee(long minutes,
                               LocalDateTime entry,
                               LocalDateTime exit) {

        return pricingRule.fee(minutes, entry, exit);
    }
}

class AdvancedParking {

    private final ParkingVehicle[][] slots;

    AdvancedParking(int[] capacities) {

        slots = new ParkingVehicle[capacities.length][];

        for (int i = 0; i < capacities.length; i++) {

            if (capacities[i] < 0)
                throw new IllegalArgumentException("Invalid capacity");

            slots[i] = new ParkingVehicle[capacities[i]];
        }
    }

    int[] allocate(ParkingVehicle vehicle) {

        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {

                if (slots[f][s] == null) {
                    slots[f][s] = vehicle;
                    return new int[]{f, s};
                }
            }
        }

        return null;
    }

    public static void main(String[] args) {

        AdvancedParking parking =
            new AdvancedParking(new int[]{2, 1, 3, 2, 4});

        ParkingVehicle ebike =
            new ParkingVehicle("TN01A1",
                new FlatFeePricingRule(20));

        ParkingVehicle van =
            new ParkingVehicle("TN02B2",
                new DeliveryVanPricingRule(30));

        ParkingVehicle car =
            new ParkingVehicle("TN03C3",
                new HourlyPricingRule(30));

        System.out.println(
            "E-bike Slot: " +
            Arrays.toString(parking.allocate(ebike)));

        System.out.println(
            "Van Slot: " +
            Arrays.toString(parking.allocate(van)));

        System.out.println(
            "Car Slot: " +
            Arrays.toString(parking.allocate(car)));

        LocalDateTime entry =
            LocalDateTime.of(2026, 11, 19, 10, 0);

        LocalDateTime exit =
            entry.plusHours(2);

        long minutes = Duration.between(entry, exit).toMinutes();

        System.out.println(
            "E-bike Fee: Rs." +
            ebike.calculateFee(minutes, entry, exit));

        System.out.println(
            "Delivery Van Fee: Rs." +
            van.calculateFee(minutes, entry, exit));

        System.out.println(
            "Car Fee: Rs." +
            car.calculateFee(minutes, entry, exit));

        OneDayPass pass = new OneDayPass(
            "TN03C3", LocalDate.of(2026, 11, 19));

        System.out.println(
            "One Day Pass Valid: " +
            pass.isValid("TN03C3",
                LocalDate.of(2026, 11, 19)));

        System.out.println(
            "Five Floors Total Capacity: 12");
    }
}
