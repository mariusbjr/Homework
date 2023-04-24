package com.example.demo.mizuho.rest;

import com.example.demo.mizuho.domain.Order;
import com.example.demo.mizuho.service.OrderBook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// TODO add swagger definitions
@RestController
public class ApiController {

  private final OrderBook orderBook;

  public ApiController(OrderBook orderBook) {
    this.orderBook = orderBook;
  }

  @PostMapping("/order")
  public ResponseEntity addOrder(Order order) {
    if (order.isBuyOrder()) {
      orderBook.bid(order);
    } else {
      orderBook.offer(order);
    }
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/oder/{id}")
  public ResponseEntity removeOrder(@PathVariable long id) {
    if (orderBook.removeOrder(id)) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().body("OrderId not found");
    }
  }

  @PatchMapping("/order/{id}/{newSize}")
  public ResponseEntity modifyOrder(@PathVariable long id, @PathVariable long newSize) {
    if (orderBook.modifyOrderSize(id, newSize)) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().body("OrderId not found");
    }
  }

  @GetMapping("/price/{side}/{level}")
  public ResponseEntity getPriceLevel(@PathVariable char side, @PathVariable int level) {
    return ResponseEntity.ok(orderBook.getSidePriceByLevel(side, level));
  }

  @GetMapping("/totalSize/{side}/{level}")
  public ResponseEntity getTotalSize(@PathVariable char side, @PathVariable int level) {
    return ResponseEntity.ok(orderBook.getTotalSizeByLevel(side, level));
  }

  @GetMapping("/orders/{side}")
  public ResponseEntity getOrders(@PathVariable char side) {
    return ResponseEntity.ok(orderBook.getOrders(side));
  }
}
