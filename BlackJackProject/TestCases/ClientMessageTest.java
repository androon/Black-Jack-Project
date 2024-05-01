package TestCases;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ClassSource.ClientMessage;
import ClassSource.MessageType;

class ClientMessageTest {
	ClientMessage clientMessage;
	
	@BeforeEach
	public void setup() {
		clientMessage = new ClientMessage();
	}
	
	
	@Test
	public void testClientMessageConstructor() {
		assertEquals("Undefined", clientMessage.getUsername());
		assertEquals("Undefined", clientMessage.getPassword());
		assertFalse(clientMessage.getIsDealer());
		assertEquals(0, clientMessage.getDepAmount());
		assertEquals(0, clientMessage.getWithdrawAmount());
		assertEquals(0, clientMessage.getPlayerID());
		assertEquals(0, clientMessage.getWinAmount());
		assertEquals(0, clientMessage.getLossAmount());
		assertEquals(0, clientMessage.getBetAmount());
		assertEquals(MessageType.UNKNOWN, clientMessage.getType());
	}
	
	
	@Test
	public void testSetandGetUsername() {
		clientMessage.setUsername("bob");
		assertEquals("bob",clientMessage.getUsername());
	}
	
	@Test
	public void testSetandGetPassword() {
		clientMessage.setPassword("password");
		assertEquals("password",clientMessage.getPassword());
		
	}
	@Test
	public void testSetandGetPlayerID() {
		clientMessage.setPlayerID(1);
		assertEquals(1, clientMessage.getPlayerID());
	}
	
	@Test
	public void testSetandGetisDealer() {
		clientMessage.setIsDealer(false);
		assertFalse(clientMessage.getIsDealer());
		
		clientMessage.setIsDealer(true);
		assertTrue(clientMessage.getIsDealer());
	}
	
	@Test
	public void testSetandGetDepAmount() {
		clientMessage.setDepAmount(20);
		assertEquals(20, clientMessage.getDepAmount());
	}
	
	@Test
	public void testSetandGetWinAmount() {
		clientMessage.setWinAmount(40);
		assertEquals(40, clientMessage.getWinAmount());
	}
	
	@Test
	public void testSetandGetLossAmount() {
		clientMessage.setLossAmount(53);
		assertEquals(53, clientMessage.getLossAmount());
	}
	
	@Test
	public void testSetandGetBetAmount() {
		clientMessage.setBetAmount(14);
		assertEquals(14, clientMessage.getBetAmount());
	}
	
	@Test
	public void testSetandGetBankRoll() {
		clientMessage.setBankroll(93);
		assertEquals(93, clientMessage.getBankRoll());
	}
	
	@Test
	public void testSetandGetType() {
		clientMessage.setMessageType(MessageType.DOUBLE_DOWN);
		assertEquals(MessageType.DOUBLE_DOWN, clientMessage.getType());
	}
	
	

}
