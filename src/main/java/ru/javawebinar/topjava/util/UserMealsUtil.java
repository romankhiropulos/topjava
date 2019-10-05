package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.javawebinar.topjava.util.TimeUtil.isBetween;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,10,0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,13,0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,20,0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,10,0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,13,0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,20,0), "Ужин", 510)
        );
        List<UserMealWithExceed> list = getFilteredWithExceeded(mealList, LocalTime.of(7, 0),
                LocalTime.of(22, 0), 2000);

        List<UserMealWithExceed> listOptional = getFilteredWithExceededOptional(mealList, LocalTime.of(7, 0),
                LocalTime.of(12, 0), 2000);

        list.forEach(x -> System.out.println(x.getDescription() + " " + x.getDateTime() + " " + x.getCalories() + " " + x.isExceed()));
        // listOptional.forEach(x -> System.out.println(x.getDescription() + " " + x.getDateTime() + " " + x.getCalories() + " " + x.isExceed()));
    }

    public static List<UserMealWithExceed> getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime,
                                                                   LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> allDays = new HashMap<>();
        mealList.forEach(meal -> allDays.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum));

        List<UserMealWithExceed> userMealWithExceedList = new ArrayList<>();
        mealList.forEach(meal -> {
            if (isBetween(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                userMealWithExceedList.add(new UserMealWithExceed(meal.getDateTime(), meal.getDescription(),
                        meal.getCalories(), allDays.get(meal.getDateTime().toLocalDate()) > caloriesPerDay));
            }
        });

        return userMealWithExceedList;
    }

    public static List<UserMealWithExceed> getFilteredWithExceededOptional(List<UserMeal> mealList, LocalTime startTime,
                                                                           LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> allDays = mealList.stream()
                .collect(Collectors.toMap(
                        userMeal -> userMeal.getDateTime().toLocalDate(),
                        UserMeal::getCalories,
                        Integer::sum));

        return mealList.stream()
                .filter(meal -> isBetween(meal.getDateTime().toLocalTime(), startTime, endTime))
                .flatMap(meal -> Stream.of(new UserMealWithExceed(meal.getDateTime(), meal.getDescription(),
                        meal.getCalories(), allDays.get(meal.getDateTime().toLocalDate()) > caloriesPerDay)))
                .collect(Collectors.toList());
    }
}
