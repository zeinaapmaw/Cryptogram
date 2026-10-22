import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;

public class NumberCryptogram extends Cryptogram<Integer> {
    private HashMap<Integer, Character> cryptogramAlphabet = new HashMap<>();

    public NumberCryptogram() {
        this("default_phrases.txt");
    }

    public NumberCryptogram(String file) {
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
        setCryptogramAlphabet(generateNumMap());
    }

    public NumberCryptogram(String phrase, HashMap<Integer, Character> cryptogramAlphabet) {
        super(phrase);
        setCryptogramAlphabet(cryptogramAlphabet);
        this.cryptogramAlphabet = cryptogramAlphabet;
    }

   public HashMap<Integer, Character> generateNumMap() {
       int len = getAlphabet().length();
       ArrayList<Integer> rng = getRandIndex(len);
       for(int i = 0; i < len; i++) {
           cryptogramAlphabet.put(rng.get(i) + 1,getAlphabet().charAt(i));  // HashMap<EncryptedNumber + 1, PlainLetter>
       }
       return cryptogramAlphabet;
   }

    @Override
    public char getPlainLetter(Integer cryptoValue) {
        try {
            return cryptogramAlphabet.get(cryptoValue);
        } catch (Exception e) {
            throw new IllegalArgumentException("No letter found for crypto value: " + cryptoValue);
        }
    }

    @Override
    public Integer getCryptoToken(char plainLetter) {
        for(Entry<Integer, Character> entry: cryptogramAlphabet.entrySet()) {
            if(entry.getValue() == plainLetter) {return entry.getKey();}
        }
        throw new IllegalArgumentException("No crypto value found for letter: " + plainLetter);
    }
}


