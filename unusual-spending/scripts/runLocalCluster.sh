echo '[runLocalCluster] Building back-end...'
mvn clean package -Dquarkus.container-image.build=true || exit

echo '[runLocalCluster] Building front-end...'
cd frontend || exit
npm test || exit
npm run build || exit
docker build -t io.github.jonarzz/unusual-spending-frontend:1.0.0-SNAPSHOT .
cd ..

echo '[runLocalCluster] Starting up Minikube...'
minikube start

echo '[runLocalCluster] Loading images to Minikube...'
kubectl scale --replicas=0 deployment/payment-service
kubectl scale --replicas=0 deployment/frontend-app
minikube image load io.github.jonarzz/payment-service:1.0.0-SNAPSHOT
minikube image load io.github.jonarzz/unusual-spending-frontend:1.0.0-SNAPSHOT

echo '[runLocalCluster] Reloading cluster configuration'
kubectl apply -k k8s/

(sleep 5 && open http://localhost) &

echo '[runLocalCluster] Opening a Minikube tunnel - admin privileges required'
minikube tunnel

