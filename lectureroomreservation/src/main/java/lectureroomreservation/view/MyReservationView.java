package lectureroomreservation.view;

import lectureroomreservation.control.ReservationController;
import lectureroomreservation.model.DataManager;
import lectureroomreservation.model.Reservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MyReservationView extends JFrame {
    private ReservationController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private String userId;

    public MyReservationView(String userId) {
        this.userId = userId;
        this.controller = new ReservationController();

        setTitle("내 예약 확인 및 취소");
        setSize(800, 400); // 🚨 컬럼 추가로 인해 창의 가로 폭을 확장했습니다.
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());

        // 상단 안내 문구
        JLabel titleLabel = new JLabel(" '" + userId + "' 님의 강의실 예약 신청 내역입니다.", SwingConstants.LEFT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        add(titleLabel, BorderLayout.NORTH);

        // 🚨 테이블 구조 확장 ("동반인원", "동반자 명단" 컬럼 신규 추가)
        String[] columns = {"예약번호", "강의실", "시간", "동반인원", "동반자 명단", "상태"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 셀 더블클릭 수정 방지
            }
        };
        table = new JTable(tableModel);
        
        // 명단 내용이 길어질 수 있으므로 해당 컬럼 가로폭을 넓게 설정
        table.getColumnModel().getColumn(4).setPreferredWidth(200); 
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 하단 기능 버튼 패널
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton cancelBtn = new JButton("예약 취소");
        JButton closeBtn = new JButton("닫기");

        bottomPanel.add(cancelBtn);
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // [이벤트] 예약 취소 버튼 클릭 시
        cancelBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "취소할 예약을 목록에서 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String resId = (String) tableModel.getValueAt(selectedRow, 0);
            
            int confirm = JOptionPane.showConfirmDialog(this, "정말로 이 예약을 취소하시겠습니까?", "예약 취소 확인", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = controller.cancelReservation(resId, userId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "예약이 성공적으로 취소되었습니다.");
                    loadMyReservationData(); // 테이블 새로고침
                } else {
                    JOptionPane.showMessageDialog(this, "이미 반려되거나 취소된 상태의 예약은 다시 취소할 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // [이벤트] 닫기 버튼 클릭 시
        closeBtn.addActionListener(e -> dispose());

        // 최초 화면이 켜질 때 데이터 로드
        loadMyReservationData();
    }

    // 로그인한 학생의 예약 데이터를 테이블에 매핑하는 메서드
    private void loadMyReservationData() {
        tableModel.setRowCount(0); // 기존 행 초기화
        List<Reservation> reservations = DataManager.getInstance().getReservations();

        for (Reservation r : reservations) {
            // 오직 로그인한 본인(userId)의 예약 데이터만 걸러서 바인딩
            if (r.getUserId().equals(userId)) {
                String timeInfo = r.getDayOfWeek() + " (" + r.getFromPeriod() + " ~ " + r.getToPeriod() + "교시)";
                
                // 🚨 과거 데이터(null 값) 방지를 위한 Null-Safe 처리
                String details = r.getCompanionDetails();
                String displayDetails = (details == null || details.trim().isEmpty()) ? "(없음)" : details;

                Object[] rowData = {
                        r.getReservationId(),
                        r.getRoomId(),
                        timeInfo,
                        r.getCompanionCount() + "명", // 동반 인원 표시
                        displayDetails,               // 동반자 명단 표시
                        r.getStatus()
                };
                tableModel.addRow(rowData);
            }
        }
    }
}