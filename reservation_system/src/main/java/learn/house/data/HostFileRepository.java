package learn.house.data;

import learn.house.models.Host;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HostFileRepository implements HostRepository {

    private static final String HEADER =
            "id,last_name,email,phone,address,city,state,postal_code,standard_rate,weekend_rate";
    private final String filePath;

    public HostFileRepository(@Value("${data.host.path:data/hosts.csv}") String filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<Host> findAll() throws DataException {
        List<Host> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                Host host = deserialize(line);
                if (host != null) result.add(host);
            }
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
            throw new DataException("Could not read hosts file.", ex);
        }
        return result;
    }

    @Override
    public Host findById(String id) throws DataException {
        return findAll().stream()
                .filter(h -> h.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Host findByEmail(String email) throws DataException {
        return findAll().stream()
                .filter(h -> h.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Host add(Host host) throws DataException {
        if (host.getId() == null || host.getId().isBlank()) {
            host.setId(java.util.UUID.randomUUID().toString());
        }
        List<Host> all = findAll();
        all.add(host);
        writeAll(all);
        return host;
    }

    @Override
    public boolean update(Host host) throws DataException {
        List<Host> all = findAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(host.getId())) {
                all.set(i, host);
                writeAll(all);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteById(String id) throws DataException {
        List<Host> all = findAll();
        boolean removed = all.removeIf(h -> h.getId().equals(id));
        if (removed) writeAll(all);
        return removed;
    }

    private Host deserialize(String line) {
        String[] fields = line.split(",", -1);
        if (fields.length < 10) return null;
        try {
            Host h = new Host();
            h.setId(fields[0].trim());
            h.setLastName(fields[1].trim());
            h.setEmail(fields[2].trim());
            h.setPhone(fields[3].trim());
            h.setAddress(fields[4].trim());
            h.setCity(fields[5].trim());
            h.setState(fields[6].trim());
            h.setPostalCode(fields[7].trim());
            h.setStandardRate(new BigDecimal(fields[8].trim()));
            h.setWeekendRate(new BigDecimal(fields[9].trim()));
            return h;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String serialize(Host h) {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                h.getId(), h.getLastName(), h.getEmail(), h.getPhone(),
                h.getAddress(), h.getCity(), h.getState(), h.getPostalCode(),
                h.getStandardRate().toPlainString(), h.getWeekendRate().toPlainString());
    }

    private void writeAll(List<Host> hosts) throws DataException {
        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.println(HEADER);
            hosts.forEach(h -> writer.println(serialize(h)));
        } catch (FileNotFoundException ex) {
            throw new DataException("Could not write hosts file.", ex);
        }
    }
}
