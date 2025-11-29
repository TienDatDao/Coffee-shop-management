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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Duration;
import view.MockTest.MockOrder;
import view.MockTest.MockOrderItem;
import view.MockTest.MockOrderService;

import java.io.File;
import java.net.URL;
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
    private IOrder iOrder = new MockOrder();

    private IOrderService iOrderService = new MockOrderService();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Thiết lập ToggleGroup( dùng đề nhóm các thành phần radioButton hoặc tương tự)
        paymentGroup = new ToggleGroup();
        rbCash.setToggleGroup(paymentGroup);
        rbCard.setToggleGroup(paymentGroup);
        rbWallet.setToggleGroup(paymentGroup);
        rbCash.setSelected(true);

        // Thêm dữ liệu vào ListView
        listItems.getItems().setAll(iOrder.getItems());
        // Custom mỗi item dạng Box
        listItems.setCellFactory(lv -> new ListCell<IOrderItem>() {
            @Override
            protected void updateItem(IOrderItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                }
                else {
                    // 1. Container chính của một Item (.list-item-cell)
                    VBox itemVBox = new VBox(5);
                    itemVBox.getStyleClass().add("list-item-cell");
                    itemVBox.setPadding(Insets.EMPTY);

                    // Tạo HBox Chính (mainItemContent) để có thể sắp xếp Ngang
                    HBox mainItemContent = new HBox(15);
                    mainItemContent.getStyleClass().add("main-item-content");
                    // 2. Ảnh món ăn (Bên trái)
                    ImageView itemImage = new ImageView(item.getImage()); // sẽ lấy ảnh từ việc người dùng đưa vào be
                    itemImage.setFitWidth(80);
                    itemImage.setFitHeight(100);
                    itemImage.setPreserveRatio(false); // Giữ tỉ lệ ảnh
                    itemImage.setSmooth(true);
                    itemImage.getStyleClass().add("item-image");


                    // 3. VBox Tên và Mô tả và Giá (Cạnh ảnh bên phải)
                    VBox nameAndDescBox = new VBox(3);
                    nameAndDescBox.getStyleClass().add("item-text-container");

                    Label lblName = new Label(item.getName());
                    lblName.getStyleClass().add("item-name");

                    Label lblDescription = new Label(item.getCategory());
                    lblDescription.getStyleClass().add("item-description");
                    Label lblPrice = new Label("Giá: " + item.getPrice()+"vnđ");
                    lblPrice.getStyleClass().add("item-price");
                    nameAndDescBox.getChildren().addAll(lblName, lblDescription,lblPrice);

                    HBox.setHgrow(nameAndDescBox, Priority.ALWAYS);
                    // 4. VBox chứa label của số lượng ở dưới bên góc phải( chưa tối ưu)
                    VBox detailVBox = new VBox(5);
                    detailVBox.getStyleClass().add("item-detail-vbox");

                    Label lblQty = new Label("Số lượng: x" + item.getQuantity());
                    lblQty.getStyleClass().add("item-quantity");
                    Label lblSubtotal = new Label("Thành tiền(sau ưu đãi): $" + item.getSubtotal());
                    lblSubtotal.getStyleClass().add("item-subtotal");

                    detailVBox.getChildren().addAll( lblQty);

                    // 5. Thêm các Container vào HBox Chính
                    mainItemContent.getChildren().addAll(itemImage, nameAndDescBox, detailVBox);
                    itemVBox.getChildren().addAll(mainItemContent);
                    setGraphic(itemVBox);
                }
            }
        });

        updateTotal();
        // load ảnh của các phương thức/ ngân hàng có thể dùng để thanh toán
        loadImage();
    }
    // dùng để load ảnh các ngân hàng có thể dùng để thanh toán
    private void loadImage(){
        // danh sách các ngân hàng liên kết ( hiện đang sử dụng ảnh minh họa( 1 ảnh)
        String[] files = {
                "/view/PaymentPage/ListPaymentMethod.png"
        };
        // load địa chỉ chuyển thành ảnh
        for (String file : files) {
            ImageView iv = new ImageView(new Image(file));

            iv.setFitWidth(200);
            iv.setFitHeight(200);
            iv.setPreserveRatio(true);

            flowImages.getChildren().add(iv);
        }
    }

    private void updateTotal() {
        double sumSubtotal = listItems.getItems().stream().mapToDouble(IOrderItem::getSubtotal).sum();
        lblSubTotal.setText(String.format("Thành tiền: %.2fvnđ", sumSubtotal));

        double sum = iOrder.getTotalPrice();
        lblTotal.setText(String.format("Tổng tiền: %.0fvnđ", sum));
        double discount = sum-sumSubtotal;
        lblDiscount.setText(String.format("Ưu đãi: -%.2fvnđ", discount));
    }

    @FXML
    private void handlePay() {
        RadioButton selected = (RadioButton) paymentGroup.getSelectedToggle();
        if (selected == null) {
            lblMessage.setText("Chọn phương thức thanh toán!");
            lblMessage.setStyle("-fx-text-fill: red;");
            return;
        }
        iOrderService.createOrder(iOrder.getTableId(), iOrder.getItems(), iOrder.getOrderId());
        lblMessage.setText("Thanh toán thành công qua phương thức " + selected.getText() + "!");
        lblMessage.setStyle("-fx-text-fill: green;");
        // test thử quay về trang login
        delayThenRun(1, () -> goToPage("/view/LoginPage/Login.fxml", "/view/LoginPage/Login.css"));
    }
    // độ trễ trước khi chuyển trang ( đề xuất cho hợp lí)
    private void delayThenRun(double seconds, Runnable action) {
        PauseTransition pause = new PauseTransition(Duration.seconds(seconds));
        pause.setOnFinished(e -> action.run());
        pause.play();
    }
    // chuyển trang sau khi thanh toán xong
    private void goToPage(String fxmlPath, String cssPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = btnPay.getScene();
            scene.setRoot(root);

            // Xóa tất cả stylesheet hiện tại
            scene.getStylesheets().clear();

            // Thêm CSS mới
            if (cssPath != null) {
                scene.getStylesheets().add(
                        getClass().getResource(cssPath).toExternalForm()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleBack(){
        System.out.println("quan que");// chưa có back
    }
}
