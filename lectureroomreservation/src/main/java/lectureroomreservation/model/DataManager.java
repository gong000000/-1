package lectureroomreservation.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instance;
    private final String RES_FILE = "reservations.dat";
    private final String USER_FILE = "users.dat";
    private final String LECTURE_FILE = "lectures.dat"; // 💡 정규 수업 저장용 파일 추가
    
    private List<Reservation> reservations;
    private List<Lecture> regularLectures;
    private List<User> users;
    private List<LectureRoom> lectureRooms;

    private DataManager() {
        reservations = loadReservations();
        users = loadUsers();
        regularLectures = loadRegularLectures(); // 💡 파일에서 수업을 불러오도록 변경
        initLectureRooms(); 
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    // 강의실 기초 데이터 세팅
    private void initLectureRooms() {
        lectureRooms = new ArrayList<>();
        lectureRooms.add(new LectureRoom("908", "일반 강의실", 40, true));
        lectureRooms.add(new LectureRoom("912", "일반 강의실", 50, true));
        lectureRooms.add(new LectureRoom("913", "일반 강의실", 50, true));
        lectureRooms.add(new LectureRoom("914", "일반 강의실", 30, false));
        lectureRooms.add(new LectureRoom("911", "PC 실습실", 40, true));
        lectureRooms.add(new LectureRoom("915", "PC 실습실", 40, true));
        lectureRooms.add(new LectureRoom("916", "PC 실습실", 35, true));
        lectureRooms.add(new LectureRoom("918", "PC 실습실", 35, false));
    }

    public List<LectureRoom> getLectureRooms() { return lectureRooms; }

    public int getRoomCapacity(String roomId) {
        if (lectureRooms != null) {
            for (LectureRoom room : lectureRooms) {
                if (room.getRoomId().equals(roomId)) {
                    return room.getCapacity(); 
                }
            }
        }
        return 40; 
    }

    @SuppressWarnings("unchecked")
    private List<User> loadUsers() {
        File file = new File(USER_FILE);
        if (!file.exists()) {
            List<User> defaultUsers = new ArrayList<>();
            defaultUsers.add(new User("admin", "admin", "ADMIN"));
            return defaultUsers;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<User>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    public List<User> getUsers() { return users; }

    // 🚨 [신규 추가] 정규 수업 데이터를 파일에서 불러오는 메서드
    @SuppressWarnings("unchecked")
    private List<Lecture> loadRegularLectures() {
        File file = new File(LECTURE_FILE);
        // 파일이 없으면 기존의 기본 데이터 3개를 생성해서 반환 (최초 1회 실행용)
        if (!file.exists()) {
            List<Lecture> defaultLectures = new ArrayList<>();
            defaultLectures.add(new Lecture("912", "소프트웨어공학", "월", 1, 2));
            defaultLectures.add(new Lecture("912", "객체지향분석설계", "화", 3, 5));
            defaultLectures.add(new Lecture("913", "데이터베이스", "월", 3, 4));
            return defaultLectures;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Lecture>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 🚨 [신규 추가] 정규 수업 데이터를 파일에 영구 저장하는 메서드
    public void saveRegularLectures() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LECTURE_FILE))) {
            oos.writeObject(regularLectures);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Lecture> getRegularLectures() { return regularLectures; }

    @SuppressWarnings("unchecked")
    private List<Reservation> loadReservations() {
        File file = new File(RES_FILE);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Reservation>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveReservations() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RES_FILE))) {
            oos.writeObject(reservations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addReservation(Reservation res) {
        reservations.add(res);
        saveReservations();
    }

    public List<Reservation> getReservations() { return reservations; }
}