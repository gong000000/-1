package lectureroomreservation.control;

import lectureroomreservation.model.*;
import java.util.Iterator;
import java.util.List;

public class ReservationController {
    private DataManager dataManager = DataManager.getInstance();

    public int getOriginalCapacity(String roomId) {
        for (LectureRoom lr : dataManager.getLectureRooms()) {
            if (lr.getRoomId().equals(roomId)) {
                return lr.getOriginalCapacity();
            }
        }
        return dataManager.getRoomCapacity(roomId);
    }

    public int getRemainingCapacity(String roomId, String day, int period) {
        int roomCapacity = getOriginalCapacity(roomId);
        int currentReserved = 0;

        for (Reservation r : dataManager.getReservations()) {
            if (r.getRoomId().equals(roomId) && r.getDayOfWeek().equals(day) && 
                !r.getStatus().startsWith("반려") && !r.getStatus().equals("취소")) {
                
                if (period >= r.getFromPeriod() && period <= r.getToPeriod()) {
                    currentReserved += (1 + r.getCompanionCount());
                }
            }
        }
        int limit = roomCapacity / 2;
        int remaining = limit - currentReserved;
        return remaining < 0 ? 0 : remaining;
    }

    public String requestReservation(String roomId, String userId, String day, int from, int to, int companionCount, String companionDetails, String purpose, int pCount) {
        return requestReservation(roomId, userId, day, from, to, companionCount, companionDetails);
    }

