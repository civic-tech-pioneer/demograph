# Debate

A repository with an experimental setup for structured debates.

# Todo

## Model

- See `domain.plantuml`

## Technologies

- GraphQL API to query/subscribe to contestables
- MongoDB for persistence
- Spring Boot reactive framework
- Kotlin and coroutines

## Architecture

- Updates to contestables are persisted
- Impacts are computed and propagated along outgoing edges
- Propagation is managed by a priority based scheduler, using change magnitude as priority, among other metrics

## Storage

- Text based storage

## Rendering

- Dot or PlantUML transformation

