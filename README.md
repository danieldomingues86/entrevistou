# Entrevistou V2.2

Plataforma de preparação para entrevistas técnicas.

## Stack
- Frontend: React 18 + TypeScript + Vite
- Backend: Java 21 + Spring Boot 3.5 + JPA
- Banco local: H2 em memória

## Rodar o backend
```powershell
cd backend
mvn clean spring-boot:run
```
Backend: http://localhost:8080

## Rodar o frontend
Em outro terminal:
```powershell
cd frontend
npm install
npm run dev
```
Frontend: http://localhost:5173

## Login
Nesta versão o login é visual. Qualquer clique em **Entrar**, Google, GitHub, LinkedIn ou Criar conta abre o dashboard.

## Testes
Backend:
```powershell
cd backend
mvn test
```

Frontend / validação TypeScript + build de produção:
```powershell
cd frontend
npm install
npm run build
```

## GitHub
```powershell
git init
git add .
git commit -m "Entrevistou V2.2"
git branch -M main
git remote add origin https://github.com/SEU-USUARIO/entrevistou.git
git push -u origin main
```
