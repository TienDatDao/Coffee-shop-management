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
import view.AppConfig;
import view.Helper.LanguageManager;
import view.MainScreen.MenuManagerPage.Dialog.ItemDialogController;
import view.MainTest;
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

    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private IMenuService menuService;
    private List<MenuItemWrapper> fullMenu = new ArrayList<>();
    private MenuItemWrapper selectedItem;
    private VBox selectedCard;

    private final Map<String, VBox> itemCardMap = new HashMap<>();
    private boolean editMode = false;

    private final NumberFormat currencyFormatter =
            NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    private DateTimeFormatter dateFormatter;

    // ================= INITIALIZE =================
    @FXML
    public void initialize() {
        menuService = MainTest.SHARED_MENU_SERVICE;

        Locale locale = LanguageManager.getInstance().getBundle().getLocale();
        dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy", locale);

        root.setOnMouseClicked(this::handleClickOutside);

        for (IMenuItem item : menuService.getAllItems()) {
            fullMenu.add(new MenuItemWrapper(item));
        }

        centerMenuGrid.setAlignment(Pos.CENTER_LEFT);
        centerMenuGrid.setPadding(new Insets(20, 20, 50, 20));

        dateLabel.setText(LocalDate.now().format(dateFormatter));

        setupSearch();
        renderAll();
    }

    // ================= SEARCH =================
    private void setupSearch() {
        searchField.textProperty().addListener((obs, o, n) ->
                renderFiltered(searchMenu(n))
        );
    }

    private List<MenuItemWrapper> searchMenu(String keyword) {
        if (keyword == null || keyword.isBlank()) return fullMenu;
        String lower = keyword.toLowerCase();
        List<MenuItemWrapper> result = new ArrayList<>();
        for (MenuItemWrapper w : fullMenu) {
            if (w.nameProperty().get().toLowerCase().contains(lower)) {
                result.add(w);
            }
        }
        return result;
    }

    // ================= RENDER =================
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
        VBox card = new VBox(10);
        card.setPrefWidth(170);
        card.setMaxWidth(170);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("product-card");

        ImageView iv = new ImageView();
        iv.setFitWidth(130);
        iv.setFitHeight(100);
        iv.setPreserveRatio(false);
        iv.imageProperty().bind(w.imageProperty());

        var clip = new javafx.scene.shape.Rectangle(130, 130);
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        iv.setClip(clip);

        Label nameLbl = new Label();
        nameLbl.setWrapText(true);
        nameLbl.setMinHeight(40);
        nameLbl.setMaxWidth(150);
        nameLbl.setAlignment(Pos.CENTER);
        nameLbl.setTextAlignment(TextAlignment.CENTER);
        nameLbl.getStyleClass().add("card-title");
        nameLbl.textProperty().bind(w.nameProperty());

        Label priceLbl = new Label();
        priceLbl.getStyleClass().add("card-price");
        priceLbl.textProperty().bind(
                Bindings.createStringBinding(
                        () -> currencyFormatter.format(w.priceProperty().get()),
                        w.priceProperty()
                )
        );

        card.getChildren().addAll(iv, nameLbl, priceLbl);
        card.setUserData(w);

        card.setOnMouseClicked(e -> {
            if (selectedItem == w) {
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

    // ================= SELECTION =================
    private void refreshSelection() {
        for (Node node : centerMenuGrid.getChildren()) {
            VBox card = (VBox) node;
            MenuItemWrapper item = (MenuItemWrapper) card.getUserData();

            boolean selected = selectedItem != null &&
                    item.idProperty().get().equals(selectedItem.idProperty().get());

            card.getStyleClass().remove("selected");

            if (selected) {
                card.getStyleClass().add("selected");
                animateScale(card, 1.07);
            } else {
                animateScale(card, 1.0);
            }
        }

        if (editMode) applyDimmedEffect();
        else clearDimmedEffect();
    }

    private void applyDimmedEffect() {
        for (Node n : centerMenuGrid.getChildren()) {
            animateFade(n, n == selectedCard ? 1.0 : 0.5);
        }
    }

    private void clearDimmedEffect() {
        for (Node n : centerMenuGrid.getChildren()) {
            animateFade(n, 1.0);
        }
    }

    private void handleClickOutside(MouseEvent e) {
        Node node = e.getPickResult().getIntersectedNode();
        while (node != null) {
            if (node.getStyleClass().contains("product-card")) return;
            node = node.getParent();
        }

        selectedItem = null;
        selectedCard = null;
        editMode = false;
        refreshSelection();
        updateToolbarState();
    }

    // ================= ANIMATION =================
    private void animateScale(Node n, double scale) {
        ScaleTransition st = new ScaleTransition(Duration.millis(130), n);
        st.setToX(scale);
        st.setToY(scale);
        st.setInterpolator(Interpolator.EASE_BOTH);
        st.play();
    }

    private void animateFade(Node n, double to) {
        FadeTransition ft = new FadeTransition(Duration.millis(150), n);
        ft.setToValue(to);
        ft.setInterpolator(Interpolator.EASE_BOTH);
        ft.play();
    }

    private void updateToolbarState() {
        boolean has = selectedItem != null;
        btnEdit.setDisable(!has);
        btnDelete.setDisable(!has);
    }

    // ================= FILTER =================
    @FXML private void filterAll() { renderFiltered(fullMenu); }

    @FXML private void filterDrink() {
        renderFiltered(fullMenu.stream()
                .filter(w -> "Drink".equalsIgnoreCase(w.categoryProperty().get()))
                .toList());
    }

    @FXML private void filterFood() {
        renderFiltered(fullMenu.stream()
                .filter(w -> "Food".equalsIgnoreCase(w.categoryProperty().get()))
                .toList());
    }

    // ================= CRUD =================
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
            item.setId(UUID.randomUUID().toString());
            menuService.addMenuItem(item);

            MenuItemWrapper w = new MenuItemWrapper(item);
            fullMenu.add(w);
            VBox card = createProductCard(w);
            centerMenuGrid.getChildren().add(card);
            itemCardMap.put(w.idProperty().get(), card);
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
                .setEditing(selectedItem.getOriginal());

        StageHelper.showDialog(
                root,
                LanguageManager.getInstance().getString("mem.editDish"),
                btnEdit.getScene().getWindow()
        );

        menuService.updateMenuItem(selectedItem.getOriginal());
        selectedItem.updateFromOriginal();
        refreshSelection();
    }

    @FXML
    private void onDelete() {
        if (selectedItem == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(LanguageManager.getInstance().getString("mem.acept"));
        alert.setHeaderText(LanguageManager.getInstance().getString("mem.deleteDish"));
        alert.setContentText(
                LanguageManager.getInstance().getString("mem.realConfirm")
                        + selectedItem.nameProperty().get() + " ?"
        );

        alert.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            menuService.deleteMenuItem(selectedItem.idProperty().get());
            centerMenuGrid.getChildren().remove(itemCardMap.remove(selectedItem.idProperty().get()));
            fullMenu.remove(selectedItem);
            selectedItem = null;
            refreshSelection();
            updateToolbarState();
        });
    }
    @FXML
    private void menuManager() {
        try {
            ResourceBundle bundle = LanguageManager.getInstance().getBundle();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/MainScreen/MenuManagerPage/MenuManager.fxml"),
                    bundle
            );
            Parent root = loader.load();
            Stage stage = (Stage) centerMenuGrid.getScene().getWindow();
            Scene scene = new Scene(root);

            // >>> SỬA DÒNG NÀY: Quay về Main thì tham số thứ 2 là null (không cần Settings.css)
            AppConfig.applyTheme(scene, "/view/MainScreen/MenuManagerPage/MenuManager.css");

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void mainScreen() {
        try {
            ResourceBundle bundle = LanguageManager.getInstance().getBundle();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/MainScreen/MainView.fxml"),
                    bundle
            );
            Parent root = loader.load();
            Stage stage = (Stage) centerMenuGrid.getScene().getWindow();
            Scene scene = new Scene(root);

            AppConfig.applyTheme(scene, "/view/MainScreen/Main.css");

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void openSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen/SettingsPage/Settings.fxml"));
            ResourceBundle bundle = LanguageManager.getInstance().getBundle();
            loader.setResources(bundle);
            Parent root = loader.load();
            Stage stage = (Stage) centerMenuGrid.getScene().getWindow();

            // Giữ kích thước cũ
            Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());

            view.AppConfig.applyTheme(scene, "/view/MainScreen/SettingsPage/Settings.css");

            stage.setTitle(LanguageManager.getInstance().getString("mainc.setting_system"));
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            Stage stage = (Stage) centerMenuGrid.getScene().getWindow();

            Scene scene = new Scene(root, 1000, 600);
            scene.getStylesheets().add(
                    getClass().getResource("/view/LoginPage/Login.css").toExternalForm()
            );

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(LanguageManager.getInstance().getString("mainc.notnot"));
        }
    }
}
