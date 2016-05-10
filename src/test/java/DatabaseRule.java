import org.junit.rules.ExternalResource;
import org.sql2o.*;

public class DatabaseRule extends ExternalResource {

  @Override
  protected void before() {
    DB.sql2o = new Sql2o("jdbc:postgresql://localhost:5432/registrar_test", null, null);
  }

  @Override
  protected void after() {
    try(Connection con = DB.sql2o.open()) {
      String deleteStudentQuery = "DELETE FROM student *;";
      String deleteCourseQuery = "DELETE FROM course *;";
      String deleteStudentScheduleQuery = "DELETE FROM student_schedule *;";
      con.createQuery(deleteStudentQuery).executeUpdate();
      con.createQuery(deleteCourseQuery).executeUpdate();
      con.createQuery(deleteStudentScheduleQuery).executeUpdate();
    }
  }
}
