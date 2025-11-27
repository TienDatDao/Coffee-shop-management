package view.MockTest;

import Interface.IOrderItem;

public class MockOrderItem implements IOrderItem {
    private String menuItemId;
    private int quantity;
    private String subtotal;
    public MockOrderItem(String menuItemId, int quantity, String subtotal){
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }
    @Override
    public String getMenuItemId() {
        return null;
    }

    @Override
    public int getQuantity() {
        return 0;
    }

    @Override
    public double getSubtotal() {
        return 0;
    }
}
