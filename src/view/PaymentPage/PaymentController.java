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
import service.OrderService;
import view.Helper.LanguageManager;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {

    @FXML private ListView<IOrderItem> listItems;
    @FXML private RadioButton rbCash;
    @FXML private RadioButton rbCard;
    @FXML private RadioButton rbWallet;
    @FXML private Label lblTotal;
    @FXML private Button btnPay;
    @FXML private Label lblMessage;
    @FXML private FlowPane flowImages;
    @FXML private Button btnBack;
    @FXML private Label lblSubTotal;
    @FXML private Label lblDiscount;

    private ToggleGroup paymentGroup;
    private IOrder iOrder; // Đối tượng Order được truyền từ MainController

    // Formatter tiền tệ VNĐ
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    // Dịch vụ Order (sử dụng để tạo/lưu Order khi thanh toán)
    private final IOrderService iOrderService = new OrderService();

    // --- PHƯƠNG THỨC NHẬN DỮ LIỆU TỪ CONTROLLER KHÁC ---

    /**
     * Nhận đối tượng IOrder từ MainController và kích hoạt tải dữ liệu.
     * Phương thức này được gọi SAU initialize().
     */
    public void setIOrder(IOrder order) {
        this.iOrder = order;
        // TẢI DỮ LIỆU SAU KHI IORDER ĐÃ ĐƯỢC GÁN
        loadOrderDetails();
    }

    // --- INITIALIZE VÀ LOGIC TẢI DỮ LIỆU ---

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Chỉ thiết lập các thành phần UI

        // Thiết lập ToggleGroup
        paymentGroup = new ToggleGroup();
        rbCash.setToggleGroup(paymentGroup);
        rbCard.setToggleGroup(paymentGroup);
        rbWallet.setToggleGroup(paymentGroup);
        rbCash.setSelected(true);

        // Load ảnh của các phương thức/ngân hàng
        loadImage();
    }

    /**
     * Tải dữ liệu từ đối tượng IOrder đã được truyền và cập nhật UI.
     * Chỉ gọi sau khi iOrder đã được gán (trong setOrder).
     */
    private void loadOrderDetails() {
        if (this.iOrder == null) {
            // Đây là lớp bảo vệ, không nên xảy ra nếu MainController gọi setOrder
            System.err.println("Lỗi: iOrder chưa được gán giá trị!");
            return;
        }

        // 1. Cập nhật ListView
        // Dùng getItems() từ IOrder để lấy danh sách IOrderItem (Wrapper Class)
        listItems.getItems().setAll(iOrder.getItems());

        // 2. Custom mỗi item dạng Box
        listItems.setCellFactory(lv -> new ListCell<IOrderItem>() {
            @Override
            protected void updateItem(IOrderItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    // Dùng BorderPane: Trái chứa Ảnh+Tên, Phải chứa Tiền
                    BorderPane cellLayout = new BorderPane();
                    cellLayout.getStyleClass().add("list-item-cell");
                    cellLayout.setPadding(new Insets(10, 15, 10, 15));

                    // --- PHẦN TRÁI (LEFT): Ảnh + Tên món ---
                    HBox leftBox = new HBox(15); // Khoảng cách giữa ảnh và tên là 15
                    leftBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                    ImageView itemImage = new ImageView(item.getImage());
                    itemImage.setFitWidth(80);
                    itemImage.setFitHeight(80);
                    // Bo tròn ảnh nhẹ nhàng
                    javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(80, 80);
                    clip.setArcWidth(20); clip.setArcHeight(20);
                    itemImage.setClip(clip);

                    VBox infoBox = new VBox(5);
                    infoBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                    Label lblName = new Label(item.getName());
                    lblName.getStyleClass().add("item-name");

                    Label lblPrice = new Label(LanguageManager.getInstance().getString("pay.price") + " " + currencyFormatter.format(item.getPrice()));
                    lblPrice.getStyleClass().add("item-price-small");

                    infoBox.getChildren().addAll(lblName, lblPrice);
                    leftBox.getChildren().addAll(itemImage, infoBox);

                    cellLayout.setLeft(leftBox);

                    // --- PHẦN PHẢI (RIGHT): Số lượng + Tổng tiền ---
                    VBox rightBox = new VBox(5);
                    rightBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

                    Label lblQty = new Label(LanguageManager.getInstance().getString("pay.quantity") + " " + item.getQuantity());
                    lblQty.getStyleClass().add("item-quantity");

                    Label lblSubtotal = new Label(currencyFormatter.format(item.getSubtotal()));
                    lblSubtotal.getStyleClass().add("item-subtotal");
                    // Căn lề phải cho text
                    lblSubtotal.setTextAlignment(javafx.scene.text.TextAlignment.RIGHT);

                    rightBox.getChildren().addAll(lblQty, lblSubtotal);
                    cellLayout.setRight(rightBox);

                    setGraphic(cellLayout);
                }
            }
        });

        // 3. Cập nhật tổng tiền và ưu đãi
        updateTotalsDisplay();
    }

    private void loadImage(){
        String[] files = {
                "/view/PaymentPage/ListPaymentMethod.png"
        };
        for (String file : files) {
            try {
                ImageView iv = new ImageView(file);
                iv.setFitWidth(200);
                iv.setFitHeight(200);
                iv.setPreserveRatio(true);
                flowImages.getChildren().add(iv);
            } catch (Exception e) {
                System.err.println(LanguageManager.getInstance().getString("pay.warningpt") + file);
            }
        }
    }

    /**
     * Cập nhật các Label tổng tiền, Subtotal và Discount.
     * Chỉ gọi sau khi iOrder đã được gán.
     */
    // Trong PaymentController.java

    private void updateTotalsDisplay() {
        if (iOrder == null) return;

        double sumSubtotal = iOrder.getItems().stream().mapToDouble(IOrderItem::getSubtotal).sum();
        double sumTotal = iOrder.getTotalPrice();

        String textTongTien = LanguageManager.getInstance().getString("pay.total"); // Giả sử key là "Tổng tiền"
        String textThanhTien = LanguageManager.getInstance().getString("pay.subtotal"); // Giả sử key là "Thành tiền"

        // Hoặc nếu bạn muốn gõ trực tiếp tiếng Việt:
        // String textTongTien = "Tổng tiền:";
        // String textThanhTien = "Thành tiền:";

        lblSubTotal.setText(textThanhTien + " " + currencyFormatter.format(sumSubtotal));
        lblTotal.setText(textTongTien + " " + currencyFormatter.format(sumTotal));

        // Phần giảm giá giữ nguyên
        double discount = sumSubtotal - sumTotal;
        lblDiscount.setText(String.format(LanguageManager.getInstance().getString("pay.discount"), currencyFormatter.format(discount)));
    }
    // --- LOGIC XỬ LÝ SỰ KIỆN ---

    @FXML
    private void handlePay() {
        RadioButton selected = (RadioButton) paymentGroup.getSelectedToggle();
        if (selected == null) {
            lblMessage.setText(LanguageManager.getInstance().getString("pay.choicePay"));
            lblMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        // Tạo order mới (lưu vào hệ thống)
        iOrderService.createOrder(iOrder.getTableId(), iOrder.getItems(), iOrder.getOrderId());

        if(selected.getText().equals(LanguageManager.getInstance().getString("pay.methodcode"))){
            showConfirmationDialog();
        }
        else{
            lblMessage.setText(LanguageManager.getInstance().getString("pay.success") + selected.getText() + "!");
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
        // Logic quay lại màn hình MainScreen
        try {
            goToPage("/view/MainScreen/MainView.fxml", "/view/MainScreen/Main.css");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- PHƯƠNG THỨC PHỤ TRỢ ĐỂ QUAY LẠI MÀN HÌNH MENU CHÍNH---

    private void delayThenRun(double seconds, Runnable action) {
        PauseTransition pause = new PauseTransition(Duration.seconds(seconds));
        pause.setOnFinished(e -> action.run());
        pause.play();
    }

    private void goToPage(String fxmlPath, String cssPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        ResourceBundle bundle = LanguageManager.getInstance().getBundle();
        loader.setResources(bundle);
        Parent root = loader.load();

        Scene scene = btnPay.getScene();
        scene.setRoot(root);

        scene.getStylesheets().clear();

        if (cssPath != null) {
            scene.getStylesheets().add(
                    getClass().getResource(cssPath).toExternalForm()
            );
        }
    }
    // ô hiển thị QRCode
    private void showConfirmationDialog() {
        // 1. Tạo hộp thoại
        Dialog<ButtonType> dialog = new Dialog<>();

        // Tải CSS và áp dụng kiểu dáng hiện đại (dùng ItemDialog.css)
        URL cssUrl = getClass().getResource("/view/MainScreen/MenuManagerPage/Dialog/ItemDialog.css");
        if (cssUrl != null) {
            dialog.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
            // Áp dụng lớp dialog-root để có bóng đổ, bo góc và nền trắng
            dialog.getDialogPane().getStyleClass().add("dialog-root");

            // Bỏ header mặc định để chỉ dùng label tùy chỉnh
            dialog.setHeaderText(null);
        }

        // 2. Tải ảnh QR Code
        // Đảm bảo tệp QRPay.jpg tồn tại trong đường dẫn /view/PaymentPage/
        ImageView qrImage = new ImageView(getClass().getResource("/view/PaymentPage/QRPay1.jpg").toExternalForm());
        qrImage.setFitWidth(300);
        qrImage.setFitHeight(300);
        qrImage.setPreserveRatio(true);

        // 3. Tạo layout cho nội dung hộp thoại (VBox)
        VBox content = new VBox(15);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        content.setPadding(new Insets(30));

        // Tiêu đề tùy chỉnh với màu Accent (Indigo)
        Label title = new Label(LanguageManager.getInstance().getString("pay.createOrder"));
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4F46E5;"); // Màu Indigo

        Label instruction = new Label(LanguageManager.getInstance().getString("pay.goiY"));
        instruction.setStyle("-fx-text-fill: #475569;"); // Màu chữ xám/đậm

        content.getChildren().addAll(
                title,
                instruction,
                qrImage
        );

        dialog.getDialogPane().setContent(content);

        // 4. Thêm nút "Đóng & Quay lại Menu"
        ButtonType closeButton = new ButtonType(LanguageManager.getInstance().getString("pay.close"), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        // Áp dụng kiểu dáng CSS cho nút
        final Button finishButton = (Button) dialog.getDialogPane().lookupButton(closeButton);
        finishButton.getStyleClass().addAll("dialog-button", "confirm");

        // 5. Xử lý sự kiện khi nhấn nút
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

        // 6. Hiển thị hộp thoại (và chặn các thao tác khác)
        dialog.showAndWait();

        // Cập nhật lại lblMessage sau khi đóng dialog
        lblMessage.setText(LanguageManager.getInstance().getString("pay.sc"));
        lblMessage.setStyle("-fx-text-fill: green;");
    }
}
