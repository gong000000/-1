package lectureroomreservation.view;

import lectureroomreservation.control.ReservationController; 
import lectureroomreservation.model.DataManager;
import lectureroomreservation.model.User;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainMenuView extends JFrame {
    private String userId;
    private String role;
    private JButton notiBtn; 
    private ReservationController controller; 

    public MainMenuView(String userId, String role) {
        this.userId = userId;
        this.role = role;
        this.controller = new ReservationController(); 

        setTitle("강의실 예약 시스템 - 메인 메뉴 (" + role + ")");
        setSize(400, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout()); 

        // 1. 상단 패널 (오른쪽 정렬로 알림 버튼 배치)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        notiBtn = new JButton("🔔 알림");
        updateNotiButtonText(); // 알림 개수 반영
        topPanel.add(notiBtn);
        add(topPanel, BorderLayout.NORTH); 

        // 2. 중앙 패널 (기존 메인 메뉴 기능 버튼들)
        JPanel centerPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JLabel welcomeLabel = new JLabel(userId + "님 환영합니다! (" + role + " 권한)", SwingConstants.CENTER);
        centerPanel.add(welcomeLabel);

        JButton infoBtn = new JButton("ℹ️ 강의실/기자재 정보 조회");
        JButton reserveBtn = new JButton("강의실 예약하기");
        JButton timetableBtn = new JButton("🏫 학과 정규 수업 시간표 조회");
        JButton myResBtn = new JButton("내 예약 확인");
        JButton logoutBtn = new JButton("로그아웃");

        if (role.equals("TA")) {
            myResBtn.setText("전체 예약 관리 (승인/반려)");
            notiBtn.setVisible(false); // 관리자(조교)는 알림 버튼 숨김
        }

        centerPanel.add(infoBtn);
        centerPanel.add(reserveBtn);
        centerPanel.add(timetableBtn);
        centerPanel.add(myResBtn);
        centerPanel.add(logoutBtn);
        
        add(centerPanel, BorderLayout.CENTER); 

        // 3. 버튼 액션 리스너 설정
        notiBtn.addActionListener(e -> showNotifications());
        infoBtn.addActionListener(e -> new RoomInfoView().setVisible(true));
        
        // 🚨 [핵심 수정] 예약 화면을 부를 때 userId와 함께 'role(역할)'도 같이 넘겨주도록 변경!
        reserveBtn.addActionListener(e -> new ReservationView(userId, role).setVisible(true));
        
        timetableBtn.addActionListener(e -> {
            String koreanRole = role.equals("TA") ? "조교" : "학생";
            new RegularTimetableView(controller, koreanRole).setVisible(true);
        });
        
        myResBtn.addActionListener(e -> {
            if (role.equals("TA")) {
                new AdminReservationView().setVisible(true);
            } else {
                new MyReservationView(userId).setVisible(true);
            }
        });
        
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginView().setVisible(true);
        });

        // 화면이 완전히 켜진 직후에 팝업을 띄우도록 타이밍 조절 (invokeLater)
        if (!role.equals("TA")) {
            SwingUtilities.invokeLater(() -> checkNotificationsOnLogin(userId));
        }
    }

    // 로그인 시 알림을 체크하고 화면에 바로 팝업을 띄워주는 메서드
    private void checkNotificationsOnLogin(String targetUserId) {
        for (User u : DataManager.getInstance().getUsers()) {
            if (u.getUserId().equals(targetUserId)) {
                List<String> notis = u.getNotifications();
                if (notis != null && !notis.isEmpty()) {
                    StringBuilder sb = new StringBuilder("새로운 시스템 알림이 있습니다!\n(자세한 내용은 우측 상단 🔔알림함에서도 확인 가능합니다.)\n\n");
                    for (String msg : notis) {
                        sb.append("- ").append(msg).append("\n");
                    }
                    
                    // 로그인 즉시 알림 팝업 출력!
                    JOptionPane.showMessageDialog(this, sb.toString(), "새 알림 (Observer)", JOptionPane.INFORMATION_MESSAGE);
                    
                    // 사용자가 우측 상단의 알림 버튼을 직접 눌러야만 비워지도록 알림 데이터를 보존합니다.
                }
                break;
            }
        }
    }

    // 알림 버튼의 텍스트와 색상을 갱신하는 메서드
    private void updateNotiButtonText() {
        for (User u : DataManager.getInstance().getUsers()) {
            if (u.getUserId().equals(userId)) {
                int count = (u.getNotifications() == null) ? 0 : u.getNotifications().size();
                if (count > 0) {
                    notiBtn.setText("🔔 새 알림 (" + count + ")");
                    notiBtn.setForeground(Color.RED); // 안 읽은 알림이 있으면 빨간색 강조
                } else {
                    notiBtn.setText("🔔 알림 (0)");
                    notiBtn.setForeground(Color.BLACK);
                }
                break;
            }
        }
    }

    // 우측 상단 알림 버튼을 수동으로 클릭했을 때 대화상자를 보여주는 메서드
    private void showNotifications() {
        for (User u : DataManager.getInstance().getUsers()) {
            if (u.getUserId().equals(userId)) {
                List<String> notis = u.getNotifications();
                
                if (notis == null || notis.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "새로운 알림이 없습니다.", "알림함", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    StringBuilder sb = new StringBuilder("수신된 알림 목록:\n\n");
                    for (String msg : notis) {
                        sb.append("- ").append(msg).append("\n");
                    }
                    JOptionPane.showMessageDialog(this, sb.toString(), "알림함", JOptionPane.INFORMATION_MESSAGE);
                    
                    // 사용자가 직접 알림함을 열어서 확인했을 때 알림을 삭제하고 파일에 저장합니다.
                    u.clearNotifications();
                    DataManager.getInstance().saveUsers();
                    updateNotiButtonText();
                }
                break;
            }
        }
    }
}