# Short URL Reservation Microservice Deployment on AWS Lambda and API Gateway

## Pre-requisites
* AWS CLI
* SAM CLI
* Maven

## Building the project
From the project root folder (where `template.yml` is located),
use SAM CLI to build the project:
```bash
$ sam build
```

## Testing locally with the SAM CLI
From the project root folder (where `template.yml` is located),
use SAM CLI to deploy the project on `localhost:4000`:

```bash
$ sam local start-api -p 4000
```

## Deploying to AWS Lambda and API Gateway
From the project root folder (where `template.yml` is located),
use SAM CLI to deploy the project on AWS Lambda and API Gateway:

```
$ sam deploy
```
