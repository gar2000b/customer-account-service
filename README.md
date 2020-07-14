# customer-account-service
Customer Account Service

docker network create -d bridge account 
docker network ls  

docker build -t gar2000b/customer-account-service .  
docker run -it -d -p 9088:9088 --network="customer-account" --name customer-account-service gar2000b/customer-account-service  

All optional:

docker create -it gar2000b/customer-account-service bash  
docker ps -a  
docker start ####  
docker ps  
docker attach ####  
docker remove ####  
docker image rm gar2000b/customer-account-service  
docker exec -it customer-account-service sh  
docker login  
docker push gar2000b/customer-account-service  