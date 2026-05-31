package lectureroomreservation.view;

import lectureroomreservation.control.ReservationController;
import lectureroomreservation.model.DataManager;
import lectureroomreservation.model.Reservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AdminReservationView extends JFrame {
    private ReservationController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;

    public AdminReservationView() {
        controller = new ReservationController();

        setTitle("전체 예약 관리 (관리자 모드)");
        setSize(850, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());

        // 1. 상단 패널
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(new JLabel("예약 상태 필터:"));
        filterCombo = new JComboBox<>(new String[]{"전체 보기", "대기", "승인", "반려", "취소"});
        topPanel.add(filterCombo);
        add(topPanel, BorderLayout.NORTH);

        // 2. 중앙 패널
        String[] columns = {"예약번호", "신청자ID", "강의실", "시간", "동반인원", "동반자 명단", "상태"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(5).setPreferredWidth(200);
        
        // 여러 예약을 한 번에 선택하여 반려할 수 있도록 다중 선택 모드 활성화
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 3. 하단 패널
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton approveBtn = new JButton("승인");
        JButton rejectBtn = new JButton("반려");
        JButton sortBtn = new JButton("상태별 정렬");
        JButton closeBtn = new JButton("닫기");

        bottomPanel.add(approveBtn);
        bottomPanel.add(rejectBtn);
        bottomPanel.add(sortBtn);
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // 이벤트 연결
        filterCombo.addActionListener(e -> loadReservationData((String) filterCombo.getSelectedItem()));

        // [승인 버튼 이벤트]
        approveBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "승인할 예약을 목록에서 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String resId = (String) tableModel.getValueAt(selectedRow, 0);
            controller.updateReservationStatus(resId, "승인");
            JOptionPane.showMessageDialog(this, "선택한 예약이 승인되었습니다.");
            loadReservationData((String) filterCombo.getSelectedItem());
        });

        // [반려 버튼 이벤트]
        rejectBtn.addActionListener(e -> {
            int[] selectedRows = table.getSelectedRows();
            
            if (selectedRows.length == 0) {
                JOptionPane.showMessageDialog(this, "반려할 예약을 목록에서 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String reason = JOptionPane.showInputDialog(this, 
                    selectedRows.length + "건의 예약을 반려합니다.\n반려 사유를 입력하세요:", 
                    "반려 사유 입력", JOptionPane.QUESTION_MESSAGE);
            
            if (reason == null) return; 
            
            if (reason.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "반려 사유를 반드시 입력해야 합니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<String> selectedIds = new ArrayList<>();
            for (int row : selectedRows) {
                String resId = (String) tableModel.getValueAt(row, 0); 
                selectedIds.add(resId);
            }

            controller.rejectMultipleReservations(selectedIds, reason.trim());

            JOptionPane.showMessageDialog(this, "선택한 예약이 반려 처리되었으며, 학생에게 알림이 전송되었습니다.");
            loadReservationData((String) filterCombo.getSelectedItem());
        });

        // [정렬 버튼 이벤트]
        sortBtn.addActionListener(e -> {
            filterCombo.setSelectedIndex(0);
            loadReservationData("정렬"); 
        });

        // [닫기 버튼 이벤트]
        closeBtn.addActionListener(e -> dispose());

        loadReservationData("전체 보기");
    }

    private void loadReservationData(String filterStatus) {
        tableModel.setRowCount(0);
        
        List<Reservation> reservations = new ArrayList<>(DataManager.getInstance().getReservations());

        if (filterStatus.equals("정렬")) {
            reservations.sort((r1, r2) -> r1.getStatus().compareTo(r2.getStatus()));
            filterStatus = "전체 보기";
        }

        for (Reservation r : reservations) {
            String status = r.getStatus();

            if (filterStatus.equals("전체 보기") || status.startsWith(filterStatus)) {
                String timeInfo = r.getDayOfWeek() + " (" + r.getFromPeriod() + " ~ " + r.getToPeriod() + "교시)";
                
                String details = r.getCompanionDetails();
                String displayDetails = (details == null || details.trim().isEmpty()) ? "(없음)" : details;

                Object[] rowData = {
                        r.getReservationId(),
                        r.getUserId(),
                        r.getRoomId(),
                        timeInfo,
                        r.getCompanionCount() + "명",
                        displayDetails,
                        status
                };
                tableModel.addRow(rowData);
            }
        }
    }
}