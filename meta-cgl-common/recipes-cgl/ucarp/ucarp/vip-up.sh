#! /bin/sh
exec 2> /dev/null

#/sbin/ip addr add "$2"/24 dev "$1"

# or alternatively:
ifconfig "$1":254 "$2" netmask 255.255.255.0
