    // this class is a to-do list manager that allows a user
    // to add items to a todo list, mark them as complete, 
    // save items to a file, and load items from file


    import java.util.*;
    import java.io.*;

    public class TodoListManager {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);
        List<String> list = new ArrayList<>();
        String userChoice = "";

        System.out.println("Welcome to your TODO List Manager!");
        while (!userChoice.equalsIgnoreCase("q")) {
            System.out.println("What would you like to do?");
            System.out.print("(A)dd TODO, (M)ark TODO as done, " +
                                "(L)oad TODOs, (S)ave TODOs, (Q)uit? ");
            userChoice = console.nextLine();
            if (userChoice.equalsIgnoreCase("a")) {
                addItem(console, list);
            } else if (userChoice.equalsIgnoreCase("m")) {
                markItemAsDone(console, list);
            } else if (userChoice.equalsIgnoreCase("l")) {
                loadItems(console, list); 
            } else if (userChoice.equalsIgnoreCase("s")) {
                saveItems(console, list);
            }  else if (!userChoice.equalsIgnoreCase("q")){
                System.out.println("Unknown input: " + userChoice);
                printTodos(list);
                }
            }
        }

    // this method prints out a list of todo items
    // parameters:
    //      List<String> todos = list of todo items
    public static void printTodos(List<String> todos) {
        if (todos.size() > 0) {
            System.out.println("Today's TODOs:");
            int count = 1;
            for (int i = 0; i < todos.size(); i++) {
                System.out.println("  " + count + ": " + todos.get(i));
                count++;
            }
        } else {
            System.out.println("Today's TODOs:");
            System.out.println("  You have nothing to do yet today! Relax!");
        }
    }

    // this method allows the user to enter a todo item anywhere on the list
    // parameters: 
    //      Scanner console = retrieve user input
    //      List<String> todos = list of todo items
    public static void addItem(Scanner console, List<String> todos) {
        String userInput = "";
        String input = "";
        System.out.print("What would you like to add? ");
        userInput = console.nextLine();

        if (!todos.isEmpty()) {
            System.out.print("Where in the list should it be (1-" + (todos.size() + 1) + ")?" +
                                " (Enter for end): "); 
            input = console.nextLine(); 
            if (!input.isEmpty()) {
                int num = Integer.parseInt(input);
                todos.add(num - 1, userInput);
            } else {
                todos.add(userInput);
            }                    
        } else {
            todos.add(userInput);
        }
        printTodos(todos);
    }

    // this method allows the user to mark todo items off the list
    // parameters: 
    //      Scanner console: retrive user input
    //      List<String> todos = list of todo items
    public static void markItemAsDone(Scanner console, List<String> todos) {
        String input = "";
        int num = 0;
        
        if (!todos.isEmpty()) {
            System.out.print("Which item did you complete (1-" +
                                todos.size() + ")? ");
            input = console.nextLine(); 
            num = Integer.parseInt(input);
            todos.remove(num - 1); 
        } else if (todos.size() == 1) {
            System.out.print("Which item did you complete (1-1)? ");
            input = console.nextLine();
            num = Integer.parseInt(input);
        } else {
            System.out.println("All done! Nothing left to mark as done!");
        }
        printTodos(todos);
    }

    // this method loads and prints out a list of 
    // todo items from a file entered by the user
    // parameters: 
    //      Scanner console: retrive user input
    //      List<String> todos = list of todo items
    public static void loadItems(Scanner console, List<String> todos)
                                throws FileNotFoundException {
        System.out.print("File name? ");
        String fileName = console.nextLine();
        Scanner scan = new Scanner(new File(fileName));
        todos.clear();

        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            todos.add(line);
        }
        printTodos(todos);
    }

    // this method saves a list of todo items to a file entered by the user
    // parameters: 
    //      Scanner console: retrive user input
    //      List<String> todos = list of todo items
    public static void saveItems(Scanner console, List<String> todos)
                                throws FileNotFoundException {
        System.out.print("File name? ");
        String outputFileName = console.nextLine();
        PrintStream output = new PrintStream(new File(outputFileName));

        for (int i = 0; i < todos.size(); i++) {
            output.println(todos.get(i));
        }
        printTodos(todos);
    }
    }