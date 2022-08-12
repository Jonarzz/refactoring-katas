echo '[reloadFrontEnd] Building front-end...'
cd frontend || exit
#npm test || exit
#npm run build || exit
cd build || exit

echo '[reloadFrontEnd] Reloading AWS S3 bucket'
(localstack status | grep started && awslocal s3 cp --recursive . s3://frontend-app-bucket) ||
  (cd ../.. && scripts/aws/runLocalAwsStack.sh)

echo '[reloadFrontEnd] Running E2E tests...'
cd ..
TZ=UTC npx cypress run --config video=false || exit
