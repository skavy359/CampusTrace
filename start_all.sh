#!/bin/bash

# Create logs directory
mkdir -p logs

echo "Killing any existing Spring Boot instances..."
pkill -f spring-boot:run || true
sleep 2

echo "Starting Eureka Server..."
cd eureka-server
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx256m -Xms128m" > ../logs/eureka-server.log 2>&1 &
cd ..

echo "Waiting 15 seconds for Eureka to initialize..."
sleep 15

echo "Starting backend services..."
for service in user-service lost-item-service found-item-service claim-service notification-service; do
  echo "Starting $service..."
  cd $service
  ./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx256m -Xms128m" > ../logs/${service}.log 2>&1 &
  cd ..
done

echo "Waiting 20 seconds for backend services to initialize..."
sleep 20

echo "Starting API Gateway..."
cd api-gateway
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx256m -Xms128m" > ../logs/api-gateway.log 2>&1 &
cd ..

echo "All services are starting up!"
echo "Keeping script alive so background processes aren't killed..."
wait
