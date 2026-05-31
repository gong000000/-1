package lectureroomreservation.model.sorting;

import lectureroomreservation.model.Reservation;
import java.util.List;

public abstract class ReservationSorter {
    // Template Method: 정렬 후 출력하는 일련의 과정
    public final void sortAndDisplay(List<Reservation> list) {
        sort(list);
        display(list);
    }

    // 하위 클래스에서 구현할 정렬 알고리즘
    protected abstract void sort(List<Reservation> list);

    private void display(List<Reservation> list) {
        for (Reservation r : list) {
            System.out.println("예약 번호: " + r.getReservationId() + " 상태: " + r.getStatus());
        }
    }
}