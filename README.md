# PanalSuite Mobile

MVP académico Android para la gestión móvil de flujos de trabajo de PanalSuite (DSY1105 · Desarrollo de Aplicaciones Móviles · Duoc UC). Usa solo datos ficticios.

## Stack

- Kotlin + Jetpack Compose + Material 3
- Arquitectura MVVM
- Navigation Compose
- Almacenamiento local con SharedPreferences (solicitudes, comentarios y sesión)

## Funcionalidades

- Login validado con sesión persistente
- Bandeja según rol: el solicitante ve sus solicitudes, el resolutor las asignadas y el supervisor todas, con indicadores por estado
- Filtros por estado
- Formulario de nueva solicitud con validaciones
- Detalle con cambio de estado (solo resolutor y supervisor), comentarios y trazabilidad de cada cambio
- Animaciones: splash, transiciones entre pantallas, listas, avisos y botones
- Tema claro y oscuro

## Usuarios de prueba

Contraseña para todos: `Panal123`

| Rol         | Correo                 |
|-------------|------------------------|
| Solicitante | solicitante@panal.test |
| Resolutor   | resolutor@panal.test   |
| Supervisor  | supervisor@panal.test  |

## Diagrama de arquitectura

```mermaid
flowchart TB
    subgraph UI["ui"]
        A["AppNavHost"] --> S["SplashScreen"]
        A --> L["LoginScreen"]
        A --> I["InboxScreen"]
        A --> F["TicketFormScreen"]
        A --> D["TicketDetailScreen"]
    end
    subgraph VM["viewmodel"]
        SVM["SessionViewModel"]
        LVM["LoginViewModel"]
        IVM["InboxViewModel"]
        FVM["TicketFormViewModel"]
        DVM["TicketDetailViewModel"]
    end
    subgraph REPO["repository"]
        UR["UserRepository"]
        SR["SessionRepository"]
        TR["TicketRepository"]
    end
    SP1[("SharedPreferences: session")]
    SP2[("SharedPreferences: tickets")]

    S --> SVM
    L --> LVM
    I --> IVM
    F --> FVM
    D --> DVM
    SVM --> SR
    SVM --> TR
    LVM --> UR
    LVM --> SR
    IVM --> SR
    IVM --> TR
    FVM --> SR
    FVM --> TR
    DVM --> SR
    DVM --> TR
    DVM --> UR
    SR --> SP1
    TR --> SP2
```

## Diagrama de flujo

```mermaid
flowchart TD
    A(["Abrir app"]) --> B{"¿Sesión guardada?"}
    B -- No --> C["Login"]
    C --> V{"¿Datos válidos?"}
    V -- No --> C
    V -- Sí --> G["Guardar sesión"]
    G --> I["Bandeja según rol"]
    B -- Sí --> I
    I --> N{"¿Puede crear?"}
    N -- Sí --> NC["Nueva solicitud"]
    NC --> NV{"¿Formulario válido?"}
    NV -- No --> NC
    NV -- Sí --> DT
    I --> DT["Detalle"]
    DT --> CM["Comentar"]
    CM --> DT
    DT --> R{"¿Resolutor o supervisor?"}
    R -- Sí --> ST["Cambiar estado y registrar trazabilidad"]
    ST --> DT
    I --> LO["Cerrar sesión"]
    LO --> C
```

## Estructura

```
app/src/main/java/com/duoc/panalsuite/
├── MainActivity.kt
├── model/          ← qué es cada dato
├── repository/     ← de dónde salen los datos
├── viewmodel/      ← el estado y las decisiones
└── ui/             ← lo que se dibuja
    ├── navigation/
    └── theme/
```

## Ejecutar

1. Clonar el repositorio y abrirlo en Android Studio.
2. Sincronizar Gradle.
3. Ejecutar en un emulador o dispositivo con Android 7.0 (API 24) o superior.

## Autora

Macarena del Pilar Leighton Ortiz · Duoc UC