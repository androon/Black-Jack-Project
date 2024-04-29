import static org.junit.Assert.*;
import org.junit.Test;

public class ResponseTest {

    @Test
    public void testConstructor() {
        Response response = new Response();
        assertNotNull(response);
        assertEquals(ResponseType.UNKNOWN, response.getType());
        assertFalse(response.getValidated());
        assertEquals(0, response.getPlayerID());
        assertFalse(response.getIsDealer());
        assertEquals(0, response.getWinAmount());
        assertEquals(0, response.getLossAmount());
        assertEquals(0, response.getBankroll());
        assertNull(response.getUsername());
    }

    @Test
    public void testSettersAndGetters() {
        Response response = new Response();
        response.setType(ResponseType.REQUEST_START_ROUND);
        assertEquals(ResponseType.REQUEST_START_ROUND, response.getType());

        response.setValidated(true);
        assertTrue(response.getValidated());

        response.setPlayerID(123);
        assertEquals(123, response.getPlayerID());

        response.setDealer(true);
        assertTrue(response.getIsDealer());

        response.setWinAmount(1000);
        assertEquals(1000, response.getWinAmount());

        response.setLossAmount(500);
        assertEquals(500, response.getLossAmount());

        response.setBankRoll(2000);
        assertEquals(2000, response.getBankroll());

        response.setUsername("User");
        assertEquals("User", response.getUsername());
    }
}
