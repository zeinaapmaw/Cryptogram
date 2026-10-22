import java.io.*;
import java.util.*;


public class Game<E> {
    Cryptogram<E> currentCryptogram;
    HashMap<E, Character> playerGameMapping = new HashMap<>();
    static Players allPlayers = new Players();
    Player currentPlayer;
    boolean isValid = true;
    UserInterface ui = new UserInterface();
    String CryptoType;
    boolean solutioned = false;
    boolean quit = false;

    @SuppressWarnings("unchecked")
    public Game(Player p, String cryptType) {
        currentPlayer = p;

        if (cryptType.equalsIgnoreCase("number")) {
            currentCryptogram = (Cryptogram<E>) new NumberCryptogram();
            CryptoType = "number";
        } else if (cryptType.equalsIgnoreCase("letter")) {
            currentCryptogram = (Cryptogram<E>) new LetterCryptogram();
            CryptoType = "letter";
        } else {
            ui.printMessage("Invalid cryptogram type. Please enter a valid cryptogram type. (letter/number):");
            isValid = false;
        }
    }

    public Game(Player p) {
        currentPlayer = p;
    }

    public Player loadPlayer() {
        return allPlayers.loadOrCreateUser(currentPlayer.getUsername());
    }


    void generateCryptogram() {
        String phrase = currentCryptogram.getPhrase();

        System.out.println(phrase);
        for (int i = 0; i < phrase.length(); i++) {
            if (String.valueOf(phrase.charAt(i)).matches("[a-zA-Z]")) {
                E key = currentCryptogram.getCryptoToken(phrase.charAt(i));
                if (playerGameMapping.containsKey(key)) {
                    System.out.print(playerGameMapping.get(key) + " ");
                } else if (currentCryptogram instanceof NumberCryptogram) {
                    System.out.print("__ ");
                } else if (key.toString().matches("[WM]")) {
                    System.out.print("＿ ");
                } else {
                    System.out.print("_ ");
                }
            } else {
                System.out.print(phrase.charAt(i) + " ");
            }
        }

        System.out.println();
        for (int i = 0; i < currentCryptogram.getPhrase().length(); i++) {
            if (String.valueOf(phrase.charAt(i)).matches("[A-Z]")) {
                String cryptoToken = currentCryptogram.getCryptoToken(phrase.charAt(i)).toString();
                if (cryptoToken.length() == 1 && currentCryptogram instanceof NumberCryptogram) {
                    System.out.print("0" + cryptoToken + " ");
                } else {
                    System.out.print(cryptoToken + " ");
                }
            } else {
                System.out.print(phrase.charAt(i) + " ");
            }
        }

        System.out.println();

        ui.printMessage("Would you like to:\n" +
                "enter (e) a letter\n" +
                "undo (u) a letter\n" +
                "show solution (s)\n" +
                "view the top ten (t) scores\n" +
                "get a hint (h)\n" +
                "view the letter frequencies (f)\n" +
                "quit the game (q)");

        String input = ui.getInput();

        if (input.equalsIgnoreCase("enter") || input.equalsIgnoreCase("e")) {
            enterLetter();
        } else if (input.equalsIgnoreCase("undo") || input.equalsIgnoreCase("u")) {
            if (playerGameMapping.isEmpty()) {
                ui.printMessage("Haven't guessed, no letters to undo!");
                return;
            }
            undoLetter();
        } else if (input.equalsIgnoreCase("s")) {
            showSolution();
        } else if (input.equalsIgnoreCase("h")) {
            getHint();
        } else if (input.equalsIgnoreCase("t")) {
            topTen();
        } else if (input.equalsIgnoreCase("f")) {
            viewFrequencies();
        } else if (input.equalsIgnoreCase("q")) {
            quit = true;
        } else {
            ui.printMessage("Invalid input...");
        }
    }


    void enterLetter() {
        ui.printMessage("Enter the encrypted letter/number you want to guess: ");


        E cryptoToken;
        String temp = ui.getInput();
        if (currentCryptogram instanceof LetterCryptogram && temp.matches("[a-zA-Z]")) {
            cryptoToken = (E) (Character) (Character.toUpperCase(temp.charAt(0)));
        } else if (currentCryptogram instanceof NumberCryptogram && isInteger(temp)) {
            cryptoToken = (E) (Integer) (Integer.parseInt(temp));
        } else {
            ui.printMessage("Invalid input.");
            return;
        }

        boolean found = false;
        for (int i = 0; i < currentCryptogram.getPhrase().length(); i++) {
            if (currentCryptogram.getPhrase().charAt(i) == currentCryptogram.getPlainLetter(cryptoToken)) {
                found = true;
                break;
            }
        }

        if (!found) {
            ui.printMessage("encrypted letter/number doesn't exist in current cryptogram!");
            return;
        }

        ui.printMessage("Enter the letter you want to guess: ");
        Character userGuess = Character.toUpperCase(ui.getInput().charAt(0));


        //if the letter is the correct letter, increment the correct guesses
        if (userGuess.toString().equalsIgnoreCase(String.valueOf(currentCryptogram.getPlainLetter(cryptoToken)))) {
            currentPlayer.incrementCorrectGuess();
        }

        currentPlayer.incrementTotalGuess();
        playerGameMapping.put(cryptoToken, userGuess);
    }


