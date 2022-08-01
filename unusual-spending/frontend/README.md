# Unusual Spending front-end application

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

## Available Scripts

- `npm start`
- `npm test`
- `npm run build`
- `npm run eject` (one-way operation)

## Build and deploy
1. Build the application:

    npm run build

2. Build the Docker image:

    
    docker build -t io.github.jonarzz/unusual-spending-frontend:1.0.0-SNAPSHOT .

3. 

4. Load the image into Minikube registry (replica count for the deployment should be 0):


    minikube image load io.github.jonarzz/unusual-spending-frontend:1.0.0-SNAPSHOT

3. After reloading the `frontend-app` deployment, ingress serves the application at http://localhost/ (`minikube tunnel` required).
