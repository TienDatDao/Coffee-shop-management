package Interface;

public interface IMenuItem {

    String getId();
    void setId(String id);

    String getName();
    void setName(String name);

    double getPrice();
    void setPrice(double price);

    String getCategory();
    void setCategory(String category);

    // CHỈ LƯU PATH
    String getImagePath();
    void setImagePath(String path);
}
