# Don't Wreck My House

`Don't Wreck My House` is a Java console application for managing vacation rental reservations. The application allows a user to manage guests, manage hosts, and create, edit, view, or cancel reservations using CSV-based file storage.

## Overview

This project is organized using a layered design:

- `ui`: handles console input/output and application flow
- `domain`: contains business rules and validation
- `data`: reads and writes CSV files
- `models`: defines the application entities

Spring is used for dependency injection so the controller, services, and repositories can be wired together cleanly.

## Features

- View reservations for a host by host email
- Make a reservation for a guest and host
- Edit an existing reservation
- Cancel a future reservation
- Manage guests
- Manage hosts
- Calculate reservation totals using weekday and weekend rates
- Validate reservation rules before saving

## Business Rules

The domain layer enforces several rules:

- A reservation must include a valid guest and host
- Reservation dates are required
- The start date must be before the end date
- New reservations must start in the future
- Reservations for the same host cannot overlap
- Reservation totals are calculated from the host's standard and weekend rates
- Reservations that have already started or passed cannot be cancelled
- Guest and host emails must be unique

## Project Structure

Key classes:

- [App.java](/Users/dosa/Documents/repositories/module05-mastery-project-dont-wreck-my-house-fardouse-1/src/main/java/learn/house/App.java): application entry point
- [Controller.java](/Users/dosa/Documents/repositories/module05-mastery-project-dont-wreck-my-house-fardouse-1/src/main/java/learn/house/ui/Controller.java): coordinates user workflows
- [View.java](/Users/dosa/Documents/repositories/module05-mastery-project-dont-wreck-my-house-fardouse-1/src/main/java/learn/house/ui/View.java): console prompts and output
- [ReservationService.java](/Users/dosa/Documents/repositories/module05-mastery-project-dont-wreck-my-house-fardouse-1/src/main/java/learn/house/domain/ReservationService.java): reservation validation and pricing
- [GuestService.java](/Users/dosa/Documents/repositories/module05-mastery-project-dont-wreck-my-house-fardouse-1/src/main/java/learn/house/domain/GuestService.java): guest validation and operations
- [HostService.java](/Users/dosa/Documents/repositories/module05-mastery-project-dont-wreck-my-house-fardouse-1/src/main/java/learn/house/domain/HostService.java): host validation and operations
- [ReservationFileRepository.java](/Users/dosa/Documents/repositories/module05-mastery-project-dont-wreck-my-house-fardouse-1/src/main/java/learn/house/data/ReservationFileRepository.java): reservation persistence
- [GuestFileRepository.java](/Users/dosa/Documents/repositories/module05-mastery-project-dont-wreck-my-house-fardouse-1/src/main/java/learn/house/data/GuestFileRepository.java): guest persistence
- [HostFileRepository.java](/Users/dosa/Documents/repositories/module05-mastery-project-dont-wreck-my-house-fardouse-1/src/main/java/learn/house/data/HostFileRepository.java): host persistence

## Data Storage

The application uses CSV files stored in the `data/` directory:

- `data/guests.csv`: guest records
- `data/hosts.csv`: host records
- `data/reservations/`: one reservation file per host, named with the host id

Runtime paths are configured in [application.properties](/Users/dosa/Documents/repositories/module05-mastery-project-dont-wreck-my-house-fardouse-1/src/main/resources/application.properties):

- `data.guest.path=data/guests.csv`
- `data.host.path=data/hosts.csv`
- `data.reservations.dir=data/reservations`

## Tech Stack

- Java 17
- Maven
- Spring Context
- JUnit 5

## Running the Application

From the project root:

```bash
mvn compile
mvn exec:java -Dexec.mainClass="learn.house.App"
```

If you prefer, you can also run the `main` method in [App.java](/Users/dosa/Documents/repositories/module05-mastery-project-dont-wreck-my-house-fardouse-1/src/main/java/learn/house/App.java) from your IDE.

## Running Tests

```bash
mvn test
```

## Example Demo Flow

One simple demo path:

1. View reservations for a host
2. Enter a host email
3. Make a new reservation for a guest and host
4. Enter future dates
5. Review the calculated total
6. Confirm the reservation

## Notes

- Reservation totals are recalculated when a reservation is created or edited
- Reservation files store reservation data by host, which keeps host-specific lookups simple
- Validation messages are returned through a `Result<T>` object instead of using exceptions for normal user input problems
