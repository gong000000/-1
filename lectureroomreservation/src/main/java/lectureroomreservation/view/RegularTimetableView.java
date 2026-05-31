package lectureroomreservation.view;

import lectureroomreservation.control.ReservationController;
import lectureroomreservation.model.DataManager;
import lectureroomreservation.model.Lecture;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RegularTimetableView extends JFrame {

    private ReservationController controller;
    private JTable table;
    private DefaultTableModel tableModel;

    // 로그인한 유저의 역할(userRole)을 넘겨받습니다.
    public RegularTimetableView(ReservationController controller, String userRole) {
        this.controller = controller;

        setTitle("학과 정규 수업 시간표 조회");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. 중앙 테이블 패널
        String[] columns = {"과목명", "강의실", "요일", "시작 교시", "종료 교시"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 2. 하단 버튼 패널
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton closeBtn = new JButton("닫기");

        // userRole이 "조교" 또는 "관리자"일 경우에만 관리 버튼을 띄웁니다.
        if (userRole != null && (userRole.equals("조교") || userRole.equals("관리자"))) {
            JButton manageBtn = new JButton("정규 수업 관리");
            JButton refreshBtn = new JButton("새로고침");

            // 🚨 [핵심 수정 부분] LectureManagementView를 열 때 userRole을 함께 넘겨줍니다!
            manageBtn.addActionListener(e -> {
                new LectureManagementView(controller, userRole).setVisible(true);
            });

            // 관리 창에서 추가/삭제 후 목록을 다시 불러오기 위한 버튼
            refreshBtn.addActionListener(e -> {
                loadLectureData();
            });

            bottomPanel.add(manageBtn);
            bottomPanel.add(refreshBtn);
        }

        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // 닫기 이벤트
        closeBtn.addActionListener(e -> dispose());

        // 초기 데이터 로드
        loadLectureData();
    }

    // 테이블에 수업 목록을 띄우는 메서드
    private void loadLectureData() {
        tableModel.setRowCount(0);
        List<Lecture> lectures = DataManager.getInstance().getRegularLectures();
        for (Lecture lec : lectures) {
            Object[] row = {
                lec.getSubjectName(), 
                lec.getRoomId(),
                lec.getDayOfWeek(),
                lec.getFromPeriod(),
                lec.getToPeriod()
            };
            tableModel.addRow(row);
        }
    }
}
