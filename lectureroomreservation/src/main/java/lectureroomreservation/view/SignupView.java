package lectureroomreservation.view;

import lectureroomreservation.model.DataManager;
import lectureroomreservation.model.User;
import javax.swing.*;
import java.awt.*;

public class SignupView extends JFrame {
    public SignupView() {
        setTitle("회원가입 (Builder)");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 10, 10));

        JTextField idField = new JTextField();
        JPasswordField pwField = new JPasswordField();
        JTextField nameField = new JTextField();
        // 이미지와 동일한 신분 선택 드롭다운
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"학생", "조교", "교수"});
        JButton signupBtn = new JButton("회원가입 (Builder)");

        add(new JLabel(" 아이디:")); add(idField);
        add(new JLabel(" 비밀번호:")); add(pwField);
        add(new JLabel(" 이름(가입용):")); add(nameField);
        add(new JLabel(" 신분 선택:")); add(roleCombo);
        add(new JLabel("")); add(new JLabel("")); // 간격 맞추기
        add(new JLabel("")); add(signupBtn);

        signupBtn.addActionListener(e -> {
            String role = "STUDENT";
            if (roleCombo.getSelectedItem().equals("조교")) role = "TA";
            else if (roleCombo.getSelectedItem().equals("교수")) role = "PROFESSOR";

            User newUser = new User(idField.getText(), new String(pwField.getPassword()), nameField.getText(), role);
            DataManager.getInstance().addUser(newUser);
            JOptionPane.showMessageDialog(this, "회원가입이 완료되었습니다!");
            dispose();
        });
    }
}