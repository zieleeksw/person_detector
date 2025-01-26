Write-Host "Stopping microservices..."
docker stop eventful-photo 2>$null

docker rm eventful-photo 2>$null

Write-Host "Stopping docker-compose..."
docker-compose down

Write-Host "Deleting images..."
docker rmi eventful-photo 2>$null

Write-Host "Cleaning done!"