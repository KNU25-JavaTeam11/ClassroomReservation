package org.javateam11.ClassroomReservation.view;

import org.javateam11.ClassroomReservation.controller.ReservationDetailController;
import org.javateam11.ClassroomReservation.model.SlotStatus;
import org.javateam11.ClassroomReservation.dto.ReservationDto;
import org.javateam11.ClassroomReservation.service.ReservationService;
import org.javateam11.ClassroomReservation.service.TokenManager;
import org.javateam11.ClassroomReservation.view.components.StyleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.AbstractTableModel;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;

/**
 * API에서 받아온 예약 데이터를 기반으로 타임라인을 표시
 */
class TimelineTableModel extends AbstractTableModel {
    private final int slotCount;
    private final SlotStatus[] statuses;
    private final String[] columnNames;
    private final LocalTime[] timeSlots;
    private final int intervalMinutes;

    public TimelineTableModel(int startHour, int endHour, int intervalMinutes) {
        this.intervalMinutes = intervalMinutes;
        this.slotCount = (endHour - startHour) * 60 / intervalMinutes;
        this.statuses = new SlotStatus[slotCount];
        this.columnNames = new String[slotCount];
        this.timeSlots = new LocalTime[slotCount];

        // 초기 상태, 컬럼 이름 설정
        for (int i = 0; i < slotCount; i++) {
            statuses[i] = SlotStatus.AVAILABLE;
            int totalMinutes = startHour * 60 + i * intervalMinutes;
            int h = totalMinutes / 60;
            int m = totalMinutes % 60;
            timeSlots[i] = LocalTime.of(h, m);
            columnNames[i] = String.format("%02d:%02d", h, m);
        }
    }

    public void updateWithReservations(List<ReservationDto> reservations, LocalDate queryDate) {
        LocalTime currentTime = LocalTime.now();
        LocalDate currentDate = LocalDate.now();

        // 모든 슬롯을 초기화 (현재 날짜와 시간 기준으로)
        for (int i = 0; i < slotCount; i++) {
            LocalTime slotTime = timeSlots[i];
            LocalTime slotEndTime = slotTime.plusMinutes(intervalMinutes);

            // 조회 날짜가 현재 날짜보다 이전이면 모든 슬롯을 PAST로 설정
            if (queryDate.isBefore(currentDate)) {
                statuses[i] = SlotStatus.PAST;
            }
            // 조회 날짜가 현재 날짜와 같으면 현재 시간 기준으로 판단
            else if (queryDate.isEqual(currentDate)) {
                if (slotEndTime.isBefore(currentTime) || slotEndTime.equals(currentTime)) {
                    statuses[i] = SlotStatus.PAST;
                } else {
                    statuses[i] = SlotStatus.AVAILABLE;
                }
            }
            // 조회 날짜가 현재 날짜보다 이후면 모든 슬롯을 AVAILABLE로 설정
            else {
                statuses[i] = SlotStatus.AVAILABLE;
            }
        }

        // 예약된 시간대를 표시
        for (ReservationDto reservation : reservations) {
            markReservedTime(reservation.getStartTime(), reservation.getEndTime(),
                    reservation.getStudentId(), queryDate, currentDate, currentTime);
        }

        fireTableDataChanged();
    }

    private void markReservedTime(LocalTime startTime, LocalTime endTime, String studentId,
            LocalDate queryDate, LocalDate currentDate, LocalTime currentTime) {
        // 현재 로그인한 사용자의 학번 가져오기
        String currentStudentId = TokenManager.getInstance().getCurrentStudentId();
        boolean isMyReservation = currentStudentId != null && currentStudentId.equals(studentId);

        for (int i = 0; i < slotCount; i++) {
            LocalTime slotTime = timeSlots[i];
            LocalTime slotEndTime = slotTime.plusMinutes(intervalMinutes);

            // 슬롯이 예약 시간과 겹치는지 확인
            if (isTimeOverlap(slotTime, slotEndTime, startTime, endTime)) {
                // 조회 날짜가 현재 날짜보다 이전이면 PAST 상태 유지
                if (queryDate.isBefore(currentDate)) {
                    statuses[i] = SlotStatus.PAST;
                }
                // 조회 날짜가 현재 날짜와 같으면 현재 시간 기준으로 판단
                else if (queryDate.isEqual(currentDate)) {
                    if (slotEndTime.isBefore(currentTime) || slotEndTime.equals(currentTime)) {
                        statuses[i] = SlotStatus.PAST;
                    } else if (slotTime.isBefore(currentTime) && slotEndTime.isAfter(currentTime)) {
                        // 현재 사용 중인 시간대
                        statuses[i] = SlotStatus.IN_USE;
                    } else {
                        // 미래의 예약 - 내 예약인지 확인
                        statuses[i] = isMyReservation ? SlotStatus.MY_RESERVED : SlotStatus.RESERVED;
                    }
                }
                // 조회 날짜가 현재 날짜보다 이후면 예약된 시간으로 표시
                else {
                    statuses[i] = isMyReservation ? SlotStatus.MY_RESERVED : SlotStatus.RESERVED;
                }
            }
        }
    }

