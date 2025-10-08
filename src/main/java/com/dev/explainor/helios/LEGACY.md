# Legacy Conductor Code

## Статус: @Deprecated

Весь код в пакете `com.dev.explainor.helios.layout` помечен как **устаревший** и будет удалён в будущих версиях.

## Причина

Заменён новым движком **Genesis** (`com.dev.explainor.genesis`).

## Устаревшие Компоненты

### Layout Strategies
- `HubAndSpokeLayoutStrategy` → Genesis LayoutManager
- `HierarchicalLayoutStrategy` → Genesis LayoutManager
- `LinearChainLayoutStrategy` → Genesis LayoutManager
- `GridLayoutStrategy` → Genesis LayoutManager

### Layout Managers
- `GraphBasedLayoutManager` → Genesis LayoutManager
- `SimpleHintLayoutManager` → Genesis LayoutManager

### Utilities
- `PathFinder` → Genesis PathFinder (в разработке)
- `ArrowRoutingHelper` → Genesis routing

## Тесты

Все тесты помечены `@Disabled` с причиной:
```java
@Disabled("Legacy conductor code - replaced by Genesis layout engine")
```

## Миграция

Используйте новый API:
```java
// Старый подход (deprecated)
@Autowired
GraphBasedLayoutManager layoutManager;

// Новый подход (Genesis)
@Autowired
com.dev.explainor.genesis.layout.LayoutManager layoutManager;
```

## Удаление

Запланировано после завершения **Этапа 2: "Река"**.

