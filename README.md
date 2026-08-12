# Credit Scoring Sandbox

Учебен проект (Проект 3, FinTech) — Loan Origination & Credit Scoring Sandbox.

## Структура на repo-то

```
credit-scoring-sandbox/
├── backend/     Spring Boot API (Java, Maven, PostgreSQL)
├── frontend/    React приложение (Applicant + Analyst портали)
└── README.md
```

## Стартиране на backend

```
cd backend
mvn spring-boot:run
```

Изисква локална PostgreSQL инстанция — виж `backend/src/main/resources/application.properties` за конфигурация на връзката.

## Стартиране на frontend

```
cd frontend
npm install
npm run dev
```
