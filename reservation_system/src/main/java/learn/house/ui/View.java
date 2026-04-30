package learn.house.ui;

import learn.house.models.Guest;
import learn.house.models.Host;
import learn.house.models.Reservation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

@Component
public class View {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private final Scanner scanner = new Scanner(System.in);

    public int selectMainMenuOption() {
        printHeader("Main Menu");
        System.out.println("0. Exit");
        System.out.println("1. View Reservations for Host");
        System.out.println("2. Make a Reservation");
        System.out.println("3. Edit a Reservation");
        System.out.println("4. Cancel a Reservation");
        System.out.println("5. Manage Guests");
        System.out.println("6. Manage Hosts");
        return readInt("Select [0-6]: ", 0, 6);
    }

    public int selectGuestMenuOption() {
        printHeader("Guest Management");
        System.out.println("0. Back to Main Menu");
        System.out.println("1. View All Guests");
        System.out.println("2. Add a Guest");
        System.out.println("3. Edit a Guest");
        System.out.println("4. Delete a Guest");
        return readInt("Select [0-4]: ", 0, 4);
    }

    public int selectHostMenuOption() {
        printHeader("Host Management");
        System.out.println("0. Back to Main Menu");
        System.out.println("1. View All Hosts");
        System.out.println("2. Add a Host");
        System.out.println("3. Edit a Host");
        System.out.println("4. Delete a Host");
        return readInt("Select [0-4]: ", 0, 4);
    }

    public void displayReservations(Host host, List<Reservation> reservations) {
        printHeader(host.getLastName() + ": " + host.getCity() + ", " + host.getState());
        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
            return;
        }
        for (Reservation r : reservations) {
            String guestName = r.getGuest().getLastName() + ", " + r.getGuest().getFirstName();
            System.out.printf("ID: %d, %s - %s, Guest: %s, Email: %s, Total: $%.2f%n",
                    r.getId(),
                    r.getStartDate().format(FORMATTER),
                    r.getEndDate().format(FORMATTER),
                    guestName,
                    r.getGuest().getEmail(),
                    r.getTotal());
        }
    }

    public void displayGuests(List<Guest> guests) {
        printHeader("All Guests");
        if (guests.isEmpty()) { System.out.println("No guests found."); return; }
        for (Guest g : guests) {
            System.out.printf("ID: %d, %s %s, %s%n", g.getId(), g.getFirstName(), g.getLastName(), g.getEmail());
        }
    }

    public void displayHosts(List<Host> hosts) {
        printHeader("All Hosts");
        if (hosts.isEmpty()) { System.out.println("No hosts found."); return; }
        for (Host h : hosts) {
            System.out.printf("%s, %s %s, %s, Rate: $%.2f/$%.2f%n",
                    h.getLastName(), h.getCity(), h.getState(), h.getEmail(),
                    h.getStandardRate(), h.getWeekendRate());
        }
    }

    public void displaySummary(Reservation reservation) {
        printHeader("Summary");
        System.out.println("Start: " + reservation.getStartDate().format(FORMATTER));
        System.out.println("End:   " + reservation.getEndDate().format(FORMATTER));
        System.out.printf("Total: $%.2f%n", reservation.getTotal());
    }

    public void displayResult(List<String> messages) {
        if (messages.isEmpty()) return;
        printHeader("Errors");
        messages.forEach(System.out::println);
    }

    public void displaySuccess(String message) {
        printHeader("Success");
        System.out.println(message);
    }

    public void displayError(String message) {
        System.out.println("[ERROR] " + message);
    }

    public String readRequiredString(String prompt) {
        String value;
        do {
            System.out.print(prompt);
            value = scanner.nextLine().trim();
            if (value.isBlank()) System.out.println("Value is required.");
        } while (value.isBlank());
        return value;
    }

    public String readString(String prompt, String defaultValue) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();
        return value.isBlank() ? defaultValue : value;
    }

    public LocalDate readDate(String prompt) {
        LocalDate date = null;
        while (date == null) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                date = LocalDate.parse(input, FORMATTER);
            } catch (DateTimeParseException ex) {
                System.out.println("Invalid date format. Use MM/dd/yyyy.");
            }
        }
        return date;
    }

    public LocalDate readDate(String prompt, LocalDate defaultDate) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isBlank()) return defaultDate;
        try {
            return LocalDate.parse(input, FORMATTER);
        } catch (DateTimeParseException ex) {
            System.out.println("Invalid date format. Keeping original.");
            return defaultDate;
        }
    }

    public BigDecimal readBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                BigDecimal value = new BigDecimal(input);
                if (value.compareTo(BigDecimal.ZERO) > 0) return value;
                System.out.println("Value must be greater than zero.");
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number.");
            }
        }
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) return value;
                System.out.printf("Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number.");
            }
        }
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number.");
            }
        }
    }

    public boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) return true;
            if (input.equals("n") || input.equals("no")) return false;
            System.out.println("Please enter y or n.");
        }
    }

    public void printHeader(String title) {
        System.out.println();
        System.out.println(title);
        System.out.println("=".repeat(title.length()));
    }

    public void enterToContinue() {
        System.out.print("Press [Enter] to continue...");
        scanner.nextLine();
    }
}