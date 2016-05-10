import java.util.*;
import java.text.*;
import org.sql2o.*;

public class Student {
  private int id;
  private String name;
  private String doe; //DATE OF ENROLLMENT//
  private Format formatter = new SimpleDateFormat("MMMM dd yyyy");

  public Student(String name) {
    Date date = new Date();
    this.name = name;
    this.doe = formatter.format(date);
  }

  public String getName() {
    return name;
  }

  public int getId() {
    return id;
  }

  public String getDoe() {
    return doe;
  }

  public static List<Student> all() {
    String sql = "SELECT id, name FROM Students";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Student.class);
    }
  }

  @Override
  public boolean equals(Object otherStudent) {
    if (!(otherStudent instanceof Student)) {
      return false;
    } else {
      Student newStudent = (Student) otherStudent;
      return this.getName().equals(newStudent.getName()) &&
             this.getId() == newStudent.getId();
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO Students(name, doe) VALUES (:name, :doe)";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("name", this.name)
        .addParameter("doe", this.doe)
        .executeUpdate()
        .getKey();
    }
  }

  public static Student find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM Students where id=:id";
      Student student = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Student.class);
      return student;
    }
  }

  public void addCourse(Course course) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO students_courses (student_id, course_id) VALUES (:student_id, :course_id)";
      con.createQuery(sql)
        .addParameter("student_id", this.getId())
        .addParameter("course_id", course.getId())
        .executeUpdate();
    }
  }

  public List<Course> getCourses() {
    try(Connection con = DB.sql2o.open()){
      String joinQuery = "SELECT course_id FROM students_courses WHERE student_id = :student_id";
      List<Integer> courseIds = con.createQuery(joinQuery)
        .addParameter("student_id", this.getId())
        .executeAndFetch(Integer.class);

      List<Course> courses = new ArrayList<Course>();

      for (Integer courseId : courseIds) {
        String courseQuery = "Select * From courses WHERE id = :courseId";
        Course course = con.createQuery(courseQuery)
          .addParameter("courseId", courseId)
          .executeAndFetchFirst(Course.class);
        courses.add(course);
      }
      return courses;
    }
  }

  public void delete() {
    try(Connection con = DB.sql2o.open()) {
      String deleteQuery = "DELETE FROM categories WHERE id = :id;";
        con.createQuery(deleteQuery)
          .addParameter("id", this.getId())
          .executeUpdate();

      String joinDeleteQuery = "DELETE FROM students_courses WHERE student_id = :studentId";
        con.createQuery(joinDeleteQuery)
          .addParameter("studentId", this.getId())
          .executeUpdate();
    }
  }

}
