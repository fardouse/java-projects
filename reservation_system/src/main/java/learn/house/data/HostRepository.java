package learn.house.data;

import learn.house.models.Host;
import java.util.List;

public interface HostRepository {
    List<Host> findAll() throws DataException;
    Host findById(String id) throws DataException;
    Host findByEmail(String email) throws DataException;
    Host add(Host host) throws DataException;
    boolean update(Host host) throws DataException;
    boolean deleteById(String id) throws DataException;
}