    void undoLetter() {
        ui.printMessage("Enter the letter you want to remove: ");
        char charInput = Character.toUpperCase(ui.getInput().charAt(0));
        E valueKey = null;
        for (Map.Entry<E, Character> entry : playerGameMapping.entrySet()) {
            if (entry.getValue() == charInput) {
                valueKey = entry.getKey();
            }
        }
        if (valueKey != null) {
            playerGameMapping.remove(valueKey);
        } else {
            ui.printMessage("letter doesn't exist!");
        }
    }


    public boolean checkIfWon() {
        String input;
        String phrase = currentCryptogram.getPhrase();

        //update accuracy
        currentPlayer.updateAccuracy();
        if (!solutioned) {

            if (quit) {
                return true;
            }


        }

        Set<Character> uniqueLetters = new HashSet<>();
        String upperPhrase = phrase.toUpperCase();

        //this works as hashsets will eliminate duplicates
        for (int i = 0; i < upperPhrase.length(); i++) {
            char c = upperPhrase.charAt(i);
            if (Character.isLetter(c)) {
                uniqueLetters.add(c);
            }
        }


        if (uniqueLetters.size() == playerGameMapping.size()) {
            //this covers the case if every letter has been guessed and wanna check if there is a wrong letter
            for (int i = 0; i < phrase.length(); i++) {
                char plainLetter = phrase.charAt(i);

                if (Character.isLetter(plainLetter)) {
                    E key = (E) currentCryptogram.getCryptoToken(plainLetter);
                    Character userGuess = playerGameMapping.get(key);


                    if (!userGuess.toString().equalsIgnoreCase(String.valueOf(plainLetter))) {
                        ui.printMessage("the guesses are wrong, try again!");
                        return false;
                    }
                }
            }
        } else {
            //this covers the case for if the cryptogram is unfinished
            for (int i = 0; i < phrase.length(); i++) {
                char plainLetter = phrase.charAt(i);


                // We only care about alphabetic characters
                if (Character.isLetter(plainLetter)) {
                    E key = (E) currentCryptogram.getCryptoToken(plainLetter);
                    Character userGuess = playerGameMapping.get(key);

                    if (userGuess == null) {
                        return false;
                    }

                }
            }
        }

        // If we looped through the whole phrase and never returned false, they won!
        ui.printMessage("Congratulations! You solved it!");
        currentPlayer.incrementCryptogramsCompleted();
        wipeFile();
        return true;
    }


