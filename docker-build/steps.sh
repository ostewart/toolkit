docker build -t trailmagic/web:$(git rev-parse --verify HEAD) .
docker tag trailmagic/web:$(git rev-parse --verify HEAD) us.gcr.io/horsecodes/trailmagic-web:$(git rev-parse --verify HEAD)
gcloud docker push us.gcr.io/horsecodes/trailmagic-web

