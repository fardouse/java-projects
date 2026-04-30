package learn.house.domain;

import learn.house.data.DataException;
import learn.house.data.GuestRepository;
import learn.house.data.HostRepository;
import learn.house.data.ReservationRepository;
import learn.house.models.Guest;
import learn.house.models.Host;
import learn.house.models.Reservation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepo;
    private final GuestRepository guestRepo;
    private final HostRepository hostRepo;

    public ReservationService(ReservationRepository reservationRepo,
                              GuestRepository guestRepo,
                              HostRepository hostRepo) {
        this.reservationRepo = reservationRepo;
        this.guestRepo = guestRepo;
        this.hostRepo = hostRepo;
    }

        public List<Reservation> findByHost (String hostId) throws DataException {
            List<Reservation> reservations = reservationRepo.findByHost(hostId);
            Map<Integer, Guest> guestMap = guestRepo.findAll().stream()
                    .collect(Collectors.toMap(Guest::getId, g -> g));
            Host host = hostRepo.findById(hostId);
            for (Reservation r : reservations) {
                r.setGuest(guestMap.getOrDefault(r.getGuest().getId(), r.getGuest()));
                if (host != null) {
                    r.setHost(host);
                }
            }
            return reservations;
        }

        public Result<Reservation> add (Reservation reservation) throws DataException {
            Result<Reservation> result = validate(reservation, true);
            if (!result.isSuccess()) return result;

            reservation.setTotal(calculateTotal(reservation));
            Reservation added = reservationRepo.add(reservation);
            result.setPayload(added);
            return result;
        }

        public Result<Reservation> update (Reservation reservation) throws DataException {
            Result<Reservation> result = validate(reservation, false);
            if (!result.isSuccess()) return result;

            reservation.setTotal(calculateTotal(reservation));
            boolean updated = reservationRepo.update(reservation);
            if (!updated) {
                result.addErrorMessage("Reservation ID " + reservation.getId() + " was not found.");
                return result;
            }
            result.setPayload(reservation);
            return result;
        }


        public Result<Reservation> deleteById ( int reservationId, String hostId) throws DataException {
            Result<Reservation> result = new Result<>();
            List<Reservation> all = findByHost(hostId);
            Reservation target = all.stream()
                    .filter(r -> r.getId() == reservationId)
                    .findFirst()
                    .orElse(null);

            if (target == null) {
                result.addErrorMessage("Reservation " + reservationId + " not found for host.");
                return result;
            }
            if (!target.getStartDate().isAfter(LocalDate.now())) {
                result.addErrorMessage("Cannot cancel a reservation that has already started or passed.");
                return result;
            }
            reservationRepo.deleteById(reservationId, hostId);
            result.setPayload(target);
            return result;
        }

        public BigDecimal calculateTotal (Reservation reservation){
            Host host = reservation.getHost();
            BigDecimal total = BigDecimal.ZERO;
            LocalDate date = reservation.getStartDate();

            while (date.isBefore(reservation.getEndDate())) {
                DayOfWeek day = date.getDayOfWeek();
                if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                    total = total.add(host.getWeekendRate());
                } else {
                    total = total.add(host.getStandardRate());
                }
                date = date.plusDays(1);
            }
            return total;
        }

        private Result<Reservation> validate (Reservation reservation,boolean isNew) throws DataException {
            Result<Reservation> result = new Result<>();

            if (reservation == null) {
                result.addErrorMessage("Reservation cannot be null.");
                return result;
            }

            if (reservation.getGuest() == null) {
                result.addErrorMessage("Guest is required.");
            } else {
                Guest guest = guestRepo.findById(reservation.getGuest().getId());
                if (guest == null) {
                    result.addErrorMessage("Guest does not exist.");
                }
            }

            if (reservation.getHost() == null) {
                result.addErrorMessage("Host is required.");
            } else {
                Host host = hostRepo.findById(reservation.getHost().getId());
                if (host == null) {
                    result.addErrorMessage("Host does not exist.");
                }
            }

            if (reservation.getStartDate() == null) {
                result.addErrorMessage("Start date is required.");
            }
            if (reservation.getEndDate() == null) {
                result.addErrorMessage("End date is required.");
            }

            if (reservation.getStartDate() != null && reservation.getEndDate() != null) {
                if (!reservation.getStartDate().isBefore(reservation.getEndDate())) {
                    result.addErrorMessage("Start date must be before end date.");
                }
                if (isNew && !reservation.getStartDate().isAfter(LocalDate.now())) {
                    result.addErrorMessage("Start date must be in the future.");
                }
            }

            if (!result.isSuccess()) return result;

            List<Reservation> existing = reservationRepo.findByHost(reservation.getHost().getId());
            for (Reservation other : existing) {
                if (!isNew && other.getId() == reservation.getId()) continue;

                boolean overlaps = reservation.getStartDate().isBefore(other.getEndDate())
                        && reservation.getEndDate().isAfter(other.getStartDate());
                if (overlaps) {
                    result.addErrorMessage(String.format(
                            "Dates overlap with existing reservation %d (%s - %s).",
                            other.getId(), other.getStartDate(), other.getEndDate()));
                }
            }

            return result;
        }
}
