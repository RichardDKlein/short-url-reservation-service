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
use one of two shell scripts to deploy the project to AWS Lambda
and API Gateway.

Use

```
$ ./deploy_prod.sh
```

to deploy the production build of the project, and use

```
$ ./deploy_test.sh
```

to deploy the test build.
