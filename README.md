# Job Tracker

A Spring Boot API for tracking job applications, interviews and notes, backed by
PostgreSQL.

## Run with fictional data

Install **JDK 25**, clone your fork, and make sure PostgreSQL is running. The
application loads local settings from `.env` in the repository root. For a new
clone, copy the example and fill in your PostgreSQL credentials:

```sh
cp .env.example .env
```

On Windows PowerShell, use `Copy-Item .env.example .env`.

```dotenv
DB_URL=jdbc:postgresql://localhost:5432/jobtrackerdb
DB_USERNAME=your_database_user
DB_PASSWORD=your_database_password
DEMO_SEED_ENABLED=false
DEMO_SEED_PASSWORD=choose-a-long-local-demo-password
```

`.env` is ignored by Git; `.env.example` contains placeholders and can be committed.
`application.properties` references these variables and explicitly imports `.env`
using [Spring Boot's configuration import](https://docs.spring.io/spring-boot/reference/features/external-config.html#features.external-config.files.importing-extensionless).
Run commands from the repository root, and use that directory as the working
directory in your IDE. This import reads Java properties syntax: leave values
unquoted, escape literal backslashes as `\\`, and put comments on separate lines.

Use your own PostgreSQL credentials. The database name must match the final part
of `spring.datasource.url`. `ddl-auto=update` creates tables inside an existing
database; it cannot create the PostgreSQL database itself. For a new setup,
connect to the existing `postgres` database and create `jobtrackerdb` once:

```sh
psql --host localhost --port 5432 --username YOUR_DATABASE_USER --dbname postgres --command 'CREATE DATABASE jobtrackerdb;'
```

Skip this command if `jobtrackerdb` already exists. See the
[PostgreSQL database creation documentation](https://www.postgresql.org/docs/current/manage-ag-createdb.html).

If your PostgreSQL connection is already configured, run this command from the
repository root:

```sh
./mvnw spring-boot:run -Dspring-boot.run.arguments="--jobtracker.demo.seed.enabled=true"
```

On Windows, use:

```powershell
.\mvnw.cmd spring-boot:run '-Dspring-boot.run.arguments=--jobtracker.demo.seed.enabled=true'
```

The Maven wrapper downloads Maven and project dependencies on the first run, so
internet access is needed. JPA creates missing tables using `ddl-auto=update`, and
the enabled seeder automatically inserts the fictional dataset into the configured
PostgreSQL database. No manual data imports are needed.

Seeding is disabled by default. You can also enable it by changing this setting in
`.env` and starting the application normally:

```properties
DEMO_SEED_ENABLED=true
```

No Spring profile is required. Setting the property to `false`, or omitting it,
disables seeding. A command-line value overrides the value in the properties file.

The API uses stateless HTTP Basic authentication backed by registered database
accounts. Public registrations always receive the `APPLICANT` role; clients cannot
choose or elevate their role. Create an applicant account with a `POST` request to
`http://localhost:8080/api/auth/register` and this JSON body:

```sh
curl --request POST http://localhost:8080/api/auth/register \
  --header 'Content-Type: application/json' \
  --data '{"firstName":"Taylor","lastName":"Reed","email":"taylor@example.com","password":"a-long-local-password"}'
```

Registration returns `201 Created`. In Postman, select **Authorization → Basic
Auth**, enter the registered email as the username and its password, and send
requests to `http://localhost:8080/api/applications`. Include those credentials
on each request; the API does not issue tokens or use sessions. Use HTTPS outside
local development because Basic Auth sends credentials with each request.
To authenticate as one of the fictional seeded users, set `DEMO_SEED_ENABLED=true`
and choose a strong local-only `DEMO_SEED_PASSWORD` in `.env`. Both seeded
accounts use that password. Each account can access only its own applications.
Whenever seeding is enabled at startup, the seeder deletes and recreates the two
fictional seed accounts and all applications, notes, and interviews owned by
those accounts. Other user accounts and their data are preserved. Any changes
made to the fictional seed accounts' records or passwords are discarded on the
next seeded startup.

### Applicant and HR specialist roles

Applicants can create, view, edit, or delete only their own applications. They
cannot change application status; new applications start as `APPLIED`. HR
specialists can view, edit, change the status of, or delete any applicant's
application, but cannot create applicant-owned records. The seeded demo accounts
are applicants.

There is no public HR signup or role-change endpoint. To provision an HR specialist,
first register an account normally, then have a trusted database administrator
promote that account:

```sql
UPDATE users SET user_role = 'HR_SPECIALIST' WHERE email = 'hr@example.com';
```

The change takes effect on the account's next authentication. Do not expose database
credentials to users or allow registration requests to assign roles.

Application routes currently available:

| Method | Path | Notes |
| --- | --- | --- |
| `GET` | `/api/applications` | Applicants list their own; HR specialists list all |
| `GET` | `/api/applications/application?applicationId=<uuid>` | Read an accessible application |
| `POST` | `/api/applications/application` | Applicants create an application; the date defaults to today |
| `PUT` | `/api/applications/application` | Update an accessible application; include its `applicationId` |
| `PATCH` | `/api/applications/application/status` | HR specialists only: change status using JSON `applicationId` and `newStatus` |
| `DELETE` | `/api/applications/application?applicationId=<uuid>` | Delete an accessible application |
| `GET` | `/api/applications/{applicationId}/interviews` | Applicants view interviews for their own application; HR can view any |
| `GET` | `/api/applicants/{applicantEmail}/interviews` | Applicants may view their own; HR specialists may view any applicant |
| `GET` | `/api/applications/{applicationId}/interviews` | Applicants view interviews for their own application; HR can view any |
| `GET` | `/api/applicants/{applicantEmail}/interviews` | View interviews by applicant; applicants may request only their own |
| `GET` | `/api/interviews` | HR specialists only: list all interviews |

Create requests accept JSON fields `companyName`, `position`, `location`, `jobUrl`,
`source`, and `applicationDate` (`YYYY-MM-DD`). Update requests accept the same
fields plus the required `applicationId`, which you can get from the response.
Applicants cannot access another applicant's IDs; HR specialists can manage
applications across accounts.

To change an application's status, send `PATCH /api/applications/application/status`
with a JSON body such as:

```json
{
  "applicationId": "0d4bf92a-b496-45c7-b245-df8259f6d870",
  "newStatus": "OFFER"
}
```

### Included data

| Records | Count | Examples |
| --- | ---: | --- |
| Users | 2 | Maya Chen and Alex Morgan (`maya.chen@example.test`, `alex.morgan@example.test`) |
| Job applications | 12 | Six per user, covering `SAVED`, `APPLIED`, `INTERVIEW`, `OFFER`, `REJECTED`, `WITHDRAWN` |
| Interviews | 6 | All interview types, with past and upcoming appointments |
| Notes | 12 | One note per application with fictional preparation, feedback or follow-up details |

All names, companies, emails and job links in the dataset are fictional. Dates are
relative to the day the data is first inserted. Saved jobs have no application date.

`DemoDataSeeder` runs after JPA initializes the tables. It saves users, applications,
interviews and notes in a single transaction. UUIDs and audit timestamps are
generated by the existing entity mappings.

On every startup with seeding enabled, the seeder replaces data belonging to the
two reserved fictional demo emails (`maya.chen@example.test` and
`alex.morgan@example.test`) in one transaction. It removes their associated
interviews, notes, applications, and accounts before recreating the sample
dataset. It does not delete other users or their data. Disable seeding to preserve
the demo dataset and any changes made to it.

The data stays in PostgreSQL after stopping or restarting the application. Seeding
is disabled in the default configuration, so a normal startup uses:

```sh
./mvnw spring-boot:run
```

If you enabled seeding in `.env`, you can disable it for one run:

```sh
./mvnw spring-boot:run -Dspring-boot.run.arguments="--jobtracker.demo.seed.enabled=false"
```

This skips new fictional inserts and preserves data already in the database. It
still requires the configured PostgreSQL database to exist. If startup reports
`FATAL: database "app_db" does not exist`, either correct the datasource URL to
your existing database (for this setup, `jobtrackerdb`) or create the database named
in the URL. A Hibernate dialect error can follow the failed connection and is
resolved by fixing the database connection.

### Configure a fork without editing properties

You can supply environment variables instead of creating `.env`. Environment
variables override values in `.env`:

```sh
DB_URL=jdbc:postgresql://localhost:5432/jobtrackerdb \
DB_USERNAME=YOUR_DATABASE_USER \
DB_PASSWORD=YOUR_DATABASE_PASSWORD \
./mvnw spring-boot:run -Dspring-boot.run.arguments="--jobtracker.demo.seed.enabled=true"
```
