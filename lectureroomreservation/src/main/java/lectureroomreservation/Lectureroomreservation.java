package lectureroomreservation;

import lectureroomreservation.view.LoginView;
import javax.swing.SwingUtilities;

public class Lectureroomreservation {
    public static void main(String[] args) {
        // Swing GUI는 Event Dispatch Thread에서 실행하는 것이 안전합니다.
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
}