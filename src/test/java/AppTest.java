
import org.fluentlenium.adapter.FluentTest;
import org.junit.ClassRule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import static org.fluentlenium.core.filter.FilterConstructor.*;
import static org.assertj.core.api.Assertions.assertThat;
import org.sql2o.*;
import org.junit.*;

public class AppTest extends FluentTest {
  public WebDriver webDriver = new HtmlUnitDriver();

  @Override
  public WebDriver getDefaultDriver() {
    return webDriver;
  }

  @ClassRule
  public static ServerRule server = new ServerRule();

  @Before
    public void setUp() {
    DB.sql2o = new Sql2o("jdbc:postgresql://localhost:5432/to_do_test", null, null);
  }

  @After
  public void tearDown() {
    try(Connection con = DB.sql2o.open()) {
      String deleteTasksQuery = "DELETE FROM tasks *;";
      String deleteCategoriesQuery = "DELETE FROM categories *;";
      con.createQuery(deleteTasksQuery).executeUpdate();
      con.createQuery(deleteCategoriesQuery).executeUpdate();
    }
  }


  @Test
  public void rootTest() {
    goTo("http://localhost:4567");
    assertThat(pageSource()).contains("to do list");
  }

  @Test
  public void addCategoryAndCheckForIt() {
    Category testCategory = new Category ("Lawncare");
    testCategory.save();
    String categoryPath = String.format("http://localhost:4567/categories/%d", testCategory.getId());
    goTo(categoryPath);
    assertThat(pageSource()).contains("Lawncare");
  }

  @Test
  public void addTaskToCategory() {
    goTo("http://localhost:4567");
    fill("#name").with("Lawncare");
    submit(".btn");
    click("a", withText("Lawncare"));
    fill("#task").with("Mow the lawn");
    submit(".btn");
    assertThat(pageSource()).contains("Mow the lawn");
  }

}
