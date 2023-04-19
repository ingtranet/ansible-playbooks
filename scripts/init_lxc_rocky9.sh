#!/bin/bash

dnf upgrade -y
dnf install -y openssh-server vim
echo "PermitRootLogin yes" > /etc/ssh/sshd_config.d/permit-root-login.conf
systemctl enable sshd
systemctl start sshd