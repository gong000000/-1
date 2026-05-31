package lectureroomreservation.model;

import java.io.Serializable;

public class LectureRoom implements Serializable {
    private String roomId;
    private String type;
    private int capacity;
    private boolean isAvailable;
    private String facilities; 
    private boolean hasProjector; // 기존 RoomInfoView 호환용
    
    // 💡 [추가] 원본 수용인원 유실 방지를 위한 백업 변수
    private int originalCapacity = -1;

    // [신규] 5개 파라미터를 받는 생성자
    public LectureRoom(String roomId, String type, int capacity, boolean isAvailable, String facilities) {
        this.roomId = roomId;
        this.type = type;
        this.capacity = capacity;
        this.isAvailable = isAvailable;
        this.facilities = facilities;
        this.hasProjector = (facilities != null && facilities.contains("빔프로젝터"));
    }

    // [과거 호환용] 기존 DataManager에서 4개만 넣어서 생성하는 코드용
    public LectureRoom(String roomId, String type, int capacity, boolean isAvailable) {
        this(roomId, type, capacity, isAvailable, "기본 시설");
        this.hasProjector = true; 
    }

    public String getRoomId() { return roomId; }
    public String getType() { return type; }
    public String getRoomType() { return type; } // 기존 RoomInfoView 호환용
    public int getCapacity() { return capacity; }
    public boolean isAvailable() { return isAvailable; }
    public String getFacilities() { return facilities; }
    public boolean hasProjector() { return hasProjector; } // 기존 RoomInfoView 호환용

    // 💡 [추가] AvailableRoomSearch의 컴파일 에러(cannot find symbol)를 해결하는 세터
    public void setCapacity(int capacity) {
        if (this.originalCapacity == -1) {
            this.originalCapacity = this.capacity; // 처음 변경되기 직전의 진짜 원본 수용량을 보존
        }
        this.capacity = capacity;
    }

    // 💡 [추가] 컨트롤러에서 데이터 계산 꼬임 없이 원본 수용량을 가져올 수 있게 해주는 메서드
    public int getOriginalCapacity() {
        return this.originalCapacity == -1 ? this.capacity : this.originalCapacity;
    }
}