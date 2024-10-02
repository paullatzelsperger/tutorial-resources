variable "bob-bpn" {
  default = "BPNL000000000002"
}

variable "bob-did" {
  default = "did:web:bob-ih%3A7083:bob"
}


variable "bob-azure-account-name" {
  default = "bobazureaccount"
}

variable "bob-azure-account-key" {
  default = "bobazurekey"
}

variable "bob-azure-key-sas" {
  default = "sv=2023-01-03&ss=btqf&srt=sco&st=2023-11-23T13%3A18%3A49Z&se=2030-01-01T00%3A00%3A00Z&sp=rwdftlacup&sig=Xki%2B2nHBT2V8oe%2BMmS%2BI%2FHzcAsTFCYWDv6t6zT%2FWXFM%3D"
}

variable "bob-ingress-host" {
  default = "localhost"
}

variable "bob-humanReadableName" {
  default = "bob"
}

variable "bob-identityhub-host" {
  description = "Kubernetes service name of the IdentityHub"
  default     = "bob-ih"
}

variable "bob-catalogserver-host" {
  description = "Kubernetes service name (=Host) for Bob's Catalog Server"
  default     = "bob-cs"
}
