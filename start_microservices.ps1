$ErrorActionPreference = "Stop"

Write-Host "Building eventful-photo..."
Set-Location eventful-photo
mvn clean package -DskipTests
docker build -t eventful-photo .
Set-Location ..

Write-Host "Building photo-analysis..."
Set-Location photo-analysis
mvn clean package -DskipTests
docker build -t photo-analysis .
Set-Location ..


Write-Host "Running docker-compose..."
docker-compose up -d

Write-Host "Waiting 10 seconds for dependencies..."
Start-Sleep -Seconds 10

Write-Host "Running microservices..."

docker run -d -p 8080:8080 --name eventful-photo --network person_detector_microservices_network eventful-photo
docker run -d -p 8081:8081 --name photo-analysis --network person_detector_microservices_network photo-analysis
#docker run -d -p 8082:8082 --name photo-analysis-second --network person_detector_microservices_network photo-analysis
Write-Host "Waiting 10 seconds for microservices..."
Start-Sleep -Seconds 10

Write-Host "Everything done!"