    private boolean isTimeOverlap(LocalTime slot1Start, LocalTime slot1End,
            LocalTime slot2Start, LocalTime slot2End) {
        return slot1Start.isBefore(slot2End) && slot2Start.isBefore(slot1End);
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return slotCount;
    }

    @Override
    public Object getValueAt(int row, int col) {
        return statuses[col];
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return SlotStatus.class;
    }

}

/**
 * 스타일이 적용된 셀 렌더러
 */
class StyledStatusCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        if (value instanceof SlotStatus status) {

            // 스타일링된 색상 적용
            switch (status) {
                case AVAILABLE:
                    c.setBackground(new Color(52, 152, 219)); // 하늘색
                    break;
                case RESERVED:
                    c.setBackground(StyleManager.getDangerColor());
                    break;
                case MY_RESERVED:
                    c.setBackground(StyleManager.getSuccessColor()); // 초록색
                    break;
                case IN_USE:
                    c.setBackground(StyleManager.getWarningColor());
                    break;
                case PAST:
                    c.setBackground(new Color(149, 165, 166)); // 진한 회색
                    break;
                default:
                    c.setBackground(StyleManager.getBackgroundColor());
                    break;
            }
        }

        setText(""); // 텍스트는 비워두고 색으로만 표현
        setHorizontalAlignment(SwingConstants.CENTER);
        setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        setOpaque(true);

        return c;
    }
}

/**
 * ReservationDetailView는 시설 상세정보 창 및 예약 타임라인 UI를 담당합니다.
 * 시설의 현재 예약 정보 및 예약 가능한 시간대를 보여줍니다.
 * ReservationDetailController를 통해 컨트롤러와 통신합니다.
 */
public class ReservationDetailView extends JFrame {
    private ReservationDetailController reservationDetailController;
    private ReservationService reservationService;
    private TimelineTableModel timelineModel;
    private JTextField dateField;
    private JTable timelineTable;
    private JLabel roomNameLabel;
    private JLabel buildingNameLavel;
    private Long currentRoomId;

    public void setController(ReservationDetailController reservationDetailController) {
        this.reservationDetailController = reservationDetailController;
        String title = "상세보기: " + reservationDetailController.getName();
        setTitle(title);
        if (roomNameLabel != null) {
            roomNameLabel.setText(reservationDetailController.getName());
        }
        if (buildingNameLavel != null) {
            buildingNameLavel.setText(reservationDetailController.getBuildingName());
        }

        // 창이 열릴 때 자동으로 현재 날짜로 예약 조회 수행
        SwingUtilities.invokeLater(() -> {
            if (dateField != null && timelineModel != null) {
                loadReservationData();
            }
        });
    }

    public ReservationDetailView() {
        this.reservationService = new ReservationService();
        initializeUI();
        setupEventHandlers();
    }

