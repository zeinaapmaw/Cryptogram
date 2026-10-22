import java.util.Scanner;

public class UserInterface {

    public String getInput() {
        Scanner input = new Scanner(System.in);
        return input.nextLine();
    }

    public int getInt() {
        Scanner input = new Scanner(System.in);
        return input.nextInt();
    }


    public void printMessage(String input) {
        System.out.println(input);
    }

    public void initialDialogue() {
        System.out.println("Welcome to our cryptogram game!");
        System.out.println("Do you have an account already? (yes/no)");
        System.out.println("Enter: ");
    }

    public void playerHasDialogue(String name) {
        System.out.println("Welcome back " + name);
    }

    public void playerHasNotDialogue() {
        System.out.println("Please enter a username: ");
    }

    public void accountTakenDialogue() {
        System.out.println("That username is taken!");
        System.out.println("Please enter another username: ");
    }

}