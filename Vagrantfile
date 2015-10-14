# -*- mode: ruby -*-
# vi: set ft=ruby :

VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.synced_folder ".", "/vagrant"
  # config.vm.synced_folder ".", "/vagrant", mount_options: ["dmode=700,fmode=600"]
  config.vm.define "cd" do |d|
    d.vm.box = "ubuntu/trusty64"
    d.vm.hostname = "cd"
    d.vm.network "private_network", ip: "10.100.198.200"
    d.vm.provision :shell, path: "scripts/bootstrap_ansible.sh"
    d.vm.provision :shell, inline: "ansible-playbook /vagrant/ansible/cd.yml -c local -vv"
    d.vm.provider "virtualbox" do |v|
      v.memory = 1536
      #v.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
      #v.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
      #v.customize ["modifyvm", :id, "--nictype1", "virtio"]
    end
  end
  config.vm.define "prod" do |d|
    d.vm.box = "ubuntu/trusty64"
    d.vm.hostname = "prod"
    d.vm.network "private_network", ip: "10.100.198.201"
    d.vm.provider "virtualbox" do |v|
      v.memory = 1024
      #v.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
      #v.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
      #v.customize ["modifyvm", :id, "--nictype1", "virtio"]
    end
  end
  config.vm.define "cd-jenkins" do |d|
    d.vm.box = "ubuntu/trusty64"
    d.vm.hostname = "cd"
    d.vm.network "private_network", ip: "10.100.198.202"
    d.vm.provision :shell, path: "scripts/bootstrap_ansible.sh"
    d.vm.provision :shell, inline: "ansible-playbook /vagrant/ansible/cd-jenkins.yml -c local -vv"
    d.vm.provider "virtualbox" do |v|
      v.memory = 1536
      #v.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
      #v.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
      #v.customize ["modifyvm", :id, "--nictype1", "virtio"]
    end
  end
  (1..3).each do |i|
    config.vm.define "serv-disc-0#{i}" do |d|
      d.vm.box = "ubuntu/trusty64"
      d.vm.hostname = "serv-disc-0#{i}"
      d.vm.network "private_network", ip: "10.100.194.20#{i}"
      d.vm.provider "virtualbox" do |v|
        v.memory = 1024
        #v.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
        #v.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
        #v.customize ["modifyvm", :id, "--nictype1", "virtio"]
      end
    end
  end
  config.vm.define "proxy" do |d|
    d.vm.box = "ubuntu/trusty64"
    d.vm.hostname = "proxy"
    d.vm.network "private_network", ip: "10.100.193.200"
    d.vm.provider "virtualbox" do |v|
      v.memory = 1024
      #v.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
      #v.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
      #v.customize ["modifyvm", :id, "--nictype1", "virtio"]
    end
  end
  config.vm.define "swarm-master" do |d|
    d.vm.box = "ubuntu/trusty64"
    d.vm.hostname = "swarm-master"
    d.vm.network "private_network", ip: "10.100.195.200"
    d.vm.provider "virtualbox" do |v|
      v.memory = 1024
      #v.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
      #v.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
      #v.customize ["modifyvm", :id, "--nictype1", "virtio"]
    end
  end
  (1..2).each do |i|
    config.vm.define "swarm-node-#{i}" do |d|
      d.vm.box = "ubuntu/trusty64"
      d.vm.hostname = "swarm-node-#{i}"
      d.vm.network "private_network", ip: "10.100.195.20#{i}"
      d.vm.provider "virtualbox" do |v|
        v.memory = 1024
        #v.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
        #v.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
        #v.customize ["modifyvm", :id, "--nictype1", "virtio"]
      end
    end
  end
#  if Vagrant.has_plugin?("vagrant-cachier")
#    config.cache.scope = :box
#  end
end