    public void saveGame() {
        File file = new File(currentPlayer.getUsername() + "_crypto_save.txt");
        if (file.exists()) {
            file.delete();
        }

        try {
            if (!file.exists()) file.createNewFile();

            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println(CryptoType);
                writer.println(currentCryptogram.getPhrase());

                for (Map.Entry<E, Character> entry : currentCryptogram.getCryptogramAlphabet().entrySet()) {
                    writer.println(entry.getKey() + " " + entry.getValue());
                }

                writer.println(":");

                for (Map.Entry<E, Character> entry : playerGameMapping.entrySet()) {
                    writer.println(entry.getKey() + " " + entry.getValue());
                }

                //System.out.println("Game saved.");
            }

        } catch (Exception e) {
            System.out.println("Failed to save game.");
            e.printStackTrace();
        }
    }


    public void loadGame() {
        File file = new File(currentPlayer.getUsername() + "_crypto_save.txt");

        try {
            Scanner buffer = new Scanner(new FileReader(file));

            try {
                String type = buffer.nextLine();
                String phrase = buffer.nextLine();

                HashMap<E, Character> cryptogramAlphabet = new HashMap<>();
                HashMap<E, Character> loadedPlayerMapping = new HashMap<>();

                while (buffer.hasNextLine()) {
                    String line = buffer.nextLine();
                    if (line.equals(":")) break;

                    String[] parts = line.split(" ");
                    E key = parseKey(parts[0], type);
                    Character value = parts[1].charAt(0);
                    cryptogramAlphabet.put(key, value);
                }

                while (buffer.hasNextLine()) {
                    String line = buffer.nextLine();
                    String[] parts = line.split(" ");
                    E key = parseKey(parts[0], type);
                    Character value = parts[1].charAt(0);
                    loadedPlayerMapping.put(key, value);
                }

                if (type.equalsIgnoreCase("letter")) {
                    this.currentCryptogram = (Cryptogram<E>)
                            new LetterCryptogram(phrase, (HashMap<Character, Character>) cryptogramAlphabet);
                    CryptoType = "letter";
                } else {
                    this.currentCryptogram = (Cryptogram<E>)
                            new NumberCryptogram(phrase, (HashMap<Integer, Character>) cryptogramAlphabet);
                    CryptoType = "number";
                }

                this.playerGameMapping = loadedPlayerMapping;
                System.out.println("Game loaded.");
            } catch (Exception e) {
                throw new IllegalArgumentException("Cryptogram save file is corrupted.");
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("The cryptogram save file doesn't exist!");
        }
    }

    @SuppressWarnings("unchecked")
    private E parseKey(String input, String type) {
        if (type.equalsIgnoreCase("letter")) {
            return (E) (Character) input.charAt(0);
        } else {
            return (E) (Integer) Integer.parseInt(input);
        }
    }

    public void wipeFile() {
        File file = new File(currentPlayer.getUsername() + "_crypto_save.txt");
        if (file.exists()) {
            file.delete();
        }
        else {
            System.out.println("No file to wipe.");
        }
    }

    public boolean fileCheck(String pathName) {
        File file = new File(pathName);
        return file.exists();
    }


    public void showSolution(){
        ui.printMessage("Solution: " + currentCryptogram.getPhrase());
        solutioned = true;
        wipeFile();
    }

    public boolean isSolutioned() {
        return solutioned;
    }


    public void topTen(){
        Map<String, Integer> compCrypt = allPlayers.getAllPlayersCompletedCryptos();

        ui.printMessage("Top 10 Cryptogram Players");

        compCrypt.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> {
                    ui.printMessage(entry.getKey() + ": Cryptograms Completed: " + entry.getValue());
                });
        ui.printMessage("");
    }

    void getHint() {
        String phrase = currentCryptogram.getPhrase();
        int i = 0;

        while(i < phrase.length()) {
            char plainLetter = phrase.charAt(i);
            E cryptoToken = currentCryptogram.getCryptoToken(plainLetter);

            if(playerGameMapping.containsKey(cryptoToken)) {
                Character letterGuessed = playerGameMapping.get(cryptoToken);

                if(!letterGuessed.equals(plainLetter)) {
                    playerGameMapping.put(cryptoToken, plainLetter);
                    break;
                }
            }
            else {
                playerGameMapping.put(cryptoToken, plainLetter);
                break;
            }
            i++;
        }

    }

    public void viewFrequencies() {
        Map<Character, Long> plainLetterFrequencies = currentCryptogram.getFrequencies();
        for (Map.Entry<Character, Long> entry : plainLetterFrequencies.entrySet()) {
            double frequency = (double) entry.getValue() / currentCryptogram.getPhrase().length() * 100;
            System.out.println(currentCryptogram.getCryptoToken(entry.getKey()) + " -> " + frequency + "%");
        }

        ui.printMessage("Would you like to view common letter frequencies? (Y/N)");
        String input = ui.getInput();
        if(input.equalsIgnoreCase("y")) {
            ui.printMessage("");
            ui.printMessage("Common letter frequencies in phrases: ");
            ui.printMessage("E → 12.7%\n" +
                    "T → 9.1%\n" +
                    "A → 8.2%\n" +
                    "O → 7.5%\n" +
                    "I → 7.0%\n" +
                    "N → 6.7%\n" +
                    "S → 6.3%\n" +
                    "H → 6.1%\n" +
                    "R → 6.0%\n" +
                    "D → 4.3%\n" +
                    "L → 4.0%\n" +
                    "C → 2.8%\n" +
                    "U → 2.8%\n" +
                    "M → 2.4%\n" +
                    "W → 2.4%\n" +
                    "F → 2.2%\n" +
                    "G → 2.0%\n" +
                    "Y → 2.0%\n" +
                    "P → 1.9%\n" +
                    "B → 1.5%\n" +
                    "V → 1.0%\n" +
                    "K → 0.8%\n" +
                    "J → 0.15%\n" +
                    "X → 0.15%\n" +
                    "Q → 0.10%\n" +
                    "Z → 0.07%");
            ui.printMessage("");
        }
        else if(!input.equalsIgnoreCase("n")) {
            ui.printMessage("");
            ui.printMessage("Invalid input.");
        }
    }
    boolean isInteger(String str) {
        try{
            Integer.parseInt(str);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
}



