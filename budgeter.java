// Asks the user for input regarding income and expenses and 
// calculates their total monthly income, expenses, and net income.

import java.util.*;

   public class Budgeter {
   // represents total number of days in a month
   public static final int DAYS_IN_MONTH = 31;
   public static void main(String[] args) {
      Scanner console = new Scanner(System.in);
      
      introMessage();
      double totalIncome = getInput(console, "income");
      double totalExpense = expenses(console);
      reportIncome(round2(totalIncome), round2(totalExpense));
   }

   // Prints out introduction message to the program
   public static void introMessage() {
      System.out.println("This program asks for your monthly income and");
      System.out.println("expenses, then tells you your net monthly income."); 
      System.out.println();           
   }

   // Reads in user expenditures and adds it to cumulative sum
   // Scanner console: scanner used to retrieve input
   // String type: represents the type of expenditure
   // Returns cumulative sum of expenditures 
   public static double getInput(Scanner console, String type) {
      System.out.print("How many categories of " + type + "? ");
      int input = console.nextInt(); 
      double totalInput = 0;
      for (int i = 1; i <= input; i++) {
         System.out.print("    Next " + type + " amount? $");
         double userInput = console.nextDouble();
         totalInput += userInput;
      } 
      return totalInput;  
   }

   // Reads in and computes total expenses
   // Scanner console: scanner used to retrieve input
   // Returns total monthly or daily expenses based on user's choice
   public static double expenses(Scanner console) {  
      System.out.println();    
      System.out.print("Enter 1) monthly or 2) daily expenses? ");
      int expenseType = console.nextInt();
      double totalExpense = getInput(console, "expense");                               
      System.out.println();
      if (expenseType == 2) {
         totalExpense = totalExpense * DAYS_IN_MONTH;
      }       
      return totalExpense;
   }

   // Calculates and prints total income and expenses per month.
   // Calculates and prints total daily income and expenses.
   // double totalIncome: total calculated income based on user input
   // double totalExpense: total calculated expenses based on user input
   public static void reportIncome(double totalIncome, double totalExpense) {
      double dailyIncome = totalIncome / DAYS_IN_MONTH;
      double dailyExpense = totalExpense;
      System.out.println("Total income = $" + totalIncome +
            " ($" + round2(dailyIncome) + "/day)");
      System.out.println("Total expenses = $" + totalExpense + 
            " ($" + round2(dailyExpense/DAYS_IN_MONTH) + "/day)"); 
      System.out.println(); 
      spending(round2(totalIncome), round2(totalExpense));    
   }

   // Calculates and prints total amount earned of spent per month.
   // Prints out the spender/saver category the user falls in based
   // on total amount spent or earned per month and an associated message:
   // double totalIncome: total calculated income based on user input
   // double totalExpense: total calculated expenses based on user input
   public static void spending(double totalIncome, double totalExpense) {
      double amountEarned = totalIncome - totalExpense;
      double amountSpent = totalExpense - totalIncome;

      if (totalIncome > totalExpense) { 
         System.out.println("You earned $" + round2(amountEarned) + 
               " more than you spent this month.");
      } else if (totalIncome == totalExpense) {
         System.out.println("You spent $0.0 more than you earned this month.");
      } else {
      System.out.println("You spent $" + round2(amountSpent) +
            " more than you earned this month.");
      }

      String bigSaver = "big saver";
      String saver = "saver";
      String spender = "spender";
      String bigSpender = "big spender";
      
      if (amountEarned > 250) {
         System.out.println("You're a " + bigSaver + ".");
         System.out.println("Keep up the great work!");
      }  else if (amountEarned <= 250 && amountEarned > 0) {
            System.out.println("You're a " + saver + ".");
            System.out.println("Great job this month, I suggest" + 
                  "you monitor your spending.");
      }  else if (amountSpent < 250 && amountSpent >= 0) {
            System.out.println("You're a " + spender + ".");
            System.out.println("Work on you saving more and spending less.");
      }  else if (amountSpent >= 250) {
            System.out.println("You're a " + bigSpender + ".");
            System.out.println("You've been spending slightly too" + 
                  "much this month, I suggest you start saving.");
      }  
   }

   // Rounds the given value to two decimal places and returns the result.
   // double num: the number to round
   public static double round2(double num) {
      return Math.round(num * 100.0) / 100.0;
   }  
   } 
