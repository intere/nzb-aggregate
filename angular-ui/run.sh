#!/bin/bash

PWD=$(dirname $0)

cd $PWD;

echo "Starting Server: $PWD";
python -m SimpleHTTPServer 8000
