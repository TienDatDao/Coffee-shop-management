package view.MainScreen.MenuManagerPage;

import Interface.IMenuItem;
import Interface.IMenuService;
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
import view.MainScreen.MainController;
import view.MainScreen.MenuManagerPage.Dialog.ItemDialogController;
import view.MainTest;
import view.MockTest.MockMenuService;
import view.Wrapper.MenuItemWrapper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MenuManagerController {

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
        menuService = MainTest.SHARED_MENU_SERVICE;

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
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(12));
        card.setPrefWidth(170);
        card.getStyleClass().add("product-card");

        ImageView iv = new ImageView();
        iv.setFitWidth(130);
        iv.setFitHeight(130);
        iv.setPreserveRatio(true);

        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(130, 130);
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        iv.setClip(clip);

        iv.imageProperty().bind(w.imageProperty()); // BIND trực tiếp với wrapper

        Label nameLbl = new Label();
        nameLbl.setWrapText(true);
        nameLbl.setMaxWidth(150);
        nameLbl.textProperty().bind(w.nameProperty()); // BIND trực tiếp

        Label priceLbl = new Label();
        priceLbl.getStyleClass().add("card-price");
        priceLbl.textProperty().bind(w.priceProperty().asString("%.0f VNĐ"));

        card.getChildren().addAll(iv, nameLbl, priceLbl);

        card.setOnMouseClicked(e -> {
            if (selectedItem != null && selectedItem.idProperty().get().equals(w.idProperty().get())) {
                selectedItem = null;
            } else {
                selectedItem = w;
            }
            refreshSelection();
            updateToolbarState();
        });

        return card;
    }

    private void refreshSelection() {
        for (var node : centerMenuGrid.getChildren()) {
            if (node instanceof VBox card) {
                boolean isSel = selectedItem != null &&
                        card == itemCardMap.get(selectedItem.idProperty().get());
                if (isSel) {
                    if (!card.getStyleClass().contains("selected")) card.getStyleClass().add("selected");
                } else {
                    card.getStyleClass().remove("selected");
                }
            }
        }
    }

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
        MainController mainController = loader.getController();
        mainController.loadData();
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
}
