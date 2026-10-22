import org.junit.jupiter.api.*;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class PlayersTest {

    static String PLAYERS_FILE = "AllPlayersStats.txt";

    @BeforeEach
    void clearFile() throws IOException {
        new PrintWriter(new File(PLAYERS_FILE)).close();
    }

    @AfterAll
    static void deleteFile() {
        new File(PLAYERS_FILE).delete();
    }



    @Test
    void constructorCreatesEmptyListWhenFileIsEmpty() {
        Players players = new Players();
        assertTrue(players.allPlayers.isEmpty());
    }

    @Test
    void constructorLoadsPlayersFromFile() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PLAYERS_FILE))) {
            writer.println("alice");
            writer.println(80);
            writer.println(8);
            writer.println(10);
            writer.println(3);
            writer.println(2);
        }
        Players players = new Players();
        assertEquals(1, players.allPlayers.size());
        assertEquals("alice", players.allPlayers.get(0).getUsername());
    }

    @Test
    void constructorLoadsMultiplePlayersFromFile() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PLAYERS_FILE))) {
            writer.println("alice");
            writer.println(80); writer.println(8); writer.println(10);
            writer.println(3); writer.println(2);
            writer.println("bob");
            writer.println(50); writer.println(5); writer.println(10);
            writer.println(4); writer.println(1);
        }
        Players players = new Players();
        assertEquals(2, players.allPlayers.size());
    }

    @Test
    void constructorLoadsCorrectStatsFromFile() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PLAYERS_FILE))) {
            writer.println("alice");
            writer.println(80);
            writer.println(8);
            writer.println(10);
            writer.println(3);
            writer.println(2);
        }
        Players players = new Players();
        Player alice = players.allPlayers.get(0);
        assertEquals(80, alice.getAccuracy());
        assertEquals(8, alice.getCorrectGuesses());
        assertEquals(10, alice.getTotalGuesses());
        assertEquals(3, alice.getCryptogramsPlayed());
        assertEquals(2, alice.getCryptogramsCompleted());
    }



    @Test
    void addPlayerIncreasesListSize() {
        Players players = new Players();
        players.addPlayer(new Player("alice"));
        assertEquals(1, players.allPlayers.size());
    }

    @Test
    void addPlayerStoresCorrectPlayer() {
        Players players = new Players();
        players.addPlayer(new Player("alice"));
        assertEquals("alice", players.allPlayers.get(0).getUsername());
    }


    @Test
    void findPlayerReturnsPlayerIfExists() {
        Players players = new Players();
        players.addPlayer(new Player("alice"));
        assertNotNull(players.findPlayer(new Player("alice")));
    }

    @Test
    void findPlayerReturnsNullIfNotExists() {
        Players players = new Players();
        players.addPlayer(new Player("alice"));
        assertNull(players.findPlayer(new Player("bob")));
    }

    @Test
    void findPlayerMatchesByUsernameOnly() {
        Players players = new Players();
        players.addPlayer(new Player("alice", 90, 9, 10, 5, 3));
        Player found = players.findPlayer(new Player("alice"));
        assertNotNull(found);
        assertEquals(90, found.getAccuracy());
    }


    @Test
    void loadOrCreateUserReturnsExistingPlayer() {
        Players players = new Players();
        players.addPlayer(new Player("alice", 80, 8, 10, 3, 2));
        Player result = players.loadOrCreateUser("alice");
        assertEquals(80, result.getAccuracy());
    }

    @Test
    void loadOrCreateUserCreatesNewPlayerIfNotFound() {
        Players players = new Players();
        Player result = players.loadOrCreateUser("newuser");
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
    }

    @Test
    void loadOrCreateUserAddsNewPlayerToList() {
        Players players = new Players();
        players.loadOrCreateUser("newuser");
        assertEquals(1, players.allPlayers.size());
    }

    @Test
    void loadOrCreateUserDoesNotDuplicateExistingPlayer() {
        Players players = new Players();
        players.addPlayer(new Player("alice"));
        players.loadOrCreateUser("alice");
        assertEquals(1, players.allPlayers.size());
    }


    @Test
    void savePlayersWritesAllPlayers() throws IOException {
        Players players = new Players();
        players.addPlayer(new Player("alice", 80, 8, 10, 3, 2));
        players.addPlayer(new Player("bob", 50, 5, 10, 4, 1));
        players.savePlayers();

        Players reloaded = new Players();
        assertEquals(2, reloaded.allPlayers.size());
    }

    @Test
    void savePlayersPreservesStats() throws IOException {
        Players players = new Players();
        players.addPlayer(new Player("alice", 80, 8, 10, 3, 2));
        players.savePlayers();

        Players reloaded = new Players();
        Player alice = reloaded.allPlayers.get(0);
        assertEquals("alice", alice.getUsername());
        assertEquals(80, alice.getAccuracy());
        assertEquals(8, alice.getCorrectGuesses());
        assertEquals(10, alice.getTotalGuesses());
        assertEquals(3, alice.getCryptogramsPlayed());
        assertEquals(2, alice.getCryptogramsCompleted());
    }

    @Test
    void savePlayersOverwritesPreviousFile() throws IOException {
        Players players = new Players();
        players.addPlayer(new Player("alice"));
        players.savePlayers();

        Players players2 = new Players();
        players2.addPlayer(new Player("bob"));
        players2.savePlayers();

        Players reloaded = new Players();
        assertEquals(2, reloaded.allPlayers.size());
    }


    @Test
    void getAllPlayersAccuraciesReturnsCorrectMap() {
        Players players = new Players();
        players.addPlayer(new Player("alice", 80, 8, 10, 3, 2));
        players.addPlayer(new Player("bob", 50, 5, 10, 4, 1));
        Map<String, Integer> acc = players.getAllPlayersAccuracies();
        assertEquals(80, acc.get("alice"));
        assertEquals(50, acc.get("bob"));
    }

    @Test
    void getAllPlayersCryptogramsPlayedReturnsCorrectMap() {
        Players players = new Players();
        players.addPlayer(new Player("alice", 80, 8, 10, 3, 2));
        players.addPlayer(new Player("bob", 50, 5, 10, 4, 1));
        Map<String, Integer> played = players.getAllPlayersCryptogramsPlayed();
        assertEquals(3, played.get("alice"));
        assertEquals(4, played.get("bob"));
    }

    @Test
    void getAllPlayersCompletedCryptosReturnsCorrectMap() {
        Players players = new Players();
        players.addPlayer(new Player("alice", 80, 8, 10, 3, 2));
        players.addPlayer(new Player("bob", 50, 5, 10, 4, 1));
        Map<String, Integer> completed = players.getAllPlayersCompletedCryptos();
        assertEquals(2, completed.get("alice"));
        assertEquals(1, completed.get("bob"));
    }

    @Test
    void statMapsAreEmptyWhenNoPlayers() {
        Players players = new Players();
        assertTrue(players.getAllPlayersAccuracies().isEmpty());
        assertTrue(players.getAllPlayersCryptogramsPlayed().isEmpty());
        assertTrue(players.getAllPlayersCompletedCryptos().isEmpty());
    }
}