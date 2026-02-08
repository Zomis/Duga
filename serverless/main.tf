terraform {
  required_version = ">= 1.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    local = {
      source  = "hashicorp/local"
      version = "~> 2.4"
    }
  }
	backend "s3" {
		region = local.region
	}
}

data "aws_caller_identity" "current" {}
data "aws_partition" "current" {}
data "aws_region" "current" {}

locals {
	region = "eu-central-1"
}

provider "aws" {
	region = local.region

	default_tags {
		tags = {
			project = "duga"
		}
	}
}
