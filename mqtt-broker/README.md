# MQTT Broker (Eclipse Mosquitto) with Auth via Docker and Kubernetes

This repo provides a ready-to-run Mosquitto MQTT broker requiring username/password authentication, both for Docker and Kubernetes.

## VM Access

Access your VM via:

```
ssh <login>@andromeda.lasdpc.icmc.usp.br -p 2183
```

External access: your professor opened port `6183`. We'll expose internal MQTT port `1883` as `6183` externally.

## Copy the project to the VM (SCP)

From your local machine, run:

```
scp -P 2183 -r /home/draude/Documents/IC/mqtt-broker ema@andromeda.lasdpc.icmc.usp.br:~/mqtt-broker
```

## Docker Setup

1. Create the password file:

```
# Install mosquitto-clients if not present
sudo apt update
sudo apt install -y mosquitto-clients

# Inside the repo root
cd /home/draude/Documents/IC/mqtt-broker
mkdir -p mosquitto/data mosquitto/log

# Create password file with a user and password
mosquitto_passwd -b -c mosquitto/passwordfile <user> <password>
```

2. Start broker (host port 6183 -> container 1883):

```
docker compose up -d
```

3. Test publish/subscribe:

```
# In one terminal
mosquitto_sub -h localhost -p 6183 -t test/topic -u <user> -P <password>

# In another terminal
mosquitto_pub -h localhost -p 6183 -t test/topic -m "hello" -u <user> -P <password>
```

To change the exposed port, edit `docker-compose.yml` ports mapping (left side of `6183:1883`).

## Kubernetes Setup

1. Create namespace and config:

```
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml -n mqtt-broker
```

2. Create secret from your local password file:

```
# Option A: from existing password file
kubectl -n mqtt-broker create secret generic mosquitto-auth \
  --from-file=passwordfile=mosquitto/passwordfile

# Option B: use the provided manifest (edit `k8s/secret.yaml`)
# kubectl apply -f k8s/secret.yaml -n mqtt-broker
```

3. Deploy and service (Service exposes NodePort 6183):

```
kubectl apply -f k8s/deployment.yaml -n mqtt-broker
kubectl apply -f k8s/service.yaml -n mqtt-broker
kubectl -n mqtt-broker get pods,svc
```

4. External access options:

- NodePort: already set to `6183`. Connect to `<vm-ip>:6183`.
- Port-forward (quick test):

```
kubectl -n mqtt-broker port-forward svc/mosquitto 1883:1883
```

5. Test from client:

```
mosquitto_sub -h <vm-ip> -p 6183 -t test/topic -u <user> -P <password>
mosquitto_pub -h <vm-ip> -p 6183 -t test/topic -m "hello" -u <user> -P <password>
```

## VM Setup (commands to run inside the VM)

SSH into the VM:

```
ssh ema@andromeda.lasdpc.icmc.usp.br -p 2183
```

Docker option:

```
# Go to project folder you copied via scp
cd ~/mqtt-broker

# Ensure data/log folders exist
mkdir -p mosquitto/data mosquitto/log

# If you haven't created the password file locally, you can create it here too
# Replace <user> and <password>
mosquitto_passwd -b -c mosquitto/passwordfile <user> <password>

# Start broker exposing external port 6183
docker compose up -d

# Verify logs
docker logs -f mosquitto

# Test locally from VM
mosquitto_sub -h localhost -p 6183 -t test/topic -u <user> -P <password> &
mosquitto_pub -h localhost -p 6183 -t test/topic -m "hello" -u <user> -P <password>
```

Kubernetes option:

```
# Go to project folder
cd ~/mqtt-broker

# Namespace and config
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml -n mqtt-broker

# Create secret from password file
kubectl -n mqtt-broker create secret generic mosquitto-auth \
  --from-file=passwordfile=mosquitto/passwordfile --dry-run=client -o yaml | \
  kubectl apply -f -

# Deploy and expose via NodePort 6183
kubectl apply -f k8s/deployment.yaml -n mqtt-broker
kubectl apply -f k8s/service.yaml -n mqtt-broker

# Check resources
kubectl -n mqtt-broker get pods,svc

# Test from another machine using VM's external IP and port 6183
# Example (replace <vm-ip>)
mosquitto_sub -h <vm-ip> -p 6183 -t test/topic -u <user> -P <password> &
mosquitto_pub -h <vm-ip> -p 6183 -t test/topic -m "hello" -u <user> -P <password>
```

## Notes

- Authentication: `allow_anonymous false` is set, requiring valid user/password.
- Persistence: Enabled and data is stored in `/mosquitto/data`.
- Logging: Directed to stdout; access via `docker logs mosquitto` or `kubectl logs`.
- Adjust resource limits, replicas, and probes as needed for production.
