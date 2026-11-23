# Scripts operacionales de Kafka

Los scripts se ejecutan desde la raiz del repositorio en EC2 y no crean, eliminan ni modifican recursos Kafka.

## Requisitos

- Docker Compose v2.
- Archivo `.env.aws` existente en la raiz.
- Servicio `kafka` iniciado mediante `docker-compose.aws.yml`.
- Acceso a Bash.

Dar permisos de ejecucion una sola vez:

```bash
chmod +x scripts/kafka/*.sh
```

## Resumen operativo

```bash
bash scripts/kafka/summary.sh
```

## Consultas individuales

```bash
bash scripts/kafka/list-topics.sh
bash scripts/kafka/describe-topics.sh
bash scripts/kafka/describe-topic-configs.sh
bash scripts/kafka/list-consumer-groups.sh
bash scripts/kafka/describe-consumer-groups.sh
```

## Interpretacion

- `describe-topics.sh` muestra particiones, lider, replicas y replicas sincronizadas.
- `describe-topic-configs.sh` muestra las configuraciones del topico; se revisan especialmente `retention.ms` y `cleanup.policy`.
- `describe-consumer-groups.sh` muestra offsets confirmados, offsets finales y LAG por particion.
- Un offset confirmado ausente aparece como no disponible o como ausencia de asignacion segun el estado real del grupo; el script no lo transforma en cero.
- Los scripts usan `localhost:9092` dentro del contenedor Kafka y no muestran ni modifican variables secretas.
