output "management-endpoint" {
  value = "${kubernetes_service.catalog-server-service.metadata.0.name}:${var.ports.management}"
}