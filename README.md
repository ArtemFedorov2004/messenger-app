```bash
docker run --name messenger-db -p 5433:5432 \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin \
  -e POSTGRES_DB=messenger\
  postgres:16
```