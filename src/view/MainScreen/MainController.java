package view.MainScreen;

import Interface.IMenuItem;
import Interface.IMenuService;
import Interface.IOrder;
import Interface.IOrderItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import view.Helper.LanguageManager;
import view.MainTest;
import view.MockTest.MockOrder;
import view.MockTest.MockOrderItem;
import view.PaymentPage.PaymentController;
import view.Wrapper.OrderItemWrapper;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController {

    // --- FXML UI COMPONENTS ---
    @FXML private Label dateLabel;
    @FXML private TextField searchField;
    @FXML private FlowPane menuGrid;

    @FXML private TableView<OrderItemWrapper> orderTable; // TableView vẫn dùng Class cụ thể để bind property
    @FXML private TableColumn<OrderItemWrapper, String> colName;
    @FXML private TableColumn<OrderItemWrapper, Number> colQty;
    @FXML private TableColumn<OrderItemWrapper, Number> colTotal;

    @FXML private Label subTotalLabel;
    @FXML private Label totalLabel;

    // --- DATA & SERVICES ---
    private List<IMenuItem> fullMenu;

    // Gọi Service thông qua Interface
    private IMenuService menuService;

    // Danh sách hiển thị trên bảng order
    private ObservableList<OrderItemWrapper> currentOrder = FXCollections.observableArrayList();

    // Formatter tiền tệ VNĐ
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @FXML
    public void initialize() {
        // 1. Khởi tạo Service
        menuService = MainTest.SHARED_MENU_SERVICE;

        menuGrid.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                view.AppConfig.applyTheme(newScene);
            }
        });

        // 2. Lấy dữ liệu từ Service
        fullMenu = menuService.getAllItems();

        // lấy ngày hiện tại
        dateLabel.setText(LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy", Locale.forLanguageTag("vi-VN"))));
        // 3. Setup giao diện
        setupTable();
        renderMenuGrid(fullMenu); // Hiển thị tất cả ban đầu

        // 4. Listener tìm kiếm
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleSearch(newValue);
        });

    }

    private void setupTable() {
        // Thêm đoạn này để menu luôn căn chỉnh đẹp
        menuGrid.setAlignment(Pos.TOP_CENTER);
        menuGrid.setPadding(new Insets(20, 20, 50, 20)); // Padding dưới 50 để ko bị che bởi mép màn hình
        // Cấu hình cột cho bảng Order
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colQty.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());

        // Format cột tiền tệ
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

    // Hàm render nhận vào danh sách Interface IMenuItem
    private void renderMenuGrid(List<IMenuItem> items) {
        menuGrid.getChildren().clear();
        for (IMenuItem item : items) {
            VBox card = createProductCard(item);
            menuGrid.getChildren().add(card);
        }
    }

    // Tạo thẻ sản phẩm từ Interface
    private VBox createProductCard(IMenuItem item) {
        VBox card = new VBox(10); //
        double cardWidth = 170;   // Tăng độ rộng thẻ một chút
        card.setPrefWidth(cardWidth);
        card.setMaxWidth(cardWidth);
        card.getStyleClass().add("product-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new javafx.geometry.Insets(10)); // Padding nội bộ thẻ

        // --- XỬ LÝ ẢNH ---
        ImageView imageView = new ImageView();

        // Xử lý trường hợp ảnh lỗi hoặc null (Best practice)
        try {
            imageView.setImage(item.getImage());
        } catch (Exception e) {
            // Có thể set ảnh placeholder ở đây nếu muốn
        }

        imageView.setFitWidth(130);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(false); // Giữ tỷ lệ ảnh

        // Bo tròn ảnh (Soft square)
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(130, 130);
        clip.setArcWidth(30); // Bo tròn nhiều hơn cho mềm mại
        clip.setArcHeight(30);
        imageView.setClip(clip);

        // --- THÔNG TIN TEXT ---
        VBox infoBox = new VBox(6);
        infoBox.setAlignment(Pos.CENTER);

        Label nameLabel = new Label(item.getName());
        nameLabel.getStyleClass().add("card-title");
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        nameLabel.setMinHeight(40); // Cố định chiều cao tên để thẻ đều nhau

        Label priceLabel = new Label(currencyFormatter.format(item.getPrice()));
        priceLabel.getStyleClass().add("card-price");

        infoBox.getChildren().addAll(nameLabel, priceLabel);
        card.getChildren().addAll(imageView, infoBox);

        // --- SỰ KIỆN CLICK & ANIMATION ---
        card.setOnMouseClicked(e -> {
            addToCart(item);
            // Hiệu ứng nhún nhẹ khi click
            javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(100), card);
            st.setFromX(1.0); st.setFromY(1.0);
            st.setToX(0.95); st.setToY(0.95);
            st.setAutoReverse(true);
            st.setCycleCount(2);
            st.play();
        });

        return card;
    }

    private void handleSearch(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            renderMenuGrid(fullMenu);
            return;
        }
        // Stream filter trên Interface
        List<IMenuItem> filtered = fullMenu.stream()
                .filter(m -> m.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        renderMenuGrid(filtered);
    }

    // Các nút lọc danh mục
    @FXML void filterAll() { renderMenuGrid(fullMenu); }
    @FXML void filterDrink() {
        renderMenuGrid(fullMenu.stream().filter(m -> "Drink".equalsIgnoreCase(m.getCategory())).collect(Collectors.toList()));
    }
    @FXML void filterFood() {
        renderMenuGrid(fullMenu.stream().filter(m -> "Food".equalsIgnoreCase(m.getCategory())).collect(Collectors.toList()));
    }

    // --- LOGIC GIAO DIỆN BÊN PHẢI (ORDER) ---

    // Tham số đầu vào là Interface IMenuItem
    private void addToCart(IMenuItem item) {
        // Kiểm tra xem món đã có trong giỏ chưa
        for (OrderItemWrapper orderItem : currentOrder) {
            // So sánh ID thông qua Interface
            if (orderItem.getId().equals(item.getId())) {
                orderItem.increaseQuantity();
                refreshOrderState();
                return;
            }
        }
        // Nếu chưa có, tạo mới OrderItem (OrderItem nhận vào IMenuItem trong constructor)
        OrderItemWrapper newItem = new OrderItemWrapper(item, 1);
        currentOrder.add( newItem);
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
            if (selected.quantityProperty().get() == 0 ) {
                currentOrder.remove(selected);
            }
            refreshOrderState();
        }
    }

    @FXML
    private void handleRemoveItem() {
        OrderItemWrapper selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            currentOrder.remove(selected);
            refreshOrderState();
        }
    }

    private void refreshOrderState() {
        orderTable.refresh();
        updateTotal();
    }

    private void updateTotal() {
        double total = 0;
        for (OrderItemWrapper item : currentOrder) {
            total += item.subtotalProperty().get();
        }
        String formatted = currencyFormatter.format(total);
        totalLabel.setText(formatted);
        if(subTotalLabel != null) subTotalLabel.setText(formatted);
    }

    @FXML
    private void handleCheckout() throws IOException {
        if (currentOrder.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Giỏ hàng đang trống!");
            alert.showAndWait();
            return;
        }

        ResourceBundle bundle = LanguageManager.getInstance().getBundle();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/PaymentPage/Payment.fxml"),
                bundle
        );
        Parent root = loader.load();

        IOrder orderToSend = new MockOrder();
        for (OrderItemWrapper itemWrapper : currentOrder) {
            IOrderItem finalItem = new MockOrderItem(itemWrapper.getMenuItem(), itemWrapper.getQuantity());
            orderToSend.setListOrderItem(finalItem);
        }

        PaymentController paymentController = loader.getController();
        paymentController.setIOrder(orderToSend);

        Scene scene = menuGrid.getScene();
        scene.setRoot(root);

        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/view/PaymentPage/Payment.css").toExternalForm());
    }
    @FXML
    private void menuManager(){
        if(MainTest.MOCK_AUTH_SERVICE.getCurrentUser().getRole().equals("Manager")) {
            // >>> BẮT ĐẦU PHẦN CHUYỂN TRANG <<<
            try {
                // 1. Lấy Stage hiện tại (từ bất kỳ thành phần nào trên Scene)
                Stage currentStage = (Stage) menuGrid.getScene().getWindow();
                ResourceBundle bundle = LanguageManager.getInstance().getBundle();

                // 2. Tải FXML của màn hình chính
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen/MenuManagerPage/MenuManager.fxml"));
                loader.setResources(bundle);
                // 3. Tải Root Node
                Parent root = loader.load();

                // 4. Tạo Scene mới và thiết lập Stage
                Scene scene = new Scene(root);
                view.AppConfig.applyTheme(scene);
                scene.getStylesheets().add(
                        getClass().getResource("/view/MainScreen/MenuManagerPage/MenuManager.css").toExternalForm()
                );

                //  Đặt tiêu đề mới cho cửa sổ
                currentStage.setTitle("Coffee Shop Management - Welcome ");
                currentStage.setScene(scene);
                currentStage.show();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Bạn không có quyền truy cập!");
            alert.showAndWait();        }
    }
    @FXML
    private void logout(){
        try {
            ResourceBundle bundle = LanguageManager.getInstance().getBundle();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/LoginPage/Login.fxml"),
                    bundle
            );
            Parent root = loader.load();
            Stage stage = (Stage) menuGrid.getScene().getWindow();

            Scene scene = new Scene(root, 1000, 600);
            scene.getStylesheets().add(
                    getClass().getResource("/view/LoginPage/Login.css").toExternalForm()
            );

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Không thể tải trang đăng nhập.");
        }
    }

    @FXML
    private void openSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen/SettingsPage/Settings.fxml"));
            ResourceBundle bundle = LanguageManager.getInstance().getBundle();
            loader.setResources(bundle);
            Parent root = loader.load();
            Stage stage = (Stage) menuGrid.getScene().getWindow();

            // Giữ kích thước cũ
            Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());

            // >>> THÊM DÒNG NÀY <<<
            view.AppConfig.applyTheme(scene);

            stage.setTitle("Cài đặt hệ thống");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}