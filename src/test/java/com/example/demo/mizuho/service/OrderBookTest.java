package com.example.demo.mizuho.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.demo.mizuho.domain.Order;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderBookTest {

  @Test
  void GIVEN_bidOrder_MATCH_sellOrder() {
    OrderBook ob = new OrderBook(new TreeSet<>(), Set.of(offerOrder(1, 10, 20)), new HashMap<>());

    ob.bid(bidOrder(2, 10, 20));
    assertEquals(0, ob.getBids().size());
    assertEquals(0, ob.getOffers().size());
  }

  @Test
  void GIVEN_bidOrder_NoMATCH_sellOrder() {
    OrderBook ob = new OrderBook(new TreeSet<>(), Set.of(offerOrder(1, 10, 20)), new HashMap<>());

    ob.bid(bidOrder(2, 8, 1));
    assertEquals(1, ob.getBids().size());
    assertEquals(1, ob.getOffers().size());
  }

  @Test
  void GIVEN_offerOrder_MATCH_bidOrder() {
    OrderBook ob = new OrderBook(Set.of(bidOrder(1, 10, 20)), new TreeSet<>(), new HashMap<>());

    ob.offer(offerOrder(1, 10, 20));
    assertEquals(0, ob.getBids().size());
    assertEquals(0, ob.getOffers().size());
  }

  // TODO add coverage for multiple use cases

  @Test
  void GIVEN_bidOrderId_REMOVE_oder() {
    Order newBid = bidOrder(10, 1, 1);
    Set<Order> bids = mockBids();
    bids.add(newBid);
    OrderBook ob = new OrderBook(bids, mockOffers(), Map.of(10L, newBid));

    ob.removeOrder(10L);
    assertEquals(5, ob.getBids().size());
    assertEquals(5, ob.getOffers().size());
  }

  @Test
  void GIVEN_sideAndLevel_GET_totalSize() {
    OrderBook ob = new OrderBook(mockBids(), mockOffers(), new HashMap<>());

    assertEquals(1, ob.getTotalSizeByLevel('B', 1));
    assertEquals(20, ob.getTotalSizeByLevel('B', 2));
    assertEquals(20, ob.getTotalSizeByLevel('B', 3));
    assertEquals(1, ob.getTotalSizeByLevel('B', 4));

    assertEquals(10, ob.getTotalSizeByLevel('O', 1));
    assertEquals(2, ob.getTotalSizeByLevel('O', 2));
    assertEquals(10, ob.getTotalSizeByLevel('O', 3));
    assertEquals(1, ob.getTotalSizeByLevel('O', 4));
  }

  @Test
  void GIVEN_sideAndLevel_GET_totalSize_exception() {
    OrderBook ob = new OrderBook(mockBids(), mockOffers(), new HashMap<>());

    assertThrows(RuntimeException.class, () -> ob.getTotalSizeByLevel('B', 8));
  }

  private Set<Order> mockBids() {
    Set<Order> bids = new TreeSet<>();
    bids.add(bidOrder(1, 10, 20));
    bids.add(bidOrder(2, 15, 10));
    bids.add(bidOrder(3, 15, 10));
    bids.add(bidOrder(4, 5, 1));
    bids.add(bidOrder(5, 20, 1));
    return bids;
  }

  private Set<Order> mockOffers() {
    Set<Order> offers = new TreeSet<>();
    offers.add(offerOrder(6, 10, 2));
    offers.add(offerOrder(7, 15, 5));
    offers.add(offerOrder(8, 15, 5));
    offers.add(offerOrder(9, 5, 10));
    offers.add(offerOrder(10, 80, 1));
    return offers;
  }

  private Order bidOrder(long id, long price, long size) {
    return new Order(id, price, 'B', size);
  }

  private Order offerOrder(long id, long price, long size) {
    return new Order(id, price, 'O', size);
  }
}
