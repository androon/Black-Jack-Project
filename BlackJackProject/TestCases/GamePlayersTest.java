package TestCases;

import org.junit.Before;
import org.junit.Test;

import ClassSource.GamePlayers;
import ClassSource.PlayerData;

import static org.junit.Assert.*;

import java.util.List;

public class GamePlayersTest {

    private GamePlayers gamePlayers;

    @Before
    public void setUp() {
        gamePlayers = new GamePlayers();
    }

    @Test
    public void testGamePlayersConstructor() {
        assertNotNull(gamePlayers);
        List<PlayerData> players = gamePlayers.getGamePlayers();
        assertNotNull(players);
        assertTrue(players.isEmpty());
    }

    @Test
    public void testAddPlayer() {
        PlayerData mockPlayer = new PlayerData("Player1", 1, 100, false,24,24);

        gamePlayers.addPlayer(mockPlayer);

        List<PlayerData> players = gamePlayers.getGamePlayers();
        assertEquals(1, players.size());
        assertEquals(1, players.get(0).getPlayerID());
        assertEquals("Player1", players.get(0).getUserName());
    }

    @Test
    public void testGetNumPlayers() {
        PlayerData mockPlayer1 = new PlayerData("Player1", 1, 100, false,12, 14);
        PlayerData mockPlayer2 = new PlayerData("Player2", 1, 100, false,14, 15);

        gamePlayers.addPlayer(mockPlayer1);
        gamePlayers.addPlayer(mockPlayer2);

        assertEquals(2, gamePlayers.getNumPlayers());
    }
    
}