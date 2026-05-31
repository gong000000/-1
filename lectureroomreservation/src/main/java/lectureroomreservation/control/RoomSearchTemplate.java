package lectureroomreservation.control;

import lectureroomreservation.model.DataManager;
import lectureroomreservation.model.LectureRoom;
import java.util.ArrayList;
import java.util.List;

// [Template Method Pattern] 추상 클래스
public abstract class RoomSearchTemplate {
    // 템플릿 메서드 (알고리즘의 뼈대)
    public final List<LectureRoom> search(String day, int period) {
        List<LectureRoom> allRooms = new ArrayList<>(DataManager.getInstance().getLectureRooms());
        return filterRooms(allRooms, day, period);
    }

    // 하위 클래스에서 구현할 필터링 로직
    protected abstract List<LectureRoom> filterRooms(List<LectureRoom> rooms, String day, int period);
}