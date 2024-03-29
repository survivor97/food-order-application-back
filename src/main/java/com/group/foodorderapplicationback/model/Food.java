package com.group.foodorderapplicationback.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String price;
    private String weight;

    @OneToOne(cascade = CascadeType.ALL)
    private Image foodImage;

    @ManyToOne
    private FoodCategory foodCategory;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany(mappedBy = "favouriteFood", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<User> user;

    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL)
    private List<Review> foodReview;

    @ManyToMany(mappedBy = "foodList")
    private List<Restaurant> restaurantList;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany(mappedBy = "foodList")
    private List<Orders> ordersList;

}
