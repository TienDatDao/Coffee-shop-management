package view.MainScreen.MenuManagerPage;

import Interface.IMenuItem;
import Interface.IMenuService;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import view.Helper.LanguageManager;
import view.Main;
import view.MainScreen.MenuManagerPage.Dialog.ItemDialogController;
import view.Wrapper.MenuItemWrapper;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MenuManagerController {

    @FXML private BorderPane root;
    @FXML private FlowPane centerMenuGrid;
    @FXML private TextField searchField;
    @FXML private Label dateLabel;

    // Các ID mới thêm từ FXML
    @FXML private Label lblAppTitle;
    @FXML private Label lblHeaderTitle;
    @FXML private Button btnSell;
    @FXML private Button btnManage;
    @FXML private Button btnSetting;
    @FXML private Button btnLogout;

    @FXML private Button btnFilterAll;
    @FXML private Button btnFilterDrink;
    @FXML private Button btnFilterFood;

    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private IMenuService menuService;
    private List<MenuItemWrapper> fullMenu;
    private MenuItemWrapper selectedItem;
    private VBox selectedCard = null;
    private boolean editMode = false;
    private Map<String, VBox> itemCardMap = new HashMap<>();

    // Formatter tiền tệ (Mặc định VN, có thể đổi logic nếu muốn support $ sau này)
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @FXML
    public void initialize() {
        menuService = Main.SHARED_MENU_SERVICE;
        root.setOnMouseClicked(this::handleClickOutside);

        fullMenu = new ArrayList<>();
        for (IMenuItem item : menuService.getAllItems()) {
            fullMenu.add(new MenuItemWrapper(item));
        }
        centerMenuGrid.setAlignment(Pos.CENTER_LEFT);
        centerMenuGrid.setPadding(new Insets(20, 20, 50, 20));

        setupSearch();
        renderAll();

        // Cập nhật ngôn ngữ khi khởi tạo
        updateLanguage();
    }

    private void updateLanguage() {
        LanguageManager lm = LanguageManager.getInstance();
        Locale currentLocale = lm.getBundle().getLocale();

        // 1. Sidebar & Header (Giữ lại icon bằng cách cộng chuỗi)
        lblAppTitle.setText(lm.getString("menu.pos"));
        btnSell.setText("🛒  " + lm.getString("menu.sell"));
        btnManage.setText("👪 " + lm.getString("menu.manage"));
        btnSetting.setText("⚙  " + lm.getString("menu.setting"));
        btnLogout.setText("🚪  " + lm.getString("menu.logout"));

        lblHeaderTitle.setText(lm.getString("menu.title"));

        // 2. Date Format theo ngôn ngữ
        String pattern = currentLocale.getLanguage().equals("vi") ? "EEEE, dd MMM yyyy" : "EEEE, MMM dd yyyy";
        dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern(pattern, currentLocale)));

        // 3. Search & Filters
        searchField.setPromptText("🔍 " + lm.getString("menu.search"));

        btnFilterAll.setText(lm.getString("menu.filter.all"));
        btnFilterDrink.setText("☕ " + lm.getString("menu.filter.drink"));
        btnFilterFood.setText("🍰 " + lm.getString("menu.filter.food"));

        // 4. Action Buttons
        btnAdd.setText("➕ " + lm.getString("menu.add"));
        btnEdit.setText("✏️ " + lm.getString("menu.edit"));
        btnDelete.setText("🗑️ " + lm.getString("menu.delete"));
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

    private void reloadFromService() {
        fullMenu.clear();
        for (IMenuItem item : menuService.getAllItems()) {
            fullMenu.add(new MenuItemWrapper(item));
        }
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
        VBox card = new VBox(10);
        double cardWidth = 170;
        card.setPrefWidth(cardWidth);
        card.setMaxWidth(cardWidth);

        card.getStyleClass().add("product-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new javafx.geometry.Insets(10));

        // Ảnh
        ImageView iv = new ImageView();
        iv.setFitWidth(130);
        iv.setFitHeight(100);
        iv.setPreserveRatio(false);

        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(130, 130);
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        iv.setClip(clip);
        iv.imageProperty().bind(w.imageProperty());

        // Tên món
        Label nameLbl = new Label();
        nameLbl.setWrapText(true);
        nameLbl.setMaxWidth(150);
        nameLbl.setMinHeight(40);
        nameLbl.textProperty().bind(w.nameProperty());
        nameLbl.getStyleClass().add("card-title");
        nameLbl.setTextAlignment(TextAlignment.CENTER);
        nameLbl.setAlignment(Pos.CENTER);

        // Giá
        Label priceLbl = new Label();
        priceLbl.getStyleClass().add("card-price");
        priceLbl.textProperty().bind(Bindings.createStringBinding(
                () -> currencyFormatter.format(w.priceProperty().get()),
                w.priceProperty()
        ));

        card.getChildren().addAll(iv, nameLbl, priceLbl);
        card.setUserData(w);

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
                animateScale(card, 1.07, 1.07);
            } else {
                animateScale(card, 1.0, 1.0);
            }
        }
        if (editMode) {
            applyDimmedEffect();
        } else {
            clearDimmedEffect();
        }
    }

    private void applyDimmedEffect() {
        for (Node node : centerMenuGrid.getChildren()) {
            if (node != selectedCard) animateFade(node, 0.5);
            else animateFade(node, 1.0);
        }
    }

    private void clearDimmedEffect() {
        for (Node node : centerMenuGrid.getChildren()) {
            animateFade(node, 1.0);
        }
    }

    private void handleClickOutside(MouseEvent e) {
        Node clicked = e.getPickResult().getIntersectedNode();
        while (clicked != null) {
            if (clicked.getStyleClass().contains("product-card")) return;
            clicked = clicked.getParent();
        }
        selectedItem = null;
        selectedCard = null;
        editMode = false;
        refreshSelection();
        clearDimmedEffect();
        updateToolbarState();
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
            if ("Drink".equalsIgnoreCase(w.categoryProperty().get())) drinks.add(w);
        }
        renderFiltered(drinks);
    }

    @FXML
    private void filterFood() {
        List<MenuItemWrapper> foods = new ArrayList<>();
        for (MenuItemWrapper w : fullMenu) {
            if ("Food".equalsIgnoreCase(w.categoryProperty().get())) foods.add(w);
        }
        renderFiltered(foods);
    }

    @FXML
    private void onAdd() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/MainScreen/MenuManagerPage/Dialog/ItemDialog.fxml"),
                LanguageManager.getInstance().getBundle()
        );
        Parent root = loader.load();
        loader.<ItemDialogController>getController().setEditing(null);

        StageHelper.showDialog(
                root,
                LanguageManager.getInstance().getString("mem.addDish"),
                btnAdd.getScene().getWindow()
        );

        Object ud = root.getScene().getUserData();
        if (ud instanceof IMenuItem item) {

            String path = item.getImagePath();

            item.setImagePath(path);
            item.setId(UUID.randomUUID().toString());

            menuService.addMenuItem(item);
            reloadFromService();
            renderAll();
        }
    }

    @FXML
    private void onEdit() throws IOException {
        if (selectedItem == null) return;

        editMode = true;
        applyDimmedEffect();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/MainScreen/MenuManagerPage/Dialog/ItemDialog.fxml"),
                LanguageManager.getInstance().getBundle()
        );
        Parent root = loader.load();
        loader.<ItemDialogController>getController()
                .setEditing(selectedItem.unwrap());

        StageHelper.showDialog(
                root,
                LanguageManager.getInstance().getString("mem.editDish"),
                btnEdit.getScene().getWindow()
        );

        menuService.updateMenuItem(selectedItem.unwrap());
        reloadFromService();
        renderAll();
        editMode = false;
    }

    @FXML
    private void onDelete() {
        if (selectedItem == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(LanguageManager.getInstance().getString("mem.acept"));
        alert.setHeaderText(LanguageManager.getInstance().getString("mem.deleteDish"));
        alert.setContentText(
                LanguageManager.getInstance().getString("mem.realConfirm")
                        + selectedItem.getName() + " ?"
        );

        alert.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            menuService.deleteMenuItem(selectedItem.getId());
            reloadFromService();
            renderAll();
        });
    }
    @FXML
    private void mainScreen() throws IOException {
        changeScene("/view/MainScreen/MainView.fxml", LanguageManager.getInstance().getString("app.title"), "/view/MainScreen/Main.css");
    }

    @FXML
    private void logout(){
        try {
            ResourceBundle bundle = LanguageManager.getInstance().getBundle();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginPage/Login.fxml"));
            loader.setResources(bundle);
            Parent root = loader.load();

            Stage stage = (Stage) centerMenuGrid.getScene().getWindow();
            Scene scene = new Scene(root, 700, 475);
            scene.getStylesheets().add(
                    getClass().getResource("/view/LoginPage/Login.css").toExternalForm()
            );

            stage.setMaximized(false);
            stage.setFullScreen(false);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.setTitle(LanguageManager.getInstance().getString("login.title"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openSettings() {
        changeScene("/view/MainScreen/SettingsPage/Settings.fxml", LanguageManager.getInstance().getString("app.title"), "/view/MainScreen/SettingsPage/Settings.css");
    }

    // Hàm phụ trợ để chuyển scene gọn gàng hơn
    private void changeScene(String fxmlPath, String title, String cssPath) {
        try {
            ResourceBundle bundle = LanguageManager.getInstance().getBundle();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setResources(bundle);
            Parent root = loader.load();

            Stage stage = (Stage) centerMenuGrid.getScene().getWindow();
            Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());

            if (cssPath != null) {
                view.AppConfig.applyTheme(scene, cssPath);
            }

            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}