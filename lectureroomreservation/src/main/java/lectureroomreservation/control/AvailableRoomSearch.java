package lectureroomreservation.control;

import lectureroomreservation.model.LectureRoom;
import java.util.List;

public class AvailableRoomSearch extends RoomSearchTemplate {
    @Override
    protected List<LectureRoom> filterRooms(List<LectureRoom> rooms, String day, int period) {
        ReservationController checkController = new ReservationController();
        
        // 1. 정규 수업이 있거나 인원이 이미 50% 제한선 이상 가득 찬 강의실 제외
        rooms.removeIf(room -> checkController.isRoomFullAtTime(room.getRoomId(), day, period));
        
        // 🚨 [추가 요구사항] 테이블의 '수용 가능인원' 컬럼에 '신청 가능한 남은 인원수'가 표시되도록 설정
        for (LectureRoom room : rooms) {
            int remaining = checkController.getRemainingCapacity(room.getRoomId(), day, period);
            
            // 💡 만약 LectureRoom 모델에 setCapacity(int) 메서드가 없다면 
            // LectureRoom.java 파일에 public void setCapacity(int capacity) { this.capacity = capacity; } 를 추가해주세요.
            room.setCapacity(remaining); 
        }
        
        return rooms;
    }
}