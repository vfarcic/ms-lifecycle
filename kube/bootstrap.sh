#!/usr/bin/env bash

vagrant up kube-master kube-node-1 kube-node-2

# Master
vagrant ssh kube-master

echo "
10.100.199.200 kube-master
10.100.199.201 kube-node-1
10.100.199.202 kube-node-2" | sudo tee -a /etc/hosts

sudo apt-key adv \
    --keyserver hkp://p80.pool.sks-keyservers.net:80 \
    --recv-keys 58118E89F3A912897C070ADBF76221572C52609D

echo "deb https://apt.dockerproject.org/repo ubuntu-trusty main" \
    | sudo tee /etc/apt/sources.list.d/docker.list

sudo apt-get update

sudo apt-get install -y docker-engine

sudo apt-get install -y git

git clone https://github.com/kubernetes/kubernetes.git

cd kubernetes/docs/getting-started-guides/docker-multinode/

sudo MASTER_IP=10.100.199.200 ./master.sh

cd ~

wget https://storage.googleapis.com/kubernetes-release/release/v1.0.1/bin/linux/amd64/kubectl

chmod +x kubectl

sudo mv kubectl /usr/local/bin/

exit

# Node 1

vagrant ssh kube-node-1

echo "
10.100.199.200 kube-master
10.100.199.201 kube-node-1
10.100.199.202 kube-node-2" | sudo tee -a /etc/hosts

sudo apt-key adv \
    --keyserver hkp://p80.pool.sks-keyservers.net:80 \
    --recv-keys 58118E89F3A912897C070ADBF76221572C52609D

echo "deb https://apt.dockerproject.org/repo ubuntu-trusty main" \
    | sudo tee /etc/apt/sources.list.d/docker.list

sudo apt-get update

sudo apt-get install -y docker-engine

sudo apt-get install -y git

git clone https://github.com/kubernetes/kubernetes.git

cd kubernetes/docs/getting-started-guides/docker-multinode/

sudo MASTER_IP=10.100.199.200 ./worker.sh

exit

# Node 2

vagrant ssh kube-node-2

echo "
10.100.199.200 kube-master
10.100.199.201 kube-node-1
10.100.199.202 kube-node-2" | sudo tee -a /etc/hosts

sudo apt-key adv \
    --keyserver hkp://p80.pool.sks-keyservers.net:80 \
    --recv-keys 58118E89F3A912897C070ADBF76221572C52609D

echo "deb https://apt.dockerproject.org/repo ubuntu-trusty main" \
    | sudo tee /etc/apt/sources.list.d/docker.list

sudo apt-get update

sudo apt-get install -y docker-engine

sudo apt-get install -y git

git clone https://github.com/kubernetes/kubernetes.git

cd kubernetes/docs/getting-started-guides/docker-multinode/

sudo MASTER_IP=10.100.199.200 ./worker.sh

exit

# Master

vagrant ssh kube-master

kubectl get nodes

export KUBE_SERVER=10.100.199.200

kubectl -s "$KUBE_SERVER:8080" --namespace=kube-system create -f /vagrant/kube/skydns-rc.yaml

kubectl -s "$KUBE_SERVER:8080" --namespace=kube-system create -f /vagrant/kube/skydns-svc.yaml

kubectl create -f /vagrant/kube/busybox.yaml

kubectl get pods busybox

kubectl exec busybox -- nslookup kubernetes.default


kubectl run nginx --image=nginx --port=80

kubectl expose rc nginx --port=80

ADDRESS=$(kubectl get svc nginx --template={{.spec.clusterIP}})

curl $ADDRESS