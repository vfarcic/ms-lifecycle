# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure(2) do |config|
  if (/cygwin|mswin|mingw|bccwin|wince|emx/ =~ RUBY_PLATFORM) != nil
    config.vm.synced_folder ".", "/vagrant", mount_options: ["dmode=700,fmode=600"]
  else
    config.vm.synced_folder ".", "/vagrant"
  end
  config.vm.define "cd" do |d|
    d.vm.box = "ubuntu/trusty64"
    d.vm.hostname = "cd"
    d.vm.network "private_network", ip: "10.100.198.200"
    d.vm.provision :shell, path: "scripts/bootstrap_ansible.sh"
    d.vm.provision :shell, inline: "PYTHONUNBUFFERED=1 ansible-playbook /vagrant/ansible/cd.yml -c local"
    d.vm.provider "virtualbox" do |v|
      v.memory = 2048
    end
  end
  config.vm.define "prod" do |d|
    d.vm.box = "ubuntu/trusty64"
    d.vm.hostname = "prod"
    d.vm.network "private_network", ip: "10.100.198.201"
    d.vm.provider "virtualbox" do |v|
      v.memory = 1024
    end
  end
  config.vm.define "logging" do |d|
    d.vm.box = "ubuntu/trusty64"
    d.vm.hostname = "logging"
    d.vm.network "private_network", ip: "10.100.198.202"
    d.vm.provider "virtualbox" do |v|
      v.memory = 1536
    end
  end
  (1..3).each do |i|
    config.vm.define "serv-disc-0#{i}" do |d|
      d.vm.box = "ubuntu/trusty64"
      d.vm.hostname = "serv-disc-0#{i}"
      d.vm.network "private_network", ip: "10.100.194.20#{i}"
      d.vm.provider "virtualbox" do |v|
        v.memory = 1024
      end
    end
  end
  config.vm.define "proxy" do |d|
    d.vm.box = "ubuntu/trusty64"
    d.vm.hostname = "proxy"
    d.vm.network "private_network", ip: "10.100.193.200"
    d.vm.provider "virtualbox" do |v|
      v.memory = 1024
    end
  end
  config.vm.define "swarm-master" do |d|
    d.vm.box = "ubuntu/wily64"
    d.vm.hostname = "swarm-master"
    d.vm.network "private_network", ip: "10.100.192.200"
    d.vm.provider "virtualbox" do |v|
      v.memory = 1024
    end
  end
  (1..2).each do |i|
    config.vm.define "swarm-node-#{i}" do |d|
      d.vm.box = "ubuntu/wily64"
      d.vm.hostname = "swarm-node-#{i}"
      d.vm.network "private_network", ip: "10.100.192.20#{i}"
      d.vm.provider "virtualbox" do |v|
        v.memory = 1536
      end
    end
  end
  (1..2).each do |i|
    config.vm.define "mesos-#{i}" do |d|
      d.vm.box = "bento/centos-7.1"
      d.vm.hostname = "mesos-#{i}"
      d.vm.network "private_network", ip: "10.100.197.20#{i}"
      d.vm.provider "virtualbox" do |v|
        v.memory = 1536
      end
    end
  end
  if Vagrant.has_plugin?("vagrant-cachier")
    config.cache.scope = :box
  end
  if Vagrant.has_plugin?("vagrant-vbguest")
    config.vbguest.auto_update = false
    config.vbguest.no_install = true
    config.vbguest.no_remote = true
  end
end