    public String requestReservation(String roomId, String userId, String day, int from, int to, int companionCount, String companionDetails) {
        int reservedHours = to - from + 1;
        if (reservedHours > 2) {
            return "예약 가능시간을 초과했습니다. (최대 2시간)";
        }
        
        if (from > to) {
            return "시작 교시가 종료 교시보다 클 수 없습니다.";
        }

        if (isConflictWithRegularLectures(roomId, day, from, to)) {
            return "해당 시간은 학과 정규 수업이 진행되므로 예약할 수 없습니다.";
        }
        
        int roomCapacity = getOriginalCapacity(roomId); 
        int maxAllowedPeople = roomCapacity / 2; 

        for (int period = from; period <= to; period++) {
            if (isRoomFullAtTime(roomId, day, period, 1 + companionCount)) {
                return "강의실 수용인원 50% 이상이라 예약이 불가능합니다.\n" +
                       "(강의실 총 수용인원: " + roomCapacity + "명, 50% 제한: " + maxAllowedPeople + "명)";
            }
        }
        
        try {
            String resId = "RES-" + (System.currentTimeMillis() % 100000);
            Reservation newRes = new Reservation(resId, userId, roomId, day, from, to);
            newRes.setCompanionCount(companionCount);
            newRes.setCompanionDetails(companionDetails);
            newRes.setStatus("대기");

            dataManager.addReservation(newRes);
            dataManager.saveReservations(); 
            
            return "SUCCESS";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String checkRoomAvailability(String roomId, String day, int from, int to) {
        for (int p = from; p <= to; p++) {
            if (isRoomFullAtTime(roomId, day, p)) {
                return "선택한 시간대에 강의실이 이미 가득 찼습니다.";
            }
        }
        return "SUCCESS";
    }

    private boolean isConflictWithRegularLectures(String roomId, String day, int fromPeriod, int toPeriod) {
        for (Lecture lec : dataManager.getRegularLectures()) {
            if (lec.getRoomId().equals(roomId) && lec.getDayOfWeek().equals(day)) {
                if (fromPeriod <= lec.getToPeriod() && toPeriod >= lec.getFromPeriod()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isRoomFullAtTime(String roomId, String day, int period) {
        return isRoomFullAtTime(roomId, day, period, 0);
    }

    public boolean isRoomFullAtTime(String roomId, String day, int period, int totalIncoming) {
        for (Lecture lec : dataManager.getRegularLectures()) {
            if (lec.getRoomId().equals(roomId) && lec.getDayOfWeek().equals(day)) {
                if (period >= lec.getFromPeriod() && period <= lec.getToPeriod()) return true;
            }
        }

        int roomCapacity = getOriginalCapacity(roomId);
        int currentReserved = 0;

        for (Reservation r : dataManager.getReservations()) {
            if (r.getRoomId().equals(roomId) && r.getDayOfWeek().equals(day) && 
                !r.getStatus().startsWith("반려") && !r.getStatus().equals("취소")) {
                
                if (period >= r.getFromPeriod() && period <= r.getToPeriod()) {
                    currentReserved += (1 + r.getCompanionCount());
                }
            }
        }

        if (totalIncoming == 0) {
            return currentReserved >= (roomCapacity / 2);
        } else {
            return (currentReserved + totalIncoming) > (roomCapacity / 2);
        }
    }

    public void rejectMultipleReservations(List<String> reservationIds, String reason) {
        List<Reservation> allReservations = dataManager.getReservations();
        Iterator<Reservation> iterator = allReservations.iterator();
        
        while (iterator.hasNext()) {
            Reservation res = iterator.next();
            
            if (reservationIds.contains(res.getReservationId())) {
                res.setStatus("반려 (사유: " + reason + ")");
                
                String noticeMessage = "신청하신 강의실 [" + res.getRoomId() + "] 예약이 관리자에 의해 반려되었습니다. (사유: " + reason + ")";
                addNotification(res.getUserId(), noticeMessage);
            }
        }
        
        dataManager.saveReservations();
        dataManager.saveUsers();
    }

    public void updateReservationStatus(String reservationId, String newStatus) {
        List<Reservation> list = dataManager.getReservations();
        String targetUserId = null;
        for (Reservation r : list) {
            if (r.getReservationId().equals(reservationId)) {
                r.setStatus(newStatus);
                targetUserId = r.getUserId();
                break;
            }
        }
        dataManager.saveReservations();

        if (targetUserId != null) {
            String msg = "예약하신 [" + reservationId + "] 건의 상태가 '" + newStatus + "'(으)로 변경되었습니다.";
            addNotification(targetUserId, msg);
            dataManager.saveUsers();
        }
    }

    public boolean cancelReservation(String reservationId, String userId) {
        List<Reservation> list = dataManager.getReservations();
        for (Reservation r : list) {
            if (r.getReservationId().equals(reservationId) && r.getUserId().equals(userId)) {
                if (r.getStatus().startsWith("반려") || r.getStatus().startsWith("취소")) {
                    return false;
                }
                r.setStatus("취소");
                dataManager.saveReservations();
                return true;
            }
        }
        return false;
    }

    public void forceCancelReservationsByUser(String userId, String cancelReason) {
        List<Reservation> reservations = dataManager.getReservations();
        Iterator<Reservation> iterator = reservations.iterator(); 

        while (iterator.hasNext()) {
            Reservation res = iterator.next();
            if (res.getUserId().equals(userId) && 
               (res.getStatus().equals("대기") || res.getStatus().equals("승인"))) {
                
                res.setStatus("취소");
                
                String noticeMessage = "[" + res.getRoomId() + "호] 교수의 우선 예약으로 인해 예약이 강제 취소되었습니다. 사유: " + cancelReason;
                addNotification(res.getUserId(), noticeMessage);
            }
        }
        
        dataManager.saveReservations(); 
        dataManager.saveUsers(); 
    }

    private User findUser(String id) {
        return dataManager.getUsers().stream().filter(u -> u.getUserId().equals(id)).findFirst().orElse(null);
    }

    private void addNotification(String id, String msg) {
        User u = findUser(id);
        if (u != null) u.addNotification(msg);
    }

    public String addRegularLecture(String lectureName, String roomId, String day, int from, int to) {
        if (from > to) {
            return "시작 교시가 종료 교시보다 클 수 없습니다.";
        }

        for (Lecture lec : dataManager.getRegularLectures()) {
            if (lec.getRoomId().equals(roomId) && lec.getDayOfWeek().equals(day)) {
                if (from <= lec.getToPeriod() && to >= lec.getFromPeriod()) {
                    return "해당 강의실의 선택된 시간에 이미 다른 정규 수업이 있습니다.";
                }
            }
        }

        try {
            Lecture newLecture = new Lecture(roomId, lectureName, day, from, to);
            
            dataManager.getRegularLectures().add(newLecture);
            
            // 🚨 [수정 반영] 추가 완료 시 파일에 확실히 저장!
            dataManager.saveRegularLectures(); 
            
            return "SUCCESS";
        } catch (Exception e) {
            return "수업 추가 중 오류 발생: " + e.getMessage();
        }
    }

    public boolean deleteRegularLecture(String roomId, String day, int fromPeriod) {
        List<Lecture> lectures = dataManager.getRegularLectures();
        for (int i = 0; i < lectures.size(); i++) {
            Lecture lec = lectures.get(i);
            if (lec.getRoomId().equals(roomId) && lec.getDayOfWeek().equals(day) && lec.getFromPeriod() == fromPeriod) {
                lectures.remove(i);
                
                // 🚨 [수정 반영] 삭제 완료 시 파일에 확실히 덮어쓰기!
                dataManager.saveRegularLectures(); 
                
                return true;
            }
        }
        return false;
    }
}