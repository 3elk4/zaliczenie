package edu.iis.mto.testreactor.coffee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

import edu.iis.mto.testreactor.coffee.milkprovider.MilkProvider;
import edu.iis.mto.testreactor.coffee.milkprovider.MilkProviderException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Optional;

class CoffeeMachineTest {
    private static Grinder grinder = Mockito.mock(Grinder.class);
    private static MilkProvider milkProvider = Mockito.mock(MilkProvider.class);
    private static CoffeeReceipes recipes = Mockito.mock(CoffeeReceipes.class);

    private static CoffeeMachine coffeeMachine;

    private final CoffeeSize IRREVELANT_COFFEE_SIZE = CoffeeSize.STANDARD;
    private final CoffeType IRREVELANT_COFFEE_TYPE = CoffeType.ESPRESSO;
    private final int IRREVELANT_MILK_AMOUNT = 1;
    private final int IRREVELANT_WATER_AMOUNT = 3;
    private final double IRREVELANT_WEIGHT_GR = 5.0;
    private final int ZERO = 0;

    @BeforeAll
    public static void init() {
        coffeeMachine = new CoffeeMachine(grinder, milkProvider, recipes);
    }

    @Test
    public void itCompiles() {
        assertThat(true, Matchers.equalTo(true));
    }

    @Test
    void shouldReturnCoffeeWithProperData() {
        CoffeOrder order = createOrder(IRREVELANT_COFFEE_SIZE, IRREVELANT_COFFEE_TYPE);
        CoffeeReceipe recipe = createRecipe(IRREVELANT_MILK_AMOUNT, IRREVELANT_COFFEE_SIZE, IRREVELANT_WATER_AMOUNT);

        Mockito.when(grinder.canGrindFor(IRREVELANT_COFFEE_SIZE)).thenReturn(true);
        Mockito.when(grinder.grind(IRREVELANT_COFFEE_SIZE)).thenReturn(IRREVELANT_WEIGHT_GR);
        Mockito.when(recipes.getReceipe(IRREVELANT_COFFEE_TYPE)).thenReturn(Optional.of(recipe));

        Coffee cafe = coffeeMachine.make(order);
        Assertions.assertTrue(cafe.getMilkAmout().isPresent());
        Assertions.assertEquals(IRREVELANT_MILK_AMOUNT, cafe.getMilkAmout().get());
        Assertions.assertEquals(IRREVELANT_WEIGHT_GR, cafe.getCoffeeWeigthGr());
        Assertions.assertEquals(IRREVELANT_WATER_AMOUNT, cafe.getWaterAmount());
    }

    @Test
    void shouldCallInOrderProceduresOfMakingCoffee() throws MilkProviderException {
        CoffeOrder order = createOrder(IRREVELANT_COFFEE_SIZE, IRREVELANT_COFFEE_TYPE);
        CoffeeReceipe recipe = createRecipe(IRREVELANT_MILK_AMOUNT, IRREVELANT_COFFEE_SIZE, IRREVELANT_WATER_AMOUNT);

        Mockito.when(grinder.canGrindFor(IRREVELANT_COFFEE_SIZE)).thenReturn(true);
        Mockito.when(grinder.grind(IRREVELANT_COFFEE_SIZE)).thenReturn(IRREVELANT_WEIGHT_GR);
        Mockito.when(recipes.getReceipe(IRREVELANT_COFFEE_TYPE)).thenReturn(Optional.of(recipe));

        Coffee cafe = coffeeMachine.make(order);
        InOrder callOrder = Mockito.inOrder(grinder, recipes, milkProvider);

        callOrder.verify(grinder).canGrindFor(IRREVELANT_COFFEE_SIZE);
        callOrder.verify(grinder).grind(IRREVELANT_COFFEE_SIZE);
        callOrder.verify(recipes, times(3)).getReceipe(IRREVELANT_COFFEE_TYPE);
        callOrder.verify(milkProvider).heat();
        callOrder.verify(milkProvider).pour(IRREVELANT_MILK_AMOUNT);
    }

