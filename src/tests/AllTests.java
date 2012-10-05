package tests;

import static org.junit.Assert.fail;

import java.awt.Color;
import java.io.FileWriter;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import utils.Configurations;
import world.objects.items.Key;
import world.objects.Lock;
import world.objects.Player;
import world.objects.Trigger;
import world.objects.doors.Door;
import data.XMLParser;


@RunWith(Suite.class)
@SuiteClasses({ 
	CubeTests.class, 
	WorldTests.class, 
	XMLParsingTests.class, 
	ActionTests.class, 
	CubeTests.class,
	NetworkingTests.class,
})

public class AllTests {
}
