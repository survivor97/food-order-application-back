package com.group.foodorderapplicationback.service;

import com.group.foodorderapplicationback.model.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantService {
    List<Restaurant> findAll();
    Optional<Restaurant> findById(Long id);
    Restaurant update(Restaurant restaurant);
    Restaurant save(Restaurant restaurant);
    void deleteById(Long id);
}
