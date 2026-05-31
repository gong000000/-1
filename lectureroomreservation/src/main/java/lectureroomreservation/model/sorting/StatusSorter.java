package lectureroomreservation.model.sorting;

import lectureroomreservation.model.Reservation;
import java.util.Comparator;
import java.util.List;

public class StatusSorter extends ReservationSorter {
    
    @Override
    protected void sort(List<Reservation> list) {
        // 예약 상태(Status) 문자열을 기준으로 오름차순 정렬
        // APPROVED -> CANCELED -> PENDING -> REJECTED 순으로 정렬됨
        list.sort(Comparator.comparing(Reservation::getStatus));
        System.out.println("[시스템] 상태 기준으로 정렬이 완료되었습니다.");
    }
}