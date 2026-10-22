import org.junit.jupiter.api.*;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class NumberCryptogramTest {

    static String TEST_FILE = "test_phrases.txt";

    @BeforeAll
    static void createTestFile() throws IOException {
        PrintWriter writer = new PrintWriter(new File(TEST_FILE));
        writer.println("HELLO");
        writer.println("WORLD");
        writer.println("HELLO WORLD");
        writer.close();
    }

    @AfterAll
    static void deleteTestFile() {
        new File(TEST_FILE).delete();
    }

    @Test
    void constructorLoadsPhrase() {
        NumberCryptogram nc = new NumberCryptogram(TEST_FILE);
        String phrase = nc.getPhrase();
        assertTrue(phrase.equals("HELLO") || phrase.equals("WORLD") || phrase.equals("HELLO WORLD"));
    }

    @Test
    void constructorWithPhraseAndMapStoresCorrectly() {
        HashMap<Integer, Character> map = new HashMap<>();
        for (int i = 0; i < 26; i++) {
            map.put(i + 1, (char) ('A' + i));
        }
        NumberCryptogram nc = new NumberCryptogram("HELLO", map);
        assertEquals("HELLO", nc.getPhrase());
        assertEquals(map, nc.getCryptogramAlphabet());
    }

    @Test
    void generateNumMapContains26Entries() {
        NumberCryptogram nc = new NumberCryptogram(TEST_FILE);
        assertEquals(26, nc.getCryptogramAlphabet().size());
    }

    @Test
    void generateNumMapKeysAreBetween1And26() {
        NumberCryptogram nc = new NumberCryptogram(TEST_FILE);
        for (int key : nc.getCryptogramAlphabet().keySet()) {
            assertTrue(key >= 1 && key <= 26);
        }
    }

    @Test
    void generateNumMapValuesAreAllUppercaseLetters() {
        NumberCryptogram nc = new NumberCryptogram(TEST_FILE);
        for (char value : nc.getCryptogramAlphabet().values()) {
            assertTrue(value >= 'A' && value <= 'Z');
        }
    }

    @Test
    void generateNumMapKeysAreUnique() {
        NumberCryptogram nc = new NumberCryptogram(TEST_FILE);
        Set<Integer> keys = nc.getCryptogramAlphabet().keySet();
        assertEquals(26, keys.size());
    }

    @Test
    void generateNumMapValuesAreUnique() {
        NumberCryptogram nc = new NumberCryptogram(TEST_FILE);
        Set<Character> values = new HashSet<>(nc.getCryptogramAlphabet().values());
        assertEquals(26, values.size());
    }

    @Test
    void getPlainLetterReturnsCorrectLetter() {
        NumberCryptogram nc = new NumberCryptogram(TEST_FILE);
        Map.Entry<Integer, Character> entry = nc.getCryptogramAlphabet().entrySet().iterator().next();
        int cryptoValue = entry.getKey();
        char plainLetter = entry.getValue();
        assertEquals(plainLetter, nc.getPlainLetter(cryptoValue));
    }

    @Test
    void getCryptoTokenReturnsCorrectToken() {
        NumberCryptogram nc = new NumberCryptogram(TEST_FILE);
        Map.Entry<Integer, Character> entry = nc.getCryptogramAlphabet().entrySet().iterator().next();
        int cryptoValue = entry.getKey();
        char plainLetter = entry.getValue();
        assertEquals(cryptoValue, nc.getCryptoToken(plainLetter));
    }

    @Test
    void getPlainLetterAndCryptoTokenAreInverses() {
        NumberCryptogram nc = new NumberCryptogram(TEST_FILE);
        for (Map.Entry<Integer, Character> entry : nc.getCryptogramAlphabet().entrySet()) {
            int crypto = entry.getKey();
            char plain = entry.getValue();
            assertEquals(plain, nc.getPlainLetter(crypto));
            assertEquals(crypto, nc.getCryptoToken(plain));
        }
    }

    @Test
    void getPlainLetterThrowsForInvalidValue() {
        NumberCryptogram nc = new NumberCryptogram(TEST_FILE);
        assertThrows(IllegalArgumentException.class, () -> nc.getPlainLetter(999));
    }

    @Test
    void getCryptoTokenThrowsForInvalidLetter() {
        NumberCryptogram nc = new NumberCryptogram(TEST_FILE);
        assertThrows(IllegalArgumentException.class, () -> nc.getCryptoToken('#'));
    }

    @Test
    void constructorThrowsIfFileNotFound() {
        assertThrows(RuntimeException.class, () -> new NumberCryptogram("nonexistent_file.txt"));
    }

    @Test
    void constructorThrowsIfFileIsEmpty() throws IOException {
        String emptyFile = "empty_test.txt";
        new PrintWriter(new File(emptyFile)).close();
        try {
            assertThrows(RuntimeException.class, () -> new NumberCryptogram(emptyFile));
        } finally {
            new File(emptyFile).delete();
        }
    }
}