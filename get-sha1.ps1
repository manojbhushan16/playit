# PowerShell script to get SHA-1 fingerprint for debug keystore
# Run this script in PowerShell to get your debug SHA-1 fingerprint

Write-Host "Getting SHA-1 fingerprint for debug keystore..." -ForegroundColor Green

$debugKeystore = "$env:USERPROFILE\.android\debug.keystore"
$alias = "androiddebugkey"
$storepass = "android"
$keypass = "android"

if (Test-Path $debugKeystore) {
    Write-Host "`nFound debug keystore at: $debugKeystore" -ForegroundColor Yellow
    Write-Host "`nExtracting SHA-1 fingerprint..." -ForegroundColor Yellow
    
    keytool -list -v -keystore $debugKeystore -alias $alias -storepass $storepass -keypass $keypass | Select-String "SHA1"
    
    Write-Host "`nCopy the SHA-1 value above and add it to Firebase Console:" -ForegroundColor Green
    Write-Host "1. Go to Firebase Console -> Project Settings -> Your Android App" -ForegroundColor Cyan
    Write-Host "2. Click 'Add fingerprint'" -ForegroundColor Cyan
    Write-Host "3. Paste the SHA-1 value" -ForegroundColor Cyan
    Write-Host "4. Download the updated google-services.json" -ForegroundColor Cyan
    Write-Host "5. Replace app/google-services.json with the new file" -ForegroundColor Cyan
} else {
    Write-Host "Debug keystore not found at: $debugKeystore" -ForegroundColor Red
    Write-Host "`nTo generate it, run in Android Studio Terminal:" -ForegroundColor Yellow
    Write-Host "cd android" -ForegroundColor Cyan
    Write-Host "gradlew signingReport" -ForegroundColor Cyan
}

