#! /usr/bin/python

import sys

dict = {}

for line in sys.stdin:
  ip = line.strip()
  if ip in dict:
    dict[ip] += 1
  else:
    dict[ip] = 1

ips = dict.keys()
for ip in ips:
  print "{0}\t{1}".format(ip, dict[ip])
