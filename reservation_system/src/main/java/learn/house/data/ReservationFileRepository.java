package learn.house.data;

import learn.house.models.Guest;
import learn.house.models.Host;
import learn.house.models.Reservation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReservationFileRepository implements ReservationRepository {
    private static final String HEADER = "id,start_date,end_date,guest_id,total";
    private final String directory;

    public ReservationFileRepository(@Value("${data.reservations.dir:data/reservations}") String directory) {
        this.directory = directory;
    }

    @Override
    public List<Reservation> findByHost(String hostId) throws DataException {
        List<Reservation> result = new ArrayList<>();
        String filePath = getFilePath(hostId);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                Reservation r = deserialize(line);
                if (r != null) {
                    Host host = new Host();
                    host.setId(hostId);
                    r.setHost(host);
                    result.add(r);
                }
            }
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
            throw new DataException("Could not read reservations for host: " + hostId, ex);
        }
        return result;
    }

    @Override
    public Reservation add(Reservation reservation) throws DataException {
        List<Reservation> all = findByHost(reservation.getHost().getId());
        int nextId = all.stream().mapToInt(Reservation::getId).max().orElse(0) + 1;
        reservation.setId(nextId);
        all.add(reservation);
        writeAll(all, reservation.getHost().getId());
        return reservation;
    }

    @Override
    public boolean update(Reservation reservation) throws DataException {
        List<Reservation> all = findByHost(reservation.getHost().getId());
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == reservation.getId()) {
                all.set(i, reservation);
                writeAll(all, reservation.getHost().getId());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteById(int reservationId, String hostId) throws DataException {
        List<Reservation> all = findByHost(hostId);
        boolean removed = all.removeIf(r -> r.getId() == reservationId);
        if (removed) writeAll(all, hostId);
        return removed;
    }

    private String getFilePath(String hostId) {
        return directory + File.separator + hostId + ".csv";
    }

    private Reservation deserialize(String line) {
        String[] fields = line.split(",", -1);
        if (fields.length < 5) return null;
        try {
            Reservation r = new Reservation();
            r.setId(Integer.parseInt(fields[0].trim()));
            r.setStartDate(LocalDate.parse(fields[1].trim()));
            r.setEndDate(LocalDate.parse(fields[2].trim()));
            Guest guest = new Guest();
            guest.setId(Integer.parseInt(fields[3].trim()));
            r.setGuest(guest);
            r.setTotal(new BigDecimal(fields[4].trim()));
            return r;
        } catch (Exception ex) {
            return null;
        }
    }

    private String serialize(Reservation r) {
        return String.format("%s,%s,%s,%s,%s",
                r.getId(),
                r.getStartDate(),
                r.getEndDate(),
                r.getGuest().getId(),
                r.getTotal().toPlainString());
    }

    private void writeAll(List<Reservation> reservations, String hostId) throws DataException {
        String filePath = getFilePath(hostId);
        new File(directory).mkdirs();
        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.println(HEADER);
            reservations.forEach(r -> writer.println(serialize(r)));
        } catch (FileNotFoundException ex) {
            throw new DataException("Could not write reservations file for host: " + hostId, ex);
        }
    }
}
