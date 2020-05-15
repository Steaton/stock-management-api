## Backend Assessment  

# Application
There are many stock exchanges and many stocks in the world.

* Stock exchange properties:- id(Number), name(String), description(String) , liveInMarket(boolean)
* Stock properties:- id (Number), name (String), description(String), currentPrice (Amount) and lastUpdate (Timestamp).

Rules:
* Each Stock exchange can have many stocks
* Stock exchange having less than 5 stocks can not be live in the market ie liveInMarket is false.
* The particular stock can be listed in many stock exchanges and all the properties of a stock will remain same in all the exchanges.


Application should contain the below end points:

  * List one stockExchange with all the stocks( api/stock-exchange/{name} )
  * Able to create a stock ( api/stock )
  * Able to add the stock to the stock exchange (api/stock-exchange/{name} )
  * Able to delete the stock from the stock exchange (api/stock-exchange/{name} )
  * Update the price of a stock(api/stock )
  * Able to the delete the stock from the system (api/stock)


# Implementation
* We would like you to create a java based backend application using framework Spring which manages the stock exchange application
* All the endpoints should be only be available to authorized user(This is not an absolute requirement but good to have)
* Application can us a  memory database like h2.
* Multiple users of the system can use the system simultanously.
* Treat this application as a real MVP that should go to production.
* Make sure to include an explanation on how to run and build your solution, and provide initialization resources (e.g. scripts,...) as well.

# Merge request  
Provide us with a merge request to master of this repository.

# Duration
This assignment should take 6-7 hours at the most.

# What we will be looking for
* We want you to express your best design and coding skills.
* We will be looking for quality code and best practices.

Good luck!
