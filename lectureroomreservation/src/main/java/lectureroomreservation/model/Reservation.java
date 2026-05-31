package lectureroomreservation.model;

import java.io.Serializable;

public class Reservation implements Serializable {
    private String reservationId;
    private String userId;
    private String roomId;
    private String dayOfWeek;
    private int fromPeriod;
    private int toPeriod;
    private String status;
    private int companionCount;
    private String companionDetails;
    
    // [신규] 교수/조교 예약 전용 필드
    private String purpose;       // 사용 목적 (보강, 세미나, 학생 지도)
    private int participantCount; // 참석 인원

    public Reservation(String reservationId, String userId, String roomId, String dayOfWeek, int fromPeriod, int toPeriod) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.roomId = roomId;
        this.dayOfWeek = dayOfWeek;
        this.fromPeriod = fromPeriod;
        this.toPeriod = toPeriod;
        this.status = "대기";
    }

    // Getters and Setters
    public String getReservationId() { return reservationId; }
    public String getUserId() { return userId; }
    public String getRoomId() { return roomId; }
    public String getDayOfWeek() { return dayOfWeek; }
    public int getFromPeriod() { return fromPeriod; }
    public int getToPeriod() { return toPeriod; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getCompanionCount() { return companionCount; }
    public void setCompanionCount(int c) { this.companionCount = c; }
    public String getCompanionDetails() { return companionDetails; }
    public void setCompanionDetails(String d) { this.companionDetails = d; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String p) { this.purpose = p; }
    public int getParticipantCount() { return participantCount; }
    public void setParticipantCount(int pc) { this.participantCount = pc; }
}