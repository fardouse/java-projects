package learn.house.ui;

import learn.house.data.DataException;
import learn.house.domain.GuestService;
import learn.house.domain.HostService;
import learn.house.domain.ReservationService;
import learn.house.domain.Result;
import learn.house.models.Guest;
import learn.house.models.Host;
import learn.house.models.Reservation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Controller {

    private final ReservationService reservationService;
    private final GuestService guestService;
    private final HostService hostService;
    private final View view;

    public Controller(ReservationService reservationService,
                      GuestService guestService,
                      HostService hostService,
                      View view) {
        this.reservationService = reservationService;
        this.guestService = guestService;
        this.hostService = hostService;
        this.view = view;
    }

    public void run() {
        view.printHeader("Don't Wreck My House");
        try {
            runMenuLoop();
        } catch (DataException ex) {
            view.displayError("A critical data error has occurred: " + ex.getMessage());
        }
        System.out.println("Goodbye.");
    }

    private void runMenuLoop() throws DataException {
        int option;
        do {
            option = view.selectMainMenuOption();
            switch (option) {
                case 1 -> viewReservationsForHost();
                case 2 -> makeReservation();
                case 3 -> editReservation();
                case 4 -> cancelReservation();
                case 5 -> manageGuests();
                case 6 -> manageHosts();
            }
        } while (option != 0);
    }

    private void viewReservationsForHost() throws DataException {
        view.printHeader("View Reservations for Host");
        Host host = findHostByEmail();
        if (host == null) return;

        List<Reservation> reservations = reservationService.findByHost(host.getId());
        reservations.sort(Comparator.comparing(Reservation::getStartDate));
        view.displayReservations(host, reservations);
        view.enterToContinue();
    }

    private void makeReservation() throws DataException {
        view.printHeader("Make a Reservation");

        Guest guest = findGuestByEmail();
        if (guest == null) return;

        Host host = findHostByEmail();
        if (host == null) return;

        List<Reservation> existing = reservationService.findByHost(host.getId());
        List<Reservation> future = existing.stream()
                .filter(r -> r.getStartDate().isAfter(LocalDate.now()))
                .sorted(Comparator.comparing(Reservation::getStartDate))
                .collect(Collectors.toList());
        view.displayReservations(host, future);

        LocalDate startDate = view.readDate("Start (MM/dd/yyyy): ");
        LocalDate endDate   = view.readDate("End (MM/dd/yyyy): ");

        Reservation reservation = new Reservation();
        reservation.setGuest(guest);
        reservation.setHost(host);
        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);
        reservation.setTotal(reservationService.calculateTotal(reservation));

        view.displaySummary(reservation);
        if (!view.readBoolean("Is this okay? [y/n]: ")) {
            System.out.println("Reservation cancelled.");
            return;
        }

        Result<Reservation> result = reservationService.add(reservation);
        if (result.isSuccess()) {
            view.displaySuccess("Reservation " + result.getPayload().getId() + " created.");
        } else {
            view.displayResult(result.getMessages());
        }
    }

    private void editReservation() throws DataException {
        view.printHeader("Edit a Reservation");

        Guest guest = findGuestByEmail();
        if (guest == null) return;

        Host host = findHostByEmail();
        if (host == null) return;

        List<Reservation> reservations = reservationService.findByHost(host.getId()).stream()
                .filter(r -> r.getGuest().getId() == guest.getId())
                .sorted(Comparator.comparing(Reservation::getStartDate))
                .collect(Collectors.toList());

        if (reservations.isEmpty()) {
            System.out.println("No reservations found for this guest/host combination.");
            return;
        }

        view.displayReservations(host, reservations);
        int id = view.readInt("Reservation ID: ");

        Reservation reservation = reservations.stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElse(null);

        if (reservation == null) {
            view.displayError("Reservation " + id + " not found.");
            return;
        }

        view.printHeader("Editing Reservation " + id);
        LocalDate newStart = view.readDate(
                "Start (" + reservation.getStartDate() + "): ", reservation.getStartDate());
        LocalDate newEnd   = view.readDate(
                "End (" + reservation.getEndDate() + "): ", reservation.getEndDate());

        reservation.setStartDate(newStart);
        reservation.setEndDate(newEnd);
        reservation.setTotal(reservationService.calculateTotal(reservation));

        view.displaySummary(reservation);
        if (!view.readBoolean("Is this okay? [y/n]: ")) {
            System.out.println("Edit cancelled.");
            return;
        }

        Result<Reservation> result = reservationService.update(reservation);
        if (result.isSuccess()) {
            view.displaySuccess("Reservation " + id + " updated.");
        } else {
            view.displayResult(result.getMessages());
        }
    }

    private void cancelReservation() throws DataException {
        view.printHeader("Cancel a Reservation");

        Guest guest = findGuestByEmail();
        if (guest == null) return;

        Host host = findHostByEmail();
        if (host == null) return;

        List<Reservation> future = reservationService.findByHost(host.getId()).stream()
                .filter(r -> r.getGuest().getId() == guest.getId())
                .filter(r -> r.getStartDate().isAfter(LocalDate.now()))
                .sorted(Comparator.comparing(Reservation::getStartDate))
                .collect(Collectors.toList());

        if (future.isEmpty()) {
            System.out.println("No future reservations found for this guest/host combination.");
            return;
        }

        view.displayReservations(host, future);
        int id = view.readInt("Reservation ID: ");

        Result<Reservation> result = reservationService.deleteById(id, host.getId());
        if (result.isSuccess()) {
            view.displaySuccess("Reservation " + id + " cancelled.");
        } else {
            view.displayResult(result.getMessages());
        }
    }

    private void manageGuests() throws DataException {
        int option;
        do {
            option = view.selectGuestMenuOption();
            switch (option) {
                case 1 -> view.displayGuests(guestService.findAll());
                case 2 -> addGuest();
                case 3 -> editGuest();
                case 4 -> deleteGuest();
            }
        } while (option != 0);
    }

    private void addGuest() throws DataException {
        view.printHeader("Add Guest");
        Guest guest = new Guest();
        guest.setFirstName(view.readRequiredString("First Name: "));
        guest.setLastName(view.readRequiredString("Last Name: "));
        guest.setEmail(view.readRequiredString("Email: "));
        guest.setPhone(view.readString("Phone: ", ""));
        guest.setState(view.readString("State (2-letter): ", ""));

        Result<Guest> result = guestService.add(guest);
        if (result.isSuccess()) view.displaySuccess("Guest added with ID " + result.getPayload().getId() + ".");
        else view.displayResult(result.getMessages());
    }

    private void editGuest() throws DataException {
        view.printHeader("Edit Guest");
        Guest guest = findGuestByEmail();
        if (guest == null) return;

        guest.setFirstName(view.readString("First Name (" + guest.getFirstName() + "): ", guest.getFirstName()));
        guest.setLastName(view.readString("Last Name (" + guest.getLastName() + "): ", guest.getLastName()));
        guest.setEmail(view.readString("Email (" + guest.getEmail() + "): ", guest.getEmail()));
        guest.setPhone(view.readString("Phone (" + guest.getPhone() + "): ", guest.getPhone()));
        guest.setState(view.readString("State (" + guest.getState() + "): ", guest.getState()));

        Result<Guest> result = guestService.update(guest);
        if (result.isSuccess()) view.displaySuccess("Guest updated.");
        else view.displayResult(result.getMessages());
    }

    private void deleteGuest() throws DataException {
        view.printHeader("Delete Guest");
        Guest guest = findGuestByEmail();
        if (guest == null) return;
        System.out.printf("Delete %s %s? ", guest.getFirstName(), guest.getLastName());
        if (!view.readBoolean("[y/n]: ")) { System.out.println("Cancelled."); return; }
        Result<Guest> result = guestService.deleteById(guest.getId());
        if (result.isSuccess()) view.displaySuccess("Guest deleted.");
        else view.displayResult(result.getMessages());
    }

    private void manageHosts() throws DataException {
        int option;
        do {
            option = view.selectHostMenuOption();
            switch (option) {
                case 1 -> view.displayHosts(hostService.findAll());
                case 2 -> addHost();
                case 3 -> editHost();
                case 4 -> deleteHost();
            }
        } while (option != 0);
    }

    private void addHost() throws DataException {
        view.printHeader("Add Host");
        Host host = new Host();
        host.setLastName(view.readRequiredString("Last Name: "));
        host.setEmail(view.readRequiredString("Email: "));
        host.setPhone(view.readString("Phone: ", ""));
        host.setAddress(view.readString("Address: ", ""));
        host.setCity(view.readString("City: ", ""));
        host.setState(view.readString("State (2-letter): ", ""));
        host.setPostalCode(view.readString("Postal Code: ", ""));
        host.setStandardRate(view.readBigDecimal("Standard Rate (weekday): $"));
        host.setWeekendRate(view.readBigDecimal("Weekend Rate: $"));

        Result<Host> result = hostService.add(host);
        if (result.isSuccess()) view.displaySuccess("Host added with ID " + result.getPayload().getId() + ".");
        else view.displayResult(result.getMessages());
    }

    private void editHost() throws DataException {
        view.printHeader("Edit Host");
        Host host = findHostByEmail();
        if (host == null) return;

        host.setLastName(view.readString("Last Name (" + host.getLastName() + "): ", host.getLastName()));
        host.setEmail(view.readString("Email (" + host.getEmail() + "): ", host.getEmail()));
        host.setPhone(view.readString("Phone (" + host.getPhone() + "): ", host.getPhone()));
        host.setAddress(view.readString("Address (" + host.getAddress() + "): ", host.getAddress()));
        host.setCity(view.readString("City (" + host.getCity() + "): ", host.getCity()));
        host.setState(view.readString("State (" + host.getState() + "): ", host.getState()));
        host.setPostalCode(view.readString("Postal Code (" + host.getPostalCode() + "): ", host.getPostalCode()));
        System.out.printf("Standard Rate (current: $%.2f) — press Enter to keep: ", host.getStandardRate());
        String sr = new java.util.Scanner(System.in).nextLine().trim();
        if (!sr.isBlank()) host.setStandardRate(new BigDecimal(sr));
        System.out.printf("Weekend Rate (current: $%.2f) — press Enter to keep: ", host.getWeekendRate());
        String wr = new java.util.Scanner(System.in).nextLine().trim();
        if (!wr.isBlank()) host.setWeekendRate(new BigDecimal(wr));

        Result<Host> result = hostService.update(host);
        if (result.isSuccess()) view.displaySuccess("Host updated.");
        else view.displayResult(result.getMessages());
    }

    private void deleteHost() throws DataException {
        view.printHeader("Delete Host");
        Host host = findHostByEmail();
        if (host == null) return;
        System.out.print("Delete " + host.getLastName() + "? ");
        if (!view.readBoolean("[y/n]: ")) { System.out.println("Cancelled."); return; }
        Result<Host> result = hostService.deleteById(host.getId());
        if (result.isSuccess()) view.displaySuccess("Host deleted.");
        else view.displayResult(result.getMessages());
    }

    private Guest findGuestByEmail() throws DataException {
        String email = view.readRequiredString("Guest Email: ");
        Guest guest = guestService.findByEmail(email);
        if (guest == null) {
            view.displayError("Guest with email '" + email + "' not found.");
        }
        return guest;
    }

    private Host findHostByEmail() throws DataException {
        String email = view.readRequiredString("Host Email: ");
        Host host = hostService.findByEmail(email);
        if (host == null) {
            view.displayError("Host with email '" + email + "' not found.");
        }
        return host;
    }
}