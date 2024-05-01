package TestCases;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({ CardTest.class, ClientMessageTest.class, DeckTest.class, GameLogicTest.class, GameManagerTest.class,
		GamePlayersTest.class, LoadUserDataTest.class, PlayerDataTest.class, ResponseTest.class, UserDataTest.class })
public class AllTests {

}
