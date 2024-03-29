#!/bin/bash

dnf upgrade -y
dnf install -y openssh-server vim
echo "PermitRootLogin yes" > /etc/ssh/sshd_config.d/permit-root-login.conf
systemctl enable sshd
systemctl start sshd
echo 'L /dev/kmsg - - - - /dev/console' > /etc/tmpfiles.d/kmsg.conf
