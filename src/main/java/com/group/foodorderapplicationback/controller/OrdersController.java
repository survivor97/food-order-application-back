package com.group.foodorderapplicationback.controller;

import com.group.foodorderapplicationback.model.Address;
import com.group.foodorderapplicationback.model.Food;
import com.group.foodorderapplicationback.model.OrderStatus;
import com.group.foodorderapplicationback.model.Orders;
import com.group.foodorderapplicationback.service.OrdersService;
import com.group.foodorderapplicationback.service.WebSocketService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrdersController {
    private final OrdersService ordersService;
    private final WebSocketService webSocketService;

    @GetMapping(value = "/orders/all")
    public ResponseEntity<List<Orders>> getAllOrders() {
        return ResponseEntity.ok().body(ordersService.findAll());
    }

    @GetMapping(value = "/orders/accepted")
    public ResponseEntity<List<Orders>> getAllAcceptedOrders() {
        return ResponseEntity.ok().body(ordersService.findAllAcceptedOrders());
    }

    @GetMapping(value = "/orders", params = "status")
    public ResponseEntity<List<Orders>> getAllOrdersByOrderStatus(@RequestParam OrderStatus status) {
        return ResponseEntity.ok().body(ordersService.findAllByOrderStatus(status));
    }

    @PostMapping(value = "/orders/new", params = "userId")
    public ResponseEntity<Orders> newOrder(@RequestParam Long userId, @RequestBody Orders order) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/orders/new").toUriString());    //Status 201 - created
        return ResponseEntity.created(uri).body(ordersService.insertOrder(userId, order));
    }

    @PostMapping(value = "/orders/new-authenticated")
    public ResponseEntity<Orders> newOrder(HttpServletRequest request, @RequestBody Orders order) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/orders/new-authenticated").toUriString());    //Status 201 - created
        Orders newOrder = ordersService.insertOrderForAuthenticatedUser(request, order);
        webSocketService.notifyStaff("new-order");
        return ResponseEntity.created(uri).body(newOrder);
    }

    @PutMapping(value = "/orders/accept-order")
    public ResponseEntity<Orders> setAcceptedStatus(@RequestParam Long id) {
        Orders order = ordersService.setAcceptedStatus(id);
        webSocketService.notifyAllDeliveryUsers("new-order-available");
        webSocketService.notifyCustomer("order-update", order.getUser().getUsername());
        return ResponseEntity.ok().body(order);
    }

    @PutMapping(value = "/orders/prepare-order")
    public ResponseEntity<Orders> setPreparingStatus(@RequestParam Long id) {
        Orders order = ordersService.setPreparingStatus(id);
        if(order.getDeliveryUser() != null) {
            webSocketService.notifyDeliveryUser("order-update", order.getDeliveryUser().getUsername());
        }
        webSocketService.notifyCustomer("order-update", order.getUser().getUsername());
        return ResponseEntity.ok().body(order);
    }

    @PutMapping(value = "/orders/pick-ready")
    public ResponseEntity<Orders> setPickReadyStatus(@RequestParam Long id) {
        Orders order = ordersService.setPickReadyStatus(id);
        if(order.getDeliveryUser() != null) {
            webSocketService.notifyDeliveryUser("order-update", order.getDeliveryUser().getUsername());
        }
        webSocketService.notifyCustomer("order-update", order.getUser().getUsername());
        return ResponseEntity.ok().body(order);
    }

    @PutMapping(value = "/orders/on-the-way")
    public ResponseEntity<Orders> setOnTheWayStatus(@RequestParam Long id) {
        Orders order = ordersService.setOnTheWay(id);
        if(order.getDeliveryUser() != null) {
            webSocketService.notifyDeliveryUser("order-update", order.getDeliveryUser().getUsername());
        }
        webSocketService.notifyCustomer("order-update", order.getUser().getUsername());
        return ResponseEntity.ok().body(order);
    }

    @PutMapping(value = "/orders/set-delivered-status", params = "id")
    public ResponseEntity<Orders> setDeliveredStatus(@RequestParam Long id) {
        Orders order = ordersService.setDeliveredStatus(id);
        webSocketService.notifyCustomer("order-update", order.getUser().getUsername());
        return ResponseEntity.ok().body(ordersService.setDeliveredStatus(id));
    }

    @PutMapping(value = "/orders/set-rejected-status", params = "id")
    public ResponseEntity<Orders> setRejectedStatus(@RequestParam Long id) {
        Orders order = ordersService.setRejectedStatus(id);
        if(order.getDeliveryUser() != null) {
            webSocketService.notifyDeliveryUser("order-rejected", order.getDeliveryUser().getUsername());
        }
        webSocketService.notifyAllDeliveryUsers("new-order-available");
        webSocketService.notifyCustomer("order-update", order.getUser().getUsername());
        return ResponseEntity.ok().body(order);
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderMap {
        List<Food> foodList;
        Address address;
    }
}
