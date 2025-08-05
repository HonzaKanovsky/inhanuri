# InhaNuri - Foreign Community Notice Board
A multilingual platform for foreigners in Korea to access notices, ask questions, and build community about INHA University.

# Live version
https://www.nurinews.kr/

## 🌍 Key Features
- **Notice Board**: Official announcements and updates
- **Q&A Forum**: Tag-based question system with answers
- **Community Board**: User discussions with comments/likes
- **Password-protected Editing**: No login required for posting
- **Admin Dashboard**: Content management system

## 🛠 Technology Stack
| Component       | Technology               |
|----------------|--------------------------|
| **Frontend**   | Vue.js 3, Vuetify        |
| **Backend**    | Spring Boot 3, Java 17   |
| **Database**   | PostgreSQL 15            |
| **Auth**       | Password-based editing   |
| **Infra**      | Docker, Nginx            |

---

## 🚀 Quick Start

### Prerequisites
- Docker 20.10+
- Docker Compose 2.12+

### Installation
```bash
git clone https://github.com/your-repo/inhanuri.git
cd inhanuri
docker-compose up --build