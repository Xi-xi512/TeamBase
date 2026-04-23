package com.canteen.service;

import com.canteen.dao.DishDAO;
import com.canteen.entity.Dish;
import com.canteen.util.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DishServiceTest {

    @Mock
    private DishDAO dishDAO;

    @InjectMocks
    private DishService dishService;

    @Test
    void getTomorrowMenu_shouldQueryByTomorrowDate() {
        String tomorrow = DateUtils.tomorrowYyyyMmDd();
        List<Dish> expected = List.of(new Dish("D001", "宫保鸡丁", 15, "desc", tomorrow));
        when(dishDAO.findByMenuDate(tomorrow)).thenReturn(expected);

        List<Dish> actual = dishService.getTomorrowMenu();

        assertEquals(expected, actual);
        verify(dishDAO).findByMenuDate(tomorrow);
    }
}
