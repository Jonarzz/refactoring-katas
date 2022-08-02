echo '[reloadBackEnd] Building back-end...'
mvn clean package -Dquarkus.container-image.build=true || exit

echo '[reloadBackEnd] Loading images to Minikube...'
kubectl scale --replicas=0 deployment/payment-service
minikube image load io.github.jonarzz/payment-service:1.0.0-SNAPSHOT
kubectl scale --replicas=1 deployment/payment-service

echo '[reloadBackEnd] Back-end reloaded'