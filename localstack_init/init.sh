#!/bin/bash
# LocalStack Initialization Script
awslocal dynamodb create-table \
    --table-name Events \
    --attribute-definitions AttributeName=id,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST

awslocal dynamodb create-table \
    --table-name Orders \
    --attribute-definitions AttributeName=id,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST

awslocal sqs create-queue --queue-name purchase-requests

awslocal secretsmanager create-secret --name aws/credentials --secret-string '{"accessKey":"test","secretKey":"test"}'
