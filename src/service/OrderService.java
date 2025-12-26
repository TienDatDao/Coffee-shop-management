package service;

import DAO.OrderDAO;
import Interface.IMenuItem;
import model.MenuItem;
import model.Order;
import model.OrderItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderService {
    private MenuService menuService;
    private OrderDAO orderDAO;

    private Order currentOrder;

    public OrderService(MenuService menuService) {
        this.menuService = menuService;
        this.orderDAO = new OrderDAO();
    }

    public void createOrder(String tableId, List<OrderItem> items) {
        this.currentOrder = new Order();
        this.currentOrder.setTableId(tableId);
        this.currentOrder.setCreatedTime(new Date());
        this.currentOrder.setStatus("Pending");
        this.currentOrder.setItems(new ArrayList<>());

        System.out.println("Đã khởi tạo đơn hàng tạm cho bàn: " + tableId);
    }

    public void addItem(MenuItem item, int quantity, String note) {
        if (this.currentOrder == null) {
            System.err.println("Chưa khởi tạo đơn hàng! Hãy gọi createOrder trước.");
            return;
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setMenuItem(item);
        orderItem.setQuantity(quantity);
        orderItem.setNote(note);

        this.currentOrder.addOrderItem(orderItem);
        System.out.println("Đã thêm món: " + item.getName());
    }

    public Order getCurrentOrder() {
        return this.currentOrder;
    }

    public Order getOrderById(String orderIdStr) {
        return null;
    }

    public void removeItem(int itemId) {
        if (this.currentOrder != null && itemId >= 0 && itemId < this.currentOrder.getItems().size()) {
            this.currentOrder.getItems().remove(itemId);
            System.out.println("Đã xóa món tại vị trí: " + itemId);
        }
    }

    public void updateItem(MenuItem item) {
        if (menuService != null) menuService.updateMenuItem(item);
    }

    public List<IMenuItem> getAllItems() {
        return (menuService != null) ? menuService.getAllItems() : new ArrayList<>();
    }

    public IMenuItem getItemById(String itemId) {
        return (menuService != null) ? menuService.getItemById(itemId) : null;
    }
}