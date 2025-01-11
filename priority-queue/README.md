## Redisson priority queue example
### Description
A simple application to demonstrate prioritized consumption of elements using Redisson library.
`edu.udemy.redis.producer.OrderCollector` pushes elements in batches to Redis sorted set and notifies consumer(s).
`edu.udemy.redis.consumer.OrderProcessor` reads elements on notification.
Elements are prioritized by UserClass. Elements having the same class are not further prioritized, but Redis sorts them lexicographically, treating as plain text. 
### Running application
 * start Redis at default address redis://127.0.0.1:6379. I.e. with Docker:
   - run `docker-compose up` from project directory
   - build application `gradlew build`
   - start application
### Ideas for further improvements
 * Externalize configuration to yaml file
 * Run application in Docker container
 * Add external API to feed elements