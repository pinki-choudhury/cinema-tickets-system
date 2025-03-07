# **Cinema Ticket Booking System**

## **Overview**

This project is a Java-based implementation of a cinema ticket booking system. It allows users to purchase tickets for different types of attendees (Adult, Child, Infant) while adhering to specific business rules and constraints. The system calculates the total cost of tickets and reserves seats accordingly.

**Key Features**
* Purchase tickets for Adults, Children, and Infants.

* Validate ticket purchase requests (e.g., maximum of 25 tickets, at least one adult ticket required).

* Calculate the total cost of tickets.

* Reserve seats for valid ticket purchases.

## **Prerequisites**
Before running the project, ensure you have the following installed:

* Java Development Kit (JDK) 18 or later

* Maven (for dependency management and building the project)

* Git (for cloning the repository)

## **Getting Started**

1. **Clone the Repository** <br>
   Clone the repository to your local machine using the following command: <br>
`git clone https://github.com/your-username/cinema-ticket-booking-system.git` <br>
`cd cinema-ticket-booking-system/cinema-tickets-java`

2. **Build the Project** <br>
Use Maven to build the project: <br>
`mvn clean install`

3. **Run the Tests** <br>
To run the unit tests, use the following command:<br>
`mvn test`

4. **Generating code coverage Report**<br>
 Run the following Maven command to generate the JaCoCo code coverage report:<br>
 `mvn clean test jacoco:report` <br>
 
 The report will be generated in the `target/site/jacoco/` directory. Open `index.html` in your browser to view the coverage report.

## **Dependencies**
The project uses the following dependencies:

* JUnit : For unit testing.

* Mockito: For mocking dependencies in tests.

* JaCoCo: For code coverage reporting.

### JaCoCo Configuration
To generate a code coverage report using JaCoCo, add the following plugin to your `pom.xml`:
```xml
        <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.8.10</version> <!-- Use the latest version -->
                <executions>
                    <execution>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
```

## **Project Structure**
```
├── src
│   ├── main
│   │   └── java
│   │       ├── thirdparty
│   │       │   ├── paymentgateway
│   │       │   │   ├── TicketPaymentService.java
│   │       │   │   └── TicketPaymentServiceImpl.java
│   │       │   └── seatbooking
│   │       │       ├── SeatReservationService.java
│   │       │       └── SeatReservationServiceImpl.java
│   │       └── uk
│   │           └── gov
│   │               └── dwp
│   │                   └── uc
│   │                       └── pairtest
│   │                           ├── domain
│   │                           │   └── TicketTypeRequest.java
│   │                           ├── exception
│   │                           │   └── InvalidPurchaseException.java
│   │                           ├── TicketService.java
│   │                           └── TicketServiceImpl.java
│   └── test
│       ├── java
│       │   └── uk
│       │       └── gov
│       │           └── dwp
│       │               └── uc
│       │                   └── pairtest
│       │                       └── TicketServiceImplTest.java
│       └── resources
└── pom.xml
```
## **Business Rules**
The system adheres to the following business rules:

1. **Ticket Types:**

    * Adult: £25 per ticket.
  
    * Child: £15 per ticket.
  
    * Infant: £0 per ticket (no seat allocation).

4. **Maximum Tickets:** No more than 25 tickets can be purchased in a single transaction.

5. **Adult Requirement:** At least one adult ticket must be purchased for any child or infant tickets.

6. **Seat Allocation:** Infants do not occupy a seat.

## **Testing**
The project includes unit tests to validate the functionality of the TicketServiceImpl class. You can run the tests using:<br>
`mvn test`

**Test Scenarios**
* Valid ticket purchase requests.

* Invalid account IDs.

* Requests exceeding the maximum ticket limit.

* Requests without an adult ticket.

* Mixed ticket type requests.

