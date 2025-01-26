$ErrorActionPreference = "Stop"

Write-Host "Building eventful-photo..."
Set-Location eventful-photo
mvn clean package -DskipTests
docker build -t eventful-photo .
Set-Location ..

Write-Host "Running docker-compose..."
docker-compose up -d

Write-Host "Waiting 10 seconds for dependencies..."
Start-Sleep -Seconds 10

Write-Host "Running microservices..."

docker run -d -p 8080:8080 --name eventful-photo --network person_detector_microservices_network eventful-photo

Write-Host "Everything done!"