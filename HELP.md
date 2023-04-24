# Getting Started

### How to run locally

1. Run DemoMizuhoApplication
2. Access swagger via http://localhost:8080/swagger-ui.html url
3. Add your own orders to Order queue via POST method /api/order
4. Trigger engine using /api/kickoff

### Additional operations for OrderBook available via Swagger
* DELETE order from OrderBook
* Modify Order size by OrderId
* Given a side and a level (an integer value >0) return the price for that level
* Given a side and a level return the total size available for that level
* Given a side return all the orders from that side of the book, in level- and time-order