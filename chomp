// this class represents a game of Chomp that implements
// the AbstractStrategyGame interface 

import java.util.*;

public class Chomp implements AbstractStrategyGame {
    private String[][][] gameboard;
    private int player;
    private int winner;
    private boolean gameover;
    private int numLayers;

    // contructs a new game of Chomp
    public Chomp() {
        player = 1;
        winner = -1;
        gameover = false;
        
        gameboard = new String[4][5];
        for (int i = 0; i < gameboard.length; i++) {
            for (int j = 0; j < gameboard[i].length; j++) {
                for (int k = 0; k < numLayers)
                gameboard[i][j] = "🤎";
            }
        } 
    }
    
    // displays the game instructions to the user
    // returns:
    //      String: containing instructions to play the game
    public String instructions() {
        String instructions = "";
        instructions += "♡ Players take turns choosing one of the hearts and 'chomping'";
        instructions += "it,\n♡ along with all hearts to the right and below it.\n♡ The ";
        instructions += "player who chomps the top-left most poisened heart loses the game.\n";  
        instructions += "♡ To make a move, enter the row and column of the heart you "; 
        instructions += "wish to chomp.\n♡ The current game state is represented as a "; 
        instructions += "grid of heart emojis, where\n♡ '🤎' represents an unchomped heart "; 
        instructions += "and '🤍'  represents a chomped heart.\n♡ Once the game has ended, "; 
        instructions += "the winner will be displayed.";
        return instructions;
    }

    // displays the current state of the game
    // returns:
    //      String: representing the game's current state
    public String toString() {
        String currentGame = "";
        for (int i = 0; i < gameboard.length; i++) {
            for (int j = 0; j < gameboard[i].length; j++) {
                currentGame += gameboard[i][j] + " ";
            }
            currentGame += "\n";
        }       
        return currentGame;
    }

    // returns whether or not the game has ended
    // returns:
    //      boolean: true if the game is over & false otherwise
    public boolean isGameOver() {
        return gameover;
    }

    // returns which player won the game
    // returns: 
    //      int: integer of which player won the game
    //      1 = player 1, 2 = player 2 & -1 if the game is not over
    public int getWinner() {
        if (!gameover) {
            return -1;
        }
        return winner;
    }

    // returns the next player in the game
    // returns:
    //      int: integer of which player's turn it is
    //      1 = player 1, 2 = player 2 & -1 if the game is not over
    public int getNextPlayer() {
        if (gameover) {
            return -1;
        } else {
            return player;
        }
    }

    // given the input, places a white heart where the player specifies
    // brown heart = unchomped, white heart = chomped
    // if the user enters a row/column that is less than zero, or greater than
    // the number of rows/column, or a coordinate that represents a white chomped
    // heart, then an IllegalArgumentException is thrown & the user is reprompted
    // given a valid row & column, the heart at the specified location is chomped
    // along with all hearts to the right and below it
    // parameters:
    //      Scanner input: user input
    public void makeMove(Scanner input) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please choose a heart to chomp \n");
        System.out.print("Row: ");
        int row = scan.nextInt();
        System.out.print("Column: ");
        int col = scan.nextInt();
        row--;
        col--;

        if (row >= 0 && row < gameboard.length && col >= 0 
            && col < gameboard[0].length && gameboard[row][col] == "🤎") {
            for (int i = row; i < gameboard.length; i++) {
                for (int j = col; j < gameboard[i].length; j++) {
                    gameboard[i][j] = "🤍";
                }
            }
            gameboard.toString();

            if (gameboard[0][0] == "🤍") {
                System.out.println("Player " + player + " loses!");
                gameover = true;
                winner = player == 1 ? 2 : 1;
            } else {
                player = player == 1 ? 2 : 1;
            }
        } else {
            throw new IllegalArgumentException("Invalid heart. Please choose another heart.");
        }
    }
}
