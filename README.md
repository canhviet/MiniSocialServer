## Features

- **Authentication and Authorization**: 
  - Secure login and signup using Spring Security and JWT.
  - OAuth2 integration for external authentication (e.g., Google).

- **Post Management**: 
  - Create, edit, and delete posts.
  - Like and comment on posts.

- **User Features**:
  - Follow/unfollow other users.
  - View profile information and activity.

- **Real-Time Messaging**: 
  - Chat functionality using WebSocket and StompJS.

- **Notifications**: 
  - Receive updates about likes, comments, and new followers.

- **Database**: 
  - PostgreSQL for data persistence.
  - Redis for caching.

---

## Technologies Used

- **Frameworks and Libraries**:
  - Spring Boot (REST API, Security, Data JPA)
  - WebSocket for real-time messaging

- **Frontend Integration**:
  - Designed to work with an Angular front-end.
  - [Link](https://github.com/canhviet/MiniSocialClient)

- **Database**:
  - PostgreSQL
  - Redis

- **Others**:
  - Spring Boot Mail for sending emails

---

## Getting Started

### Prerequisites

- Java 17
- Docker 

### Installation 

**Clone the Repository**:
   ```bash
   git clone https://github.com/canhviet/MiniSocialServer.git
   cd MiniSocialServer
   ```
**Or pull api service from Docker Hub**:
   ```bash
   docker pull canhviet/my_app-api-service:latest
   ```
## License
This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for details.
