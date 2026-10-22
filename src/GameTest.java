import org.junit.jupiter.api.*;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    static String TEST_PHRASES_FILE = "default_phrases.txt";

    @BeforeAll
    static void createPhrasesFile() throws IOException {
        PrintWriter writer = new PrintWriter(new File(TEST_PHRASES_FILE));
        writer.println("HELLO");
        writer.close();
    }

    @AfterAll
    static void deletePhrasesFile() {
        new File(TEST_PHRASES_FILE).delete();
    }

    @AfterEach
    void cleanupSaveFiles() {
        new File("testUser_crypto_save.txt").delete();
    }

    private Player testPlayer() {
        return new Player("testUser");
    }


    @Test
    void constructorCreatesLetterCryptogram() {
        Game<Character> game = new Game<>(testPlayer(), "letter");
        assertTrue(game.currentCryptogram instanceof LetterCryptogram);
    }

    @Test
    void constructorCreatesNumberCryptogram() {
        Game<Integer> game = new Game<>(testPlayer(), "number");
        assertTrue(game.currentCryptogram instanceof NumberCryptogram);
    }

    @Test
    void constructorInvalidTypeSetsIsValidFalse() {
        Game<?> game = new Game<>(testPlayer(), "invalid");
        assertFalse(game.isValid);
    }

    @Test
    void constructorSetsCurrentPlayer() {
        Player p = testPlayer();
        Game<Character> game = new Game<>(p, "letter");
        assertEquals(p, game.currentPlayer);
    }

    @Test
    void constructorWithPlayerOnlySetsCurrentPlayer() {
        Player p = testPlayer();
        Game<Character> game = new Game<>(p);
        assertEquals(p, game.currentPlayer);
    }


    @Test
    void enterLetterStoresGuessForLetterCryptogram() {
        Game<Character> game = new Game<>(testPlayer(), "letter");
        char firstPlain = firstPlainLetter(game);
        Character encrypted = game.currentCryptogram.getCryptoToken(firstPlain);
        game.ui = new TestUI(new String[]{encrypted.toString(), "B"}, new int[]{});
        game.enterLetter();
        assertEquals('B', game.playerGameMapping.get(encrypted));
    }

    @Test
    void enterLetterStoresGuessForNumberCryptogram() {
        Game<Integer> game = new Game<>(testPlayer(), "number");
        char firstPlain = firstPlainLetter(game);
        Integer encrypted = game.currentCryptogram.getCryptoToken(firstPlain);
        game.ui = new TestUI(new String[]{"Z"}, new int[]{encrypted});
        game.enterLetter();
        assertEquals('Z', game.playerGameMapping.get(encrypted));
    }

    @Test
    void enterLetterIncrementsTotalGuess() {
        Game<Character> game = new Game<>(testPlayer(), "letter");
        char firstPlain = firstPlainLetter(game);
        Character encrypted = game.currentCryptogram.getCryptoToken(firstPlain);
        game.ui = new TestUI(new String[]{encrypted.toString(), "B"}, new int[]{});
        int before = game.currentPlayer.getTotalGuesses();
        game.enterLetter();
        assertEquals(before + 1, game.currentPlayer.getTotalGuesses());
    }

    @Test
    void enterLetterIncrementsCorrectGuessWhenRight() {
        Game<Character> game = new Game<>(testPlayer(), "letter");
        char firstPlain = firstPlainLetter(game);
        Character encrypted = game.currentCryptogram.getCryptoToken(firstPlain);
        game.ui = new TestUI(new String[]{encrypted.toString(), String.valueOf(firstPlain)}, new int[]{});
        int before = game.currentPlayer.getCorrectGuesses();
        game.enterLetter();
        assertEquals(before + 1, game.currentPlayer.getCorrectGuesses());
    }

    @Test
    void enterLetterDoesNotIncrementCorrectGuessWhenWrong() {
        Game<Character> game = new Game<>(testPlayer(), "letter");
        char firstPlain = firstPlainLetter(game);
        Character encrypted = game.currentCryptogram.getCryptoToken(firstPlain);
        char wrongGuess = (firstPlain == 'A') ? 'B' : 'A';
        game.ui = new TestUI(new String[]{encrypted.toString(), String.valueOf(wrongGuess)}, new int[]{});
        int before = game.currentPlayer.getCorrectGuesses();
        game.enterLetter();
        assertEquals(before, game.currentPlayer.getCorrectGuesses());
    }

    @Test
    void enterLetterRejectsTokenNotInPhrase() {
        Game<Character> game = new Game<>(testPlayer(), "letter");
        Character absentToken = absentEncryptedToken(game);
        game.ui = new TestUI(new String[]{absentToken.toString()}, new int[]{});
        game.enterLetter();
        assertTrue(game.playerGameMapping.isEmpty());
    }


    @Test
    void undoLetterRemovesExistingGuess() {
        Game<Character> game = new Game<>(testPlayer(), "letter");
        game.playerGameMapping.put('A', 'X');
        game.playerGameMapping.put('B', 'Y');
        game.ui = new TestUI(new String[]{"X"}, new int[]{});
        game.undoLetter();
        assertFalse(game.playerGameMapping.containsValue('X'));
        assertTrue(game.playerGameMapping.containsValue('Y'));
    }

    @Test
    void undoLetterDoesNothingIfLetterNotFound() {
        Game<Character> game = new Game<>(testPlayer(), "letter");
        game.playerGameMapping.put('A', 'X');
        game.ui = new TestUI(new String[]{"Z"}, new int[]{});
        game.undoLetter();
        assertEquals(1, game.playerGameMapping.size());
    }

    @Test
    void undoLetterLeavesOtherGuessesIntact() {
        Game<Character> game = new Game<>(testPlayer(), "letter");
        game.playerGameMapping.put('A', 'X');
        game.playerGameMapping.put('B', 'Y');
        game.playerGameMapping.put('C', 'Z');
        game.ui = new TestUI(new String[]{"Y"}, new int[]{});
        game.undoLetter();
        assertEquals(2, game.playerGameMapping.size());
        assertTrue(game.playerGameMapping.containsValue('X'));
        assertTrue(game.playerGameMapping.containsValue('Z'));
    }


    @Test
    void showSolutionSetsSolutionedTrue() {
        Game<Character> game = new Game<>(testPlayer(), "letter");
        game.ui = new TestUI(new String[]{}, new int[]{});
        game.showSolution();
        assertTrue(game.isSolutioned());
    }

    @Test
    void showSolutionIsFalseByDefault() {
        Game<Character> game = new Game<>(testPlayer(), "letter");
        assertFalse(game.isSolutioned());
    }


    @Test
    void getHintAddsEntryToPlayerMapping() {
        Game<Character> game = new Game<>(testPlayer(), "letter");
        game.getHint();
        assertEquals(1, game.playerGameMapping.size());
    }

    @Test
    void getHintAddsCorrectMapping() {
        Game<Character> game = new Game<>(testPlayer(), "letter");
        game.getHint();
        for (Map.Entry<Character, Character> entry : game.playerGameMapping.entrySet()) {
            char plain = game.currentCryptogram.getPlainLetter(entry.getKey());
            assertEquals(plain, (char) entry.getValue());
        }
    }

    @Test
    void getHintCorrectsBadGuess() {
        Game<Character> game = new Game<>(testPlayer(), "letter");
        char firstPlain = firstPlainLetter(game);
        Character encrypted = game.currentCryptogram.getCryptoToken(firstPlain);
        char wrongGuess = (firstPlain == 'A') ? 'B' : 'A';
        game.playerGameMapping.put(encrypted, wrongGuess);
        game.getHint();
        assertEquals(firstPlain, (char) game.playerGameMapping.get(encrypted));
    }

    @Test
    void getHintDoesNotOverwriteCorrectGuess() {
        Game<Character> game = new Game<>(testPlayer(), "letter");
        char firstPlain = firstPlainLetter(game);
        Character encrypted = game.currentCryptogram.getCryptoToken(firstPlain);
        game.playerGameMapping.put(encrypted, firstPlain);

        // hint should move on to a different letter
        int sizeBefore = game.playerGameMapping.size();
        game.getHint();
        // either a new entry was added or size stayed same if all letters correct
        assertTrue(game.playerGameMapping.size() >= sizeBefore);
        assertEquals(firstPlain, (char) game.playerGameMapping.get(encrypted));
    }


    @Test
    void saveGameCreatesFile() {
        Game<Character> game = new Game<>(testPlayer(), "letter");
        game.saveGame();
        assertTrue(new File("testUser_crypto_save.txt").exists());
    }

    @Test
    void loadGameRestoresPhrase() {
        Game<Character> saveGame = new Game<>(testPlayer(), "letter");
        String phrase = saveGame.currentCryptogram.getPhrase();
        saveGame.saveGame();

        Game<Character> loadGame = new Game<>(testPlayer());
        loadGame.loadGame();
        assertEquals(phrase, loadGame.currentCryptogram.getPhrase());
    }

    @Test
    void loadGameRestoresPlayerMapping() {
        Game<Character> saveGame = new Game<>(testPlayer(), "letter");
        char firstPlain = firstPlainLetter(saveGame);
        Character encrypted = saveGame.currentCryptogram.getCryptoToken(firstPlain);
        saveGame.playerGameMapping.put(encrypted, firstPlain);
        saveGame.saveGame();

        Game<Character> loadGame = new Game<>(testPlayer());
        loadGame.loadGame();
        assertEquals(firstPlain, (char) loadGame.playerGameMapping.get(encrypted));
    }

    @Test
    void loadGameDoesNothingIfNoFileExists() {
        new File("testUser_crypto_save.txt").delete();
        Game<Character> game = new Game<>(testPlayer());
        assertDoesNotThrow(() -> game.loadGame());
        assertNull(game.currentCryptogram);
    }


    @Test
    void wipeFileDeletesSaveFile() {
        Game<Character> game = new Game<>(testPlayer(), "letter");
        game.saveGame();
        assertTrue(new File("testUser_crypto_save.txt").exists());
        game.wipeFile();
        assertFalse(new File("testUser_crypto_save.txt").exists());
    }

    @Test
    void wipeFileDoesNotThrowIfNoFile() {
        Game<Character> game = new Game<>(testPlayer(), "letter");
        new File("testUser_crypto_save.txt").delete();
        assertDoesNotThrow(() -> game.wipeFile());
    }


    @Test
    void fileCheckReturnsTrueIfFileExists() throws IOException {
        File f = new File("tempfile.txt");
        f.createNewFile();
        Game<Character> game = new Game<>(testPlayer(), "letter");
        assertTrue(game.fileCheck("tempfile.txt"));
        f.delete();
    }

    @Test
    void fileCheckReturnsFalseIfFileDoesNotExist() {
        Game<Character> game = new Game<>(testPlayer(), "letter");
        assertFalse(game.fileCheck("nonexistent_xyz.txt"));
    }


    @Test
    void topTenRunsWithoutException() {
        Game<Character> game = new Game<>(testPlayer(), "letter");
        game.ui = new TestUI(new String[]{}, new int[]{});
        assertDoesNotThrow(() -> game.topTen());
    }

    @Test
    void topTenRunsWithMultiplePlayers() {
        Game<Character> game = new Game<>(testPlayer(), "letter");
        game.allPlayers.addPlayer(new Player("alice", 80, 8, 10, 3, 5));
        game.allPlayers.addPlayer(new Player("bob", 50, 5, 10, 4, 2));
        game.ui = new TestUI(new String[]{}, new int[]{});
        assertDoesNotThrow(() -> game.topTen());
    }

    private <E> char firstPlainLetter(Game<E> game) {
        for (char c : game.currentCryptogram.getPhrase().toCharArray()) {
            if (Character.isLetter(c)) return c;
        }
        throw new RuntimeException("No letters in phrase");
    }

    private Character absentEncryptedToken(Game<Character> game) {
        for (char c : "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()) {
            try {
                char plain = game.currentCryptogram.getPlainLetter(c);
                if (game.currentCryptogram.getPhrase().indexOf(plain) < 0) return c;
            } catch (Exception ignored) {}
        }
        throw new RuntimeException("Could not find absent token");
    }
}