#!/bin/bash

apt update -y
apt upgrade -y
apt install -y openssh-server vim
echo "PermitRootLogin yes" > /etc/ssh/sshd_config.d/permit-root-login.conf
systemctl enable ssh
systemctl start ssh
echo 'L /dev/kmsg - - - - /dev/console' > /etc/tmpfiles.d/kmsg.conf
