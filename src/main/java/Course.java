import java.util.*;
import java.text.*;
import org.sql2o.*;

public class Course {
  private int id;
  private String courseName;
  private String courseNumber;

  public Course(String courseName, String courseNumber) {
    this.courseName = courseName;
    this.courseNumber = courseNumber;
  }

  public String getCourseName() {
    return courseName;
  }

  public String getCourseNumber() {
    return courseNumber;
  }

  public int getId() {
    return id;
  }

  public static List<Course> all() {
    String sql = "SELECT *  FROM course";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Course.class);
    }
  }

  @Override
  public boolean equals(Object otherCourse){
    if (!(otherCourse instanceof Course)) {
      return false;
    } else {
      Course newCourse = (Course) otherCourse;
      return this.getCourseNumber().equals(newCourse.getCourseNumber()) &&
             this.getId() == newCourse.getId();
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO course(courseName, courseNumber) VALUES (:courseName, :courseNumber)";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("courseName", this.courseName)
        .addParameter("courseNumber", this.courseNumber)
        .executeUpdate()
        .getKey();
    }
  }

  public static Course find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM course where id=:id";
      Course course = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Course.class);
      return course;
    }
  }

  public void update(String newDescription) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE course SET courseNumber = :courseNumber WHERE id = :id";
      con.createQuery(sql)
        .addParameter("courseNumber", this.courseNumber)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  public void addStudent(Student student) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO student_schedule (student_id, course_id) VALUES (:student_id, :course_id)";
      con.createQuery(sql)
        .addParameter("student_id", student.getId())
        .addParameter("course_id", this.getId())
        .executeUpdate();
    }
  }

  public List<Student> getStudents() {
    try(Connection con = DB.sql2o.open()){
      String joinQuery = "SELECT student_id FROM student_schedule WHERE course_id = :course_id";
      List<Integer> studentIds = con.createQuery(joinQuery)
        .addParameter("course_id", this.getId())
        .executeAndFetch(Integer.class);

      List<Student> students = new ArrayList<Student>();

      for (Integer studentId : studentIds) {
        String courseQuery = "SELECT * FROM student WHERE id = :studentId";
        Student student = con.createQuery(courseQuery)
          .addParameter("studentId", studentId)
          .executeAndFetchFirst(Student.class);
        students.add(student);
      }
      return students;
    }
  }

  public void delete() {
    try(Connection con = DB.sql2o.open()) {
      String deleteQuery = "DELETE FROM course WHERE id = :id;";
        con.createQuery(deleteQuery)
          .addParameter("id", this.getId())
          .executeUpdate();

      String joinDeleteQuery = "DELETE FROM student_schedule WHERE course_id = :courseId";
        con.createQuery(joinDeleteQuery)
          .addParameter("courseId", this.getId())
          .executeUpdate();
    }
  }

}
