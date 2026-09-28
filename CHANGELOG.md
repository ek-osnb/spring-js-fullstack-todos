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

## 2026-09-28 — `improved` branch

### Added
- `GET /api/users/{id}/todos`
- User dropdown; the table shows usernames
- Toggle completed from the table, "Update Todo" button with Cancel while editing, confirm on delete

### Fixed
- Invalid input returns `400` (ProblemDetail) instead of `500`
- XSS on the todo detail page (uses `textContent` instead of `innerHTML`)
- API errors are shown in the UI instead of being swallowed
- Search and sort survive add/edit/delete; search is case-insensitive

### Changed
- `ddl-auto=update`, so data persists between restarts
