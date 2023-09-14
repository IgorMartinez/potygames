package br.com.igormartinez.potygames.data.validation.annotations;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.igormartinez.potygames.data.validation.validators.NotDuplicatedValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * The annotated element must not have any duplicated elements.
 * <p>
 * Supported types are:
 * <ul>
 * <li>{@code List<OrderItemResquestDTO>} (id of inventory item is evaluated)</li>
 * <li>{@code List<Object>} (hash code is evaluated)</li>
 * </ul>
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotDuplicatedValidator.class)
@Documented
public @interface NotDuplicated {
    String message() default "The list cannot have duplicated items.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
