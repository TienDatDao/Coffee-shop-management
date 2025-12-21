package view.MainScreen.StatisticsPage;

import Interface.IOrder;
import Interface.IOrderItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import view.MockTest.MockOrder;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class StatisticsController {

    @FXML private ComboBox<String> timeGranularityComboBox;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private BarChart<String, Number> revenueChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    // Sẵn sàng nhận List<IOrder> từ Mock hoặc Data Access Layer
    private List<IOrder> allOrders;

    @FXML
    public void initialize() {
        // 1. Khởi tạo dữ liệu mẫu (sử dụng MockOrder)
        loadSampleOrders();

        // 2. Thiết lập ComboBox "Thống kê theo"
        ObservableList<String> options = FXCollections.observableArrayList(
                "Ngày (Theo tuần)",
                "Tháng (Theo năm)",
                "Năm"
        );
        timeGranularityComboBox.setItems(options);
        timeGranularityComboBox.getSelectionModel().select("Ngày (Theo tuần)");

        // 3. Khởi tạo biểu đồ với dữ liệu ban đầu
        updateFilterOptions("Ngày (Theo tuần)");
        timeGranularityComboBox.getSelectionModel().selectFirst();
        updateChart();
    }

    // Phương thức này đã được sửa tên onAction trong FXML
    @FXML
    void timeGranularityComboBoxChanged() {
        String granularity = timeGranularityComboBox.getSelectionModel().getSelectedItem();
        if (granularity != null) {
            updateFilterOptions(granularity);
            updateChart();
        }
    }

    // --- CHỨC NĂNG CƠ BẢN ---

    @FXML
    void updateChart() {
        String granularity = timeGranularityComboBox.getSelectionModel().getSelectedItem();
        String filterValue = filterComboBox.getSelectionModel().getSelectedItem();

        if (granularity == null) return;

        revenueChart.getData().clear();
        yAxis.setLabel("Doanh thu (VNĐ)");

        Map<String, Double> revenueData;

        if ("Ngày (Theo tuần)".equals(granularity)) {
            revenueData = getWeeklyRevenueData(filterValue);
        } else if ("Tháng (Theo năm)".equals(granularity)) {
            revenueData = getMonthlyRevenueData(filterValue);
        } else { // "Năm"
            revenueData = getYearlyRevenueData();
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");

        // Thêm dữ liệu (chỉ thêm những cột có doanh thu > 0)
        revenueData.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));

        revenueChart.getData().add(series);
    }

    // ... (Giữ nguyên updateFilterOptions và refreshData)
    @FXML
    void updateFilterOptions(String granularity) {
        ObservableList<String> filterOptions = FXCollections.observableArrayList();

        if ("Ngày (Theo tuần)".equals(granularity)) {
            filterOptions.add("Tuần hiện tại");
            filterOptions.add("Tuần trước");
            filterOptions.add("Tất cả");
            filterComboBox.setItems(filterOptions);
            filterComboBox.getSelectionModel().selectFirst();

        } else if ("Tháng (Theo năm)".equals(granularity)) {
            DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            for (int i = 0; i < 6; i++) {
                filterOptions.add(LocalDateTime.now().minusMonths(i).format(monthFormatter));
            }
            filterOptions.add("Tất cả");
            filterComboBox.setItems(filterOptions);
            filterComboBox.getSelectionModel().selectFirst();
        } else {
            filterComboBox.setItems(FXCollections.observableArrayList("Tất cả"));
            filterComboBox.getSelectionModel().selectFirst();
        }
    }

    @FXML
    void refreshData() {
        loadSampleOrders();
        updateChart();
        System.out.println("Đã tải lại dữ liệu Order.");
    }

    // --- LOGIC THỐNG KÊ (ĐÃ SỬA LỖI TRUY CẬP PHƯƠNG THỨC) ---

    private Map<String, Double> getWeeklyRevenueData(String filter) {
        xAxis.setLabel("Thứ");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(DayOfWeek.MONDAY).truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endOfWeek = now.with(DayOfWeek.SUNDAY).plusDays(1).truncatedTo(ChronoUnit.DAYS);

        if ("Tuần trước".equals(filter)) {
            startOfWeek = startOfWeek.minusWeeks(1);
            endOfWeek = endOfWeek.minusWeeks(1);
        }

        Map<String, Double> revenueByDay = Arrays.asList(DayOfWeek.values()).stream()
                .collect(Collectors.toMap(
                        d -> d.getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("vi")),
                        d -> 0.0
                ));

        LocalDateTime finalStartOfWeek = startOfWeek;
        LocalDateTime finalEndOfWeek = endOfWeek;
        allOrders.stream()
                // SỬA: Dùng getLocalDateTime()
                .filter(order -> order.getLocalDateTime().isAfter(finalStartOfWeek) && order.getLocalDateTime().isBefore(finalEndOfWeek))
                .forEach(order -> {
                    // SỬA: Dùng getLocalDateTime()
                    String dayName = order.getLocalDateTime().getDayOfWeek()
                            .getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("vi"));
                    // SỬA: Dùng getTotalPrice()
                    revenueByDay.merge(dayName, order.getTotalPrice(), Double::sum);
                });

        return revenueByDay;
    }

    private Map<String, Double> getMonthlyRevenueData(String filter) {
        xAxis.setLabel("Ngày trong Tháng");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetMonth = now;

        if (!"Tất cả".equals(filter) && filter != null) {
            // Logic phân tích tháng/năm
        }

        LocalDateTime startOfMonth = targetMonth.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endOfMonth = targetMonth.plusMonths(1).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);

        Map<String, Double> revenueByDayOfMonth = allOrders.stream()
                // SỬA: Dùng getLocalDateTime()
                .filter(order -> order.getLocalDateTime().isAfter(startOfMonth) && order.getLocalDateTime().isBefore(endOfMonth))
                .collect(Collectors.groupingBy(
                        // SỬA: Dùng getLocalDateTime()
                        order -> String.valueOf(order.getLocalDateTime().getDayOfMonth()),
                        // SỬA: Dùng getTotalPrice()
                        Collectors.summingDouble(IOrder::getTotalPrice)
                ));

        return revenueByDayOfMonth;
    }

    private Map<String, Double> getYearlyRevenueData() {
        xAxis.setLabel("Tháng");

        int currentYear = LocalDateTime.now().getYear();

        Map<String, Double> revenueByMonth = allOrders.stream()
                // SỬA: Dùng getLocalDateTime()
                .filter(order -> order.getLocalDateTime().getYear() == currentYear)
                .collect(Collectors.groupingBy(
                        // SỬA: Dùng getLocalDateTime()
                        order -> order.getLocalDateTime().format(DateTimeFormatter.ofPattern("MM/yyyy")),
                        // SỬA: Dùng getTotalPrice()
                        Collectors.summingDouble(IOrder::getTotalPrice)
                ));

        return revenueByMonth;
    }

    // --- DỮ LIỆU MẪU (SỬA ĐỂ TẠO MockOrder) ---

    private void loadSampleOrders() {

    }

    // Hàm hỗ trợ tạo MockOrder với thời gian cụ thể
    private IOrder createMockOrder(LocalDateTime time, List<IOrderItem> items) {
        MockOrder order = new MockOrder("T01", items, "ORD" + new Random().nextInt(1000));
        order.setLocalDateTime(time);
        return order;
    }

    // --- PHƯƠNG THỨC CHUYỂN CẢNH ---

    @FXML
    void mainScreen() {
        System.out.println("Chuyển đến màn hình Bán hàng...");
    }

    @FXML
    void statisticsScreen() {
        // Đang ở màn hình thống kê
    }

    @FXML
    void menuManagerScreen() {
        System.out.println("Chuyển đến màn hình Quản lý Thực đơn...");
    }

    @FXML
    void settingsScreen() {
        System.out.println("Chuyển đến màn hình Cài đặt...");
    }

    @FXML
    void logout() {
        System.out.println("Đăng xuất...");
    }
}