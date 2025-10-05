Entity:
=======
In model/Task.java

- class or record?
  - Does record mix with all the annotations jakarta annotations?
  - And then the records would be immutable ... do we want that? Not really, no. Not for
    in memory storage.
  - Decision: class atm. Boilerplate via IDE.
  - Rollback: Now record, as I learned about best practices of jakarta annotations going
    to DTO classes. To be discussed, especially whether frameworks like checkerframework
    or similar from jakarta / Spring Boot would be used.

Fields:
- id:
  - is a UUID. Then: random or not? If not: privacy considerations.
  - decision 1: don't care atm, random().
  - decision 2: must be provided by business value to class, no default value
  - UUID contraints???
- status
  - should naturally be an enum internally
  - but input via REST ... and text anyway
  - decision: KISS at first, i.e., strings. And opportunity to test jakarta validator.
- timestamps
  - store as ZonedDateTime instead of string to allow future sorting operations
  - decision: stored completely (no truncation to seconds) (again, business logic!)




Repository:
===========
Realise via interface to provide future flexibility. Doesn't cost much effort, so eh.

Fields:
- "Database"
  - ConcurrentHashMap. HashMap "because default", and Concurrent because REST, Spring.
    It's there, why not use it.

Unhappy:
- localisation of existsByTitleAndAuthor(). Feels like BL, but simplicity for MVP to
  repository
  Decision: KISS atm, to repository for MVP



Service:
========
Nothing wild, just the business layer.
TODO: get error handling clean of HTTP, Claude goofed there.

Controller:
===========
- Claude goofed, proposed class instead of record. Record way cleaner.
- Learned that canonical constructors can be used for front line defense and/or setting
default values of DTOs.



Error handling / exceptions:
============================
In real production code, would have own global error handler with own exception classes.
Decision: not for this toy project. Influences also: separation of concerns regarding throwing
HTTP codes.

Jakarta:
========
learned: annotations in Task.java do not enforce validation, only together
with @Valid and/or @RequestBody (in the controller only?)

