Write-Host "Stopping microservices..."
docker stop eventful-photo 2>$null
docker stop photo-analysis 2>$null
#docker stop photo-analysis-second 2>$null

docker rm eventful-photo 2>$null
docker rm photo-analysis 2>$null
#docker rm photo-analysis-second 2>$null


Write-Host "Stopping docker-compose..."
docker-compose down

Write-Host "Deleting images..."
docker rmi eventful-photo 2>$null
docker rmi photo-analysis 2>$null
#docker rmi photo-analysis-second 2>$null

Write-Host "Cleaning done!"