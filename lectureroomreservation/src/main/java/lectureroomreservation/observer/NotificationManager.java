package lectureroomreservation.observer;

import java.util.ArrayList;
import java.util.List;

public class NotificationManager {
    private List<ReservationObserver> observers = new ArrayList<>();

    public void attach(ReservationObserver observer) { observers.add(observer); }
    public void detach(ReservationObserver observer) { observers.remove(observer); }

    public void notifyObservers(String message) {
        for (ReservationObserver obs : observers) {
            obs.update(message);
        }
    }
}