#!/bin/sh
if [ "$#" -ne 1 ]; then
  echo "Usage: $0 VERISION" >&2
  exit 1
fi
cd ui && npm run build
cd .. && sbt docker:publishLocal
docker build -f services/nginx/Dockerfile -t thegame-nginx:1.0 .
docker tag thegame-nginx:1.0 gcr.io/balmy-script-278222/thegame-nginx:$1
docker tag thegame:1.0 gcr.io/balmy-script-278222/thegame:$1
docker push gcr.io/balmy-script-278222/thegame:$1
docker push gcr.io/balmy-script-278222/thegame-nginx:$1
