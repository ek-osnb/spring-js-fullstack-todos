# Changelog

## 2026-09-25 — `live-code` branch

### Added
- Search box that filters todos by title
- Todo detail page (`todos.html?id=…`): click a row to open it (multi-page app example)
- `fetchTodoById` in the API module

### Changed
- Frontend split into ES modules: `api/todo.api.js`, `utils/sorting.js`, `app.js`
- Frontend moved from `frontend/` to `spring-todo-service/src/main/resources/static`, so Spring Boot serves it and the API is called with the relative path `/api/todos`
- CORS config added while the frontend ran separately, then removed again once Spring served the frontend (same origin)
