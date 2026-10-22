import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class LetterCryptogram extends Cryptogram<Character> {
    private HashMap<Character, Character> cryptogramAlphabet = new HashMap<>();

    public LetterCryptogram(){
        this("default_phrases.txt");
    }

    public LetterCryptogram(String file) {
        ArrayList<String> tempPhrases = new ArrayList<>();
        try (Scanner reader = new Scanner(new File(file))) {
            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                if (!line.isEmpty()) {
                    tempPhrases.add(line.toUpperCase());
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + file, e);
        }

        if (tempPhrases.isEmpty()) {
            throw new RuntimeException("File is empty: " + file);
        }

        Random r = new Random();
        String phrase = tempPhrases.get(r.nextInt(tempPhrases.size()));

        super(phrase);
        setCryptogramAlphabet(generateLetterMap());
    }

    public LetterCryptogram(String phrase, HashMap<Character, Character> cryptogramAlphabet) {
        super(phrase);
        setCryptogramAlphabet(cryptogramAlphabet);
        this.cryptogramAlphabet = cryptogramAlphabet;
    }

    public HashMap<Character, Character> generateLetterMap() {
        int len = getAlphabet().length();
        ArrayList<Integer> rng = getRandIndex(len);
        for(int i = 0; i < len; i++) {
            cryptogramAlphabet.put(getAlphabet().charAt(rng.get(i)),getAlphabet().charAt(i));       // HashMap<EncryptedLetter(random index), PlainLetter>
        }
        return cryptogramAlphabet;
    }

    @Override
    public char getPlainLetter(Character cryptoLetter) {
        try {
            return cryptogramAlphabet.get(cryptoLetter);
        } catch (Exception e) {
            throw new IllegalArgumentException("No letter found for crypto letter: " + cryptoLetter);
        }
    }

    @Override
    public Character getCryptoToken(char plainLetter) {
        for(Map.Entry<Character, Character> entry: cryptogramAlphabet.entrySet()) {
            if(entry.getValue() == plainLetter) {return entry.getKey();}
        }
        throw new IllegalArgumentException("No crypto letter found for letter: " + plainLetter);
    }
}
