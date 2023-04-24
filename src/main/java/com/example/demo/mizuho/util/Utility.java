package com.example.demo.mizuho.util;

import com.example.demo.mizuho.domain.Order;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Utility {

  private Utility() {}

  public static void putToLevelMap(Map<Integer, List<Order>> levelMap, Order o, Integer level) {
    if (levelMap.containsKey(level)) {
      levelMap.get(level).add(o);
    } else {
      List<Order> l = new ArrayList<>();
      l.add(o);
      levelMap.put(level, l);
    }
  }

  public static Set<Order> clearEmptyOrders(Set<Order> orders, Map<Long, Order> orderCache) {
    orders.stream()
            .filter(o -> 0 == o.getSize())
            .map(Order::getId)
            .forEachOrdered(id -> orderCache.remove(id));
    return orders.stream()
            .filter(o -> o.getSize() > 0)
            .collect(Collectors.toCollection(TreeSet::new));
  }
}
