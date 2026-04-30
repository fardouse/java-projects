package learn.house.domain;

import learn.house.data.DataException;
import learn.house.data.GuestRepository;
import learn.house.models.Guest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GuestService {
    private final GuestRepository repo;

    public GuestService(GuestRepository repo) {
        this.repo = repo;
    }

    public List<Guest> findAll() throws DataException {
        return repo.findAll();
    }

    public Guest findById(int id) throws DataException {
        return repo.findById(id);
    }

    public Guest findByEmail(String email) throws DataException {
        return repo.findByEmail(email);
    }

    public Result<Guest> add(Guest guest) throws DataException {
        Result<Guest> result = validate(guest, true);
        if (!result.isSuccess()) return result;
        result.setPayload(repo.add(guest));
        return result;
    }

    public Result<Guest> update(Guest guest) throws DataException {
        Result<Guest> result = validate(guest, false);
        if (!result.isSuccess()) return result;
        boolean updated = repo.update(guest);
        if (!updated) result.addErrorMessage("Guest ID " + guest.getId() + " not found.");
        else result.setPayload(guest);
        return result;
    }

    public Result<Guest> deleteById(int id) throws DataException {
        Result<Guest> result = new Result<>();
        boolean deleted = repo.deleteById(id);
        if (!deleted) result.addErrorMessage("Guest ID " + id + " not found.");
        return result;
    }

    private Result<Guest> validate(Guest guest, boolean isNew) throws DataException {
        Result<Guest> result = new Result<>();
        if (guest == null) { result.addErrorMessage("Guest cannot be null."); return result; }
        if (guest.getFirstName() == null || guest.getFirstName().isBlank())
            result.addErrorMessage("First name is required.");
        if (guest.getLastName() == null || guest.getLastName().isBlank())
            result.addErrorMessage("Last name is required.");
        if (guest.getEmail() == null || guest.getEmail().isBlank()) {
            result.addErrorMessage("Email is required.");
        } else {
            Guest existing = repo.findByEmail(guest.getEmail());
            if (existing != null && (isNew || existing.getId() != guest.getId()))
                result.addErrorMessage("Email must be unique. " + guest.getEmail() + " already exists.");
        }
        return result;
    }
}
