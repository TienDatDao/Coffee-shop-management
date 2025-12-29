package service;

import Interface.IOrder;
import Interface.IOrderItem;
import Interface.IOrderService;
import DAO.OrderDAO;
import model.Order;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderService implements IOrderService {
    private final OrderDAO orderDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
    }

    @Override
    public IOrder createOrder(String tableId, List<IOrderItem> items, String orderId) {
        Order newOrder = new Order();
        newOrder.setTableId(tableId);
        // DB tự sinh thời gian, nhưng model set trước cũng được
        newOrder.setCreatedTime(new java.util.Date());
        newOrder.setItems(items); // Set thẳng List<IOrderItem>

        // Gọi DAO
        boolean tt = orderDAO.saveOrder(newOrder);
        Random generatedId = new Random();
        int x = generatedId.nextInt(100);

        if (tt) {
            newOrder.setOrderId(String.valueOf(x));
            return newOrder;
        }
        return null;
    }

    @Override
    public IOrder getOrderById(String orderId) {
        try {
            int id = Integer.parseInt(orderId);
            return orderDAO.getOrderById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public List<IOrder> getOrdersForToday() {
        long millis = System.currentTimeMillis();
        Date today = new Date(millis);

        // Cần vào DAO cài đặt lại hàm getOrdersByDate trả về List<Order> đầy đủ
        List<Order> daoOrders = orderDAO.getOrdersByDate(today);

        // Cast sang List<IOrder>
        List<IOrder> result = new ArrayList<>();
        if (daoOrders != null) {
            result.addAll(daoOrders);
        }
        return result;
    }
    public List<IOrder> getAllOrdersFromDB() {

        List<model.Order> orders = orderDAO.getAllOrders();

        // Ép kiểu về Interface để Controller sử dụng
        List<IOrder> result = new ArrayList<>();
        if (orders != null) {
            result.addAll(orders);
        }
        return result;
    }

}