#!/bin/sh
PROFILE="${SERVICE_PROFILE:=docker}"
MAX_MEM="${RESOURCE_MAX_MEM:=500M}"
MIN_MEM="${RESOURCE_MIN_MEM:=312M}"
#source /etc/profile &&
echo 10.36.21.16	esbdb-scan	esbdb-scan.lienvietpostbank.com.vn >> /etc/hosts &&
echo 10.36.21.17	esbdb-scan	esbdb-scan.lienvietpostbank.com.vn >> /etc/hosts &&
echo 10.36.21.18	esbdb-scan	esbdb-scan.lienvietpostbank.com.vn >> /etc/hosts &&
echo 10.36.21.16	esbdb.lienvietpostbank.com.vn >> /etc/hosts &&
echo 10.36.11.16	authdb-scan >> /etc/hosts &&
echo 10.43.254.91	payments.vietinbank.vn >> /etc/hosts &&
echo 10.16.193.11	esbdr.lienvietpostbank.com.vn >> /etc/hosts &&
echo 10.16.192.28	authdbdr.lienvietpostbank.com.vn >> /etc/hosts &&
echo 10.16.173.27	esbsg.lienvietpostbank.com.vn	esbsg-scan >> /etc/hosts &&
echo 10.16.173.28	esbsg.lienvietpostbank.com.vn	esbsg-scan >> /etc/hosts &&
echo 10.16.173.29	esbsg.lienvietpostbank.com.vn	esbsg-scan >> /etc/hosts &&
echo 10.16.193.24	authdbsg.lienvietpostbank.com.vn	authdbsg-scan >> /etc/hosts &&
echo 10.16.193.25	authdbsg.lienvietpostbank.com.vn	authdbsg-scan >> /etc/hosts &&
echo 10.16.193.26	authdbsg.lienvietpostbank.com.vn	authdbsg-scan >> /etc/hosts &&
echo 10.16.64.33	esbapp_2 >> /etc/hosts &&
echo 10.16.252.31	bank.cpc.vn >> /etc/hosts &&
echo 10.16.174.62	ebdbhcm	ebdbhcm.lienvietpostbank.com.vn >> /etc/hosts &&
exec java  -Xmx$MAX_MEM -Xms$MIN_MEM $KEY_OPTS -Dspring.profiles.active=$PROFILE  -jar jars/* "$@"
