class Vehicle {
    protected String regNo;
    Vehicle(String regNo) {
        this.regNo = regNo.toUpperCase();
    }
    double calculateFee(int minutes) {
        return 0;
    }
}
class TwoWheeler extends Vehicle {
    TwoWheeler(String regNo) {
        super(regNo);
    }
    @Override
    double calculateFee(int minutes) {
        if (minutes < 0)
            throw new IllegalArgumentException("Invalid duration");
        int hours = minutes == 0 ? 0 : (minutes + 59) / 60;
        return hours * 10;
    }
}
class Car extends Vehicle {
    Car(String regNo) {
        super(regNo);
    }
    @Override
    double calculateFee(int minutes) {
        if (minutes < 0)
            throw new IllegalArgumentException("Invalid duration");
        int hours = minutes == 0 ? 0 : (minutes + 59) / 60;
        return hours * 30;
    }
}
public class VehicleOverrides {
    public static void main(String[] args) {

        TwoWheeler bike = new TwoWheeler("TN01A1");
        Car car = new Car("TN02B2");

        System.out.println("Bike Fee: Rs." +
            bike.calculateFee(61));

        System.out.println("Car Fee: Rs." +
            car.calculateFee(130));
    }
}
