public class Main extends UserInterface {

    public static void main(String[] args) {
        UserInterface ui = new UserInterface();
        boolean over = false;
        boolean foundPlayer = false;
        String input;

        ui.playerHasNotDialogue();
        input = ui.getInput();


        Player tempPlayer = new Player(input);
        Game game = new Game(tempPlayer);


        if(game.allPlayers.findPlayer(tempPlayer) != null) {
            foundPlayer = true;
        }


        Player officialPlayer = game.loadPlayer();
        game.currentPlayer = officialPlayer;
        if(foundPlayer) {
            if (game.fileCheck(game.currentPlayer.getUsername() + "_crypto_save.txt")) {
                ui.printMessage("Would you like to load your previous cryptogram or play a new one? (load/new)");
                input = ui.getInput();

                if(!input.equals("load") && !input.equals("new")) {
                    while(!input.equals("load") && !input.equals("new")) {
                        ui.printMessage("Invalid input, load your last cryptogram or play a new one? (load/new)");
                        input = ui.getInput();
                    }
                }

                if (input.equalsIgnoreCase("load")) {
                    game.loadGame();
                }
            }
        }

        //cases where you want to start a new game
        if(!foundPlayer || input.equalsIgnoreCase("new") || !game.fileCheck(game.currentPlayer.getUsername() + "_crypto_save.txt")){
            ui.printMessage("What type of cryptogram would you like? (letter/number)");
            String type = ui.getInput();
            game = new Game<>(officialPlayer, type);
            game.currentPlayer.incrementCryptogramsPlayed();
        }


        if(foundPlayer) {
            game.saveGame();
            ui.printMessage("Would you like to see your stats? (Y/N):");
            input = ui.getInput();
            if (input.equalsIgnoreCase("y")) {
                ui.printMessage("Number of Cryptograms completed: " + officialPlayer.getCryptogramsCompleted());
                ui.printMessage("Total cryptograms played: " + officialPlayer.getCryptogramsPlayed());
                ui.printMessage("Your accuracy is: " + officialPlayer.getAccuracy());
            }
            else if (!input.equalsIgnoreCase("n") && !input.equalsIgnoreCase("y")) {
                ui.printMessage("Invalid input, would you like to see your stats? (Y/N):");
                input = ui.getInput();
                if (input.equalsIgnoreCase("y")) {
                    ui.printMessage("Number of Cryptograms completed: " + officialPlayer.getCryptogramsCompleted());
                    ui.printMessage("Total cryptograms played: " + officialPlayer.getCryptogramsPlayed());
                    ui.printMessage("Your accuracy is: " + officialPlayer.getAccuracy());
                }
                while (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n")) {
                    ui.printMessage("Invalid input, would you like to see your stats? (Y/N):");
                    input = ui.getInput();
                    if (input.equalsIgnoreCase("y")) {
                        ui.printMessage("Number of Cryptograms completed: " + officialPlayer.getCryptogramsCompleted());
                        ui.printMessage("Total cryptograms played: " + officialPlayer.getCryptogramsPlayed());
                        ui.printMessage("Your accuracy is: " + officialPlayer.getAccuracy());
                    }
                }
            }
        }


        while (!game.isValid) {
            ui.printMessage("Invalid type. Please enter (letter/number):");
            input = ui.getInput();
            game = new Game<>(officialPlayer, input);
        }


        while (!over) {
            game.saveGame();
            game.generateCryptogram();
            if(game.checkIfWon() || game.isSolutioned()) {
                over = true;
            }
        }

        game.allPlayers.savePlayers();

        ui.printMessage("Game Over");
    }
}