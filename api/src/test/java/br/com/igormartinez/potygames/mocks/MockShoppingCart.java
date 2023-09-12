package br.com.igormartinez.potygames.mocks;

import java.util.ArrayList;
import java.util.List;

import br.com.igormartinez.potygames.models.InventoryItem;
import br.com.igormartinez.potygames.models.ShoppingCartItem;
import br.com.igormartinez.potygames.models.User;

public class MockShoppingCart {

    private final MockUser mockerUser;
    private final MockInventoryItem mockerInventoryItem;

    public MockShoppingCart(MockUser mockerUser, MockInventoryItem mockerInventoryItem) {
        this.mockerUser = mockerUser;
        this.mockerInventoryItem = mockerInventoryItem;
    }

    public ShoppingCartItem mockEntity(int number) {
        ShoppingCartItem item = new ShoppingCartItem();
        item.setId(Long.valueOf(number));
        item.setUser(mockerUser.mockUser(number));
        item.setItem(mockerInventoryItem.mockEntity(number));
        item.setQuantity(number);
        return item;
    }

    public ShoppingCartItem mockEntity(Long id, User user, InventoryItem inventoryItem, Integer quantity) {
        ShoppingCartItem item = new ShoppingCartItem();
        item.setId(id);
        item.setUser(user);
        item.setItem(inventoryItem);
        item.setQuantity(quantity);
        return item;
    }

    public List<ShoppingCartItem> mockEntityList(int number) {
        List<ShoppingCartItem> list = new ArrayList<>();
        for (int i=1; i<=number; i++) {
            list.add(mockEntity(i));
        }
        return list;
    }
}
