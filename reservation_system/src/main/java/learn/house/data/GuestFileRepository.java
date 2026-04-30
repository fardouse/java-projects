package learn.house.data;

import learn.house.models.Guest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GuestFileRepository implements GuestRepository {

    private static final String HEADER = "guest_id,first_name,last_name,email,phone,state";
    private final String filePath;

    public GuestFileRepository(@Value("${data.guest.path:data/guests.csv}") String filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<Guest> findAll() throws DataException {
        List<Guest> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                Guest guest = deserialize(line);
                if (guest != null) result.add(guest);
            }
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
            throw new DataException("Could not read guests file.", ex);
        }
        return result;
    }

    @Override
    public Guest findById(int id) throws DataException {
        return findAll().stream()
                .filter(g -> g.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Guest findByEmail(String email) throws DataException {
        return findAll().stream()
                .filter(g -> g.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Guest add(Guest guest) throws DataException {
        List<Guest> all = findAll();
        int nextId = all.stream().mapToInt(Guest::getId).max().orElse(0) + 1;
        guest.setId(nextId);
        all.add(guest);
        writeAll(all);
        return guest;
    }

    @Override
    public boolean update(Guest guest) throws DataException {List<Guest> all = findAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == guest.getId()) {
                all.set(i, guest);
                writeAll(all);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteById(int id) throws DataException {
        List<Guest> all = findAll();
        boolean removed = all.removeIf(g -> g.getId() == id);
        if (removed) writeAll(all);
        return removed;
    }

    private Guest deserialize(String line) {
        String[] fields = line.split(",", -1);
        if (fields.length < 6) return null;
        try {
            Guest g = new Guest();
            g.setId(Integer.parseInt(fields[0].trim()));
            g.setFirstName(fields[1].trim());
            g.setLastName(fields[2].trim());
            g.setEmail(fields[3].trim());
            g.setPhone(fields[4].trim());
            g.setState(fields[5].trim());
            return g;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String serialize(Guest g) {
        return String.format("%s,%s,%s,%s,%s,%s",
                g.getId(), g.getFirstName(), g.getLastName(),
                g.getEmail(), g.getPhone(), g.getState());
    }

    private void writeAll(List<Guest> guests) throws DataException {
        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.println(HEADER);
            guests.forEach(g -> writer.println(serialize(g)));
        } catch (FileNotFoundException ex) {
            throw new DataException("Could not write guests file.", ex);
        }
    }
}
