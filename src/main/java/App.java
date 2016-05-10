import java.util.Map;
import java.util.HashMap;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import static spark.Spark.*;

public class App {
  public static void main(String[] args) {
    staticFileLocation("/public");
    String layout = "templates/layout.vtl";

    get("/", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/index.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/students", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("students", Student.all());
      model.put("template", "templates/students.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/tasks", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("courses", Course.all());
      model.put("template", "templates/courses.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/courses", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      String courseName = request.queryParams("courseName");
      String courseNumber = request.queryParams("courseNumber");
      Course newCourse = new Course(courseName, courseNumber);
      newCourse.save();
      response.redirect("/courses");
      return null;
    });

    post("/students", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      String name = request.queryParams("name");
      Student newStudent = new Student(name);
      newStudent.save();
      response.redirect("/students");
      return null;
    });

    get("/courses/:id", (request,response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      Course course = Course.find(Integer.parseInt(request.params("id")));
      model.put("course", course);
      model.put("allStudents", Student.all());
      model.put("template", "templates/course.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/students/:id", (request,response) ->{
      HashMap<String, Object> model = new HashMap<String, Object>();
      Student student = Student.find(Integer.parseInt(request.params("id")));
      model.put("student", student);
      model.put("allCourses", Course.all());
      model.put("template", "templates/student.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/add_courses", (request, response) -> {
      int courseId = Integer.parseInt(request.queryParams("course_id"));
      int studentId = Integer.parseInt(request.queryParams("student_id"));
      Student student = Student.find(studentId);
      Course course = Course.find(courseId);
      student.addCourse(course);
      response.redirect("/students/" + studentId);
      return null;
    });

    post("/add_students", (request, response) -> {
      int courseId = Integer.parseInt(request.queryParams("course_id"));
      int studentId = Integer.parseInt(request.queryParams("student_id"));
      Student student = Student.find(studentId);
      Course course = Course.find(courseId);
      course.addStudent(student);
      response.redirect("/courses/" + courseId);
      return null;
    });
  }
}