    @Test
    void shouldMakeCoffeeWithoutMilk(){
        CoffeOrder order = createOrder(IRREVELANT_COFFEE_SIZE, IRREVELANT_COFFEE_TYPE);
        CoffeeReceipe recipe = createRecipe(ZERO, IRREVELANT_COFFEE_SIZE, IRREVELANT_WATER_AMOUNT);

        Mockito.when(grinder.canGrindFor(IRREVELANT_COFFEE_SIZE)).thenReturn(true);
        Mockito.when(grinder.grind(IRREVELANT_COFFEE_SIZE)).thenReturn(IRREVELANT_WEIGHT_GR);
        Mockito.when(recipes.getReceipe(IRREVELANT_COFFEE_TYPE)).thenReturn(Optional.of(recipe));

        Coffee cafe = coffeeMachine.make(order);
        Assertions.assertFalse(cafe.getMilkAmout().isPresent());
        Assertions.assertEquals(IRREVELANT_WEIGHT_GR, cafe.getCoffeeWeigthGr());
        Assertions.assertEquals(IRREVELANT_WATER_AMOUNT, cafe.getWaterAmount());
    }

    @Test
    void shouldThrowUnsupportedCoffeeExceptionWhenNoRecipe() {
        CoffeOrder order = createOrder(IRREVELANT_COFFEE_SIZE, IRREVELANT_COFFEE_TYPE);

        Mockito.when(grinder.canGrindFor(IRREVELANT_COFFEE_SIZE)).thenReturn(true);
        Mockito.when(grinder.grind(IRREVELANT_COFFEE_SIZE)).thenReturn(IRREVELANT_WEIGHT_GR);
        Mockito.when(recipes.getReceipe(IRREVELANT_COFFEE_TYPE)).thenReturn(Optional.empty());

        Assertions.assertThrows(UnsupportedCoffeeException.class,
                                () -> coffeeMachine.make(order));
    }

    @Test
    void shouldThrowNoCoffeeBeansExceptionIfCantGrind() {
        CoffeOrder order = createOrder(IRREVELANT_COFFEE_SIZE, IRREVELANT_COFFEE_TYPE);

        Mockito.when(grinder.canGrindFor(IRREVELANT_COFFEE_SIZE)).thenReturn(false);

        Assertions.assertThrows(NoCoffeeBeansException.class,
                () -> coffeeMachine.make(order));
    }

    @Test
    void shouldThrowUnsupportedCoffeeSizeExceptionIfRecipeDoesNotContainWaterAmountForCoffee() {
        CoffeOrder order = createOrder(IRREVELANT_COFFEE_SIZE, IRREVELANT_COFFEE_TYPE);
        CoffeeReceipe recipe = CoffeeReceipe.builder().withMilkAmount(IRREVELANT_MILK_AMOUNT).build();

        Mockito.when(grinder.canGrindFor(IRREVELANT_COFFEE_SIZE)).thenReturn(true);
        Mockito.when(grinder.grind(IRREVELANT_COFFEE_SIZE)).thenReturn(IRREVELANT_WEIGHT_GR);
        Mockito.when(recipes.getReceipe(IRREVELANT_COFFEE_TYPE)).thenReturn(Optional.of(recipe));

        Assertions.assertThrows(UnsupportedCoffeeSizeException.class,
                () -> coffeeMachine.make(order));
    }

    private CoffeOrder createOrder(CoffeeSize size, CoffeType type){
        return CoffeOrder.builder().withSize(size).withType(type).build();
    }

    private CoffeeReceipe createRecipe(int milkAmount, CoffeeSize size, int waterAmount){
        return CoffeeReceipe.builder().withMilkAmount(milkAmount).withWaterAmounts(Map.of(size, waterAmount)).build();
    }
}
