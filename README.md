# rssmail (Not usable yet)

Subscribe to RSS feeds and have updates sent to something other than an RSS Reader tool, for instance email, when there is new information in the feed.

## Contributing

### Commits

#### Commits Messages

Use [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)

## AWS Account

### Expected monthly costs

Free. There are costs, but they are negligible.

### Required Infrastructure

1. S3 bucket for terraform state
2. DynamoDB table for state-lock file. Choose On Demand (PAY_PER_REQUEST) for cheapest solution

### CLI commands

To run commands with the AWS CLI, ensure you have setup rssmail profile using: `aws configure --profile rssmail`

Then you can run AWS CLI commands using the profile: `aws s3 ls --profile rssmail`

### Deployment

Ensure you have setup your AWS profile named `rssmail`, and then specify the environment variable by prefixing the command: `AWS_PROFILE=rssmail ./terraform.sh test plan`.

# Architecture

This is a Spring Boot MVC application written in Java. It consists of a

# Development

This section contains information regarding development of the project.

## Repository setup

### Dependencies

Run: `` to install all dependencies

## Testing

### Unit tests

Run: `` to run the test suite
