import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class Players {

    public ArrayList<Player> allPlayers;
    public String playersFile;
    UserInterface ui = new UserInterface();

    public Players() {
        allPlayers = new ArrayList<>();
        playersFile = "AllPlayersStats.txt";
        Scanner sc;
        Path path = Paths.get(playersFile);

        try {
            if (Files.notExists(path)) {
                Files.createFile(path);
                ui.printMessage("file doesn't exist, a new one has been made");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            sc = new Scanner(new File(playersFile));
            while (sc.hasNext()) {
                String username = sc.nextLine();
                int accuracy = sc.nextInt();
                int correctGuesses = sc.nextInt();
                int totalGuesses = sc.nextInt();
                int cryptogramsPlayed = sc.nextInt();
                int cryptogramsCompleted = sc.nextInt();
                sc.nextLine();                  //moves to next player

                Player p = new Player(username, accuracy, correctGuesses, totalGuesses, cryptogramsPlayed, cryptogramsCompleted);
                addPlayer(p);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Player findPlayer(Player p){
        return allPlayers.stream()
                .filter(cPlayer -> cPlayer.getUsername().equals(p.getUsername()))
                .findFirst()
                .orElse(null);
    }

    public void addPlayer(Player p){allPlayers.add(p);}


    public void savePlayers(){
        System.out.println("Saving " + allPlayers.size() + " players...");
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(playersFile));
            for (Player p : allPlayers) {
                writer.println(p.getUsername());
                writer.println(p.getAccuracy());
                writer.println(p.getCorrectGuesses());
                writer.println(p.getTotalGuesses());
                writer.println(p.getCryptogramsPlayed());
                writer.println(p.getCryptogramsCompleted());
            }
            writer.close();
            System.out.println("Saved Successfully");
        } catch (Exception e) {
            System.out.println("Failed to save");
        }
    }

    public Player loadOrCreateUser(String username) {
        Player exists = findPlayer(new Player(username));
        if (exists != null) {
            return exists;
        }
        Player newPlayer = new Player(username);
        addPlayer(newPlayer);
        System.out.println("New player created: " + username);
        return newPlayer;
    }

    public Map<String, Integer> getAllPlayersAccuracies(){
        Map<String, Integer> allAcc = new HashMap<>();
        for (Player p : allPlayers) {
            allAcc.put(p.getUsername(), p.getAccuracy());
        }
        return allAcc;
    }

    public Map<String, Integer> getAllPlayersCryptogramsPlayed(){
        Map<String, Integer> allCryptPly = new HashMap<>();
        for (Player p : allPlayers) {
            allCryptPly.put(p.getUsername(), p.getCryptogramsPlayed());
        }
        return allCryptPly;
    }

    public Map<String, Integer> getAllPlayersCompletedCryptos(){
        Map<String, Integer> allCryptComp = new HashMap<>();
        for (Player p : allPlayers) {
            allCryptComp.put(p.getUsername(), p.getCryptogramsCompleted());
        }
        return allCryptComp;
    }

}
