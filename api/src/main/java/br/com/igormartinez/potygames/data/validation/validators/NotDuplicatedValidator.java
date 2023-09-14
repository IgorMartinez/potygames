package br.com.igormartinez.potygames.data.validation.validators;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.igormartinez.potygames.data.request.OrderItemResquestDTO;
import br.com.igormartinez.potygames.data.validation.annotations.NotDuplicated;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotDuplicatedValidator implements ConstraintValidator<NotDuplicated, List<?>> {

    @Override
    public boolean isValid(List<?> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty())
            return true;

        if (value.get(0) instanceof OrderItemResquestDTO) {
            Set<Long> elements = new HashSet<>();
            for (Object item : value)
                if (!elements.add(((OrderItemResquestDTO) item).idInventoryItem()))
                    return false;
            return true;
        }

        Set<Integer> elements = new HashSet<>();
        for(Object item : value)
            if (!elements.add(item.hashCode()))
                return false;
        return true;
    }
    
}