    private void initializeUI() {
        setSize(800, 350);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        setBackground(StyleManager.getBackgroundColor());

        // 메인 컨텐츠 패널
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(StyleManager.getBackgroundColor());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 상단 컨트롤 패널
        JPanel controlPanel = createControlPanel();

        // 중앙 타임라인 패널
        JPanel timelinePanel = createTimelinePanel();

        // 하단 예약하기 버튼 패널
        JPanel bottomPanel = createBottomPanel();

        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(timelinePanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // 현재 날짜로 초기화
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // 초기 타임라인을 현재 시간 기준으로 설정 (빈 예약 리스트로)
        timelineModel.updateWithReservations(new ArrayList<>(), LocalDate.now());
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(760, 100)); // 너비를 줄이고 높이도 조정
        panel.setBackground(StyleManager.getBackgroundColor());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(StyleManager.getComboBorder(), 1),
                new EmptyBorder(15, 20, 15, 20)));

        // 왼쪽: 강의실 정보 및 상태
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leftPanel.setBackground(StyleManager.getBackgroundColor());

        roomNameLabel = StyleManager.createStyledLabel("");
        roomNameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        leftPanel.add(roomNameLabel);

        JLabel separator1 = StyleManager.createStyledLabel("|");
        separator1.setForeground(StyleManager.getComboBorder());
        leftPanel.add(separator1);

        buildingNameLavel = StyleManager.createStyledLabel("");
        buildingNameLavel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        buildingNameLavel.setForeground(StyleManager.getTextColor().brighter());
        leftPanel.add(buildingNameLavel);

        // 중앙: 날짜 입력 및 조회 버튼
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        centerPanel.setBackground(StyleManager.getBackgroundColor());

        JLabel dateLabel = StyleManager.createStyledLabel("조회할 날짜:");
        centerPanel.add(dateLabel);

        dateField = StyleManager.createStyledTextField();
        dateField.setPreferredSize(new Dimension(120, 35));
        centerPanel.add(dateField);

        JButton searchButton = StyleManager.createStyledButton("예약 조회", StyleManager.getPrimaryColor());
        searchButton.setPreferredSize(new Dimension(90, 35));
        searchButton.addActionListener(e -> loadReservationData());
        centerPanel.add(searchButton);

        // 오른쪽: 범례만 표시
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setBackground(StyleManager.getBackgroundColor());

        // 범례를 가로로 배치
        JPanel legendPanel = createHorizontalLegendPanel();
        rightPanel.add(legendPanel);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        panel.setBackground(StyleManager.getBackgroundColor());
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton reserveButton = StyleManager.createStyledButton("예약하기", StyleManager.getSuccessColor());
        reserveButton.setPreferredSize(new Dimension(120, 40));
        reserveButton.addActionListener(e -> {
            if (reservationDetailController != null) {
                reservationDetailController.onDetailReserveClicked();
            }
        });
        panel.add(reserveButton);

        return panel;
    }

    private JPanel createHorizontalLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        legendPanel.setBackground(StyleManager.getBackgroundColor());

        // 범례 항목들을 가로로 배치
        legendPanel.add(createCompactLegendItem("예약가능", new Color(52, 152, 219))); // 하늘색
        legendPanel.add(createCompactLegendItem("예약됨", StyleManager.getDangerColor()));
        legendPanel.add(createCompactLegendItem("내 예약", StyleManager.getSuccessColor())); // 초록색
        legendPanel.add(createCompactLegendItem("이용중", StyleManager.getWarningColor()));

        return legendPanel;
    }

    private JPanel createCompactLegendItem(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        item.setBackground(StyleManager.getBackgroundColor());

        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(12, 12));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JLabel label = new JLabel(text);
        label.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
        label.setForeground(StyleManager.getTextColor());

        item.add(colorBox);
        item.add(label);

        return item;
    }

    private JPanel createTimelinePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(StyleManager.getBackgroundColor());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(StyleManager.getComboBorder(), 1),
                new EmptyBorder(10, 15, 10, 15))); // 여백을 줄임

        // 타이틀
        JLabel titleLabel = StyleManager.createStyledLabel("일일 예약 타임라인");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12)); // 폰트 크기 축소
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 8, 0)); // 여백 축소
        panel.add(titleLabel, BorderLayout.NORTH);

        // 타임테이블 생성 (09:00 ~ 22:00, 30분 단위)
        timelineModel = new TimelineTableModel(9, 22, 30);
        timelineTable = new JTable(timelineModel);
        timelineTable.setRowHeight(40); // 높이를 줄여서 컴팩트하게
        timelineTable.setDefaultRenderer(SlotStatus.class, new StyledStatusCellRenderer());
        timelineTable.setTableHeader(null);
        timelineTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // 화면에 맞게 자동 조정
        timelineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        timelineTable.setBackground(StyleManager.getBackgroundColor());
        timelineTable.setGridColor(Color.WHITE);
        timelineTable.setShowGrid(true);
        timelineTable.setIntercellSpacing(new Dimension(1, 1));

        // 타임라인 컨테이너 패널
        JPanel timelineContainer = new JPanel(new BorderLayout());
        timelineContainer.setBackground(StyleManager.getBackgroundColor());

        // 2시간 단위 주요 눈금 패널 생성
        JPanel ticksPanel = createTicksPanel();
        timelineContainer.add(ticksPanel, BorderLayout.NORTH);

        // 타임라인 테이블을 패널에 직접 추가 (높이 제한)
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(StyleManager.getBackgroundColor());
        tablePanel.add(timelineTable, BorderLayout.CENTER);
        tablePanel.setBorder(BorderFactory.createLineBorder(StyleManager.getComboBorder()));
        tablePanel.setPreferredSize(new Dimension(0, 45)); // 테이블 높이 고정 (40px row + 여백)
        tablePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        // 전체 타임라인 레이아웃
        JPanel fullTimelinePanel = new JPanel(new BorderLayout());
        fullTimelinePanel.setBackground(StyleManager.getBackgroundColor());
        fullTimelinePanel.add(timelineContainer, BorderLayout.NORTH);
        fullTimelinePanel.add(tablePanel, BorderLayout.CENTER);

        // 남는 공간을 최소화하기 위해 전체 높이도 제한
        fullTimelinePanel.setPreferredSize(new Dimension(0, 75)); // 눈금(20px) + 테이블(45px) + 여백(10px)
        fullTimelinePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        panel.add(fullTimelinePanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 주요 눈금 패널 생성 (화면 크기에 맞게 조정)
     */
    private JPanel createTicksPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 13, 0, 0)); // 09~21시 (13개)
        panel.setBackground(StyleManager.getBackgroundColor());
        panel.setPreferredSize(new Dimension(0, 20)); // 높이를 줄임

        // 1시간 단위로 주요 시간 표시
        for (int hour = 9; hour <= 21; hour += 1) {
            JLabel tickLabel = new JLabel(String.format("%02d:00", hour), SwingConstants.LEFT);
            tickLabel.setFont(new Font("맑은 고딕", Font.BOLD, 10));
            tickLabel.setForeground(StyleManager.getPrimaryColor());
            tickLabel.setOpaque(true);
            tickLabel.setBackground(StyleManager.getTopbarColor());
            tickLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 1, StyleManager.getPrimaryColor()),
                    new EmptyBorder(2, 1, 2, 1))); // 여백을 줄임
            panel.add(tickLabel);
        }

        return panel;
    }

    private void setupEventHandlers() {
        // 날짜 필드에서 엔터키 입력 시 조회
        dateField.addActionListener(e -> loadReservationData());
    }

    private void loadReservationData() {
        String dateText = dateField.getText().trim();

        if (dateText.isEmpty()) {
            showError("날짜를 입력해주세요.");
            return;
        }

        try {
            LocalDate date = LocalDate.parse(dateText, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // API 호출하여 해당 날짜의 예약 정보 조회
            reservationService.getReservationsByDate(date,
                    reservations -> onReservationDataLoaded(reservations, date),
                    this::onLoadError);

        } catch (DateTimeParseException e) {
            showError("올바른 날짜 형식(yyyy-MM-dd)을 입력해주세요.");
        }
    }

    private void onReservationDataLoaded(List<ReservationDto> reservations, LocalDate queryDate) {
        // 현재 강의실의 예약만 필터링 (roomId 기준)
        List<ReservationDto> roomReservations = new ArrayList<>();
        if (currentRoomId != null) {
            for (ReservationDto reservation : reservations) {
                if (currentRoomId.equals(reservation.getRoomId())) {
                    roomReservations.add(reservation);
                }
            }
        }

        // 타임라인 모델 업데이트
        timelineModel.updateWithReservations(roomReservations, queryDate);
    }

    private void onLoadError(String errorMessage) {
        showError("예약 정보 조회 실패: " + errorMessage);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
    }

    public void setRoomId(Long roomId) {
        this.currentRoomId = roomId;
    }
}
