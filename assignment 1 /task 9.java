interface Chargeable {
    double chargingFee(int kWh);
}
interface Insurable {
    double insuranceFee();
}
abstract class Vehicle {
    protected String regNo;
    Vehicle(String regNo) {
        this.regNo = regNo.toUpperCase();
    }
    abstract double calculateFee(int minutes);
    void display() {
        System.out.println("Registration: " + regNo);
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
class ElectricCar extends Car
        implements Chargeable, Insurable {
    ElectricCar(String regNo) {
        super(regNo);
    }
    @Override
    public double chargingFee(int kWh) {
        return kWh * 12;
    }

    @Override
    public double insuranceFee() {
        return 1500;
    }
}

public class AbstractionDemo {
    public static void main(String[] args) {

        ElectricCar car = new ElectricCar("TN03C3");

        car.display();

        System.out.println("Parking Fee: Rs." +
            car.calculateFee(120));

        System.out.println("Charging Fee: Rs." +
            car.chargingFee(10));

        System.out.println("Insurance Fee: Rs." +
            car.insuranceFee());
    }
}
