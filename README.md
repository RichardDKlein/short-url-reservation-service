# Build and Deployment Guide

## Building the project
From the project root folder (where `template.yml` is located),
use SAM CLI to build the project:
```bash
$ sam build
```

## Deploying to AWS or localhost
From the project root folder (where `template.yml` is located),
use one of four shell scripts to deploy the production or test
version of the project to AWS or localhost.

```
$ ./deploy_aws_prod.sh
$ ./deploy_aws_test.sh
$ ./deploy_local_prod.sh
$ ./deploy_local_test.sh
```

Note that the only difference between the production and test
versions is that the test versions use different DynamoDB tables,
whose names are prefixed with `test-`.
