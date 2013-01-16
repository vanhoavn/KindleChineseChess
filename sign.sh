#!/bin/bash

TPATH=$(dirname $0)

jarsigner -keystore "${TPATH}/developer.keystore" -storepass password "$1" dktest
jarsigner -keystore "${TPATH}/developer.keystore" -storepass password "$1" ditest
jarsigner -keystore "${TPATH}/developer.keystore" -storepass password "$1" dntest
