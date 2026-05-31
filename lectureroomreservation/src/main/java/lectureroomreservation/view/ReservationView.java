package lectureroomreservation.view;

import lectureroomreservation.control.AvailableRoomSearch;
import lectureroomreservation.control.ReservationController;
import lectureroomreservation.model.LectureRoom;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReservationView extends JFrame {
    private ReservationController controller;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> dayCombo, fromTimeCombo, toTimeCombo, purposeCombo;
    private JTextField equipmentField, companionCountField, participantField;
    private JTextArea companionDetailsArea;
    private String selectedRoomId = "";
    private String userId;
    private String userRole; 

    public ReservationView(String userId, String userRole) {
        this.userId = userId;
        this.userRole = userRole; 
        this.controller = new ReservationController();
        setTitle("강의실 실시간 매칭 예약 시스템 v3.0");
        setSize(950, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. 상단 조건 설정 영역
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("실시간 빈 강의실 탐색 조건"));
        
        dayCombo = new JComboBox<>(new String[]{"월요일", "화요일", "수요일", "목요일", "금요일"});
        String[] periods = {"1교시", "2교시", "3교시", "4교시", "5교시", "6교시", "7교시", "8교시", "9교시"};
        fromTimeCombo = new JComboBox<>(periods);
        toTimeCombo = new JComboBox<>(periods);
        JButton searchBtn = new JButton("🔍 실시간 빈 강의실 탐색 (Template Method)");
        
        topPanel.add(new JLabel("날짜:")); topPanel.add(dayCombo);
        topPanel.add(new JLabel("시작 교시:")); topPanel.add(fromTimeCombo);
        topPanel.add(new JLabel("종료 교시:")); topPanel.add(toTimeCombo);
        topPanel.add(searchBtn);
        add(topPanel, BorderLayout.NORTH);

        // 2. 중앙 빈 강의실 목록 테이블
        String[] columns = {"강의실 번호", "수용 가능인원", "구비 시설 정보"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        roomTable = new JTable(tableModel);
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomTable.getSelectionModel().addListSelectionListener(e -> {
            int row = roomTable.getSelectedRow();
            if (row != -1) selectedRoomId = (String) tableModel.getValueAt(row, 0);
        });
        add(new JScrollPane(roomTable), BorderLayout.CENTER);

        // 3. 하단 세부 정보 입력란
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 5, 5, 5);

        JLabel purposeLabel = new JLabel("예약 목적 (교수/조교용):");
        JLabel participantLabel = new JLabel("참석 인원 (교수/조교용):");

        gbc.gridx = 0; gbc.gridy = 0; bottomPanel.add(purposeLabel, gbc);
        gbc.gridx = 1; purposeCombo = new JComboBox<>(new String[]{"보강", "세미나", "학생 지도", "기타 사유"}); 
        bottomPanel.add(purposeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; bottomPanel.add(participantLabel, gbc);
        gbc.gridx = 1; participantField = new JTextField("0"); 
        bottomPanel.add(participantField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; bottomPanel.add(new JLabel("추가 옵션 (대여 기자재):"), gbc);
        gbc.gridx = 1; equipmentField = new JTextField(); 
        bottomPanel.add(equipmentField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; bottomPanel.add(new JLabel("동반자 수 (학생 전용):"), gbc);
        gbc.gridx = 1; companionCountField = new JTextField("0"); 
        bottomPanel.add(companionCountField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; bottomPanel.add(new JLabel("동반자 학번/명단 명시:"), gbc);
        gbc.gridx = 1; companionDetailsArea = new JTextArea(2, 20); 
        bottomPanel.add(new JScrollPane(companionDetailsArea), gbc);

        // 🚨 [핵심 수정] 오직 "STUDENT"일 때만 교수/조교용 입력칸을 숨기도록 정확히 수정했습니다.
        if (this.userRole != null && this.userRole.equals("STUDENT")) {
            purposeLabel.setVisible(false);
            purposeCombo.setVisible(false);
            participantLabel.setVisible(false);
            participantField.setVisible(false);
        }

        JButton submitBtn = new JButton("📝 최종 예약 신청서 제출");
        submitBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        bottomPanel.add(submitBtn, gbc);
        add(bottomPanel, BorderLayout.SOUTH);

        // 실시간 빈 강의실 탐색 검증 필터 강화
        searchBtn.addActionListener(e -> {
            String day = ((String) dayCombo.getSelectedItem()).substring(0, 1);
            int fromPeriod = fromTimeCombo.getSelectedIndex() + 1;
            int toPeriod = toTimeCombo.getSelectedIndex() + 1;
            
            if (fromPeriod > toPeriod) {
                JOptionPane.showMessageDialog(this, "시작 교시가 종료 교시보다 늦을 수 없습니다.");
                return;
            }
            if ((toPeriod - fromPeriod + 1) > 3) {
                JOptionPane.showMessageDialog(this, "예약 조사는 최대 3시간 단위로만 제한됩니다.");
                return;
            }
            
            AvailableRoomSearch searchEngine = new AvailableRoomSearch();
            List<LectureRoom> availableRooms = searchEngine.search(day, fromPeriod);
            
            tableModel.setRowCount(0);
            for (LectureRoom room : availableRooms) {
                String checkResult = controller.checkRoomAvailability(room.getRoomId(), day, fromPeriod, toPeriod);
                if (checkResult.equals("SUCCESS")) {
                    tableModel.addRow(new Object[]{room.getRoomId(), room.getCapacity() + "명", room.getFacilities()});
                }
            }
            
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "선택하신 시간대 전 구간에 비어있는 강의실이 없습니다.", "안내", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 예약 제출 버튼
        submitBtn.addActionListener(e -> {
            if (selectedRoomId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "테이블 목록에서 예약할 강의실을 먼저 선택해 주세요.");
                return;
            }
            try {
                String day = ((String) dayCombo.getSelectedItem()).substring(0, 1);
                int fromPeriod = fromTimeCombo.getSelectedIndex() + 1;
                int toPeriod = toTimeCombo.getSelectedIndex() + 1;
                
                if (fromPeriod > toPeriod) {
                    JOptionPane.showMessageDialog(this, "시작 교시가 종료 교시보다 클 수 없습니다.");
                    return;
                }
                
                int compCount = Integer.parseInt(companionCountField.getText().trim());
                String compDetails = companionDetailsArea.getText().trim();
                
                String purpose = "기타 사유";
                int partCount = 0;
                
                if (purposeCombo.isVisible()) {
                    purpose = (String) purposeCombo.getSelectedItem();
                }
                if (participantField.isVisible()) {
                    partCount = Integer.parseInt(participantField.getText().trim());
                }
                
                String result = controller.requestReservation(
                    selectedRoomId, userId, day, fromPeriod, toPeriod, compCount, compDetails, purpose, partCount
                );
                
                if (result.equals("SUCCESS")) {
                    JOptionPane.showMessageDialog(this, "강의실 예약 처리가 정상 반영되었습니다.");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "예약 거절 사유: " + result, "안내", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "동반자 수와 참석 인원은 정수(숫자)형태로 적어주세요.");
            }
        });
    }
}