import java.util.*;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public abstract class Cryptogram<E> {
    private String phrase;
    private HashMap<E, Character> cryptogramAlphabet;

    public Cryptogram(String phrase) {
        this.phrase = phrase;
    }

    public void setCryptogramAlphabet(HashMap<E, Character> cryptogramAlphabet) {
        this.cryptogramAlphabet = cryptogramAlphabet;
    }

    public Map<Character, Long> getFrequencies() {
        Map<Character, Long> plainLetterFrequencies =  phrase.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return plainLetterFrequencies;
    }


    public ArrayList<Integer> getRandIndex(int len) {
        ArrayList<Integer> nums = new ArrayList<>();

        for(int i = 0; i < len; i++) nums.add(i);
        Collections.shuffle(nums);
        return nums;
    }

    public String getAlphabet() {return "ABCDEFGHIJKLMNOPQRSTUVWXYZ";}

    public String getPhrase() {return phrase;}
    public HashMap<E, Character> getCryptogramAlphabet() {return cryptogramAlphabet;}
    public abstract char getPlainLetter(E cryptoToken);
    public abstract E getCryptoToken(char plainLetter);
}
