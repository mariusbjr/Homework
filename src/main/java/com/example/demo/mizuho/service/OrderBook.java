package com.example.demo.mizuho.service;

import com.example.demo.mizuho.domain.Order;
import com.example.demo.mizuho.util.Utility;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderBook {

  Logger log = LoggerFactory.getLogger(OrderBook.class);

  private Set<Order> bids;
  private Set<Order> offers;
  private Map<Long, Order> orderCache;

  public OrderBook() {
    this.bids = new TreeSet<>();
    this.offers = new TreeSet<>();
    this.orderCache = new HashMap<>();
  }

  // used on Units
  OrderBook(Set<Order> bids, Set<Order> offers, Map<Long, Order> orderCache) {
    this.bids = bids;
    this.offers = offers;
    this.orderCache = orderCache;
  }

  public void bid(Order bidOrder) {
    log.info("Execute BID {}", bidOrder);
    offers.stream()
        .filter(offer -> offer.getSize() > 0) // safety check
        .filter(
            offer ->
                offer.getPrice() == bidOrder.getPrice() || offer.getPrice() < bidOrder.getPrice())
        .forEachOrdered(
            offer -> {
              log.debug(
                  "Matched OFFER id '{}' for BID id '{}'. Offer details: {}",
                  offer.getId(),
                  bidOrder.getId(),
                  offer);
              long matchedSize = Math.min(offer.getSize(), bidOrder.getSize());
              offer.decreaseSize(matchedSize);
              bidOrder.decreaseSize(matchedSize);
            });

    log.info("Successfully executed BID {}. Remaining size= {}", bidOrder, bidOrder.getSize());
    // cleanup executed orders
    offers = Utility.clearEmptyOrders(offers, orderCache);

    // add order to Bids list if not all "positions" were fulfilled
    if (bidOrder.getSize() > 0) {
      bids.add(bidOrder);
      orderCache.put(bidOrder.getId(), bidOrder);
    }

    logRemainingOrders();
  }

  public void offer(Order sellOrder) {
    log.info("Execute OFFER {}", sellOrder);
    bids.stream()
        .filter(bid -> bid.getSize() > 0) // safety check
        .filter(
            bid -> bid.getPrice() == sellOrder.getPrice() || bid.getPrice() > sellOrder.getPrice())
        .forEachOrdered(
            bid -> {
              log.debug(
                  "Matched BID id '{}' for OFFER id '{}'. Bid details: {}",
                  bid.getId(),
                  sellOrder.getId(),
                  bid);
              long matchedSize = Math.min(bid.getSize(), sellOrder.getSize());
              bid.decreaseSize(matchedSize);
              sellOrder.decreaseSize(matchedSize);
            });

    log.info("Successfully executed OFFER {}. Remaining size= {}", sellOrder, sellOrder.getSize());
    // cleanup executed orders
    bids = Utility.clearEmptyOrders(bids, orderCache);

    // add order to Offers list if not all "positions" were fulfilled
    if (sellOrder.getSize() > 0) {
      offers.add(sellOrder);
      orderCache.put(sellOrder.getId(), sellOrder);
    }

    logRemainingOrders();
  }

  public boolean removeOrder(long id) {
    if (!orderCache.containsKey(id)) {
      return false;
    }
    Order toBeRemoved = orderCache.get(id);
    log.info("Removing Order {}", toBeRemoved);
    if (toBeRemoved.isBuyOrder()) {
      bids.remove(toBeRemoved);
      log.debug("Bids array after order removal: {}", bids);
    } else {
      offers.remove(toBeRemoved);
      log.debug("Offers array after order removal: {}", offers);
    }
    return true;
  }

  public boolean modifyOrderSize(long id, long size) {
    if (!orderCache.containsKey(id)) {
      return false;
    }

    Order toBeModified = orderCache.get(id);
    if (toBeModified.isBuyOrder()) {
      log.debug("Modify size to '{}' for BID with id '{}'", size, toBeModified.getId());
      bids.forEach(
          o -> {
            if (o.getId() == id) {
              o.setSize(size);
            }
          });
      log.debug("Bids array: {}", bids);
    } else {
      log.debug("Modify size to '{}' for OFFER with id '{}'", size, toBeModified.getId());
      offers.forEach(
          o -> {
            if (o.getId() == id) {
              o.setSize(size);
            }
          });
      log.debug("Offers array: {}", offers);
    }
    return true;
  }

  public double getSidePriceByLevel(char side, int level){
    if('B' == side){
      Set<Double> prices = bids.stream().map(Order::getPrice).collect(Collectors.toCollection(LinkedHashSet::new));
      return prices.stream().toList().get(level - 1);
    } else {
      Set<Double> prices = offers.stream().map(Order::getPrice).collect(Collectors.toCollection(LinkedHashSet::new));
      return prices.stream().toList().get(level - 1);
    }
  }

  public long getTotalSizeByLevel(char side, int requestedLevel) {
    Map<Integer, List<Order>> levelMap = new TreeMap<>();
    if ('B' == side) {
      Set<Double> prices =
          bids.stream().map(Order::getPrice).collect(Collectors.toCollection(LinkedHashSet::new));
      AtomicInteger levelCount = new AtomicInteger(1);
      prices.forEach(
          priceLevel -> {
            int level = levelCount.getAndIncrement();
            bids.stream()
                .filter(o -> priceLevel == o.getPrice())
                .forEachOrdered(o -> Utility.putToLevelMap(levelMap, o, level));
          });
    } else {
      Set<Double> prices =
          offers.stream().map(Order::getPrice).collect(Collectors.toCollection(LinkedHashSet::new));
      AtomicInteger levelCount = new AtomicInteger(1);
      prices.forEach(
          priceLevel -> {
            int level = levelCount.getAndIncrement();
            offers.stream()
                .filter(o -> priceLevel == o.getPrice())
                .forEachOrdered(o -> Utility.putToLevelMap(levelMap, o, level));
          });
    }

    if(levelMap.get(requestedLevel) != null){
      return levelMap.get(requestedLevel).stream().mapToLong(Order::getSize).sum();
    } else {
      throw new RuntimeException("Check requested level if is a good one!");
    }
  }

  public Set<Order> getOrders(char side){
    if('B' == side){
      return bids;
    } else {
      return offers;
    }
  }

  public Set<Order> getBids(){
    return this.bids;
  }

  public Set<Order> getOffers(){
    return this.offers;
  }

  private void logRemainingOrders() {
    log.debug("Remaining BID orders: {} - {}", bids.size(), bids);
    log.debug("Remaining OFFER orders: {} - {}", offers.size(), offers);
  }
}
