package lectureroomreservation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String userId;
    private String password;
    private String userName;
    private String role; // "STUDENT", "TA", "PROFESSOR"
    private List<String> notifications = new ArrayList<>();

    public User(String userId, String password, String role) {
        this.userId = userId;
        this.password = password;
        this.role = role;
    }

    // [신규] 이름 포함 생성자 (이미지 UI 대응)
    public User(String userId, String password, String userName, String role) {
        this.userId = userId;
        this.password = password;
        this.userName = userName;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public List<String> getNotifications() { return notifications; }
    public void addNotification(String msg) { notifications.add(msg); }
    public void clearNotifications() { notifications.clear(); }
}