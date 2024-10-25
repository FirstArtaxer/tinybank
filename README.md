
# Tiny Bank

## Overview

Tiny Bank is a simple web application simulating essential banking operations. This application was built as part of a hiring process assignment to demonstrate core skills in managing transactions, user authentication, and handling concurrency.

The project is implemented in Java using Spring Boot, and it includes Spring Security for robust authentication and authorization. It allows users to perform secure transactions, manage their accounts, and view transaction histories.

## Key Features

- **User Management**: Users can register, log in, activate/deactivate accounts.
- **Transactions**: Users can deposit, withdraw, and transfer funds between accounts.
- **Balance Inquiry**: Users can check account balances.
- **Transaction History**: Users can view transaction histories.
- **Concurrency Control**: Special care has been taken to handle concurrent withdrawals without performance penalty.

## Technology Stack

- **Java** (Spring Boot): Main application framework.
- **Spring Security**: Handles authentication and authorization.
- **H2 In-memory Database**: Simplifies unit testing without external DB installation.
- **Swagger**: API documentation and testing.

## Authentication and Authorization

This project uses **Spring Security** to manage authentication and role-based access. The sample data includes an admin account (`ardeshir`, password: hashed), granting full permissions. Regular users have limited permissions and can access only their accounts.

## Transaction Types

- **Deposit/Withdraw**: Regular deposit or withdrawal transactions.
- **Transfer to Another User**: Transfers are processed similarly to withdrawals. To transfer money to another account, set the destination `accountNumber` as the `transferAccountNumber` in `TransactionRequestDto`.

## Concurrent Transaction Handling

Special attention was given to handle concurrent withdrawals efficiently. Using a `ReentrantLock` ensures transaction integrity without significant performance impact.

## Initial Data

Sample user data and transactions are pre-seeded for easy testing:

password is encrypted 12345678

```sql
INSERT INTO user_account (id, firstname, lastname, username, password, roles, account_number, is_active)
VALUES (1, 'Ardeshir', 'Ahouri','ardeshir', '$2a$10$rMe5hLcmLB8Q1u0SbEGGXuVPWaj3nfhWssAijAe1sXuMjTkyo2SGK', 'ROLE_ADMIN', 'ACC_111', TRUE);

INSERT INTO user_account (id, firstname, lastname, username, password, roles, account_number, is_active)
VALUES (2, 'Artaxer', 'Ahouri','artaxer' ,'$2a$10$rMe5hLcmLB8Q1u0SbEGGXuVPWaj3nfhWssAijAe1sXuMjTkyo2SGK', 'ROLE_USER', 'ACC_222', TRUE);
```

## How to Run the Project

1. **Clone the Repository**:
   ```bash
   git clone <repo-link>
   cd tiny-bank
   ```

2. **Run the Application**:
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Access Swagger**:
   Open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) in your browser.

## Testing the Application with Swagger

1. **Log in** using the sample credentials through the login API.
2. **Authorize**: After logging in, copy the token and set it in Swagger's **Authorize** button.
3. Test the various endpoints, such as viewing transactions, performing deposits, and transferring funds.

## Notes

This project focuses on core functionality and provides a foundation for secure, efficient bank transactions. While it doesn’t include all production-grade features, it showcases fundamental skills in backend development, secure transaction handling, and user management.