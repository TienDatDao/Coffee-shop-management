package model;

import Interface.IMenuItem;
import Interface.IOrderItem;
import javafx.scene.image.Image;

import java.io.File;

public class OrderItem implements IOrderItem {
    private int orderItemId; // ID của dòng trong database (có thể bằng 0 nếu chưa lưu)
    private IMenuItem menuItem;
    private int quantity;
    private String note;

    // --- 1. CONSTRUCTOR RỖNG ---
    public OrderItem() {
    }

    // --- 2. CONSTRUCTOR 3 THAM SỐ (Dùng khi tạo mới Order) ---
    public OrderItem(IMenuItem menuItem, int quantity, String note) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.note = note;
        this.orderItemId = 0; // Mặc định là 0
    }

    // --- 3. CONSTRUCTOR 4 THAM SỐ (Dùng khi đọc từ Database) ---
    public OrderItem(IMenuItem menuItem, int quantity, String note, int orderItemId) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.note = note;
        this.orderItemId = orderItemId;
    }

    public OrderItem(IMenuItem menuItem, int quantity){
        this.menuItem = menuItem;
        this.quantity = quantity;
    }
    // --- CÁC HÀM CỦA IOrderItem ---

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public double getSubtotal() {
        return getPrice() * quantity;
    }

    // --- CÁC HÀM CỦA IMenuItem (Ủy quyền sang object menuItem) ---
    // Vì class này implements IOrderItem (vốn extends IMenuItem), nó phải có các hàm này.

    @Override
    public String getId() {
        return menuItem != null ? menuItem.getId() : "";
    }

    @Override
    public String getName() {
        return menuItem != null ? menuItem.getName() : "Unknown";
    }

    @Override
    public double getPrice() {
        return menuItem != null ? menuItem.getPrice() : 0.0;
    }

    @Override
    public String getCategory() {
        return menuItem != null ? menuItem.getCategory() : "";
    }

    @Override
    public Image getImage() {
        String path = menuItem.getImagePath();
        if (path == null || path.isBlank()) return null;


        File f = new File("src",path);
        if (!f.exists()) {
            File file = new File("storage", path);
            if (!file.exists()) {
                System.err.println("Image not found: " + file.getAbsolutePath());
                return null;
            }

            return new Image(file.toURI().toString());
        }

        return new Image(f.toURI().toString(), true);
    }



    @Override
    public void setImagePath(String imp) {
        this.menuItem.setImagePath(imp);
    }

    // Các hàm setter của IMenuItem (nếu cần thiết cho logic update)
    @Override public void setId(String id) { if(menuItem!=null) menuItem.setId(id); }
    @Override public void setName(String name) { if(menuItem!=null) menuItem.setName(name); }
    @Override public void setPrice(double price) { if(menuItem!=null) menuItem.setPrice(price); }
    @Override public void setCategory(String category) { if(menuItem!=null) menuItem.setCategory(category); }
    @Override
    public String getImagePath() {
        return this.menuItem.getImagePath();
    }


    // --- GETTER & SETTER RIÊNG CỦA CLASS OrderItem (Cho DAO sử dụng) ---

    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public String getNote() {
        return note;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public IMenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }
}