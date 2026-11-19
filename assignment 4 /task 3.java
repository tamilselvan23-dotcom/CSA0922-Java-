import java.util.*;
public class AttendanceDemo {
    static class Student {
        private final String regNo;
        private final String name;
        Student(String regNo, String name) {
            this.regNo = regNo;
            this.name = name;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof Student))
                return false;
            Student other = (Student) obj;
            return Objects.equals(regNo, other.regNo);
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(regNo);
        }
        @Override
        public String toString() {
            return regNo + " - " + name;
        }
    }
    static class MutableStudent {
        String regNo;
        MutableStudent(String regNo) {
            this.regNo = regNo;
        }
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MutableStudent))
                return false;
            return Objects.equals(regNo,
                    ((MutableStudent) obj).regNo);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(regNo);
        }
    }

    public static void main(String[] args) {

        // Test 1: Same object reference
        Set<Student> test1 = new HashSet<>();
        Student s1 = new Student("101", "Arun");

        test1.add(s1);
        test1.add(s1);

        System.out.println("Test 1 - Same reference");
        System.out.println("Set size: " + test1.size());

        // Test 2: Equal but distinct objects
        Set<Student> attendance = new HashSet<>();

        attendance.add(new Student("101", "Arun"));
        attendance.add(new Student("101", "Arun Kumar"));
        attendance.add(new Student("102", "Bala"));

        System.out.println("\nTest 2 - Equal objects");
        System.out.println("Set size: " + attendance.size());

        for (Student s : attendance)
            System.out.println(s);

        // Test 3: Mutating a hash key
        Set<MutableStudent> test3 = new HashSet<>();

        MutableStudent student = new MutableStudent("201");
        test3.add(student);

        System.out.println("\nTest 3 - Mutable key");
        System.out.println("Before mutation: "
                + test3.contains(student));

        student.regNo = "999";

        System.out.println("After mutation: "
                + test3.contains(student));

        System.out.println("\nRecommended design:");
        System.out.println(
                "Use immutable registration numbers.");
    }
}
