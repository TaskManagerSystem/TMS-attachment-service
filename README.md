## TMS attachment service

### Overview: 
TMS attachment service responsible for handling file upload, 
download, and deletion operations associated with tasks.
The controller interacts with external services such as Dropbox
for file storage and leverages a token-based validation mechanism 
to ensure secure access.

***

## Project Repositories
The project consists of several separate repositories:

1. **[TMS Main Service](https://github.com/TaskManagerSystem/TMS-main-service)**: The main service for task and project management.
2. **[TMS Attachment Service](https://github.com/TaskManagerSystem/TMS-attachment-service)**: The service for managing attachments and integration with Dropbox.
3. **[TMS Notification Service](https://github.com/TaskManagerSystem/TMS-notification-service)**: The service for sending notifications via email and Telegram.
4. **[TMS Common DTO](https://github.com/TaskManagerSystem/TMS-common-dto)**: A shared DTO library used across all microservices.
***

### Functionality:

1. **File upload**: Uploads a file associated with a specific task to Dropbox after validating the user's token.
   Returns the Dropbox file ID if successful.
   ```bash
   POST: /api/attacments
   ```
2. **File download**: Retrieves a download link for a
file stored in Dropbox associated with a specific task,
given a valid token.
   ```bash
   GET: /api/attachents 
   ```
3. **File deletion**:
   Deletes an attachment by its ID after token validation.
   ```bash
   DELETE: /attachments/{id}
   ```
   
***

### Token validation:
Each operation is secured by a token validation process that
involves asynchronous communication with the main service via Kafka. 
The token is sent to the main service through a Kafka topic for 
verification. The controller then waits for a response from the
main service, and only if the token is validated successfully,
the corresponding action (upload, download, or delete) is performed.