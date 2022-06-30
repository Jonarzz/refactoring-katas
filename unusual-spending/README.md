# [Unusual Spending kata](https://kata-log.rocks/unusual-spending-kata)
## Requirements
You work at a credit card company and as a value-add they want to start providing alerts to users when their spending in any particular category is higher than usual.

A Payment is a simple value object with a expense, description, and category.

A Category is an enumerable type of a collection of things like 
“entertainment”, “restaurants”, and “golf”.

For a given userId, fetch the payments for the current month and the previous month.
Compare the total amount paid for each month, grouped by category. 
Filter down to the categories for which the user spent at least 50% more 
this month than last month.

Compose an e-mail message to the user that lists the categories 
for which spending was unusually high, with a subject like 
“Unusual spending of $1076 detected!” and this body:

    Hello card user!

    We have detected unusually high spending on your card in these categories:

    * You spent $148 on groceries
    * You spent $928 on travel

    Love,
    The Credit Card Company

## Simple event storming
![Event storming result](event-storming.png)

## Run locally

1. Set up the Minikube environment:


    eval $(minikube docker-env)

2. Build the project using:


    mvn clean package -Dquarkus.container-image.build=true

(also creates a Jib image and Kubernetes configuration files in the `target/kubernetes/` directory)

2. To load the image into the local Minikube instance use:


    minikube image load io.github.jonarzz/unusual-spending:1.0.0-SNAPSHOT

(image will not be reloaded if a pod in a deployment using the image is running - see: `kubectl scale` command below)

3. (Re)load Kubernetes configuration:


    kubectl apply -f target/kubernetes/kubernetes.yml

(use `minikube.yml` for local development - exposes a NodePort, does not try to load images from the registry - uses Minikube images)


Additional useful command examples:
- `kubectl get svc` (also `deploy`, `pod`) - get state of running services (deploymens, pods)
- `kubectl scale --replicas=0 deployment/unusual-spending` - stop all running pods in given deployment
- `kubectl scale --replicas=1 deployment/unusual-spending` - start pods in given deployment (with no replication) 
- `kubectl exec -it deploy/unusual-spending -- /bin/bash` - start executing commands inside given deployment;
the command after `--` could be anything, e.g. it could be a `curl` command verifying if other deployment's service 
is accessible from given deployment - in case of a single command the session quits after executing the command
- `kubectl port-forward service/unusual-spending 8080:80` - run a tunnel forwarding the localhost port `8080` to the `80` port 
in given service 
- `kubectl explain <thing>` (e.g. `kubectl explain deployments.spec.replicas`) - displays documentation of the given "thing"
- `minikube image ls --format table` - list Minikube images - by verifying image ID it's possible to check if the image was successfully reloaded 
- `minikube image rm io.github.jonarzz/unusual-spending:1.0.0-SNAPSHOT` - remove the image from Minikube images
- `minikube dashboard` - run a web-accessible dashboard showing the state of the cluster

See [kubectl](https://kubernetes.io/docs/reference/generated/kubectl/kubectl-commands) 
and [minikube](https://minikube.sigs.k8s.io/docs/commands/) commands pages for more.

## Recommended reading
- https://learnk8s.io/spring-boot-kubernetes-guide
- https://learnk8s.io/blog/kubectl-productivity