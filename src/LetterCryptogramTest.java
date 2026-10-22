import org.junit.jupiter.api.*;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class LetterCryptogramTest {

    static String TEST_FILE = "test_phrases.txt";

    @BeforeAll
    static void createTestFile() throws IOException {
        PrintWriter writer = new PrintWriter(new File(TEST_FILE));
        writer.println("HELLO");
        writer.println("WORLD");
        writer.close();
    }

    @AfterAll
    static void deleteTestFile() {
        new File(TEST_FILE).delete();
    }

    @Test
    void constructorLoadsPhrase() {
        LetterCryptogram lc = new LetterCryptogram(TEST_FILE);
        String phrase = lc.getPhrase();
        assertTrue(phrase.equals("HELLO") || phrase.equals("WORLD"));
    }

    @Test
    void constructorWithPhraseAndMapStoresCorrectly() {
        HashMap<Character, Character> map = new HashMap<>();
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), (char) ('A' + (25 - i))); // reverse alphabet
        }
        LetterCryptogram lc = new LetterCryptogram("HELLO", map);
        assertEquals("HELLO", lc.getPhrase());
        assertEquals(map, lc.getCryptogramAlphabet());
    }

    @Test
    void generateLetterMapContains26Entries() {
        LetterCryptogram lc = new LetterCryptogram(TEST_FILE);
        HashMap<Character, Character> map = lc.getCryptogramAlphabet();
        assertEquals(26, map.size());
    }

    @Test
    void generateLetterMapKeysAreAllUppercaseLetters() {
        LetterCryptogram lc = new LetterCryptogram(TEST_FILE);
        for (char key : lc.getCryptogramAlphabet().keySet()) {
            assertTrue(key >= 'A' && key <= 'Z');
        }
    }

    @Test
    void generateLetterMapValuesAreAllUppercaseLetters() {
        LetterCryptogram lc = new LetterCryptogram(TEST_FILE);
        for (char value : lc.getCryptogramAlphabet().values()) {
            assertTrue(value >= 'A' && value <= 'Z');
        }
    }

    @Test
    void getPlainLetterReturnsCorrectLetter() {
        LetterCryptogram lc = new LetterCryptogram(TEST_FILE);
        Map.Entry<Character, Character> entry = lc.getCryptogramAlphabet().entrySet().iterator().next();
        char cryptoLetter = entry.getKey();
        char plainLetter = entry.getValue();
        assertEquals(plainLetter, lc.getPlainLetter(cryptoLetter));
    }

    @Test
    void getCryptoTokenReturnsCorrectToken() {
        LetterCryptogram lc = new LetterCryptogram(TEST_FILE);
        Map.Entry<Character, Character> entry = lc.getCryptogramAlphabet().entrySet().iterator().next();
        char cryptoLetter = entry.getKey();
        char plainLetter = entry.getValue();
        assertEquals(cryptoLetter, lc.getCryptoToken(plainLetter));
    }

    @Test
    void getPlainLetterAndCryptoTokenAreInverses() {
        LetterCryptogram lc = new LetterCryptogram(TEST_FILE);
        for (Map.Entry<Character, Character> entry : lc.getCryptogramAlphabet().entrySet()) {
            char crypto = entry.getKey();
            char plain = entry.getValue();
            assertEquals(plain, lc.getPlainLetter(crypto));
            assertEquals(crypto, lc.getCryptoToken(plain));
        }
    }

    @Test
    void getPlainLetterThrowsForInvalidInput() {
        LetterCryptogram lc = new LetterCryptogram(TEST_FILE);
        assertThrows(IllegalArgumentException.class, () -> lc.getPlainLetter(','));
    }

    @Test
    void getCryptoTokenThrowsForInvalidInput() {
        LetterCryptogram lc = new LetterCryptogram(TEST_FILE);
        assertThrows(IllegalArgumentException.class, () -> lc.getCryptoToken('#'));
    }

    @Test
    void constructorThrowsIfFileNotFound() {
        assertThrows(RuntimeException.class, () -> new LetterCryptogram("nonexistent_file.txt"));
    }

    @Test
    void constructorThrowsIfFileIsEmpty() throws IOException {
        String emptyFile = "empty_test.txt";
        new PrintWriter(new File(emptyFile)).close();
        try {
            assertThrows(RuntimeException.class, () -> new LetterCryptogram(emptyFile));
        } finally {
            new File(emptyFile).delete();
        }
    }
}