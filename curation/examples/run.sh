#!/bin/bash
data=$(pwd)/$(dirname $0)
base=$(dirname $data)
arq --query $base/datamap.rq --base "file://$data/" --data $data/vocab_map.ttl --namedData $data/data.ttl
#roqet -i sparql11 -r turtle -D $PWD/data_vocab_map.ttl $base/datamap.rq 2>/dev/null
