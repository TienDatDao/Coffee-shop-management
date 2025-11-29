package view.MainScreen;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainController {

    @FXML private TextField searchField;
    @FXML private FlowPane menuGrid;

    @FXML private TableView<OrderItem> orderTable;
    @FXML private TableColumn<OrderItem, String> colName;
    @FXML private TableColumn<OrderItem, Number> colQty;
    @FXML private TableColumn<OrderItem, Number> colTotal;

    @FXML private Label totalLabel;

    // Dữ liệu
    private List<MenuItem> fullMenu = new ArrayList<>();
    private ObservableList<OrderItem> currentOrder = FXCollections.observableArrayList();

    // Formatter tiền tệ VNĐ
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @FXML
    public void initialize() {
        loadMockData();
        setupTable();
        renderMenuGrid(fullMenu); // Hiển thị tất cả ban đầu

        // Listener tìm kiếm
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleSearch(newValue);
        });
    }

    private void loadMockData() {
        // Giả lập dữ liệu từ MenuService
        fullMenu.add(new MenuItem("1", "Cà phê đen", 25000, "Drink", "/images/black_coffee.png"));
        fullMenu.add(new MenuItem("2", "Cà phê sữa", 30000, "Drink", "/images/milk_coffee.png"));
        fullMenu.add(new MenuItem("3", "Bạc xỉu", 35000, "Drink", "/images/bac_xiu.png"));
        fullMenu.add(new MenuItem("4", "Trà đào", 40000, "Drink", "/images/peach_tea.png"));
        fullMenu.add(new MenuItem("5", "Bánh Croissant", 20000, "Food", "/images/croissant.png"));
        fullMenu.add(new MenuItem("6", "Bánh Tiramisu", 45000, "Food", "/images/tiramisu.png"));
    }

    private void setupTable() {
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colQty.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());

        // Format cột tiền tệ trong bảng
        colTotal.setCellValueFactory(cellData -> cellData.getValue().subtotalProperty());
        colTotal.setCellFactory(tc -> new TableCell<OrderItem, Number>() {
            @Override
            protected void updateItem(Number price, boolean empty) {
                super.updateItem(price, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(currencyFormatter.format(price));
                }
            }
        });

        orderTable.setItems(currentOrder);
    }

    // --- LOGIC GIAO DIỆN (MENU) ---

    // Hàm render các thẻ sản phẩm
    private void renderMenuGrid(List<MenuItem> items) {
        menuGrid.getChildren().clear();
        for (MenuItem item : items) {
            VBox card = createProductCard(item);
            menuGrid.getChildren().add(card);
        }
    }

    private VBox createProductCard(MenuItem item) {
        VBox card = new VBox(5);
        card.setPrefSize(150, 200);
        card.getStyleClass().add("product-card");
        card.setAlignment(Pos.CENTER);

        // Ảnh (Placeholder nếu không tìm thấy)
        ImageView imageView = new ImageView();
        try {
            // Lưu ý: Cần folder resources/images thật hoặc dùng ảnh online
            imageView.setImage(new Image(getClass().getResourceAsStream(item.getImagePath())));
        } catch (Exception e) {
            // Fallback nếu lỗi ảnh
        }
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);

        Label nameLabel = new Label(item.getName());
        nameLabel.getStyleClass().add("card-title");
        nameLabel.setWrapText(true);

        Label priceLabel = new Label(currencyFormatter.format(item.getPrice()));
        priceLabel.getStyleClass().add("card-price");

        card.getChildren().addAll(imageView, nameLabel, priceLabel);

        // Sự kiện click vào thẻ -> Mở Popup (Ở đây làm đơn giản là thêm thẳng)
        card.setOnMouseClicked(event -> {
            // TODO: Ở đây sẽ gọi logic mở Modal Màn hình 2
            // Hiện tại demo thêm trực tiếp
            addToCart(item);
        });

        return card;
    }

    private void handleSearch(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            renderMenuGrid(fullMenu);
            return;
        }
        List<MenuItem> filtered = fullMenu.stream()
                .filter(m -> m.getName().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
        renderMenuGrid(filtered);
    }

    @FXML void filterAll() { renderMenuGrid(fullMenu); }
    @FXML void filterDrink() {
        renderMenuGrid(fullMenu.stream().filter(m -> m.getCategory().equals("Drink")).toList());
    }
    @FXML void filterFood() {
        renderMenuGrid(fullMenu.stream().filter(m -> m.getCategory().equals("Food")).toList());
    }

    // --- LOGIC GIAO DIỆN BÊN PHẢI (ORDER) ---

    private void addToCart(MenuItem item) {
        // Kiểm tra xem món đã có trong giỏ chưa
        for (OrderItem orderItem : currentOrder) {
            if (orderItem.getMenuItem().getId().equals(item.getId())) {
                orderItem.increaseQuantity();
                refreshOrderState();
                return;
            }
        }
        // Nếu chưa có, thêm mới
        OrderItem newItem = new OrderItem(item, 1);
        currentOrder.add(newItem);
        refreshOrderState();
    }

    @FXML
    private void handleIncreaseQty() {
        OrderItem selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.increaseQuantity();
            refreshOrderState();
        }
    }

    @FXML
    private void handleDecreaseQty() {
        OrderItem selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.decreaseQuantity();
            refreshOrderState(); // Subtotal tự tính lại trong Model, nhưng cần refresh tổng đơn
        }
    }

    @FXML
    private void handleRemoveItem() {
        OrderItem selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            currentOrder.remove(selected);
            refreshOrderState();
        }
    }

    private void refreshOrderState() {
        orderTable.refresh(); // Refresh lại UI bảng để hiện số mới
        updateTotal();
    }

    private void updateTotal() {
        double total = 0;
        for (OrderItem item : currentOrder) {
            total += item.subtotalProperty().get();
        }
        totalLabel.setText(currencyFormatter.format(total));
    }

    @FXML
    private void handleApplyDiscount() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Khuyến mãi");
        alert.setHeaderText(null);
        alert.setContentText("Tính năng áp dụng mã giảm giá đang phát triển.");
        alert.showAndWait();
    }

    @FXML
    private void handleCheckout() {
        if (currentOrder.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Giỏ hàng đang trống!");
            alert.showAndWait();
            return;
        }
        // Chuyển sang Màn hình 3 (Payment)
        System.out.println("Chuyển sang màn hình thanh toán với tổng tiền: " + totalLabel.getText());
    }
}