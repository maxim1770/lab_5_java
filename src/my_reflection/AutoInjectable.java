package my_reflection;

import java.lang.annotation.*;

// Декларация аннотации "AutoInjectable"
// Аннотация применима к полям классов
@Target(ElementType.FIELD)
// Аннотация будет доступна на стадии выполнения 
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoInjectable {

}
