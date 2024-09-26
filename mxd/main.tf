#
#  Copyright (c) 2023 Contributors to the Eclipse Foundation
#
#  See the NOTICE file(s) distributed with this work for additional
#  information regarding copyright ownership.
#
#  This program and the accompanying materials are made available under the
#  terms of the Apache License, Version 2.0 which is available at
#  https://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
#  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
#  License for the specific language governing permissions and limitations
#  under the License.
#
#  SPDX-License-Identifier: Apache-2.0
#

terraform {
  required_providers {
    helm = {
      source = "hashicorp/helm"
    }
    // for generating passwords, clientsecrets etc.
    random = {
      source = "hashicorp/random"
    }
    kubernetes = {
      source = "hashicorp/kubernetes"
    }
  }
}

provider "kubernetes" {
  config_path = "~/.kube/config"
}

provider "helm" {
  kubernetes {
    config_path = "~/.kube/config"
  }
}

# First connector
module "alice-connector" {
  depends_on = [module.azurite]
  source            = "./modules/connector"
  humanReadableName = "alice"
  participantId     = var.alice-bpn
  database-host     = local.alice-postgres.database-host
  database-name     = local.databases.alice.database-name
  database-credentials = {
    user     = local.databases.alice.database-username
    password = local.databases.alice.database-password
  }
  dcp-config = {
    id                     = var.alice-did
    sts_token_url          = "http://alice-ih:7084/api/credentials/token"
    sts_client_id          = var.alice-did
    sts_clientsecret_alias = "participant-alice-sts-client-secret"
  }
  dataplane = {
    privatekey-alias = "alice-dp-signer-key-alias"
    publickey-alias  = "alice-dp-verifier-key-alias"
  }

  azure-account-name    = var.alice-azure-account-name
  azure-account-key     = local.alice-azure-key-base64
  azure-account-key-sas = var.alice-azure-key-sas
  azure-url             = module.azurite.azurite-url
  minio-config = {
    minio-username = "aliceawsclient"
    minio-password = "aliceawssecret"
  }
  ingress-host = var.alice-ingress-host
}

module "alice-identityhub" {
  source = "./modules/identity-hub"
  database = {
    user     = local.databases.alice.database-username
    password = local.databases.alice.database-password
    url      = "jdbc:postgresql://${local.alice-postgres.database-host}/${local.databases.alice.database-name}"
  }
  humanReadableName = "alice-ih"
  namespace         = "default"
  participantId     = var.alice-did
  vault-url         = "http://alice-vault:8200"
  url-path          = "alice-ih"
}

# alice's catalog server
module "alice-catalog-server" {
  source            = "./modules/catalog-server"
  humanReadableName = "alice-cs"
  namespace         = "default"
  participantId     = var.alice-bpn
  vault-url         = "http://alice-vault:8200"
  bdrs-url          = "http://bdrs-server:8082/api/directory"
  database = {
    user     = local.databases.alice.database-username
    password = local.databases.alice.database-password
    url      = "jdbc:postgresql://${local.alice-postgres.database-host}/${local.databases.alice.database-name}"
  }
  dcp-config = {
    id                     = var.alice-did
    sts_token_url          = "http://alice-ih:7084/api/credentials/token"
    sts_client_id          = var.alice-did
    sts_clientsecret_alias = "participant-alice-sts-client-secret"
  }
}


# Second connector
module "bob-connector" {
  depends_on = [module.azurite]
  source            = "./modules/connector"
  humanReadableName = "bob"
  participantId     = var.bob-bpn
  database-host     = local.bob-postgres.database-host
  database-name     = local.databases.bob.database-name
  database-credentials = {
    user     = local.databases.bob.database-username
    password = local.databases.bob.database-password
  }
  dcp-config = {
    id                     = var.bob-did
    sts_token_url          = "http://bob-ih:7084/api/credentials/token"
    sts_client_id          = var.bob-did
    sts_clientsecret_alias = "participant-bob-sts-client-secret"
  }
  dataplane = {
    privatekey-alias = "bob-dp-signer-key-alias"
    publickey-alias  = "bob-dp-verifier-key-alias"
  }

  azure-account-name    = var.bob-azure-account-name
  azure-account-key     = local.bob-azure-key-base64
  azure-account-key-sas = var.bob-azure-key-sas
  azure-url             = module.azurite.azurite-url
  minio-config = {
    minio-username = "bobawsclient"
    minio-password = "bobawssecret"
  }
  ingress-host = var.bob-ingress-host
}

module "bob-identityhub" {
  source = "./modules/identity-hub"
  database = {
    user     = local.databases.bob.database-username
    password = local.databases.bob.database-password
    url      = "jdbc:postgresql://${local.bob-postgres.database-host}/${local.databases.bob.database-name}"
  }
  humanReadableName = "bob-ih"
  namespace         = "default"
  participantId     = var.bob-did
  vault-url         = "http://bob-vault:8200"
  url-path          = "bob-ih"
}

module "azurite" {
  source           = "./modules/azurite"
  azurite-accounts = "${var.alice-azure-account-name}:${local.alice-azure-key-base64};${var.bob-azure-account-name}:${local.bob-azure-key-base64};${var.trudy-azure-account-name}:${local.trudy-azure-key-base64};"
}

locals {
  alice-azure-key-base64 = base64encode(var.alice-azure-account-key)
  bob-azure-key-base64 = base64encode(var.bob-azure-account-key)
  trudy-azure-key-base64 = base64encode(var.trudy-azure-account-key)
}
