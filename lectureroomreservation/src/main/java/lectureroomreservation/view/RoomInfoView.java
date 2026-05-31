package lectureroomreservation.view;

import lectureroomreservation.model.DataManager;
import lectureroomreservation.model.LectureRoom;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RoomInfoView extends JFrame {
    
    public RoomInfoView() {
        setTitle("강의실 및 기자재 정보 조회");
        setSize(450, 300);
        setLocationRelativeTo(null);

        String[] columnNames = {"강의실 호수", "구분", "수용 인원", "프로젝터 유무"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        
        List<LectureRoom> rooms = DataManager.getInstance().getLectureRooms();
        for (LectureRoom room : rooms) {
            String projector = room.hasProjector() ? "O (있음)" : "X (없음)";
            
            // [수정 완료] 오염된 변동성 데이터 room.getCapacity() 대신 진짜 총 수용인원(getOriginalCapacity)을 매핑합니다.
            Object[] rowData = { 
                room.getRoomId(), 
                room.getRoomType(), 
                room.getOriginalCapacity() + "명", 
                projector 
            };
            model.addRow(rowData);
        }

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton closeBtn = new JButton("닫기");
        closeBtn.addActionListener(e -> dispose());
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}