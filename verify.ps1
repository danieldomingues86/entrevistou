$ErrorActionPreference = "Stop"
Write-Host "[1/2] Backend tests" -ForegroundColor Cyan
Push-Location backend
mvn test
Pop-Location

Write-Host "[2/2] Frontend production build" -ForegroundColor Cyan
Push-Location frontend
npm install
npm run build
Pop-Location

Write-Host "OK - Entrevistou V2.2 validado." -ForegroundColor Green
