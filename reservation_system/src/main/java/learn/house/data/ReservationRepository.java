package learn.house.data;

import learn.house.models.Reservation;
import java.util.List;

public interface ReservationRepository {
    List<Reservation> findByHost(String hostId) throws DataException;
    Reservation add(Reservation reservation) throws DataException;
    boolean update(Reservation reservation) throws DataException;
    boolean deleteById(int reservationId, String hostId) throws DataException;
}
