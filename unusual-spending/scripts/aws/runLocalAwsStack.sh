echo '[runLocalAwsStack] Starting Localstack in a new Terminal'
localstack status | grep running || osascript -e 'tell app "Terminal"
    do script "localstack start"
end tell'

echo '[runLocalAwsStack] Waiting for startup'
until localstack status | grep running
do
  sleep 1
done

echo '[runLocalAwsStack] Loading Cloud Formation template'
awslocal cloudformation deploy --stack-name unusual-spending-stack --template-file aws/unusual-spending-template.yaml

echo '[runLocalAwsStack] Deploying frontend app files'
cd frontend/build || exit
awslocal s3 cp --recursive . s3://frontend-app-bucket

echo '[runLocalAwsStack] Opening main page in web browser...'
open http://frontend-app-bucket.s3.localhost.localstack.cloud:4566/index.html