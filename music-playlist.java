// this class allows a user to manage a music playlist where the  
// user can create a queue of songs as well as maintain song history

import java.util.*;

public class MusicPlaylist {
    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        Queue<String> playlist = new LinkedList<>();
        Stack<String> history = new Stack<>();
        String userChoice = "";

        System.out.println("Welcome to your Music Playlist!");
        while (!userChoice.equalsIgnoreCase("q")) {
            System.out.println("(A) Add song");
            System.out.println("(P) Play song");
            System.out.println("(Pr) Print history");
            System.out.println("(C) Clear history");
            System.out.println("(D) Delete from history");
            System.out.println("(Q) Quit");
            System.out.println();
            System.out.print("Enter your choice: ");
            userChoice = console.nextLine();

            if (userChoice.equalsIgnoreCase("a")) {
                addSong(console, playlist);
            } else if (userChoice.equalsIgnoreCase("p")) {
                playSong(console, playlist, history);
            } else if (userChoice.equalsIgnoreCase("pr")) {
                printHistory(history);
            } else if (userChoice.equalsIgnoreCase("c")) {
                clearHistory(history);
            } else if (userChoice.equalsIgnoreCase("d")) {
                delete(console, history);
            } 
        }
    }

    // this method asks the user to enter a song name 
    // and adds it to the queue of songs in the playlist
    // parameters:
    //      Scanner console = retrieve user input
    //      Queue<String> userPlaylist = queue of songs
    public static void addSong(Scanner console, Queue<String> userPlaylist) {
        String userInput = "";
        System.out.print("Enter song name: ");
        userInput = console.nextLine();
        System.out.println("Successfully added " + userInput);
        System.out.println();
        System.out.println();
        userPlaylist.add(userInput);
    }

    // this method plays the song at the beginning 
    // of the playlist and adds it to the song history
    // if the playlist is empty, an IllegalStateException is thrown
    // parameters:
    //      Scanner console = retrieve user input
    //      Queue<String> userPlaylist = queue of songs 
    //      Stack<String> userHistory = song history
    public static void playSong(Scanner console, Queue<String> userPlaylist,
                                Stack<String> userHistory) {
        if (userPlaylist.isEmpty()) {
            throw new IllegalStateException();
        } 
        String nextSong = userPlaylist.peek(); 
        System.out.println("Playing song: " + nextSong);
        userHistory.push(nextSong);
        userPlaylist.remove();
        System.out.println();
        System.out.println();
    }

    // this method keeps a complete history of played songs 
    // and prints them out in reverse chronological order
    // if the song history is empty, then an IllegalStateException is thrown
    // parameters:
    //      Stack<String> songHistory = song history
    public static void printHistory(Stack<String> songHistory) {
        if (songHistory.isEmpty()) {
            throw new IllegalStateException();
        } 
        Queue<String> playedSongs = new LinkedList<>();
        sToQ(songHistory, playedSongs); 

        while (!playedSongs.isEmpty()) {
            String song = playedSongs.peek();
            songHistory.push(song);
            System.out.println("    " + playedSongs.remove()); 
        }
        System.out.println();
        System.out.println();
        sToQ(songHistory, playedSongs);
        qToS(playedSongs, songHistory);
    }

    // this method clears the history of all songs played
    // parameters: 
    //      Stack<String> userHistory = song history
    public static void clearHistory(Stack<String> songHistory) { 
        while (!songHistory.isEmpty()) {
            songHistory.pop();
        }
    }

    // this method allows a user to delete songs from the history
    // the user is prompted to enter either a negative/positve number
    // that represents how many songs to delete and whether to delete
    // the most recently played songs(positive number) or songs 
    // that were played earlier(negative number)
    // if the size of the song history is less than the number of songs
    // entered, then an IllegalArgumentException is thrown
    // parameters:
    //      Scanner console = retrieve user input
    //      Stack<String> songHistory = song history
    public static void delete(Scanner console, Stack<String> songHistory) {
        String userInput = "";
        System.out.println("A positive number will delete from recent history.");
        System.out.println("A negative number will delete from the beginning of history.");
        System.out.print("Enter number of songs to delete: ");
        userInput = console.nextLine();
        int num = Integer.parseInt(userInput);
        System.out.println();

        if (songHistory.size() < Math.abs(num)) {
            throw new IllegalArgumentException();
        }
        
        if (num > 0) {
            num = Math.abs(num);
            for (int i = 0; i < num; i++) {
                songHistory.pop();
            }
        } else if (num < 0) {
            num = Math.abs(num);
            Queue<String> playedSongs = new LinkedList<>();
            sToQ(songHistory, playedSongs);
            qToS(playedSongs, songHistory);
            for (int i = 0; i < num; i++) {
                songHistory.pop();
            }
            sToQ(songHistory, playedSongs);
            qToS(playedSongs, songHistory);
        }
    }

    // this method to moves all contents of 
    // the given stack into the given queue
    // parameters: 
    //      Stack<String> s = given stack
    //      Queue<String> q = given queue
    public static void sToQ(Stack<String> s, Queue<String> q) {
        while (!s.isEmpty()) {
            q.add(s.pop());
        }
    }

    // this method moves all contents of 
    // the given queue into the given stack 
    // parameters:
    //      Queue<String> q = given queue
    //      Stack<String> s = given stack
    public static void qToS(Queue<String> q, Stack<String> s) {
        while (!q.isEmpty()) {
            s.push(q.remove());
        }
    }
}