#!/usr/bin/env bash

# Installing Helm
#https://www.digitalocean.com/community/tutorials/how-to-install-software-on-kubernetes-clusters-with-the-helm-package-manager
cd /tmp
curl https://raw.githubusercontent.com/kubernetes/helm/master/scripts/get > install-helm.sh
chmod u+x install-helm.sh
./install-helm.sh
#Helm: bind the tiller serviceaccount to the cluster-admin role
kubectl -n kube-system create serviceaccount tiller
kubectl create clusterrolebinding tiller --clusterrole cluster-admin --serviceaccount=kube-system:tiller
helm init --service-account tiller


#Installing and Configuring Cert-Manager
#https://www.digitalocean.com/community/tutorials/how-to-set-up-an-nginx-ingress-with-cert-manager-on-digitalocean-kubernetes
kubectl apply -f https://raw.githubusercontent.com/jetstack/cert-manager/release-0.8/deploy/manifests/00-crds.yaml
kubectl label namespace kube-system certmanager.k8s.io/disable-validation="true"
helm repo add jetstack https://charts.jetstack.io
helm install --name cert-manager --namespace kube-system jetstack/cert-manager --version v0.8.0
kubectl apply -f cert/staging_issuer.yaml


#Get kubectl
mkdir -p $HOME/.kube
cat << EOF > $HOME/.kube/config
-------------------------------------
content file: k8s-su-super-admin-conf
-------------------------------------
EOF
curl -LO https://storage.googleapis.com/kubernetes-release/release/`curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt`/bin/linux/amd64/kubectl
chmod +x ./kubectl
cp kubectl $HOME
