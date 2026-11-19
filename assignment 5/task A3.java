import java.sql.*;
import java.util.*;
interface Dao<T, K> {
    void insert(T entity) throws SQLException;
    Optional<T> findById(K id) throws SQLException;
    List<T> findAll() throws SQLException;
    void update(T entity) throws SQLException;
    void delete(K id) throws SQLException;
}
class Student {
    int studentId;
    String registerNo, name, email;
    Student(int id, String reg, String name, String email) {
        this.studentId = id;
        this.registerNo = reg;
        this.name = name;
        this.email = email;
    }
    public String toString() {
        return studentId + " | " + registerNo +
               " | " + name + " | " + email;
    }
}
class StudentDao implements Dao<Student, Integer> {
    private final String url = System.getenv("DB_URL");
    private final String user = System.getenv("DB_USER");
    private final String password = System.getenv("DB_PASSWORD");
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
    private Student map(ResultSet rs) throws SQLException {
        return new Student(
            rs.getInt("student_id"),
            rs.getString("register_no"),
            rs.getString("name"),
            rs.getString("email"));
    }

    public void insert(Student s) throws SQLException {
        String sql =
            "INSERT INTO student(register_no,name,email) VALUES(?,?,?)";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(
                 sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, s.registerNo);
            ps.setString(2, s.name);
            ps.setString(3, s.email);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) s.studentId = keys.getInt(1);
            }
        }
    }

    public Optional<Student> findById(Integer id)
            throws SQLException {
        String sql = "SELECT * FROM student WHERE student_id=?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        }
        return Optional.empty();
    }

    public List<Student> findAll() throws SQLException {
        List<Student> students = new ArrayList<>();

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT * FROM student");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) students.add(map(rs));
        }
        return students;
    }

    public void update(Student s) throws SQLException {
        String sql =
            "UPDATE student SET register_no=?,name=?,email=? " +
            "WHERE student_id=?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.registerNo);
            ps.setString(2, s.name);
            ps.setString(3, s.email);
            ps.setInt(4, s.studentId);
            ps.executeUpdate();
        }
    }

    public void delete(Integer id) throws SQLException {
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(
                 "DELETE FROM student WHERE student_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}

public class StudentDaoDemo {
    public static void main(String[] args) throws Exception {
        StudentDao dao = new StudentDao();

        Student s = new Student(
            0, "REG001", "Arun", "arun@example.com");

        dao.insert(s);
        System.out.println("Inserted: " + s);

        System.out.println("Find: " + dao.findById(s.studentId));

        s.name = "Arun Kumar";
        dao.update(s);
        System.out.println("Updated: " + dao.findById(s.studentId));

        System.out.println("All students:");
        dao.findAll().forEach(System.out::println);

        dao.delete(s.studentId);
        System.out.println("Student deleted");
    }
}
