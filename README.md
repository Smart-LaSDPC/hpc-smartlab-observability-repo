# hpc-smartlab-observability-repo

SmartLab Performance Evaluation Platform

---

## 📋 Table of Contents

- [Prerequisites](#-prerequisites)
- [Quick Start: Repository & Cluster Setup](#-quick-start-repository--cluster-setup)
- [Kubernetes Deployments](#-kubernetes-deployments)
  - [1. MQTT Broker (Mosquitto)](#1-mqtt-broker-mosquitto)
  - [2. MAS-JADE (*Coming Soon*)](#2-mas-jade-coming-soon)
- [Authors & License](#-authors--license)

---

## 🛠 Prerequisites

Ensure your environment meets the following requirements before proceeding:

*   **Kubernetes Cluster:** A running and accessible Kubernetes cluster (v1.20+ recommended).
*   **Command Line Tools:**
    *   [`kubectl`](https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/) - Kubernetes CLI
    *   [`helm`](https://helm.sh/docs/intro/install/) - Package manager for Kubernetes

---

## 🚀 Quick Start: Repository & Cluster Setup

### 1. Clone the Repository
Clone this repository to your local machine or management node:

```bash
git clone https://github.com/Smart-LaSDPC/hpc-smartlab-observability-repo.git
cd hpc-smartlab-observability-repo
```

### 2. Transfer Files to Your Kubernetes Virtual Machine
If you are managing the cluster from an external machine, copy the Kubernetes configuration manifests to your target control plane or virtual machine (e.g., `andromeda.lasdpc.icmc.usp.br`):

```bash
scp -P <port> -r ./kubernetes <user>@andromeda.lasdpc.icmc.usp.br:~/smartlab
```

*(Note: Navigate into the copied `kubernetes` directory on your remote environment for the subsequent steps).*

---

## ⚙️ Kubernetes Deployments

### 1. MQTT Broker (Mosquitto)

Navigate to the copied directory and deploy the MQTT broker components step-by-step:

#### Step A: Create Namespace and ConfigMap
```bash
kubectl apply -f ./kubernetes/mqtt-broker/manifests/namespace.yaml
kubectl apply -f ./kubernetes/mqtt-broker/manifests/configmap.yaml
```

#### Step B: Create Secrets
The repository includes a default `passwordfile` configuration. The pre-configured credentials in `secret.yaml` are:
*   **User:** `lasdpc`
*   **Password:** `l@sdpC10`

Apply the pre-defined secret manifest:
```bash
kubectl apply -f ./kubernetes/mqtt-broker/manifests/secret.yaml
```

> **Custom Credentials (Optional):** If you wish to use your own password file instead of the default secret, create the secret manually:
> ```bash
> kubectl -n mqtt-broker create secret generic mosquitto-auth \
> --from-file=passwordfile=./mqtt-broker/passwordfile
> ```

#### Step C: Deploy Deployment and Service
Apply the deployment and service manifests:
```bash
kubectl apply -f ./kubernetes/mqtt-broker/manifests/deployment.yaml
kubectl apply -f ./kubernetes/mqtt-broker/manifests/service.yaml
```
*This exposes the MQTT broker inside the cluster via ClusterIP and externally via NodePort on port `31883`.*

#### Step D: Verify Deployment Resources
Check that all pods, services, and deployments are running correctly:
```bash
kubectl get all -n mqtt-broker
```
Expected output:
```text
NAME                             READY   STATUS    RESTARTS   AGE
pod/mosquitto-7c8cb4b979-97jz6   1/1     Running   0          27h

NAME                   TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)       
service/mosquitto      ClusterIP   10.97.225.65    <none>        1883/TCP      
service/mosquitto-np   NodePort    10.106.41.224   <none>        1883:31883/TCP

NAME                        READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/mosquitto   1/1     1            1           5d3h

NAME                                   DESIRED   CURRENT   READY   AGE
replicaset.apps/mosquitto-6f75c47c56   0         0         0       5d3h
replicaset.apps/mosquitto-7c8cb4b979   1         1         1       27h
```

#### Step E: Connectivity Testing
Test connectivity from another virtual machine on the same network:

1. Retrieve the node internal IPs (any node IP is valid):
   ```bash
   kubectl get nodes -o wide
   ```
   Example output:
   ```text
   NAME        STATUS   ROLES           AGE   VERSION   INTERNAL-IP
   and08-vm1   Ready    <none>          13d   v1.33.1   10.1.1.81  
   and08-vm4   Ready    <none>          13d   v1.33.1   10.1.1.84  
   tau04-vm2   Ready    control-plane   16d   v1.33.1   10.1.3.42  
   ```

2. Run subscription and publication tests using `mosquitto_sub` and `mosquitto_pub`:
   ```bash
   # Terminal 1: Subscribe to test topic
   mosquitto_sub -h <internal-ip> -p 31883 -t test/topic -u lasdpc -P l@sdpC10

   # Terminal 2: Publish message to test topic
   mosquitto_pub -h <internal-ip> -p 31883 -t test/topic -m "hello" -u lasdpc -P l@sdpC10
   ```
   *(Note: Ensure the correct port `31883` is used for NodePort external testing).*

---

### 2. MAS-JADE

*Deployment instructions and configurations for MAS-JADE will be added soon.*

---