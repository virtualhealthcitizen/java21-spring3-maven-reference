# Automate table creation using Terraform

### Example

```hcl
# run after instance+db+user exist
resource "null_resource" "init_table" {
  depends_on = [google_sql_database.db, google_sql_user.user]

  provisioner "local-exec" {
    interpreter = ["/bin/bash", "-c"]
    command = <<-EOT
      set -euo pipefail
      # Start proxy
      cloud-sql-proxy ${google_sql_database_instance.pg.connection_name} --port 5432 &
      PID=$!
      # Wait for port
      for i in {1..30}; do nc -z 127.0.0.1 5432 && break; sleep 1; done
      # Apply DDL
      PGPASSWORD='${var.db_password}' psql -h 127.0.0.1 -p 5432 -U '${var.db_user}' -d '${var.db_name}' <<'SQL'
      CREATE TABLE IF NOT EXISTS widgets (
        id UUID PRIMARY KEY,
        name TEXT NOT NULL,
        created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
        meta JSONB
      );
      SQL
      kill $PID
    EOT
  }
}
```
