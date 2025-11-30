package view.MainScreen;

import Interface.*;
import javafx.animation.TranslateTransition;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import view.MockTest.*;
import view.PaymentPage.PaymentController;
import view.Wrapper.OrderItemWrapper;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MainController {

    @FXML private TextField searchField;
    @FXML private FlowPane menuGrid;

    @FXML private TableView<OrderItemWrapper> orderTable;
    @FXML private TableColumn<OrderItemWrapper, String> colName;
    @FXML private TableColumn<OrderItemWrapper, Number> colQty;
    @FXML private TableColumn<OrderItemWrapper, Number> colTotal;

    @FXML private Label totalLabel;

    /* HAMBURGER ELEMENT */
    @FXML
    private Button menuButton;
    @FXML
    private VBox slideMenu;
    private boolean menuOpen = false;


    private IMenuService iMenuService = new MockMenuService();
    private IOrderService iOrderService = new MockOrderService();
    private ObservableList<OrderItemWrapper> currentOrder = FXCollections.observableArrayList();

    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @FXML
    public void initialize() {
        loadMockData();
        setupTable();
        renderMenuGrid(iMenuService.getAllItems());

        searchField.textProperty().addListener((o, oldVal, newVal) -> handleSearch(newVal));
// Ẩn menu ban đầu
        slideMenu.setTranslateX(-220);
        menuButton.setOnAction(e -> {
            if (!menuOpen) {
                showOverlay();
            } else {
                hideOverlay();
            }
            menuOpen = !menuOpen;
        });


    }
// hiệu ứng slidebả cho thanh trượt menu
    private void showOverlay() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(400), slideMenu);
        tt.setToX(0);
        tt.play();
    }

    private void hideOverlay() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(200), slideMenu);
        tt.setToX(-220);
        tt.play();
    }


    private void hideSlideMenu() {
        javafx.animation.TranslateTransition tt = new javafx.animation.TranslateTransition(
                javafx.util.Duration.millis(200), slideMenu);
        tt.setToX(-220); // trượt ra ngoài
        tt.play();

        menuOpen = false;
        menuButton.setVisible(false);
    }

    private void loadMockData() {}

    private void setupTable() {
        colName.setCellValueFactory(c -> c.getValue().nameProperty());
        colQty.setCellValueFactory(c -> c.getValue().quantityProperty());
        colTotal.setCellValueFactory(c -> c.getValue().subtotalProperty());

        colTotal.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Number price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty ? null : currencyFormatter.format(price));
            }
        });

        orderTable.setItems(currentOrder);
    }

    /* RENDER PRODUCT CARD */
    private void renderMenuGrid(List<IMenuItem> items) {
        menuGrid.getChildren().clear();
        for (IMenuItem item : items) {
            menuGrid.getChildren().add(createProductCard(item));
        }
    }

    private VBox createProductCard(IMenuItem item) {
        VBox card = new VBox(5);
        card.setPrefSize(150, 200);
        card.getStyleClass().add("product-card");
        card.setAlignment(Pos.CENTER);

        ImageView imageView = new ImageView();
        try { imageView.setImage(item.getImage()); } catch (Exception ignored) {}

        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);

        Label nameLabel = new Label(item.getName());
        nameLabel.getStyleClass().add("card-title");
        nameLabel.setWrapText(true);

        Label priceLabel = new Label(currencyFormatter.format(item.getPrice()));
        priceLabel.getStyleClass().add("card-price");

        card.getChildren().addAll(imageView, nameLabel, priceLabel);

        card.setOnMouseClicked(event -> addToCart(item));
        return card;
    }

    /* SEARCH */
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

    /* FILTERS */
    @FXML private void filterAll() {
        renderMenuGrid(iMenuService.getAllItems());
        hideOverlay();
    }

    @FXML private void filterDrink() {
        renderMenuGrid(iMenuService.getAllItems().stream()
                .filter(m -> m.getCategory().equals("Drink")).toList());
        hideOverlay();
    }

    @FXML private void filterFood() {
        renderMenuGrid(iMenuService.getAllItems().stream()
                .filter(m -> m.getCategory().equals("Food")).toList());
        hideOverlay();
    }

    /* ORDER */
    private void addToCart(IMenuItem item) {
        for (OrderItemWrapper orderItem : currentOrder) {
            if (orderItem.getMenuItem().getId().equals(item.getId())) {
                orderItem.increaseQuantity();
                refreshOrderState();
                return;
            }
        }

        currentOrder.add(new OrderItemWrapper(item, 1));
        refreshOrderState();
    }

    @FXML private void handleIncreaseQty() {
        OrderItemWrapper selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.increaseQuantity();
            refreshOrderState();
        }
    }

    @FXML private void handleDecreaseQty() {
        OrderItemWrapper selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.decreaseQuantity();
            refreshOrderState();
        }
    }

    @FXML private void handleRemoveItem() {
        OrderItemWrapper selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            currentOrder.remove(selected);
            refreshOrderState();
        }
    }
    @FXML
    private void handleApplyDiscount() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Khuyến mãi");
        alert.setHeaderText(null);
        alert.setContentText("Tính năng áp dụng mã giảm giá đang phát triển.");
        alert.showAndWait();
    }

    private void refreshOrderState() {
        orderTable.refresh();
        updateTotal();
    }

    private void updateTotal() {
        double total = currentOrder.stream()
                .mapToDouble(i -> i.subtotalProperty().get())
                .sum();
        totalLabel.setText(currencyFormatter.format(total));
    }

    /* CHECKOUT */
    @FXML
    private void handleCheckout() throws IOException {
        if (currentOrder.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Giỏ hàng đang trống!");
            alert.showAndWait();
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PaymentPage/Payment.fxml"));
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
}
