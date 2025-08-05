# Shticell - Multi-User Spreadsheet Application

## Overview

Shticell is a comprehensive multi-user spreadsheet application built with Java, featuring a client-server architecture that enables real-time collaboration and advanced spreadsheet functionality. The application supports multiple users working simultaneously on shared spreadsheets with a robust permission system and rich formula support.

## Architecture

The project follows a modular architecture with clear separation of concerns:

```
┌─────────────────┐    HTTP/JSON     ┌─────────────────┐
│   JavaFX Client │ ◄────────────► │   Web Server    │
│     (GUI)       │                 │   (Servlets)    │
└─────────────────┘                 └─────────────────┘
                                             │
                                             ▼
                                    ┌─────────────────┐
                                    │  Engine Core    │
                                    │ (Business Logic)│
                                    └─────────────────┘
```

### Modules

- **Client**: JavaFX-based desktop application providing the user interface
- **Server**: Servlet-based web server handling HTTP requests and managing sessions
- **Engine**: Core business logic for spreadsheet operations and calculations
- **DTO**: Data Transfer Objects for client-server communication
- **ServerSDK**: Client SDK for server communication

## Features

### 🔐 User Management & Authentication
- User login/logout functionality
- Session management
- Multi-user concurrent access

### 📊 Spreadsheet Core Features
- **Cell Operations**: Create, edit, and delete cells with various data types
- **Formulas & Functions**: Support for arithmetic, boolean, textual, and system functions
- **Ranges**: Create named ranges for easier formula references
- **Versioning**: Track sheet versions with history

### 🔧 Advanced Operations
- **Sorting**: Sort data within specified ranges
- **Filtering**: Filter rows based on column criteria
- **Dynamic Analysis**: Real-time cell value analysis
- **Data Types**: Support for numeric, textual, and boolean values

### 👥 Collaboration Features
- **Permission System**: Owner, Reader, Writer permission levels
- **Permission Requests**: Users can request access to sheets
- **Real-time Updates**: Multiple users can work simultaneously
- **Sheet Sharing**: Share spreadsheets with controlled access

### 📁 File Management
- Load spreadsheet files (XML format)
- File metadata tracking
- Owner-based access control

## Supported Functions

The application includes comprehensive function support organized into categories:

### Arithmetic Functions
- `PLUS`, `MINUS`, `TIMES`, `DIVIDE`
- `MOD`, `POW`, `ABS`, `PERCENT`

### Boolean Functions
- `AND`, `OR`, `NOT`
- `EQUAL`, `BIGGER`, `LESS`
- `IF` (conditional logic)

### System Functions
- `SUM`, `AVERAGE` (range operations)
- `REF` (cell references)

### Text Functions
- `CONCAT` (string concatenation)
- `SUB` (substring operations)

## Getting Started

### Prerequisites
- Java 21 or higher
- IntelliJ IDEA (recommended IDE)
- Apache Tomcat 10.1.30 (for server deployment)
- JavaFX runtime libraries
- External libraries: OkHttp, Gson

### Setting Up the Project

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Shticell
   ```

2. **Open in IntelliJ IDEA**
   - Open the project in IntelliJ IDEA
   - The project will automatically detect the module structure
   - Ensure JDK 21 is configured

3. **Configure Libraries**
   - Add JavaFX libraries to the project
   - Add OkHttp and Gson libraries
   - Configure Tomcat 10.1.30 as application server

4. **Build the Project**
   - Build → Build Project (or Ctrl+F9)
   - IntelliJ will compile all modules automatically

### Running the Application

1. **Start the Server**
   - Configure Tomcat run configuration in IntelliJ
   - Deploy the Server module to Tomcat
   - Start the Tomcat server

2. **Run the Client**
   - Create a run configuration for `client.main.Client`
   - Ensure JavaFX runtime is properly configured
   - Run the client application

### Project Structure

```
Shticell/
├── Client/           # JavaFX desktop application
│   ├── src/client/
│   │   ├── component/    # UI components (login, dashboard, sheet, etc.)
│   │   ├── main/         # Application entry point
│   │   └── util/         # HTTP client utilities
├── Server/           # Web server with servlets
│   ├── src/server/
│   │   ├── servlet/      # HTTP request handlers
│   │   └── util/         # Server utilities
├── Engine/           # Core business logic
│   ├── src/engine/
│   │   ├── api/          # Engine interface
│   │   ├── entity/       # Core entities (Cell, Sheet, Range)
│   │   ├── operation/    # Formula functions
│   │   └── expression/   # Expression evaluation
├── DTO/              # Data Transfer Objects
└── ServerSDK/        # Server communication SDK
```

## Usage

### Basic Workflow

1. **Login**: Start the client and log in with a username
2. **Dashboard**: View available spreadsheets and manage permissions
3. **Load File**: Upload a new spreadsheet file (XML format)
4. **Edit Sheet**: 
   - Click cells to edit values
   - Enter formulas starting with `=`
   - Use functions like `=SUM(A1:A10)` or `=IF(A1>0,"Positive","Negative")`
5. **Collaborate**: Share sheets with other users and manage permissions

### Formula Syntax

- **Cell References**: `A1`, `B5`, `C10`
- **Ranges**: `A1:C10`, `B2:B20`
- **Functions**: `=SUM(A1:A10)`, `=AVERAGE(B1:B5)`
- **Complex Formulas**: `=IF(A1>AVERAGE(B1:B10),"Above","Below")`

### Permission System

- **Owner**: Full control (read, write, manage permissions)
- **Writer**: Read and write access to cells
- **Reader**: Read-only access
- **None**: No access to the sheet

## API Endpoints

The server exposes REST endpoints for:

- `/login` - User authentication
- `/logout` - User logout
- `/sheet` - Sheet operations (GET/POST)
- `/cell` - Individual cell operations
- `/range` - Range management
- `/permission` - Permission requests

## Data Persistence

- Spreadsheet data is stored in XML format
- User sessions are managed server-side
- Permission data is maintained in memory

## Error Handling

The application includes comprehensive error handling for:
- Invalid cell references
- Circular dependency detection
- Type conversion errors
- Permission violations
- File format validation

## Contributing

1. Follow the existing code structure and naming conventions
2. Add appropriate error handling and validation
3. Update documentation for new features
4. Test multi-user scenarios thoroughly

## Technical Details

### Cell System
- Cells support multiple data types (numeric, textual, boolean)
- Automatic type conversion where appropriate
- Effective value calculation with dependency tracking

### Expression Evaluation
- Recursive expression parser
- Support for nested functions
- Lazy evaluation for performance

### Concurrency
- Thread-safe operations for multi-user access
- Synchronized blocks for critical sections
- Session-based user isolation

## License

This project is developed as part of an educational curriculum.

---

**Note**: This is a desktop application with a client-server architecture. The client must connect to a running server instance for full functionality.