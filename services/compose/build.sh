version=`perl services/compose/update_version.pl`
cd ui && npm run build
cd .. && sbt docker:publishLocal
docker build -f services/nginx/Dockerfile -t thegame-nginx:1.0 .
docker tag thegame-nginx:1.0 gcr.io/balmy-script-278222/thegame-nginx:$version
docker tag thegame:1.0 gcr.io/balmy-script-278222/thegame:$version
docker push gcr.io/balmy-script-278222/thegame:$version
docker push gcr.io/balmy-script-278222/thegame-nginx:$version
