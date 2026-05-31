package lectureroomreservation.view;

import lectureroomreservation.control.ReservationController;
import lectureroomreservation.model.DataManager;
import lectureroomreservation.model.Lecture;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LectureManagementView extends JFrame {

    private ReservationController controller;
    private DefaultTableModel tableModel;
    private JTable table;

    // 입력 필드들
    private JTextField txtLectureName, txtRoomId;
    private JComboBox<String> cbDay;
    private JComboBox<Integer> cbFrom, cbTo;

    // [핵심 변경] 생성자에 userRole(권한)을 넘겨받도록 수정했습니다.
    public LectureManagementView(ReservationController controller, String userRole) {
        this.controller = controller;
        
        // 역할에 따라 창 제목 다르게 설정
        if ("조교".equals(userRole) || "관리자".equals(userRole)) {
            setTitle("조교(관리자) - 정규 수업 시간표 관리");
        } else {
            setTitle("학과 정규 수업 시간표 조회");
        }
        
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. 상단 테이블 영역 (현재 수업 목록)
        String[] columnNames = {"과목명", "강의실", "요일", "시작 교시", "종료 교시"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        loadTableData(); // 초기 데이터 로드

        add(new JScrollPane(table), BorderLayout.CENTER);

        // 2. 하단 입력 및 제어 영역 (권한에 따라 다르게 그림)
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // 조교(관리자)일 경우에만 입력 폼과 관리 버튼을 생성합니다.
        if ("조교".equals(userRole) || "관리자".equals(userRole)) {
            // 입력 폼 패널
            JPanel inputPanel = new JPanel(new FlowLayout());
            txtLectureName = new JTextField(10);
            txtRoomId = new JTextField(6);
            String[] days = {"월", "화", "수", "목", "금"};
            cbDay = new JComboBox<>(days);
            
            Integer[] periods = {1, 2, 3, 4, 5, 6, 7, 8, 9};
            cbFrom = new JComboBox<>(periods);
            cbTo = new JComboBox<>(periods);

            inputPanel.add(new JLabel("과목명:"));
            inputPanel.add(txtLectureName);
            inputPanel.add(new JLabel("강의실:"));
            inputPanel.add(txtRoomId);
            inputPanel.add(new JLabel("요일:"));
            inputPanel.add(cbDay);
            inputPanel.add(new JLabel("시작:"));
            inputPanel.add(cbFrom);
            inputPanel.add(new JLabel("종료:"));
            inputPanel.add(cbTo);

            // 버튼 패널
            JPanel btnPanel = new JPanel(new FlowLayout());
            JButton btnAdd = new JButton("수업 추가");
            JButton btnDelete = new JButton("선택 삭제");
            JButton btnClose = new JButton("닫기");

            btnPanel.add(btnAdd);
            btnPanel.add(btnDelete);
            btnPanel.add(btnClose);

            bottomPanel.add(inputPanel, BorderLayout.CENTER);
            bottomPanel.add(btnPanel, BorderLayout.SOUTH);

            // ------------------ 조교 전용 이벤트 리스너 ------------------
            // 추가 버튼 동작
            btnAdd.addActionListener(e -> {
                String name = txtLectureName.getText().trim();
                String room = txtRoomId.getText().trim();
                String day = (String) cbDay.getSelectedItem();
                int from = (int) cbFrom.getSelectedItem();
                int to = (int) cbTo.getSelectedItem();

                if (name.isEmpty() || room.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "과목명과 강의실을 모두 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String result = controller.addRegularLecture(name, room, day, from, to);
                if (result.equals("SUCCESS")) {
                    JOptionPane.showMessageDialog(this, "정규 수업이 추가되었습니다.");
                    txtLectureName.setText("");
                    txtRoomId.setText("");
                    loadTableData(); // 테이블 새로고침
                } else {
                    JOptionPane.showMessageDialog(this, result, "추가 실패", JOptionPane.ERROR_MESSAGE);
                }
            });

            // 삭제 버튼 동작
            btnDelete.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this, "삭제할 수업을 테이블에서 먼저 선택해주세요.", "선택 오류", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String room = (String) tableModel.getValueAt(selectedRow, 1);
                String day = (String) tableModel.getValueAt(selectedRow, 2);
                int from = (int) tableModel.getValueAt(selectedRow, 3);

                int confirm = JOptionPane.showConfirmDialog(this, "정말로 이 수업을 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = controller.deleteRegularLecture(room, day, from);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "삭제되었습니다.");
                        loadTableData(); 
                    } else {
                        JOptionPane.showMessageDialog(this, "삭제에 실패했습니다. (데이터를 찾을 수 없음)", "오류", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // 관리자 닫기 버튼
            btnClose.addActionListener(e -> dispose());
            
        } else {
            // 학생일 경우: 입력 폼 없이 닫기 버튼만 가운데 정렬로 보여줍니다.
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton btnClose = new JButton("닫기");
            btnPanel.add(btnClose);
            bottomPanel.add(btnPanel, BorderLayout.SOUTH);
            
            btnClose.addActionListener(e -> dispose());
        }

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // 테이블에 최신 데이터를 불러오는 내부 메서드
    private void loadTableData() {
        tableModel.setRowCount(0); // 기존 데이터 초기화
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