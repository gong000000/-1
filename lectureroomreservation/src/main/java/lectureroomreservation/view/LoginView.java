package lectureroomreservation.view;

import lectureroomreservation.control.LoginController;
import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField idField;
    private JPasswordField passField;
    private LoginController loginController;

    public LoginView() {
        loginController = new LoginController();
        setTitle("강의실 예약 시스템 - 로그인");
        setSize(320, 180);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("ID:"));
        idField = new JTextField();
        panel.add(idField);
        
        panel.add(new JLabel("Password:"));
        passField = new JPasswordField();
        panel.add(passField);
        
        JButton loginBtn = new JButton("로그인");
        JButton signupBtn = new JButton("회원가입");
        panel.add(signupBtn);
        panel.add(loginBtn);
        
        loginBtn.addActionListener(e -> {
            String id = idField.getText();
            String pw = new String(passField.getPassword());
            
            if(loginController.authenticate(id, pw)) {
                dispose(); 
                new MainMenuView(id, loginController.getRole(id)).setVisible(true); 
            } else {
                JOptionPane.showMessageDialog(this, "로그인 실패. 아이디와 비밀번호를 확인하세요.");
            }
        });

        signupBtn.addActionListener(e -> {
            new SignupView().setVisible(true); // 회원가입 창 열기
        });
        
        add(panel);
    }
}