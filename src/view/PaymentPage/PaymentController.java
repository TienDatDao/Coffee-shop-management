package view.PaymentPage;

import Interface.IOrder;
import Interface.IOrderItem;
import Interface.IOrderService;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import view.Helper.LanguageManager;
import view.MockTest.MockOrderService;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {

    @FXML private ListView<IOrderItem> listItems;
    @FXML private RadioButton rbCash;
    @FXML private RadioButton rbCard; // QR Code
    @FXML private RadioButton rbWallet; // ATM Card
    @FXML private Label lblTotal;
    @FXML private Button btnPay;
    @FXML private Label lblMessage;
    @FXML private FlowPane flowImages;
    @FXML private Button btnBack;
    @FXML private Label lblSubTotal;
    @FXML private Label lblDiscount;
    @FXML private Label lblPaymentMethodTitle; // Mới thêm

    private ToggleGroup paymentGroup;
    private IOrder iOrder;

    // Formatter sẽ được cập nhật theo ngôn ngữ
    private NumberFormat currencyFormatter;

    private final IOrderService iOrderService = new MockOrderService();

    // --- INITIALIZE ---

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 1. Cấu hình ToggleGroup
        paymentGroup = new ToggleGroup();
        rbCash.setToggleGroup(paymentGroup);
        rbCard.setToggleGroup(paymentGroup);
        rbWallet.setToggleGroup(paymentGroup);
        rbCash.setSelected(true);

        // 2. Load ảnh
        loadImage();

        // 3. Cập nhật ngôn ngữ và formatter
        updateLanguage();
    }

    private void updateLanguage() {
        LanguageManager lm = LanguageManager.getInstance();
        Locale currentLocale = lm.getBundle().getLocale();


        java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        currencyFormatter = new java.text.DecimalFormat("#,### đ", symbols);
        // -------------------------------------------------------------

        // Cập nhật Text các nhãn (Giữ nguyên)
        lblPaymentMethodTitle.setText(lm.getString("pay.method"));
        rbCash.setText(lm.getString("pay.methodcash"));
        rbCard.setText(lm.getString("pay.methodcode"));
        rbWallet.setText(lm.getString("pay.methodcard"));
        btnPay.setText(lm.getString("pay.btn"));
        btnBack.setText("← " + lm.getString("btn.back"));

        // Cập nhật lại số liệu hiển thị
        if (iOrder != null) {
            updateTotalsDisplay();
            loadOrderDetails();
        }
    }

    // --- NHẬN DỮ LIỆU ---

    public void setIOrder(IOrder order) {
        this.iOrder = order;
        loadOrderDetails();
    }

    private void loadOrderDetails() {
        if (this.iOrder == null) return;

        // 1. Cập nhật ListView
        listItems.getItems().setAll(iOrder.getItems());

        // 2. Custom Cell Factory (Cập nhật ngôn ngữ bên trong này)
        listItems.setCellFactory(lv -> new ListCell<IOrderItem>() {
            @Override
            protected void updateItem(IOrderItem item, boolean empty) {
                super.updateItem(item, empty);

                // Quan trọng: Xóa background mặc định của ListCell để không bị vệt xanh/trắng
                setStyle("-fx-background-color: transparent; -fx-padding: 5 10;");

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // 1. KHUNG CHÍNH (HBox) - Đóng vai trò là cái thẻ (Card)
                    HBox cardLayout = new HBox(15);
                    cardLayout.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    cardLayout.getStyleClass().add("list-item-cell"); // Class CSS tạo hình thẻ

                    ImageView itemImage = new ImageView(item.getImage());

                    itemImage.setFitWidth(90);
                    itemImage.setFitHeight(90);

                    javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(90, 90);
                    clip.setArcWidth(25); // Bo góc mềm mại hơn
                    clip.setArcHeight(25);
                    itemImage.setClip(clip);

                    // 3. THÔNG TIN GIỮA (Tên + Loại)
                    VBox centerBox = new VBox(4);
                    centerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                    Label lblName = new Label(item.getName());
                    lblName.getStyleClass().add("item-name");

                    Label lblCategory = new Label(item.getCategory());
                    lblCategory.getStyleClass().add("item-description");

                    centerBox.getChildren().addAll(lblName, lblCategory);

                    // 4. THÔNG TIN PHẢI (Giá, SL, Thành tiền) - Căn lề phải
                    VBox rightBox = new VBox(2);
                    rightBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

                    // Đơn giá
                    Label lblPrice = new Label(currencyFormatter.format(item.getPrice()));
                    lblPrice.getStyleClass().add("item-price-small");

                    // Số lượng
                    Label lblQty = new Label("x" + item.getQuantity());
                    lblQty.getStyleClass().add("item-quantity");

                    // Thành tiền
                    Label lblSubtotal = new Label(currencyFormatter.format(item.getSubtotal()));
                    lblSubtotal.getStyleClass().add("item-subtotal");

                    rightBox.getChildren().addAll(lblSubtotal, lblQty, lblPrice); // Để thành tiền lên đầu hoặc cuối tùy bạn

                    // --- ĐẨY CÁC PHẦN RA XA NHAU ---
                    // Region ở giữa sẽ đẩy rightBox sang tận cùng bên phải
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                    cardLayout.getChildren().addAll(itemImage, centerBox, spacer, rightBox);
                    setGraphic(cardLayout);
                }
            }
        });

        // 3. Cập nhật các Label tổng
        updateTotalsDisplay();
    }

    private void updateTotalsDisplay() {
        if (iOrder == null) return;
        LanguageManager lm = LanguageManager.getInstance();

        double sumSubtotal = iOrder.getItems().stream().mapToDouble(IOrderItem::getSubtotal).sum();
        double sumTotal = iOrder.getTotalPrice();

        // pay.subtotal ("Thành tiền")
        lblSubTotal.setText(lm.getString("pay.subtotal") + ": " + currencyFormatter.format(sumSubtotal));

        // pay.total ("Tổng tiền")
        lblTotal.setText(lm.getString("pay.total") + ": " + currencyFormatter.format(sumTotal));

        double discount = sumSubtotal - sumTotal;
        // String format cho Discount (cần check trong properties file xem có placeholder %s không)
        // Nếu file prop là: pay.discount=Giảm giá: -%s
        try {
            lblDiscount.setText(String.format(lm.getString("pay.discount"), currencyFormatter.format(discount)));
        } catch (Exception e) {
            // Fallback nếu string format lỗi
            lblDiscount.setText("Discount: -" + currencyFormatter.format(discount));
        }
    }

    private void loadImage(){
        // (Giữ nguyên code cũ, chỉ đảm bảo file ảnh tồn tại)
        try {
            ImageView iv = new ImageView("/view/PaymentPage/ListPaymentMethod.png");
            iv.setFitWidth(200);
            iv.setFitHeight(200);
            iv.setPreserveRatio(true);
            flowImages.getChildren().add(iv);
        } catch (Exception e) {
            System.err.println("Không thể load ảnh thanh toán");
        }
    }

    // --- SỰ KIỆN ---

    @FXML
    private void handlePay() {
        LanguageManager lm = LanguageManager.getInstance();
        RadioButton selected = (RadioButton) paymentGroup.getSelectedToggle();

        if (selected == null) {
            // pay.choicePay ("Vui lòng chọn phương thức...")
            lblMessage.setText(lm.getString("pay.choicePay"));
            lblMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        iOrderService.createOrder(iOrder.getTableId(), iOrder.getItems(), iOrder.getOrderId());

        if (selected == rbCard) { // QR Code
            showConfirmationDialog();
        } else {
            // pay.success ("Thanh toán thành công qua...")
            lblMessage.setText(lm.getString("pay.success") + " " + selected.getText() + "!");
            lblMessage.setStyle("-fx-text-fill: green;");
            delayThenRun(1, () -> {
                try {
                    goToPage("/view/MainScreen/MainView.fxml", "/view/MainScreen/Main.css");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @FXML
    private void handleBack(){
        try {
            goToPage("/view/MainScreen/MainView.fxml", "/view/MainScreen/Main.css");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void delayThenRun(double seconds, Runnable action) {
        PauseTransition pause = new PauseTransition(Duration.seconds(seconds));
        pause.setOnFinished(e -> action.run());
        pause.play();
    }

    private void goToPage(String fxmlPath, String cssPath) throws IOException {
        // Quan trọng: Truyền LanguageBundle khi quay lại Main
        ResourceBundle bundle = LanguageManager.getInstance().getBundle();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setResources(bundle);

        Parent root = loader.load();
        Scene scene = btnPay.getScene();
        scene.setRoot(root);

        scene.getStylesheets().clear();
        if (cssPath != null) {
            // Áp dụng theme (dark/light) và css riêng
            view.AppConfig.applyTheme(scene, cssPath);
        }

        // Cập nhật title cửa sổ nếu cần
        ((javafx.stage.Stage)scene.getWindow()).setTitle(LanguageManager.getInstance().getString("app.title"));
    }

    private void showConfirmationDialog() {
        LanguageManager lm = LanguageManager.getInstance();
        Dialog<ButtonType> dialog = new Dialog<>();

        URL cssUrl = getClass().getResource("/view/MainScreen/MenuManagerPage/Dialog/ItemDialog.css");
        if (cssUrl != null) {
            dialog.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
            dialog.getDialogPane().getStyleClass().add("dialog-root");
            dialog.setHeaderText(null);
        }

        Random random = new Random();
        int x = random.nextInt(1, 3);
        String imagePath = (x == 1) ? "/view/PaymentPage/QRPay1.jpg" : "/view/PaymentPage/QRPay2.jpg";
        ImageView qrImage = new ImageView(getClass().getResource(imagePath).toExternalForm());
        qrImage.setFitWidth(300);
        qrImage.setFitHeight(300);
        qrImage.setPreserveRatio(true);

        VBox content = new VBox(15);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        content.setPadding(new Insets(30));

        // pay.createOrder ("Tạo đơn thành công")
        Label title = new Label(lm.getString("pay.createOrder"));
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4F46E5;");

        // pay.goiY ("Vui lòng quét mã...")
        Label instruction = new Label(lm.getString("pay.goiY"));
        instruction.setStyle("-fx-text-fill: #475569;");

        content.getChildren().addAll(title, instruction, qrImage);
        dialog.getDialogPane().setContent(content);

        // pay.close ("Đóng & Quay lại")
        ButtonType closeButton = new ButtonType(lm.getString("pay.close"), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        final Button finishButton = (Button) dialog.getDialogPane().lookupButton(closeButton);
        finishButton.getStyleClass().addAll("dialog-button", "confirm");

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == closeButton || dialogButton == ButtonType.CLOSE) {
                delayThenRun(1, () -> {
                    try {
                        goToPage("/view/MainScreen/MainView.fxml", "/view/MainScreen/Main.css");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            return null;
        });

        dialog.showAndWait();

        // pay.sc ("Thanh toán thành công")
        lblMessage.setText(lm.getString("pay.sc"));
        lblMessage.setStyle("-fx-text-fill: green;");
    }
}