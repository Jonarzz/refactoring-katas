echo '[reloadFrontEnd] Building front-end...'
cd frontend || exit
npm test || exit
npm run build || exit
docker build -t io.github.jonarzz/unusual-spending-frontend:1.0.0-SNAPSHOT .
cd ..

echo '[reloadFrontEnd] Reloading image in Minikube...'
kubectl scale --replicas=0 deployment/frontend-app
minikube image load io.github.jonarzz/unusual-spending-frontend:1.0.0-SNAPSHOT
kubectl scale --replicas=2 deployment/frontend-app

sleep 3
echo '[reloadBackEnd] Front-end reloaded. Opening main page in web browser...'
open http://localhost