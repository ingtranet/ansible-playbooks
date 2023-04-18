firewall {
    state-policy {
        established {
            action accept
        }
        invalid {
            action accept
        }
        related {
            action accept
        }
    }
    group {
        network-group RFC1918 {
            network 10.0.0.0/8
            network 172.16.0.0/12
            network 192.168.0.0/16
        }
        network-group ADMIN {
            network 10.0.0.0/24
            network 10.1.0.0/24
        }
    }
    name ROUTER-OUT {
        enable-default-log
        default-action drop
        rule 10 {
            action accept
            source {
                group {
                    network-group ADMIN
                }
            }
        }
        rule 20 {
            action accept
            destination {
                group {
                    network-group !RFC1918
                }
            }
        }
    }
    name NETCOM-OUT {
        enable-default-log
        default-action drop
        rule 10 {
            action accept
            source {
                group {
                    network-group ADMIN
                }
            }
        }
        rule 20 {
            action accept
            protocol udp
            destination {
                fqdn unbound.vd.ingtra.net
                port 53
            }
        }
        rule 21 {
            action accept
            protocol udp
            destination {
                fqdn adguard-home.vd.ingtra.net
                port 53
            }
        }
    }
    name MDC-OUT {
        enable-default-log
        default-action drop
        rule 10 {
            action accept
            source {
                group {
                    network-group ADMIN
                }
            }
        }
        rule 20 {
            action accept
            protocol tcp
            source {
                fqdn admin-proxy.vd.ingtra.net
            }
            destination {
                fqdn caddy.sd.ingtra.net
                port '80,443'
            }
        }
    }
    name LAN-OUT {
        enable-default-log
        default-action drop
        rule 10 {
            action accept
            source {
                group {
                    network-group ADMIN
                }
            }
        }
    }    
    interface eth0.1 {
        out {
            name ROUTER-OUT
        }
    }
    interface eth1.32 {
        out {
            name NETCOM-OUT
        }
    }
    interface eth1.40 {
        out {
            name NETCOM-OUT
        }
    }
    interface eth2.* {
        out {
            name LAN-OUT
        }
    }
}

interfaces {
    ethernet eth0 {
        offload {
            gro
            gso
            sg
            tso
        }
        vif 1 {
            address 10.0.1.2/24
        }
        vif 4040 {
            address 10.255.253.1/24
        }
    }
    ethernet eth1 {
        offload {
            gro
            gso
            sg
            tso
        }
        vif 32 {
            address 10.0.32.1/24
        }
        vif 40 {
            address 10.0.40.1/24
        }
    }
    ethernet eth2 {
        offload {
            gro
            gso
            sg
            tso
        }
        vif 100 {
            address 10.1.0.1/24
        }
        vif 101 {
            address 10.1.1.1/24
        }
        vif 102 {
            address 10.1.2.1/24
        }
        vif 116 {
            address 10.1.16.1/24
        }
        vif 117 {
            address 10.1.17.1/24
        }
    }
    loopback lo {
    }
}

service {
    dhcp-server {
        shared-network-name resident {
            subnet 10.1.0.0/24 {
                default-router 10.1.0.1
                name-server 10.0.32.12
                range 0 {
                    start 10.1.0.100
                    stop 10.1.0.199
                }
            }
        }
        shared-network-name nya {
            subnet 10.1.1.0/24 {
                default-router 10.1.1.1
                name-server 10.0.32.12
                range 0 {
                    start 10.1.1.100
                    stop 10.1.1.199
                }
            }
        }
        shared-network-name guest {
            subnet 10.1.2.0/24 {
                default-router 10.1.2.1
                name-server 10.0.32.12
                range 0 {
                    start 10.1.2.100
                    stop 10.1.2.199
                }
            }
        }
        shared-network-name multimedia {
            subnet 10.1.16.0/24 {
                default-router 10.1.16.1
                name-server 10.0.32.12
                range 0 {
                    start 10.1.16.100
                    stop 10.1.16.199
                }
            }
        }
        shared-network-name iot {
            subnet 10.1.17.0/24 {
                default-router 10.1.17.1
                name-server 10.0.32.12
                range 0 {
                    start 10.1.17.100
                    stop 10.1.17.199
                }
            }
        }
    }
    ntp {
        listen-address 127.0.0.1
        server time1.vyos.net {
        }
        server time2.vyos.net {
        }
        server time3.vyos.net {
        }
    }
    ssh {
        port 22
    }
}

protocols {
    static {
        route 0.0.0.0/0 {
            next-hop 10.0.1.1
        }
        route 10.0.0.0/24 {
            next-hop 10.255.253.254
        }
        route 10.0.2.0/24 {
            next-hop 10.255.253.254
        }
        route 10.2.0.0/16 {
            next-hop 10.255.253.2
        }
    }
}

system {
    config-management {
        commit-revisions 100
    }
    conntrack {
        modules {
            ftp
            h323
            nfs
            pptp
            sip
            sqlnet
            tftp
        }
    }
    host-name vyos
    name-server 10.0.32.11
    name-server 1.1.1.1
    name-server 1.0.0.1
    login {
        user vyos {
            authentication {
                encrypted-password {{ secret_vyos_encrypted_password }}
                public-keys vyos@ingtranet {
                    type ssh-rsa
                    key {{ secret_ssh_public_key }}
                }
            }
        }
    }
    syslog {
        global {
            facility all {
                level info
            }
            facility protocols {
                level debug
            }
        }
    }
}