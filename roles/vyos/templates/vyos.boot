firewall {
    state-policy {
        established {
            log enable
            action accept
        }
        invalid {
            log enable
            action accept
        }
        related {
            log enable
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
        network-group USER {
            network 10.1.0.0/20
        }
        network-group MULTIMEDIA {
            network 10.1.16.0/24
        }
        network-group MINI-DATA-CENTER {
            network 10.2.0.0/16
        }        
        domain-group FOWARD-TO-EXTERNAL-PROXY {
            address ports.ubuntu.com
            address ftp.kr.debian.org
            address archive.ubuntu.com
        }
    }
    name WAN-OUT {
        enable-default-log
        default-action drop
        rule 10 {
            log enable
            action accept
            destination {
                group {
                    network-group !RFC1918
                }
            }
        }
        rule 20 {
            log enable
            action accept
            source {
                group {
                    network-group ADMIN
                }
            }
        }
        rule 30 {
            log enable
            action accept
            protocol tcp
            source {
                fqdn admin-proxy.vd.ingtra.net
            }
            destination {
                port '80,443,4444,5000,8006'
            }
        }
    }
    name NETCOM-OUT {
        enable-default-log
        default-action drop
        rule 10 {
            log enable
            action accept
            source {
                group {
                    network-group ADMIN
                }
            }
        }
        rule 20 {
            log enable
            action accept
            protocol udp
            destination {
                fqdn unbound.vd.ingtra.net
                port 53
            }
        }
        rule 21 {
            log enable
            action accept
            protocol udp
            destination {
                fqdn adguard-home.vd.ingtra.net
                port 53
            }
        }
        rule 30 {
            log enable
            action accept
            protocol tcp
            source {
                group {
                    network-group MINI-DATA-CENTER 
                }
            }
            destination {
                fqdn http-transparent-proxy.vd.ingtra.net
                port 3128
            }
        }
        rule 40 {
            log enable
            action accept
            protocol tcp
            source {
                fqdn synology.dv.ingtra.net
            }
            destination {
                fqdn external-reverse-proxy.vd.ingtra.net
                port 80
            }
        }
    }
    name LAN-OUT {
        enable-default-log
        default-action drop
        rule 10 {
            log enable
            action accept
            source {
                group {
                    network-group ADMIN
                }
            }
        }
        rule 20 {
            log enable
            action accept
            source {
                group {
                    network-group USER
                }
            }
            destination {
                group {
                    network-group MULTIMEDIA
                }
            }
        }
        rule 30 {
            log enable
            action accept
            protocol tcp
            source {
                fqdn admin-proxy.vd.ingtra.net
            }
            destination {
                port '80,443,4444,5000,8006'
            }
        }
        rule 40 {
            log enable
            action accept
            protocol tcp
            source {
                fqdn http-transparent-proxy.vd.ingtra.net
            }
            destination {
                fqdn synology.dv.ingtra.net
                port 3128
            }
        }
    }    
    interface eth0.* {
        out {
            name WAN-OUT
        }
    }
    interface eth1.* {
        out {
            name NETCOM-OUT
        }
    }
    interface eth2.* {
        out {
            name LAN-OUT
        }
    }
    interface eth3.* {
        out {
            name LAN-OUT
        }
    }
}

nat {
    destination {
        rule 100 {
            inbound-interface eth3.4040
            protocol tcp
            source {
                address !10.2.8.11
            }
            destination {
                address !10.0.0.0/8
                port 80
            }
            translation{
                address 10.0.33.11
                port 3128
            }
        }
        rule 200 {
            inbound-interface eth3.4040
            protocol tcp
            destination {
                group {
                    domain-group FOWARD-TO-EXTERNAL-PROXY
                }
                port 80
            }
            translation {
                address 10.0.40.12
            }
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
        vif 33 {
            address 10.0.33.1/24
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
    ethernet eth3 {
        offload {
            gro
            gso
            sg
            tso
        }
        vif 234 {
            address 10.2.34.254/24
        }
        vif 4040 {
            address 10.255.253.1/24
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
    bgp {
        system-as 64521
        parameters {
            router-id 10.2.34.254
        }
        neighbor 10.2.34.51 {
            remote-as internal
        }
        neighbor 10.2.34.52 {
            remote-as internal
        }
        neighbor 10.2.34.53 {
            remote-as internal
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