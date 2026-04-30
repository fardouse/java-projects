package learn.house.domain;

import learn.house.data.DataException;
import learn.house.data.HostRepository;
import learn.house.models.Host;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class HostService {
    private final HostRepository repo;

    public HostService(HostRepository repo) {
        this.repo = repo;
    }

    public List<Host> findAll() throws DataException {
        return repo.findAll();
    }

    public Host findById(String id) throws DataException {
        return repo.findById(id);
    }

    public Host findByEmail(String email) throws DataException {
        return repo.findByEmail(email);
    }

    public Result<Host> add(Host host) throws DataException {
        Result<Host> result = validate(host, true);
        if (!result.isSuccess()) return result;
        result.setPayload(repo.add(host));
        return result;
    }

    public Result<Host> update(Host host) throws DataException {
        Result<Host> result = validate(host, false);
        if (!result.isSuccess()) return result;
        boolean updated = repo.update(host);
        if (!updated) result.addErrorMessage("Host ID " + host.getId() + " not found.");
        else result.setPayload(host);
        return result;
    }

    public Result<Host> deleteById(String id) throws DataException {
        Result<Host> result = new Result<>();
        boolean deleted = repo.deleteById(id);
        if (!deleted) result.addErrorMessage("Host ID " + id + " not found.");
        return result;
    }

    private Result<Host> validate(Host host, boolean isNew) throws DataException {
        Result<Host> result = new Result<>();
        if (host == null) { result.addErrorMessage("Host cannot be null."); return result; }
        if (host.getLastName() == null || host.getLastName().isBlank())
            result.addErrorMessage("Last name is required.");
        if (host.getEmail() == null || host.getEmail().isBlank()) {
            result.addErrorMessage("Email is required.");
        } else {
            Host existing = repo.findByEmail(host.getEmail());
            if (existing != null && (isNew || !existing.getId().equals(host.getId())))
                result.addErrorMessage("Email must be unique. " + host.getEmail() + " already exists.");
        }
        if (host.getStandardRate() == null || host.getStandardRate().compareTo(BigDecimal.ZERO) <= 0)
            result.addErrorMessage("Standard rate must be greater than zero.");
        if (host.getWeekendRate() == null || host.getWeekendRate().compareTo(BigDecimal.ZERO) <= 0)
            result.addErrorMessage("Weekend rate must be greater than zero.");
        return result;
    }
}
