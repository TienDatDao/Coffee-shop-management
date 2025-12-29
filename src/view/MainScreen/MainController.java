
package view.MainScreen;

import Interface.IMenuItem;
import Interface.IMenuService;
import Interface.IOrder;
import Interface.IOrderItem;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import model.Order;
import model.OrderItem;
import view.Helper.LanguageManager;
import view.Main;
import view.MockTest.MockOrder;
import view.MockTest.MockOrderItem;
import view.PaymentPage.PaymentController;
import view.Wrapper.MenuItemWrapper;
import view.Wrapper.OrderItemWrapper;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController {

    // --- FXML UI COMPONENTS ---
    @FXML private Label dateLabel;
    @FXML private TextField searchField;
    @FXML private FlowPane menuGrid;

    // Sidebar & Navigation
    @FXML private Label lblAppTitle;
    @FXML private Button btnSell;
    @FXML private Button btnManage;
    @FXML private Button btnSetting;
    @FXML private Button btnLogout;

    // Header & Filter
    @FXML private Label lblHeaderTitle;
    @FXML private Button btnFilterAll;
    @FXML private Button btnFilterDrink;
    @FXML private Button btnFilterFood;

    // Order Table Area
    @FXML private Label lblOrderTitle;
    @FXML private TableView<OrderItemWrapper> orderTable;
    @FXML private TableColumn<OrderItemWrapper, String> colName;
    @FXML private TableColumn<OrderItemWrapper, Number> colQty;
    @FXML private TableColumn<OrderItemWrapper, Number> colTotal;
    @FXML private Label lblPlaceholder; // Label hiển thị khi bảng trống

    // Footer Order
    @FXML private Button btnRemove;
    @FXML private Label lblSubTotalTitle;
    @FXML private Label subTotalLabel;
    @FXML private Label lblDiscountTitle;
    @FXML private Label lblTotalTitle;
    @FXML private Label totalLabel;
    @FXML private Button btnCheckout;


    // --- DATA & SERVICES ---
    private List<MenuItemWrapper> fullMenu;
    private IMenuService menuService;
    private ObservableList<OrderItemWrapper> currentOrder = FXCollections.observableArrayList();
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @FXML
    public void initialize() {
        menuService = Main.SHARED_MENU_SERVICE;

        // Listener để áp dụng theme khi scene thay đổi
        menuGrid.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                view.AppConfig.applyTheme(newScene, "/view/MainScreen/Main.css");
            }
        });

