# Ranked domain service

### How to run application
1. To run an application run docker-compose.yml

### How to build your own Docker image
1. To build your own image run `sbt docker:publishLocal`
2. To set your database configuration, change MongoDB connection string in resources/application.conf

### Routes
1. To get ranked list for all categories: http://localhost:10202/ranked-domains
2. To get ranked for one category: http://localhost:10202/ranked-domains/category/:category

### Key TODOs
1. Create tests for clients, services, routes (Using Mockito and ScalaSpec)
2. Improve ErrorHandling