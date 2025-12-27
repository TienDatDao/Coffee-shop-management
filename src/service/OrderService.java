package service;
import java.util.Map;

import Interface.IMenuItem;
import model.MenuItem;
import model.Order;
import model.OrderItem;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class OrderService {
    // Map lưu trữ đơn hàng với Key là OrderId (String)
    private Map<String, Order> orders;
    private MenuService menuService;

    // Constructor
    public OrderService(MenuService menuService) {
        this.orders = new HashMap<>();
        this.menuService = menuService;
    }

    // Tạo đơn hàng mới
    public Order createOrder(String tableId, List<OrderItem> items) {
        // Giả lập tạo ID ngẫu nhiên hoặc tăng dần
        int newIdInt = orders.size() + 1;
        
        // Cần đối tượng Staff, tạm thời để null hoặc truyền vào nếu logic yêu cầu
        Order newOrder = new Order(newIdInt, null, tableId);
        
        // Thêm các món vào đơn hàng
        if (items != null) {
            for (OrderItem item : items) {
                newOrder.addOrderItem(item);
            }
        }
        
        // Lưu vào Map
        orders.put(newOrder.getOrderId(), newOrder);
        
        return newOrder;
    }

    // Các hàm dưới đây trong UML có vẻ như đang ủy quyền (delegate) cho MenuService
    // Vì tham số là MenuItem chứ không phải OrderId
    
    public void addItem(MenuItem item) {
        if (menuService != null) {
            menuService.addMenuItem(item);
        }
    }

    public void removeItem(String itemId) {
        if (menuService != null) {
            menuService.deleteMenuItem(itemId);
        }
    }

    public void updateItem(MenuItem item) {
        if (menuService != null) {
            menuService.updateMenuItem(item);
        }
    }

    public List<IMenuItem> getAllItems() {
        if (menuService != null) {
            return menuService.getAllItems();
        }
        return new ArrayList<>();
    }

    // Lấy Order theo ID. UML trả về IOrder, ở đây mình trả về Order
    public Order getOrderById(String orderId) {
        return orders.get(orderId);
    }

    public IMenuItem getItemById(String itemId) {
        if (menuService != null) {
            return menuService.getItemById(itemId);
        }
        return null;
    }
}