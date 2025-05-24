package org.javateam11.ClassroomReservation.view;

import org.javateam11.ClassroomReservation.controller.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//셀 렌더러: SlotStatus에 따라 배경색 칠하기
class StatusCellRenderer extends DefaultTableCellRenderer {
 @Override
 public Component getTableCellRendererComponent(
         JTable table, Object value, boolean isSelected,
         boolean hasFocus, int row, int column)
 {
     Component c = super.getTableCellRendererComponent(
             table, value, isSelected, hasFocus, row, column);
     if (value instanceof SlotStatus) {
         c.setBackground(((SlotStatus) value).getColor());
     }
     setText("");  // 텍스트는 비워두고 색으로만 표현
     return c;
 }
}

/**
 * ReservationDetailView는 시설 상세정보 창 및 예약 타임라인 UI를 담당합니다.
 * 시설의 현재 예약 정보 및 예약 가능한 시간대를 보여줍니다.
 */
public class ReservationDetailView extends JFrame {
	private ReservationDetailController RDcontroller;
	
	public void setController(ReservationDetailController RDcontroller) {
        this.RDcontroller = RDcontroller;
        String title = "상세보기: " + RDcontroller.getName();
        setTitle(title);
    }
	
	public ReservationDetailView() {
		setSize(1200, 350);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(new BorderLayout());
		// 예약하기 버튼 패널
		JPanel Rpanel = new JPanel();
		Rpanel.setLayout(null);
		Rpanel.setPreferredSize(new Dimension(200, 350));
		JButton rv = new JButton("예약하기");
		rv.setBounds(55, 250, 100, 30);
		rv.addActionListener(e -> RDcontroller.onDetailReserveClicked());
		

		JLabel date_label = new JLabel("날짜 (yyyy-MM-dd):");
		date_label.setBounds(30, 40, 150, 25);
		Rpanel.add(date_label);

		JTextField date = new JTextField("");
		date.setBounds(30, 70, 150, 30);
		Rpanel.add(date);

		JButton browse_date = new JButton("예약 조회");
		browse_date.setBounds(30, 120, 150, 30);
		Rpanel.add(browse_date);
		
		// 타임테이블 패널
		// 09:00 ~ 22:00, 30분 단위
        TimelineTableModel model = new TimelineTableModel(9, 22, 30);
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setDefaultRenderer(SlotStatus.class, new StatusCellRenderer());
        table.setTableHeader(null); 
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);  
        // 클릭하면 상태 순환
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                if (col >= 0) {
                    model.toggleStatus(col);
                }
            }
        });
		// 타임 헤더 패널
		JPanel timeHeader = new JPanel(new GridLayout(1, model.getColumnCount(), 1, 1));
        for (int i = 0; i < model.getColumnCount(); i++) {
            String name = model.getColumnName(i);
            JLabel lbl = new JLabel(name, SwingConstants.CENTER);
            lbl.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            timeHeader.add(lbl);
        }
        JPanel timeTable = new JPanel();
        timeTable.setLayout(new BorderLayout());
        timeTable.add(timeHeader, BorderLayout.NORTH);
        timeTable.add(table, BorderLayout.CENTER);
		
		
		Rpanel.add(rv);
		add(Rpanel, BorderLayout.EAST);
		add(timeTable, BorderLayout.CENTER);
	}
}
