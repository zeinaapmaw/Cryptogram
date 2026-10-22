import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    // ── constructors ─────────────────────────────────────────────

    @Test
    void defaultConstructorSetsUsername() {
        Player p = new Player("alice");
        assertEquals("alice", p.getUsername());
    }

    @Test
    void defaultConstructorSetsAllStatsToZero() {
        Player p = new Player("alice");
        assertEquals(0, p.getAccuracy());
        assertEquals(0, p.getCorrectGuesses());
        assertEquals(0, p.getTotalGuesses());
        assertEquals(0, p.getCryptogramsPlayed());
        assertEquals(0, p.getCryptogramsCompleted());
    }

    @Test
    void fullConstructorSetsAllFields() {
        Player p = new Player("alice", 80, 8, 10, 3, 2);
        assertEquals("alice", p.getUsername());
        assertEquals(80, p.getAccuracy());
        assertEquals(8, p.getCorrectGuesses());
        assertEquals(10, p.getTotalGuesses());
        assertEquals(3, p.getCryptogramsPlayed());
        assertEquals(2, p.getCryptogramsCompleted());
    }

    // ── incrementers ─────────────────────────────────────────────

    @Test
    void incrementCryptogramsCompletedIncreasesBy1() {
        Player p = new Player("alice");
        p.incrementCryptogramsCompleted();
        assertEquals(1, p.getCryptogramsCompleted());
    }

    @Test
    void incrementCryptogramsPlayedIncreasesBy1() {
        Player p = new Player("alice");
        p.incrementCryptogramsPlayed();
        assertEquals(1, p.getCryptogramsPlayed());
    }

    @Test
    void incrementCorrectGuessIncreasesBy1() {
        Player p = new Player("alice");
        p.incrementCorrectGuess();
        assertEquals(1, p.getCorrectGuesses());
    }

    @Test
    void incrementTotalGuessIncreasesBy1() {
        Player p = new Player("alice");
        p.incrementTotalGuess();
        assertEquals(1, p.getTotalGuesses());
    }

    @Test
    void multipleIncrementsAccumulate() {
        Player p = new Player("alice");
        p.incrementCryptogramsPlayed();
        p.incrementCryptogramsPlayed();
        p.incrementCryptogramsPlayed();
        assertEquals(3, p.getCryptogramsPlayed());
    }

    // ── updateAccuracy ───────────────────────────────────────────

    @Test
    void updateAccuracyCalculatesCorrectly() {
        Player p = new Player("alice");
        p.incrementCorrectGuess();
        p.incrementCorrectGuess();
        p.incrementCorrectGuess();
        p.incrementTotalGuess();
        p.incrementTotalGuess();
        p.incrementTotalGuess();
        p.incrementTotalGuess();
        p.updateAccuracy();
        assertEquals(75, p.getAccuracy());
    }

    @Test
    void updateAccuracyIs100WhenAllCorrect() {
        Player p = new Player("alice");
        p.incrementCorrectGuess();
        p.incrementTotalGuess();
        p.updateAccuracy();
        assertEquals(100, p.getAccuracy());
    }

    @Test
    void updateAccuracyIs0WhenNoCorrectGuesses() {
        Player p = new Player("alice");
        p.incrementTotalGuess();
        p.incrementTotalGuess();
        p.updateAccuracy();
        assertEquals(0, p.getAccuracy());
    }

    @Test
    void updateAccuracyIs0WhenNoGuessesMade() {
        Player p = new Player("alice");
        p.updateAccuracy();
        assertEquals(0, p.getAccuracy());
    }

    @Test
    void updateAccuracyUpdatesAfterMoreGuesses() {
        Player p = new Player("alice");
        p.incrementCorrectGuess();
        p.incrementTotalGuess();
        p.updateAccuracy();
        assertEquals(100, p.getAccuracy());

        p.incrementTotalGuess();
        p.updateAccuracy();
        assertEquals(50, p.getAccuracy());
    }
}