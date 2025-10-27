# Cloud SQL Auth Proxy

### Set instance connection name

```bash
export INSTANCE_CONNECTION_NAME="lofty-root-378503:us-central1:jm-pg-demo"
```

### Run the CloudSQL auth proxy

Remove any old proxy container:

```bash
docker rm -f csql-proxy 2>/dev/null || true
```

Then use one of the below options:

#### Executable

```bash
# CLI option
# Download from: https://cloud.google.com/sql/docs/postgres/connect-auth-proxy
./cloud-sql-proxy ${INSTANCE_CONNECTION_NAME} --port 5432 &
PROXY_PID=$!
```

#### Docker

```bash

export CREDENTIALS_PATH="/Users/admin/.config/gcloud/sa-private-key.json"
docker run -d --name csql-proxy -p 127.0.0.1:5400:5432 \
  -v "$GOOGLE_APPLICATION_CREDENTIALS:/creds/key.json:ro" \
  gcr.io/cloud-sql-connectors/cloud-sql-proxy:2 \
  --address 0.0.0.0 --port 5432 \
  --credentials-file=/creds/key.json "$INSTANCE_CONNECTION_NAME"
```

### Wait for proxy to listen

```bash
for i in {1..30}; do nc -z 127.0.0.1 5433 && echo "proxy ready" && break; sleep 1; done
```

### Check if proxy is running

```bash
docker ps -a | grep csql-proxy || true
```

### Verify proxy is running on a specific port

```bash
nc -z 127.0.0.1 5400 && echo "proxy up" || echo "proxy not up"
```

### Check proxy logs

```bash
docker logs csql-proxy --tail=200 || true
```

### Check if port is in use

```bash
lsof -iTCP:5432 -sTCP:LISTEN || true
```

### List available ports

#### Common Postgres ports

```bash
for p in 5432 5433 5434 5435 5436; do (lsof -iTCP:$p -sTCP:LISTEN >/dev/null) && echo "❌ $p in use" || echo "✅ $p free"; done
```

#### Scan a wider port range

```bash
for p in {5400..5450}; do (lsof -iTCP:$p -sTCP:LISTEN >/dev/null) || echo "✅ $p free"; done
```

#### Pick the first available port

```bash
for p in {5400..5450}; do (lsof -iTCP:$p -sTCP:LISTEN >/dev/null) || { echo "First free port: $p"; break; }; done
```

### Test connection

```bash
export TF_VAR_db_user=username
export TF_VAR_db_password=password
export TF_VAR_db_name=jm_demo_db
```

Have the proxy running and healthy to `127.0.0.1:5432`.
Make sure nothing else is using port `5432`.
If it is, pick another port (e.g., 5433) and update your `psql` command.

Then run:

```bash
PGPASSWORD="$TF_VAR_db_password" psql \
  -h 127.0.0.1 -p 5400 \
  -U "$TF_VAR_db_user" -d "$TF_VAR_db_name" \
  -c "select 1;"
```

### If the test is successful

Assuming the file `/src/test/resources/init.sql` exists:

```bash
PGPASSWORD="$TF_VAR_db_password" psql \
  -h 127.0.0.1 -p 5400 \
  -U "$TF_VAR_db_user" -d "$TF_VAR_db_name" \
  -f init.sql
```

You can also create the table directly from the command line:

```bash
cat > init.sql <<'SQL'
PGPASSWORD="$TF_VAR_db_password" psql -h 127.0.0.1 -p 5400 -U "$TF_VAR_db_user" -d "$TF_VAR_db_name" -v ON_ERROR_STOP=1 <<'SQL'
CREATE TABLE IF NOT EXISTS widgets (
  id         UUID PRIMARY KEY,
  name       TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  meta       JSONB
);
SQL
```

### Single insert

```bash
PGPASSWORD="$TF_VAR_db_password" psql \
  -h 127.0.0.1 -p 5400 \
  -U "$TF_VAR_db_user" -d "$TF_VAR_db_name" \
  -c "INSERT INTO widgets (id, name, meta) VALUES (gen_random_uuid(), 'Test Widget', '{\"color\":\"red\",\"size\":10}');"
```

### Multiple inserts

```bash
PGPASSWORD="$TF_VAR_db_password" psql \
  -h 127.0.0.1 -p 5400 \
  -U "$TF_VAR_db_user" -d "$TF_VAR_db_name" \
  -c "
INSERT INTO widgets (id, name, meta) VALUES
  (gen_random_uuid(), 'Widget A', '{\"type\":\"alpha\"}'),
  (gen_random_uuid(), 'Widget B', '{\"type\":\"beta\"}'),
  (gen_random_uuid(), 'Widget C', '{\"type\":\"gamma\"}');
"
```

### Insert rows from a file

```bash
cat > /tmp/insert_widgets.sql <<'SQL'
INSERT INTO widgets (id, name, meta) VALUES
  (gen_random_uuid(), 'Widget X', '{"size":42}'),
  (gen_random_uuid(), 'Widget Y', '{"color":"blue"}');
SQL

PGPASSWORD="$TF_VAR_db_password" psql \
  -h 127.0.0.1 -p 5400 \
  -U "$TF_VAR_db_user" -d "$TF_VAR_db_name" \
  -f insert_widgets.sql
```

### Read all rows in a table

```bash
PGPASSWORD="$TF_VAR_db_password" psql \
  -h 127.0.0.1 -p 5400 \
  -U "$TF_VAR_db_user" -d "$TF_VAR_db_name" \
  -c "SELECT * FROM widgets;"
```

### Open an interactive psql session

```bash
PGPASSWORD="$TF_VAR_db_password" psql -h 127.0.0.1 -p 5400 -U "$TF_VAR_db_user" -d "$TF_VAR_db_name"
```

```sql
INSERT INTO widgets (id, name, meta) VALUES (gen_random_uuid(), 'Widget ABC', '{"shape":"sphere"}');
SELECT * FROM widgets;
```

### List tables

```psql
\l
```

### Drop tables

```bash
DROP TABLE widgets;
```

### Exit `psql`

```psql
exit
```

---

### Grant create/use to a user

```bash
PGPASSWORD="$TF_VAR_db_password" psql -h 127.0.0.1 -p 5432 -U "$TF_VAR_db_user" -d "$TF_VAR_db_name" -c \
"GRANT USAGE ON SCHEMA public TO $TF_VAR_db_user; GRANT CREATE ON SCHEMA public TO $TF_VAR_db_user;"
```

Verify `roles/cloudsql.client` on the user's service account.

---

### Stop the CloudSQL auth proxy

```bash
kill $PROXY_PID 2>/dev/null || true
# or: docker rm -f csql-proxy
```
