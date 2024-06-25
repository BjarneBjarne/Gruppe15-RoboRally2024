package cucumber_tests.changing_phases;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features",
                 glue = "cucumber_tests")

public class GameControllerTests {
}
