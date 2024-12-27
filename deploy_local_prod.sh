#!/bin/bash

ENV_VARS_FILE="env_prod.json"
PORT=4000

sam local start-api -p $PORT --env-vars $ENV_VARS_FILE
