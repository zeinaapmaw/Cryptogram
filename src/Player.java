public class Player {

    private String username;
    private int accuracy;
    private int correctGuesses;
    private int totalGuesses;
    private int cryptogramsPlayed;
    private int cryptogramsCompleted;

    public Player(String username){
        this.username = username;
        this.accuracy = 0;
        this.correctGuesses = 0;
        this.totalGuesses = 0;
        this.cryptogramsCompleted = 0;
        this.cryptogramsPlayed = 0;
    }

    public Player(String username, int accuracy, int correctGuesses, int totalGuesses, int cryptogramsPlayed, int cryptogramsCompleted) {
        this.username = username;
        this.accuracy = accuracy;
        this.correctGuesses = correctGuesses;
        this.totalGuesses = totalGuesses;
        this.cryptogramsCompleted = cryptogramsCompleted;
        this.cryptogramsPlayed = cryptogramsPlayed;
    }



    public void updateAccuracy(){
        if(totalGuesses == 0) {
            accuracy = 0;
            return;
        }
        accuracy = (correctGuesses * 100/ totalGuesses);
    }

    public void incrementCryptogramsCompleted() {cryptogramsCompleted++;}

    public void incrementCryptogramsPlayed() {
        cryptogramsPlayed++;
    }

    public void incrementCorrectGuess() {correctGuesses++;}

    public void incrementTotalGuess() {totalGuesses++;}

    public int getAccuracy() {
        return accuracy;
    }

    public int getCryptogramsCompleted() {
        return cryptogramsCompleted;
    }

    public int getCryptogramsPlayed() {
        return cryptogramsPlayed;
    }

    public int getCorrectGuesses() {return correctGuesses;}

    public int getTotalGuesses() {return totalGuesses;}

    public String getUsername(){
        return this.username;
    }
}