// 2. Lấy dữ liệu từ Service
        fullMenu = new ArrayList<>();
        for (IMenuItem item : menuService.getAllItems()) {
            fullMenu.add(new MenuItemWrapper(item));
        }        setupTable();
        renderMenuGrid(fullMenu);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleSearch(newValue);
        });

        // Cập nhật ngôn ngữ ngay khi khởi tạo
        updateLanguage();
    }

    private void updateLanguage() {
        LanguageManager lm = LanguageManager.getInstance();
        Locale currentLocale = lm.getBundle().getLocale();

        // 1. Sidebar
        lblAppTitle.setText(lm.getString("menu.pos")); // Hoặc app.title
        btnSell.setText("🛒  " + lm.getString("menu.sell"));
        btnManage.setText("👪 " + lm.getString("menu.manage"));
        btnSetting.setText("⚙  " + lm.getString("menu.setting"));
        btnLogout.setText("🚪  " + lm.getString("menu.logout"));

        // 2. Header & Date
        lblHeaderTitle.setText(lm.getString("menu.title"));
        String datePattern = currentLocale.getLanguage().equals("vi") ? "EEEE, dd MMM yyyy" : "EEEE, MMM dd yyyy";
        dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern(datePattern, currentLocale)));
        searchField.setPromptText("🔍 " + lm.getString("menu.search"));

        // 3. Filters
        btnFilterAll.setText(lm.getString("menu.filter.all"));
        btnFilterDrink.setText("☕ " + lm.getString("menu.filter.drink"));
        btnFilterFood.setText("🍰 " + lm.getString("menu.filter.food"));

        // 4. Order Table
        lblOrderTitle.setText(lm.getString("menu.currento"));
        colName.setText(lm.getString("menu.dish"));
        colQty.setText(lm.getString("pay.quantity")); // Hoặc tạo key mới menu.qty
        colTotal.setText(lm.getString("menu.money"));
        lblPlaceholder.setText(lm.getString("menu.warning")); // "Chưa có món nào"

        // 5. Footer Order
        btnRemove.setText(lm.getString("menu.delete")); // Hoặc tạo key riêng cho nút Xóa
        lblSubTotalTitle.setText(lm.getString("menu.total01"));
        lblDiscountTitle.setText(lm.getString("menu.giamgia"));
        lblTotalTitle.setText(lm.getString("menu.total02"));
        btnCheckout.setText(lm.getString("menu.pay"));
    }

    private void setupTable() {
        menuGrid.setAlignment(Pos.TOP_LEFT);
        menuGrid.setPadding(new Insets(20, 20, 50, 20));

        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colQty.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());

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
    private void renderMenuGrid(List<MenuItemWrapper> items) {
        menuGrid.getChildren().clear();
        for (MenuItemWrapper item : items) {
            VBox card = createProductCard(item);
            menuGrid.getChildren().add(card);
        }
    }

    private VBox createProductCard(MenuItemWrapper item) {
        VBox card = new VBox(10);
        double cardWidth = 170;
        card.setPrefWidth(cardWidth);
        card.setMaxWidth(cardWidth);
        card.getStyleClass().add("product-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new javafx.geometry.Insets(10));

        ImageView imageView = new ImageView();
        try {
            imageView.setImage(item.getImage());
        } catch (Exception e) {
            // Placeholder nếu lỗi ảnh
        }
        imageView.setFitWidth(130);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(false);

        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(130, 130);
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        imageView.setClip(clip);

        VBox infoBox = new VBox(6);
        infoBox.setAlignment(Pos.CENTER);

        Label nameLabel = new Label(item.getName());
        nameLabel.getStyleClass().add("card-title");
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        nameLabel.setMinHeight(40);

        Label priceLabel = new Label(currencyFormatter.format(item.getPrice()));
        priceLabel.getStyleClass().add("card-price");

        infoBox.getChildren().addAll(nameLabel, priceLabel);
        card.getChildren().addAll(imageView, infoBox);

        card.setOnMouseClicked(e -> {
            addToCart(item.unwrap());
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
        List<MenuItemWrapper> filtered = fullMenu.stream()
                .filter(m -> m.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        renderMenuGrid(filtered);
    }

    @FXML void filterAll() { renderMenuGrid(fullMenu); }
    @FXML void filterDrink() {
        renderMenuGrid(fullMenu.stream().filter(m -> "Drink".equalsIgnoreCase(m.getCategory())).collect(Collectors.toList()));
    }
    @FXML void filterFood() {
        renderMenuGrid(fullMenu.stream().filter(m -> "Food".equalsIgnoreCase(m.getCategory())).collect(Collectors.toList()));
    }

    // --- LOGIC GIAO DIỆN BÊN PHẢI (ORDER) ---
    private void addToCart(IMenuItem item) {
        for (OrderItemWrapper orderItem : currentOrder) {
            if (orderItem.getId().equals(item.getId())) {
                orderItem.increaseQuantity();
                refreshOrderState();
                return;
            }
        }
        OrderItemWrapper newItem = new OrderItemWrapper(item, 1);
        currentOrder.add(newItem);
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
            // --- BẮT ĐẦU CODE TOAST MÀU ĐỎ ---

            Stage toastStage = new Stage();
            toastStage.initOwner(menuGrid.getScene().getWindow());
            toastStage.initStyle(StageStyle.TRANSPARENT);
            toastStage.setAlwaysOnTop(true);

            // Tạo layout chính (Dạng viên thuốc)
            HBox root = new HBox(10); // Khoảng cách giữa icon và chữ là 10
            root.setPadding(new Insets(10, 20, 10, 20)); // Padding trên, phải, dưới, trái
            root.setAlignment(Pos.CENTER);

            // CSS: Màu đỏ, bo tròn 25px, chữ trắng
            root.setStyle("-fx-background-color: #e74c3c; " + // Màu đỏ
                    "-fx-background-radius: 25px; " +   // Bo tròn mạnh
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 2);");

            // Icon dấu than hoặc dấu X màu trắng
            SVGPath icon = new SVGPath();
            // Hình tròn có dấu chấm than
            icon.setContent("M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z");
            icon.setFill(Color.WHITE);

            // Nội dung Text
            Label msgLabel = new Label(LanguageManager.getInstance().getString("menu.warning")); // Hoặc lấy từ LanguageManager
            msgLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

            root.getChildren().addAll(icon, msgLabel);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            toastStage.setScene(scene);

            // Hiển thị trước (với độ mờ = 0) để tính toán được chiều rộng thực tế
            root.setOpacity(0);
            toastStage.show();

            // --- TÍNH TOÁN VỊ TRÍ GÓC DƯỚI GIỮA ---
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            // Tọa độ X = (Chiều rộng màn hình - Chiều rộng thông báo) / 2
            double xPos = screenBounds.getMinX() + (screenBounds.getWidth() - toastStage.getWidth()) / 2;
            // Tọa độ Y = Cách mép dưới 50px
            double yPos = screenBounds.getMaxY() - 80;

            toastStage.setX(xPos);
            toastStage.setY(yPos);

            // Hiệu ứng Fade In -> Chờ -> Fade Out
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            PauseTransition delay = new PauseTransition(Duration.seconds(2)); // Hiện trong 2s

            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            SequentialTransition anim = new SequentialTransition(fadeIn, delay, fadeOut);
            anim.setOnFinished(e -> toastStage.close());
            anim.play();

            // --- KẾT THÚC CODE TOAST ---

            return;
        }

        ResourceBundle bundle = LanguageManager.getInstance().getBundle();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/PaymentPage/Payment.fxml"),
                bundle
        );
        Parent root = loader.load();

        IOrder orderToSend = new Order();
        for (OrderItemWrapper itemWrapper : currentOrder) {
            IOrderItem finalItem = new OrderItem(itemWrapper.getMenuItem(), itemWrapper.getQuantity());
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
    private void logout(){
        changeScene("/view/LoginPage/Login.fxml", LanguageManager.getInstance().getString("login.title"), "/view/LoginPage/Login.css");
    }

    @FXML
    private void menuManager(){
        if(Main.MOCK_AUTH_SERVICE.getCurrentUser().getRole().equals("Manager")) {
            changeScene("/view/MainScreen/MenuManagerPage/MenuManager.fxml", LanguageManager.getInstance().getString("app.title"), "/view/MainScreen/MenuManagerPage/MenuManager.css");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(LanguageManager.getInstance().getString("mainc.notification"));
            alert.setHeaderText(null);
            alert.setContentText(LanguageManager.getInstance().getString("mainc.not")); // "Bạn không có quyền truy cập!"
            alert.showAndWait();
        }
    }

    @FXML
    private void openSettings() {
        changeScene("/view/MainScreen/SettingsPage/Settings.fxml", LanguageManager.getInstance().getString("app.title"), "/view/MainScreen/SettingsPage/Settings.css");
    }

    // Hàm chuyển cảnh chung
    private void changeScene(String fxmlPath, String title, String cssPath) {
        try {
            ResourceBundle bundle = LanguageManager.getInstance().getBundle();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setResources(bundle);

            Parent root = loader.load();
            Stage stage = (Stage) menuGrid.getScene().getWindow();

            // Xử lý riêng cho màn hình Login nếu cần set lại kích thước
            if (fxmlPath.contains("Login.fxml")) {
                Scene scene = new Scene(root, 700, 475);
                scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());

                stage.setMaximized(false);
                stage.setFullScreen(false);
                stage.setScene(scene);
                stage.sizeToScene();
                stage.centerOnScreen();
            } else {
                Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
                view.AppConfig.applyTheme(scene, cssPath);
                stage.setScene(scene);
            }

            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
