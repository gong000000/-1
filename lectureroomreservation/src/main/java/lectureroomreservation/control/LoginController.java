package lectureroomreservation.control;

import lectureroomreservation.model.DataManager;
import lectureroomreservation.model.User;

public class LoginController {
    
    // 파일에 저장된 유저 데이터와 대조
    public boolean authenticate(String id, String password) {
        for (User u : DataManager.getInstance().getUsers()) {
            if (u.getUserId().equals(id) && u.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }
    
    public String getRole(String id) {
        for (User u : DataManager.getInstance().getUsers()) {
            if (u.getUserId().equals(id)) {
                return u.getRole();
            }
        }
        return "UNKNOWN";
    }

    // 회원가입 처리
    public boolean registerUser(String id, String password) {
        // 아이디 중복 체크
        for (User u : DataManager.getInstance().getUsers()) {
            if (u.getUserId().equals(id)) {
                return false; // 이미 존재하는 아이디
            }
        }
        // 학생 계정으로 생성하여 저장
        User newUser = new User(id, password, "STUDENT");
        DataManager.getInstance().addUser(newUser);
        return true;
    }
}