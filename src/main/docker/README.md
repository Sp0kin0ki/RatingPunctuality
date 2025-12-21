Для запуска docker compose с котейнерами `postgres`, `pgadmin` + `analytics job` нужно прописать команды:

Из корневой директории

```
docker compose -f src/main/docker/docker-compose.yml up -d db pgadmin
```

Далее загрузить базу данных в `postgres`

```
docker exec -i rut-psql-lab psql -U postgres -d postgres < plain.sql
```

Запуск `analytics job` происходит в коде, но если нужно запустить руками, то:

```
docker compose -f src/main/docker/docker-compose.yml run --rm analytics-job
```

Выходной файл `flight_delay_rules.csv` с признаками задержек сохраняется в `src/main/resources/templates`