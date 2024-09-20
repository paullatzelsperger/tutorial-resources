resource "kubernetes_deployment" "dataspace-issuer-did-server" {
  metadata {
    name = "dataspace-issuer-server"
    # namespace = var.namespace
    labels = {
      App = "dataspace-issuer-server"
    }
  }

  spec {
    replicas = 1
    selector {
      match_labels = {
        App = "dataspace-issuer-server"
      }
    }

    template {
      metadata {
        labels = {
          App = "dataspace-issuer-server"
        }
      }

      spec {

        container {
          image_pull_policy = "IfNotPresent"
          image             = "nginx:latest"
          name              = "nginx"

          port {
            container_port = "80"
            name           = "web"
          }
          # maps the nginx.conf file
          volume_mount {
            mount_path = "/etc/nginx/nginx.conf"
            sub_path   = "nginx.conf"
            name       = "nginx-config"
          }

          # this maps the did.json file such that it becomes available at htp://<service-name>/dataspace-issuer/did.json
          volume_mount {
            mount_path = "/var/www/.well-known/did.json"
            sub_path   = "did.json"
            name       = "nginx-config"
          }
        }

        volume {
          name = "nginx-config"
          config_map {
            name = "nginx-conf"
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "dataspace-issuer-did-server-service" {
  metadata {
    name = "dataspace-issuer" # this must correlate with the Issuer's DID: did:web:dataspace-issuer -> http://dataspace-issuer/.well-known/did.json
  }
  spec {
    type = "NodePort"
    selector = {
      App = kubernetes_deployment.dataspace-issuer-did-server.spec.0.template.0.metadata[0].labels.App
    }
    # we need a stable IP, otherwise there will be a cycle with the issuer
    port {
      name = "web"
      port = 80
    }
  }
}

resource "kubernetes_config_map" "nginx-map" {
  metadata {
    name = "nginx-conf"
  }

  data = {
    "nginx.conf" = file("${path.cwd}/assets/nginx.conf")
    "did.json" = file("${path.cwd}/assets/issuer.did.json")
  }
}