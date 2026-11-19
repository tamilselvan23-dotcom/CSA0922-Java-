import java.time.LocalTime;
interface Chargeable {
    double chargingFee(int kWh);
}
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
    public double calculateFee(int minutes) {
        if (minutes < 0)
            throw new IllegalArgumentException("Negative duration");
        int hours = minutes == 0 ? 0 : (minutes + 59) / 60;
        return hours * ratePerHour();
    }
    public double calculateFee(LocalTime in, LocalTime out) {
        if (in == null || out == null || out.isBefore(in))
            throw new IllegalArgumentException("Invalid time");
        int minutes = (int) java.time.Duration.between(in, out).toMinutes();
        return calculateFee(minutes);
    }
    @Override
    public boolean equals(Object o) {
        return o instanceof Vehicle &&
            regNo.equals(((Vehicle) o).regNo);
    }
    @Override
    public int hashCode() {
        return regNo.hashCode();
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

class ElectricCar extends Car implements Chargeable {
    ElectricCar(String r) {
        super(r);
    }

    @Override
    public double chargingFee(int kWh) {
        if (kWh < 0)
            throw new IllegalArgumentException("Negative kWh");

        return kWh * 12.0;
    }
}

public class FeeDemo {
    public static void main(String[] args) {

        Vehicle[] parked = {
            new TwoWheeler("TN01A1"),
            new Car("TN02B2"),
            new ElectricCar("TN03C3")
        };

        for (Vehicle v : parked) {
            double fee = v.calculateFee(130);

            if (v instanceof Chargeable c)
                fee += c.chargingFee(10);

            System.out.println(
                v.getClass().getSimpleName() + " " +
                v.getRegNo() + " -> Rs " + fee);
        }

        System.out.println(
            new Car("TN02B2").equals(
                new ElectricCar("TN02B2")));
    }
}
