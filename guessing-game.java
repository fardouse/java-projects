// A guessing game that asks the user to guess a random number
// between 1 and constant MAX_VALUE. The user is given hints about
// whether the number is higher or lower based on the guess. 

import java.util.*;

public class GuessingGame {
// constant used for maximum value of a secret number
public static final int MAX_VALUE = 100;
public static void main(String[] args) {
    Scanner console = new Scanner(System.in);
    Random r = new Random();
    
    int games = 0;
    int bestGame = 1000000;
    int tryGuess = 0;
    int overallGuesses = 0;
    boolean endGame = false;
    String playAgain = "";
    
    haikuIntro();
    
    // this loop generates multiple games until 
    // the user decides to stop playing
    while (endGame == false) {
        tryGuess = playGame(console, r, overallGuesses);
        overallGuesses += tryGuess; 

        if (tryGuess < bestGame) {
            bestGame = tryGuess;
        }         
        games++;
        
        System.out.print("Do you want to play again? ");
        playAgain = console.next();                         
        if (playAgain.startsWith("y") || playAgain.startsWith("Y")) {
            endGame = false;
            System.out.println();
        } else {
            endGame = true;
        }
    }
    gameStats(games, overallGuesses, bestGame);  
}

// prints out haiku program introduction 
public static void haikuIntro() { 
    System.out.println("Can you read my mind?");
    System.out.println("My thoughts are never ending");
    System.out.println("Try to take a guess");
    System.out.println();
}

// Generates a single game, continuously asks user to guess a number and
// gives hints based on input until correct secret number is guessed.
// Returns number of guesses taken to guess secret number.
// Scanner console: scanner used to retrieve user input
// Random: random object used to generate a random secret number
// overallGuesses: number of guesses taken to guess correctly
public static int playGame(Scanner console, Random r, int overallGuesses) {
    System.out.println("I'm thinking of a number between 1 and " + (MAX_VALUE) + "...");     
    int randomNumber = r.nextInt(MAX_VALUE) + 1;
    boolean endGame = false;
    int guesses = 0;
    
    // loop continues until secret number is guessed
    while (endGame == false) {
        System.out.print("Your guess? ");
        int guess = console.nextInt();
        guesses++;
        overallGuesses += guesses;
        
        if (guess > randomNumber) {
            System.out.println("It's lower.");            
        } else if (guess < randomNumber) {
            System.out.println("It's higher.");
        } 
        if (guess == randomNumber) {
            endGame = true;
        }            
    } 
    if (guesses > 1) {
        System.out.println("You got it right in " + guesses + " guesses!");
    } else {
        System.out.println("You got it right in " + guesses + " guess!");
    }
    return guesses;      
}

// Prints out game statistics after player decides to end the game.
// Prints out total # of games, guesses, and average guesses per game
// int games: number of games the user played
// int overallGuesses: total guesses the user took to guess correctly
// int bestGame: least number of guesses used to win a game
public static void gameStats(int games, int overallGuesses, int bestGame) {
    System.out.println();
    System.out.println("Overall results:");
    System.out.println("Total games   = " + games);
    System.out.println("Total guesses = " + overallGuesses);
    System.out.println("Guesses/game  = " + round1((double)overallGuesses/games));
    System.out.println("Best game     = " + bestGame);
}

// Rounds the given value to one decimal place and returns the result.
// double num: the number to round   
public static double round1(double num) {
    return Math.round(num * 10.0) / 10.0;
}
}