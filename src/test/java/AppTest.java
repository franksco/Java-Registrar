import org.sql2o.*;
import org.junit.*;
import org.fluentlenium.adapter.FluentTest;
import org.junit.ClassRule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.*;
import static org.junit.Assert.*;

public class AppTest extends FluentTest {
  public WebDriver webDriver = new HtmlUnitDriver();

  @Override
  public WebDriver getDefaultDriver() {
    return webDriver;
  }

  @ClassRule
  public static ServerRule server = new ServerRule();

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Test
  public void rootTest() {
    goTo("http://localhost:4567/");
    assertThat(pageSource()).contains("Registrar Course Scheduling");
  }

  @Test
  public void studentIsCreatedTest() {
    goTo("http://localhost:4567/");
    click("a", withText("Students"));
    fill("#name").with("billy");
    submit(".btn");
    assertThat(pageSource()).contains("billy");
  }

  @Test
  public void courseIsCreatedTest() {
    goTo("http://localhost:4567/");
    click("a", withText("Courses"));
    fill("#courseName").with("History");
    fill("#courseNumber").with("HIST101");
    submit(".btn");
    assertThat(pageSource()).contains("HIST101");
  }

  @Test
  public void studentShowPageDisplaysName() {
    Student testStudent = new Student("Billy");
    testStudent.save();
    String url = String.format("http://localhost:4567/students/%d", testStudent.getId());
    goTo(url);
    assertThat(pageSource()).contains("Billy");
  }

  @Test
  public void courseShowPageDisplaysDescription() {
    Course testCourse = new Course("History");
    testCourse.save();
    String url = String.format("http://localhost:4567/courses/%d", testCourse.getId());
    goTo(url);
    assertThat(pageSource()).contains("History");
  }
  //
  // @Test
  // public void taskIsAddedToCategory() {
  //   Category testCategory = new Category("Household chores");
  //   testCategory.save();
  //   Task testTask = new Task("Mow the lawn");
  //   testTask.save();
  //   String url = String.format("http://localhost:4567/categories/%d", testCategory.getId());
  //   goTo(url);
  //   fillSelect("#task_id").withText("Mow the lawn");
  //   submit(".btn");
  //   assertThat(pageSource()).contains("<li>");
  //   assertThat(pageSource()).contains("Mow the lawn");
  // }
  //
  // @Test
  // public void categoryIsAddedToTask() {
  //   Category testCategory = new Category("Household chores");
  //   testCategory.save();
  //   Task testTask = new Task("Mow the lawn");
  //   testTask.save();
  //   String url = String.format("http://localhost:4567/tasks/%d", testTask.getId());
  //   goTo(url);
  //   fillSelect("#category_id").withText("Household chores");
  //   submit(".btn");
  //   assertThat(pageSource()).contains("<li>");
  //   assertThat(pageSource()).contains("Household chores");
  // }

}
