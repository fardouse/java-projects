// Reads through and processes a file containing answers from
// the Keirsey Personality test and outputs the resulting
// personality type to an output file. 

import java.util.*;
import java.io.*;

public class Personality {
    public static final int DIMENSIONS = 4;
    public static void main(String[] args) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);
   
    intro(); 

    System.out.print("input file name? ");
    Scanner input = new Scanner(new File(console.nextLine()));
    System.out.print("output file name? ");
    PrintStream output = new PrintStream(new File(console.nextLine()));
    
    while (input.hasNextLine()) {
        String name = input.nextLine();
        String responses = input.nextLine().toUpperCase();

        // counts of 'A' and 'B' responses
        int[] countA = new int[DIMENSIONS];
        int[] countB = new int[DIMENSIONS];
        tally(responses, countA, countB);
        int[] percentage = computeB(countA, countB);
        String personalityType = personality(percentage);
        printResults(output, name, percentage, personalityType);      
    }
}

   // Processes a line of responses and increments the counter 
   // in the appropriate array for the given response.
   // String responses: the String of responses for a particular player
   // int[] countA: array for 'A' responses
   // int[] countB: array for 'B' responses
    public static void tally(String responses, int[] countA, int[] countB) {
        for (int i = 0; i < responses.length(); i++) {
            int num = i % 7;
            char letter = responses.charAt(i);
            if (letter == 'A') {
            getCounts(num, countA);   
            } else if (letter == 'B') {
            getCounts(num, countB);
            }         
        }
    }

// Increments appropriate array index based on response.
// int num: expression used to determine which array index to increment.
// int[] counts: array to increment
public static void getCounts(int num, int[] counts) {
    if (num == 0) {
        counts[0]++;
    } else if (num == 1 || num == 2) {
        counts[1]++;
    } else if (num == 3 || num == 4) {
        counts[2]++;
    } else {                                      
        counts[3]++;
    }          
}
  
    // Calculates the percentage of 'B' responses and 
    // returns an array accordingly. 
    // int[] countA: array for 'A' responses
    // int[] countB: array for 'B' responses 
    public static int[] computeB(int[] countA, int[] countB) {
        int[] percentage = new int[DIMENSIONS];
        for (int i = 0; i < percentage.length; i++) {
            double num = (countB[i] * 100.0 / (countB[i] + countA[i]));
            int percent = (int) Math.round(num);
            percentage[i] = percent;
        }
        return percentage;
    }
  
    // Determines personality type based on percentage
    // of 'B' responses. Returns a string of 
    // a four letter personality type.
    // int[] percentage: array of percentage 'B' responses
    public static String personality(int[] percentage) {
        String personality = "";
        String[] type1 = new String[]{"I", "N", "F", "P"};
        String[] type2 = new String[]{"E", "S", "T", "J"};
        
        for (int i = 0; i < percentage.length; i++) {
            if (percentage[i] > 50) {
            personality += type1[i];
            } else if (percentage[i] < 50) {
            personality += type2[i];
            } else {
                personality += "X";
            }
        }
        return personality;
    }
   
    // Prints the given results to the given PrintStream.
    // PrintStream output: output source
    // String name: String containing name of player
    // int[] percentage: array of percentage 'B' responses
    // String personality: four letter personality type 
    public static void printResults(PrintStream output, 
                                    String name,int[] percentage,
                                    String personalityType) {
        output.print(name + ": "+ Arrays.toString(percentage) + " = ");  
        output.print(personalityType);
        output.println();
    }
   
    // intro message to the program
    public static void intro() {
        System.out.println("This program processes a file of answers to the");
        System.out.println("Keirsey Temperament Sorter. It converts the");
        System.out.println("various A and B answers for each person into");
        System.out.println("a sequence of B-percentages and then into a");
        System.out.println("four-letter personality type."); 
        System.out.println();
    }
}     