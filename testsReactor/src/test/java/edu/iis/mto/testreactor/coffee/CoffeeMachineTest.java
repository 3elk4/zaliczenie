package edu.iis.mto.testreactor.coffee;

import static org.hamcrest.MatcherAssert.assertThat;

import edu.iis.mto.testreactor.coffee.milkprovider.MilkProvider;
import edu.iis.mto.testreactor.coffee.milkprovider.MilkProviderException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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

    private CoffeOrder createOrder(CoffeeSize size, CoffeType type){
        return CoffeOrder.builder().withSize(size).withType(type).build();
    }

    private CoffeeReceipe createRecipe(int milkAmount, CoffeeSize size, int waterAmout){
        return CoffeeReceipe.builder().withMilkAmount(milkAmount).withWaterAmounts(Map.of(size, waterAmout)).build();
    }
}
