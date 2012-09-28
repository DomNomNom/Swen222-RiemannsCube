package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CubeTests.class, WorldTests.class, XMLParsingTests.class, ActionTests.class })
public class AllTests {

}
