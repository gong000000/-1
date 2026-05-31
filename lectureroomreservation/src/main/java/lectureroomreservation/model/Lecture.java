package lectureroomreservation.model;

import java.io.Serializable; // 💡 필수: 직렬화(파일 저장)를 위한 import

public class Lecture implements Serializable { // 💡 필수: implements Serializable 추가
    private String roomId;       // 강의실 (예: "912")
    private String subjectName;  // 과목명 (예: "소프트웨어공학")
    private String dayOfWeek;    // 요일 (예: "월")
    private int fromPeriod;      // 시작 교시
    private int toPeriod;        // 종료 교시

    public Lecture(String roomId, String subjectName, String dayOfWeek, int fromPeriod, int toPeriod) {
        this.roomId = roomId;
        this.subjectName = subjectName;
        this.dayOfWeek = dayOfWeek;
        this.fromPeriod = fromPeriod;
        this.toPeriod = toPeriod;
    }

    // Getters
    public String getRoomId() { return roomId; }
    public String getSubjectName() { return subjectName; }
    public String getDayOfWeek() { return dayOfWeek; }
    public int getFromPeriod() { return fromPeriod; }
    public int getToPeriod() { return toPeriod; }
}