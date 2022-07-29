# Unusual Spending front-end application

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

## Available Scripts

- `npm start`
- `npm test`
- `npm run build`
- `npm run eject` (one-way operation)

## Docker and Kubernetes
1. Build the Docker image (after building the application):

    
    docker build -t io.github.jonarzz/unusual-spending-frontend:1.0.0-SNAPSHOT .

2. Load the image into Minikube registry:


    minikube image load io.github.jonarzz/unusual-spending-frontend:1.0.0-SNAPSHOT

3. After reloading the `frontend-app` deployment, ingress serves the application at `localhost` (`minikube tunnel` required).
