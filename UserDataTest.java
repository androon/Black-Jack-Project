import static org.junit.Assert.*;
import org.junit.Test;

public class UserDataTest {

    @Test
    public void testConstructorAndGetters() {
        UserData userData = new UserData("user", "password", true, 10, 5, 1000);
        assertNotNull(userData);
        assertEquals("user", userData.getUsername());
        assertEquals("password", userData.getPassword());
        assertTrue(userData.getIsDealer());
        assertEquals(10, userData.getWinAmount());
        assertEquals(5, userData.getLossAmount());
        assertEquals(1000, userData.getBankroll());
    }
}