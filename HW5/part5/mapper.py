#!/usr/bin/python

import sys

for line in sys.stdin:
  if line.find('<') == -1 and len(line.strip()) != 0:
    data = line.strip().split()
    #ip, prefix1, prefix2, time, port, method, url, protocol, status, info = data
    ip = data[0]
    print "{0}".format(ip)

    #print data
