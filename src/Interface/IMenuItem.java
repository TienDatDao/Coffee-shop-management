package Interface;

import javafx.scene.image.Image;

/* Đại diện cho 1 món trong menu của quán coffee */
public interface IMenuItem {
    String getId();         // mã món (vd: CF01)
    String getName();       // tên món (vd: Cà phê sữa)
    double getPrice();      // giá món
    String getCategory();   // loại món: coffee – tea – food – drink
    Image getImage();  // thuộc tính ảnh của món ăn
    Image setImage(Image image);
    void setId(String id);
    void setName(String name);
    void setPrice(double price);
    void setCategory(String category);
    void updateFromOriginal();
}
