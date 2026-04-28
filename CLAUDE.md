# CashFlow — Claude Code Instructions

## Project Overview

Kotlin Multiplatform (KMP) personal finance app targeting **Android, iOS, and Desktop (JVM)**.

- **Single module**: everything lives in `composeApp/`
- **Package root**: `com.github.dsbezerra.cashflow`
- **Stack**: Kotlin 2.3.21 · Compose Multiplatform 1.10.3 · SQLDelight 2.3.2 · Koin 4.2.1 · AndroidX Navigation · AndroidX Paging 3
- **UI language**: Portuguese (PT-BR) — all user-visible strings are in `strings.xml`

---

## Build & Run Commands

```bash
./gradlew :composeApp:assembleDebug        # Android debug APK
./gradlew :composeApp:installDebug         # Install on connected device/emulator
./gradlew :composeApp:run                  # Desktop (JVM)
./gradlew :composeApp:testDebugUnitTest    # Android unit tests
```

---

## Architecture

Clean Architecture with MVI presentation layer.

```
composeApp/src/commonMain/kotlin/.../
├── core/
│   ├── data/
│   │   ├── db/           # DatabaseAdapters, DriverFactory
│   │   ├── mapper/       # toDomain() / toEntity() extension functions
│   │   └── repository/   # SqlDelight*Repository implementations
│   ├── designsystem/
│   │   ├── component/    # Reusable Compose components
│   │   └── theme/        # Color, Typography, Theme
│   ├── di/               # Koin modules (assembled in AppModules.kt)
│   ├── domain/
│   │   ├── model/        # Domain data classes
│   │   ├── repository/   # Repository interfaces
│   │   └── usecase/      # Use case classes
│   └── navigation/       # Routes, AppNavHost, AppShell
├── feature/
│   ├── account/
│   ├── category/
│   ├── dashboard/
│   ├── recurring/
│   ├── report/
│   ├── settings/
│   ├── transaction/
│   └── about/
└── util/                 # CoroutineUtils, DateLocale, IdGenerator
```

---

## MVI Pattern

Every screen follows this file structure:

```
feature/<name>/<screen>/
├── <Screen>State.kt       # data class with all UI state
├── <Screen>Action.kt      # sealed interface — user intents
├── <Screen>Event.kt       # sealed interface — one-time side effects
├── <Screen>ViewModel.kt   # ViewModel with onAction() entry point
└── <Screen>Screen.kt      # Root composable (takes state + callbacks, no ViewModel)
```

### State
```kotlin
data class FooState(
    val isLoading: Boolean = true,
    val items: List<Item> = emptyList(),
)
```

### Action
```kotlin
sealed interface FooAction {
    data class DeleteItem(val id: String) : FooAction
    data object Refresh : FooAction
}
```

### Event (one-time side effects)
```kotlin
sealed interface FooEvent {
    data object NavigateBack : FooEvent
    data class ShowError(val message: String) : FooEvent
}
```

### ViewModel
```kotlin
class FooViewModel(...) : ViewModel() {
    private val _state = MutableStateFlow(FooState())
    val state: StateFlow<FooState> = _state.asStateFlow()

    private val _events = Channel<FooEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onAction(action: FooAction) { ... }
}
```

---

## Repository Pattern

| Layer | Location | Naming |
|-------|----------|--------|
| Interface | `core/domain/repository/` | `FooRepository` |
| Implementation | `core/data/repository/` | `SqlDelightFooRepository` |
| Mapper | `core/data/mapper/` | `FooMapper.kt` — `toDomain()` / `toEntity()` |

Bind interface to implementation in `core/di/RepositoryModule.kt`.

---

## Koin DI

```kotlin
// Repositories — singletons
single<FooRepository> { SqlDelightFooRepository(get()) }

// ViewModels
viewModel { FooViewModel(get(), get()) }

// Use cases — new instance per injection
factory { FooUseCase(get()) }
```

All modules are assembled in `core/di/AppModules.kt`.

---

## Navigation

- Routes defined in `core/navigation/Routes.kt`
- Wired in `core/navigation/AppNavHost.kt`
- Route format:
  ```kotlin
  @Serializable data object Dashboard
  @Serializable data class TransactionDetail(val transactionId: String? = null)
  ```
- **Never pass `NavController` into feature screens.** Use callbacks: `onNavigateBack`, `onNavigateTo*`.

---

## HARD RULES

### 1. No hardcoded UI strings
All user-visible text must be in `strings.xml` and accessed via `stringResource`.

```
File: composeApp/src/commonMain/composeResources/values/strings.xml
```

```kotlin
// Correct
import com.github.dsbezerra.cashflow.generated.resources.*
import org.jetbrains.compose.resources.stringResource

Text(stringResource(Res.string.my_key))

// Wrong — never do this
Text("Minhas Transacoes")
```

### 2. Use `safeRunCatching` in coroutines
Never use bare `try/catch` in coroutine scopes. Use `safeRunCatching` from `util/CoroutineUtils.kt`, which correctly re-throws `CancellationException`.

```kotlin
// Correct
viewModelScope.launch {
    safeRunCatching { deleteUseCase(id) }
        .onFailure { _events.send(FooEvent.ShowError("Failed")) }
}

// Wrong
viewModelScope.launch {
    try { deleteUseCase(id) } catch (e: Exception) { ... }
}
```

---

## Testing

- Write unit tests for every new feature and every bug fix.
- **Location**: `composeApp/src/commonTest/`
- **Framework**: `kotlin.test` (common) · JUnit4 for Android/JVM
- **Pattern**: fake repositories, test ViewModel with `UnconfinedTestDispatcher`, assert `state` flow values and `events` channel output.

---

## Commit Style — Conventional Commits

```
feat: add budget alerts to dashboard
fix: crash when account list is empty on first launch
refactor: extract transaction mapper to separate file
chore: update Koin to 4.2.1
docs: add architecture section to README
```

Types: `feat` · `fix` · `refactor` · `chore` · `docs` · `test` · `style` · `perf`

---

## Key File Paths

| What | Path |
|------|------|
| DI module assembly | `composeApp/src/commonMain/kotlin/.../core/di/AppModules.kt` |
| SQLDelight schemas | `composeApp/src/commonMain/sqldelight/.../db/*.sq` |
| All UI strings | `composeApp/src/commonMain/composeResources/values/strings.xml` |
| Navigation routes | `composeApp/src/commonMain/kotlin/.../core/navigation/Routes.kt` |
| Navigation host | `composeApp/src/commonMain/kotlin/.../core/navigation/AppNavHost.kt` |
| Safe coroutine util | `composeApp/src/commonMain/kotlin/.../util/CoroutineUtils.kt` |
| Design system components | `composeApp/src/commonMain/kotlin/.../core/designsystem/component/` |
| App requirements spec | `specs/cashflow-app-requirements-en.md` |
