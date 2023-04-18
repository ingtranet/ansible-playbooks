#!/bin/bash

dnf install -y upgrade
dnf install -y openssh-server vim
echo "PermitRootLogin yes" > /etc/ssh/sshd_config.d/permit-root-login.conf
systemctl enable sshd
systemctl start sshd