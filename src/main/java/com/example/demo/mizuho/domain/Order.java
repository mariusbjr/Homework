package com.example.demo.mizuho.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

public class Order implements Comparable<Order>{

  private long id;
  private double price;
  private char side; // B "Bid" or O "Offer"
  private long size;
  @JsonIgnore
  private LocalDateTime timestamp = LocalDateTime.now();

  public Order(long id, double price, char side, long size) {
    this.id = id;
    this.price = price;
    this.side = side;
    this.size = size;
  }

  public long getId() {
    return id;
  }

  public double getPrice() {
    return price;
  }

  public char getSide() {
    return side;
  }

  public long getSize() {
    return size;
  }

  public LocalDateTime getTimestamp() {return timestamp;}

  public void setSize(long newSize){
    this.size = newSize;
  }

  public void decreaseSize(long delta){
    size = getSize() - delta;
  }

  @JsonIgnore
  public boolean isBuyOrder(){
    return 'B' == this.getSide();
  }

  @Override
  public String toString() {
    return "Order{" +
            "id=" + id +
            ", price=" + price +
            ", side=" + side +
            ", size=" + size +
            ", ts=" + timestamp +
            '}';
  }

  @Override
  public int compareTo(Order o) {
      if (0 == Double.compare(this.getPrice(), o.getPrice())) {
        return this.getTimestamp().compareTo(o.getTimestamp());
      } else {
        if(o.isBuyOrder()){
          return -1 * Double.compare(this.getPrice(), o.getPrice());
        } else {
          return Double.compare(this.getPrice(), o.getPrice());
      }
    }
  }
}
