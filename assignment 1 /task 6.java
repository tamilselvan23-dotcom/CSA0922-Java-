abstract class Vehicle {
    protected String regNo;
    Vehicle(String regNo) {
        if (regNo == null || regNo.isBlank())
            throw new IllegalArgumentException("Invalid registration");
        this.regNo = regNo.toUpperCase();
    }
    public String getRegNo() {
        return regNo;
    }
    abstract double ratePerHour();
}
class TwoWheeler extends Vehicle {
    TwoWheeler(String regNo) {
        super(regNo);
    }
    double ratePerHour() {
        return 10;
    }
}
class Car extends Vehicle {
    Car(String regNo) {
        super(regNo);
    }
    double ratePerHour() {
        return 30;
    }
}
class ElectricCar extends Car {
    ElectricCar(String regNo) {
        super(regNo);
    }
}
public class VehicleHierarchy {
    public static void main(String[] args) {

        TwoWheeler bike = new TwoWheeler("TN01A1");
        Car car = new Car("TN02B2");
        ElectricCar electric = new ElectricCar("TN03C3");

        System.out.println(bike.getRegNo() +
            " Rate: " + bike.ratePerHour());

        System.out.println(car.getRegNo() +
            " Rate: " + car.ratePerHour());

        System.out.println(electric.getRegNo() +
            " Rate: " + electric.ratePerHour());
    }
}
