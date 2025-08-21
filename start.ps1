$envName=Read-Host "choose environmet to run (dev,qa,prod): "
if($envName -ne "dev" -and $envName -ne "qa" -and $envName -ne "prod")
{
    Write-Host "Invalid environmet: Please choose (dev,qa,prod)"
    exit
}
Write-Host $envName
$envFile=".env.$envName"
if(-Not (Test-Path $envFile))
{
    Write-Host " environmet file $envFile not exist"
    exit
}
Write-Host "Using $envFile ........"
docker compose --env-file $envFile up --build