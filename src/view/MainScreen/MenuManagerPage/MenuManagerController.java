package view.MainScreen.MenuManagerPage;

import Interface.IMenuItem;
import Interface.IMenuService;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import view.Main;
import view.MainScreen.MenuManagerPage.Dialog.ItemDialogController;
import view.Wrapper.MenuItemWrapper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MenuManagerController {

    @FXML private BorderPane root;
    @FXML private FlowPane centerMenuGrid;
    @FXML private TextField searchField;
    @FXML private Label dateLabel;

    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private IMenuService menuService;
    private List<MenuItemWrapper> fullMenu;
    private MenuItemWrapper selectedItem;

    private Map<String, VBox> itemCardMap = new HashMap<>();

    @FXML
    public void initialize() {
        menuService = Main.SHARED_MENU_SERVICE;
        root.setOnMouseClicked(e -> handleClickOutside(e));
        // Bao dữ liệu gốc trong wrapper
        fullMenu = new ArrayList<>();
        for (IMenuItem item : menuService.getAllItems()) {
            fullMenu.add(new MenuItemWrapper(item));
        }

        dateLabel.setText(LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy", Locale.forLanguageTag("vi-VN"))));

        setupSearch();
        renderAll();
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldV, newV) ->
                renderFiltered(searchMenu(newV))
        );
    }

    private List<MenuItemWrapper> searchMenu(String keyword) {
        if (keyword == null || keyword.isEmpty()) return fullMenu;
        String lower = keyword.toLowerCase();
        List<MenuItemWrapper> result = new ArrayList<>();
        for (MenuItemWrapper w : fullMenu) {
            if (w.nameProperty().get().toLowerCase().contains(lower)) result.add(w);
        }
        return result;
    }

    private void renderAll() {
        renderFiltered(fullMenu);
    }

    private void renderFiltered(List<MenuItemWrapper> items) {
        centerMenuGrid.getChildren().clear();
        itemCardMap.clear();

        for (MenuItemWrapper w : items) {
            VBox card = createProductCard(w);
            centerMenuGrid.getChildren().add(card);
            itemCardMap.put(w.idProperty().get(), card);
        }
    }

    private VBox createProductCard(MenuItemWrapper w) {
        VBox card = new VBox(10); //
        double cardWidth = 170;   // Tăng độ rộng thẻ một chút
        card.setPrefWidth(cardWidth);
        card.setMaxWidth(cardWidth);
        card.getStyleClass().add("product-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new javafx.geometry.Insets(10)); // Padding nội bộ thẻ

        ImageView iv = new ImageView();
        iv.setFitWidth(130);
        iv.setFitHeight(100);
        iv.setPreserveRatio(false);

        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(130, 130);
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        iv.setClip(clip);

        iv.imageProperty().bind(w.imageProperty());

        Label nameLbl = new Label();
        nameLbl.setWrapText(true);
        nameLbl.setMaxWidth(150);
        nameLbl.textProperty().bind(w.nameProperty());

        Label priceLbl = new Label();
        priceLbl.getStyleClass().add("card-price");
        priceLbl.textProperty().bind(w.priceProperty().asString("%.0f VNĐ"));

        card.getChildren().addAll(iv, nameLbl, priceLbl);

        card.setUserData(w); // *** LIÊN KẾT CARD -> ITEM ***

        card.setOnMouseClicked(e -> {
            if (selectedItem != null && selectedItem.idProperty().get().equals(w.idProperty().get())) {
                selectedItem = null;
                selectedCard = null;
            } else {
                selectedItem = w;
                selectedCard = card;
            }

            refreshSelection();
            updateToolbarState();
        });

        return card;
    }

    private VBox selectedCard = null;
    private boolean editMode = false;

    private void refreshSelection() {

        for (Node node : centerMenuGrid.getChildren()) {
            VBox card = (VBox) node;
            MenuItemWrapper item = (MenuItemWrapper) card.getUserData();

            boolean isSelected = (selectedItem != null &&
                    item.idProperty().get().equals(selectedItem.idProperty().get()));

            card.getStyleClass().remove("selected");

            if (isSelected) {
                selectedCard = card;
                card.getStyleClass().add("selected");

                // Animation phóng to
                animateScale(card, 1.07, 1.07);

            } else {
                // Các card không chọn trở về scale chuẩn
                animateScale(card, 1.0, 1.0);
            }
        }

        if (editMode) {
            applyDimmedEffect();
        } else {
            clearDimmedEffect();
        }
    }
    // hiệu ứng cho click card
    private void applyDimmedEffect() {
        for (Node node : centerMenuGrid.getChildren()) {
            if (node != selectedCard) {
                animateFade(node, 0.5);
            } else {
                animateFade(node, 1.0);
            }
        }
    }
    private void clearDimmedEffect() {
        for (Node node : centerMenuGrid.getChildren()) {
            animateFade(node, 1.0);
        }
    }

    private void handleClickOutside(MouseEvent e) {

        // Nếu đang click vào 1 card thì không làm gì
        Node clicked = e.getPickResult().getIntersectedNode();

        while (clicked != null) {
            if (clicked.getStyleClass().contains("product-card")) {
                return; // click vào card -> bỏ qua
            }
            clicked = clicked.getParent();
        }

        // Click đúng vào vùng trống -> reset
        selectedItem = null;
        selectedCard = null;
        editMode = false;

        refreshSelection();   // bỏ selected + scale
        clearDimmedEffect();  // bỏ mờ
        updateToolbarState(); // disable edit/delete nút
    }

    private void animateScale(Node node, double toX, double toY) {
        ScaleTransition st = new ScaleTransition(Duration.millis(130), node);
        st.setToX(toX);
        st.setToY(toY);
        st.setInterpolator(Interpolator.EASE_BOTH);
        st.play();
    }
    private void animateFade(Node node, double to) {
        FadeTransition ft = new FadeTransition(Duration.millis(150), node);
        ft.setToValue(to);
        ft.setInterpolator(Interpolator.EASE_BOTH);
        ft.play();
    }
    // hiêu ứng cho click card
    private void updateToolbarState() {
        boolean has = selectedItem != null;
        btnEdit.setDisable(!has);
        btnDelete.setDisable(!has);
    }
    // ---------------- FILTER ----------------
    @FXML
    private void filterAll() {
        renderFiltered(fullMenu);
    }

    @FXML
    private void filterDrink() {
        List<MenuItemWrapper> drinks = new ArrayList<>();
        for (MenuItemWrapper w : fullMenu) {
            if ("Drink".equalsIgnoreCase(w.categoryProperty().get())) {
                drinks.add(w);
            }
        }
        renderFiltered(drinks);
    }

    @FXML
    private void filterFood() {
        List<MenuItemWrapper> foods = new ArrayList<>();
        for (MenuItemWrapper w : fullMenu) {
            if ("Food".equalsIgnoreCase(w.categoryProperty().get())) {
                foods.add(w);
            }
        }
        renderFiltered(foods);
    }


    @FXML private void onAdd() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen/MenuManagerPage/Dialog/ItemDialog.fxml"));
        Parent root = loader.load();
        ItemDialogController dc = loader.getController();
        dc.setEditing(null);

        StageHelper.showDialog(root, "Thêm món", btnAdd.getScene().getWindow());

        Object ud = root.getScene() != null ? root.getScene().getUserData() : null;
        if (ud instanceof IMenuItem newItem) {
            newItem.setId(UUID.randomUUID().toString());
            menuService.addMenuItem(newItem);

            MenuItemWrapper wrapper = new MenuItemWrapper(newItem);
            fullMenu.add(wrapper);

            VBox card = createProductCard(wrapper);
            centerMenuGrid.getChildren().add(card);
            itemCardMap.put(wrapper.idProperty().get(), card);
        }
    }

    @FXML private void onEdit() throws IOException {
        editMode = true;
        applyDimmedEffect();
        if (selectedItem == null) return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen/MenuManagerPage/Dialog/ItemDialog.fxml"));
        Parent root = loader.load();
        ItemDialogController dc = loader.getController();
// Truyền đối tượng gốc cho Dialog để nó cập nhật các thuộc tính của nó
        IMenuItem itemToEdit = selectedItem.getOriginal();
        dc.setEditing(itemToEdit);

        StageHelper.showDialog(root, "Sửa món", btnEdit.getScene().getWindow());

        // Sau khi Dialog đóng, các thuộc tính của 'itemToEdit' (là đối tượng gốc) ĐÃ được cập nhật.

        System.out.println(itemToEdit.getName());
        // 1. Cập nhật Service (lưu thay đổi vào cơ sở dữ liệu/mock)
        menuService.updateMenuItem(itemToEdit);
        selectedItem.updateFromOriginal();
        refreshSelection();
    }

    @FXML private void onDelete() {
        if (selectedItem == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Xóa món");
        alert.setContentText("Bạn chắc chắn muốn xóa: " + selectedItem.nameProperty().get() + " ?");

        alert.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                menuService.deleteMenuItem(selectedItem.idProperty().get());

                VBox card = itemCardMap.get(selectedItem.idProperty().get());
                if (card != null) centerMenuGrid.getChildren().remove(card);
                itemCardMap.remove(selectedItem.idProperty().get());
                fullMenu.remove(selectedItem);

                selectedItem = null;
                refreshSelection();
                updateToolbarState();
            }
        });
    }
    @FXML
    private void mainScreen() throws IOException {

        // 1. Lấy Stage hiện tại (từ bất kỳ thành phần nào trên Scene)
        Stage currentStage = (Stage) centerMenuGrid.getScene().getWindow();

        // 2. Tải FXML của màn hình chính
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen/MainView.fxml"));


        // 3. Tải Root Node
        Parent root = loader.load();
        // 4. Tạo Scene mới và thiết lập Stage
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                getClass().getResource("/view/MainScreen/Main.css").toExternalForm()
        );

        //  Đặt tiêu đề mới cho cửa sổ
        currentStage.setTitle("Coffee Shop Management - Welcome ");
        currentStage.setMaximized(true);
        currentStage.setScene(scene);
        currentStage.show();
    }
    @FXML
    private void logout(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginPage/Login.fxml"));
            Stage stage = (Stage) centerMenuGrid.getScene().getWindow();

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
}
