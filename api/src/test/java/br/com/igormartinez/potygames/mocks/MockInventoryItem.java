package br.com.igormartinez.potygames.mocks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import br.com.igormartinez.potygames.data.dto.v1.InventoryItemDTO;
import br.com.igormartinez.potygames.models.InventoryItem;
import br.com.igormartinez.potygames.models.Product;
import br.com.igormartinez.potygames.models.YugiohCard;

public class MockInventoryItem {

    private final MockProduct productMocker;
    private final MockYugiohCard yugiohCardMocker;

    public MockInventoryItem(MockProduct productMocker, MockYugiohCard yugiohCardMocker) {
        this.productMocker = productMocker;
        this.yugiohCardMocker = yugiohCardMocker;
    }

    private InventoryItem mockEntity(int number, Product product, YugiohCard yugiohCard) {
        InventoryItem item = new InventoryItem();
        item.setId(Long.valueOf(number));

        if (product != null) 
            item.setProduct(product);
        else if (yugiohCard != null)
            item.setYugiohCard(yugiohCard);
        
        item.setVersion("Version " + number);
        item.setCondition("Condition " + number);
        item.setPrice(new BigDecimal(number + ".99"));
        item.setQuantity(number);

        return item;
    }

    public InventoryItem mockEntityWithProduct(int number) {
        return mockEntity(number, productMocker.mockEntity(number), null);
    }

    public InventoryItem mockEntityWithYugiohCard(int number) {
        return mockEntity(number, null, yugiohCardMocker.mockEntity(number));
    }

    public InventoryItem mockEntity(InventoryItemDTO itemDTO) {
        InventoryItem item = new InventoryItem();
        item.setId(itemDTO.id());

        if (itemDTO.product() != null) 
            item.setProduct(productMocker.mockEntity(itemDTO.product().intValue()));
        
        if (itemDTO.yugiohCard() != null)
            item.setYugiohCard(yugiohCardMocker.mockEntity(itemDTO.yugiohCard().intValue()));
        
        item.setVersion(itemDTO.version());
        item.setCondition(itemDTO.condition());
        item.setPrice(itemDTO.price());
        item.setQuantity(itemDTO.quantity());

        return item;
    }

    public List<InventoryItem> mockEntityList(int startNumber, int endNumber) {
        List<InventoryItem> list = new ArrayList<>();
        for (int i=startNumber; i<=endNumber; i++) {
            list.add(
                i%2 == 0
                ? mockEntityWithProduct(i)
                : mockEntityWithYugiohCard(i)
            );
        }
        return list;
    }

    public Page<InventoryItem> mockPage(int totalElements, Pageable pageable) {
        int sizePage = pageable.getPageSize();
        int numberPage = pageable.getPageNumber();

        int startNumber = 1 + (sizePage * numberPage);
        int endNumber = (numberPage + 1) * sizePage;
        List<InventoryItem> mockList = mockEntityList(startNumber, Math.min(totalElements, endNumber));
        
        Page<InventoryItem> page = new PageImpl<>(mockList, pageable, totalElements);
        return page;
    }

    public InventoryItemDTO mockDTOWithProduct(int number) {
        return new InventoryItemDTO(
            Long.valueOf(number), 
            Long.valueOf(number), 
            null, 
            "Version " + number, 
            "Condition " + number, 
            new BigDecimal(number + ".99"), 
            number);
    }

    public InventoryItemDTO mockDTOWithYugiohCard(int number) {
        return new InventoryItemDTO(
            Long.valueOf(number), 
            null, 
            Long.valueOf(number), 
            "Version " + number, 
            "Condition " + number, 
            new BigDecimal(number + ".99"), 
            number);
    }
}
