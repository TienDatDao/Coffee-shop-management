package view.MainScreen;

import Interface.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import view.MockTest.*;
import view.PaymentPage.PaymentController;
import view.Wrapper.OrderItemWrapper;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MainController {

    @FXML
    private TextField searchField;
    @FXML
    private FlowPane menuGrid;

    @FXML
    private TableView<OrderItemWrapper> orderTable;
    @FXML
    private TableColumn<OrderItemWrapper, String> colName;
    @FXML
    private TableColumn<OrderItemWrapper, Number> colQty;
    @FXML
    private TableColumn<OrderItemWrapper, Number> colTotal;

    @FXML
    private Label totalLabel;

    // Dữ liệu
    private IMenuService iMenuService = new MockMenuService();
    private IOrderService iOrderService = new MockOrderService();
    private ObservableList<OrderItemWrapper> currentOrder = FXCollections.observableArrayList();

    // Formatter tiền tệ VNĐ
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    @FXML
    public void initialize() {
        loadMockData();
        setupTable();
        renderMenuGrid(iMenuService.getAllItems()); // Hiển thị tất cả ban đầu

        // Listener tìm kiếm
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleSearch(newValue);
        });
    }

    private void loadMockData() {

    }

    private void setupTable() {
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colQty.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());

        // Format cột tiền tệ trong bảng
        colTotal.setCellValueFactory(cellData -> cellData.getValue().subtotalProperty());
        colTotal.setCellFactory(tc -> new TableCell<OrderItemWrapper, Number>() {
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
    private void renderMenuGrid(List<IMenuItem> items) {
        menuGrid.getChildren().clear();
        for (IMenuItem item : items) {
            VBox card = createProductCard(item);
            menuGrid.getChildren().add(card);
        }
    }

    private VBox createProductCard(IMenuItem item) {
        VBox card = new VBox(5);
        card.setPrefSize(150, 200);
        card.getStyleClass().add("product-card");
        card.setAlignment(Pos.CENTER);

        // Ảnh (Placeholder nếu không tìm thấy)
        ImageView imageView = new ImageView();
        try {
            // Lưu ý: Cần folder resources/images thật hoặc dùng ảnh online
            imageView.setImage(item.getImage());
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
            renderMenuGrid(iMenuService.getAllItems());
            return;
        }
        List<IMenuItem> filtered = iMenuService.getAllItems().stream()
                .filter(m -> m.getName().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
        renderMenuGrid(filtered);
    }

    @FXML
    void filterAll() {
        renderMenuGrid(iMenuService.getAllItems());
    }

    @FXML
    void filterDrink() {
        renderMenuGrid(iMenuService.getAllItems().stream().filter(m -> m.getCategory().equals("Drink")).toList());
    }

    @FXML
    void filterFood() {
        renderMenuGrid(iMenuService.getAllItems().stream().filter(m -> m.getCategory().equals("Food")).toList());
    }

    // --- LOGIC GIAO DIỆN BÊN PHẢI (ORDER) ---

    private void addToCart(IMenuItem item) {
        // Kiểm tra xem món đã có trong giỏ chưa
        for (OrderItemWrapper orderItem : currentOrder) {
            if (orderItem.getMenuItem().getId().equals(item.getId())) {
                orderItem.increaseQuantity();
                refreshOrderState();
                return;
            }
        }
        // Nếu chưa có, thêm mới, thêm ở dạng lớp bọc cho fe
        IOrderItem newItem = new OrderItemWrapper(item, 1);
        // ép kiểu thành lớp bọc
        currentOrder.add((OrderItemWrapper) newItem);
        refreshOrderState();
    }

    @FXML
    private void handleIncreaseQty() {
        OrderItemWrapper selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.increaseQuantity();
            refreshOrderState();
        }
    }

    @FXML
    private void handleDecreaseQty() {
        OrderItemWrapper selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.decreaseQuantity();
            refreshOrderState(); // Subtotal tự tính lại trong Model, nhưng cần refresh tổng đơn
        }
    }

    @FXML
    private void handleRemoveItem() {
        IOrderItem selected = orderTable.getSelectionModel().getSelectedItem();
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
        for (OrderItemWrapper item : currentOrder) {
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
    private void handleCheckout() throws IOException {

        if (currentOrder.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Giỏ hàng đang trống!");
            alert.showAndWait();
            return;
        } else {

            // >>> BẮT ĐẦU PHẦN CHUYỂN TRANG <<<
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PaymentPage/Payment.fxml"));
            Parent root = loader.load();

            // 1. TẠO IOrder chính thức TẠI ĐÂY, điền dữ liệu từ Wrapper
            IOrder orderToSend = new MockOrder();

            for (OrderItemWrapper itemWrapper : currentOrder) {
                // Tạo Model Cốt lõi từ dữ liệu của Wrapper
                IOrderItem finalItem = new MockOrderItem(
                        itemWrapper.getMenuItem(),
                        itemWrapper.getQuantity() // Lấy số lượng đã cập nhật
                );
                orderToSend.setListOrderItem(finalItem);
            }
            PaymentController paymentController = loader.getController();
            paymentController.setIOrder(orderToSend);
            Scene scene = menuGrid.getScene();
            scene.setRoot(root);

            // Xóa tất cả stylesheet hiện tại
            scene.getStylesheets().clear();

            // Thêm CSS
                scene.getStylesheets().add(
                        getClass().getResource("/view/PaymentPage/Payment.css").toExternalForm()
                );


            // >>> KẾT THÚC PHẦN CHUYỂN TRANG <<<
        }
    }
}