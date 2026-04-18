# SCM Project - Automated Kubernetes Deployment Script (PowerShell)
# This script fully automates the deployment process for Windows users

param(
    [string]$Namespace = "dev",
    [string]$ManifestDir = "k8s"
)

$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "╔════════════════════════════════════════════════════════╗" -ForegroundColor Blue
Write-Host "║  SCM Project - Kubernetes Deployment Automation        ║" -ForegroundColor Blue
Write-Host "║  Namespace: $Namespace" -ForegroundColor Blue
Write-Host "╚════════════════════════════════════════════════════════╝" -ForegroundColor Blue
Write-Host ""

# Function to wait for resource
function Wait-ForResource {
    param(
        [string]$ResourceType,
        [string]$ResourceName,
        [string]$Namespace,
        [int]$Timeout = 300
    )

    Write-Host "⏳ Waiting for $ResourceType/$ResourceName to be ready (timeout: ${Timeout}s)..." -ForegroundColor Yellow

    try {
        $result = kubectl wait --for=condition=ready "$ResourceType/$ResourceName" -n $Namespace --timeout="${Timeout}s" 2>$null
        Write-Host "✓ $ResourceType/$ResourceName is ready" -ForegroundColor Green
        return $true
    } catch {
        Write-Host "✗ $ResourceType/$ResourceName failed to become ready" -ForegroundColor Red
        return $false
    }
}

try {
    # Step 1: Create namespace
    Write-Host "Step 1: Creating/Verifying Namespace" -ForegroundColor Blue
    kubectl create namespace $Namespace 2>$null | Out-Null
    Write-Host "✓ Namespace $Namespace ready" -ForegroundColor Green
    Write-Host ""

    # Step 2: Apply namespace manifest if it exists
    $namespacePath = Join-Path $ManifestDir "namespace.yaml"
    if (Test-Path $namespacePath) {
        Write-Host "Step 2: Applying Namespace Manifest" -ForegroundColor Blue
        kubectl apply -f $namespacePath | Out-Null
        Write-Host "✓ Namespace manifest applied" -ForegroundColor Green
        Write-Host ""
    }

    # Step 3: Apply secrets
    Write-Host "Step 3: Deploying Secrets" -ForegroundColor Blue
    $secretPath = Join-Path $ManifestDir "secrets.yaml"
    if (Test-Path $secretPath) {
        $secretContent = Get-Content $secretPath -Raw
        # Note: envsubst is not available in PowerShell, so we apply directly
        # For environment variable substitution, you would need to implement it differently
        $secretContent | kubectl apply -f - -n $Namespace | Out-Null
        Write-Host "✓ Secrets deployed" -ForegroundColor Green
    } else {
        Write-Host "✗ secrets.yaml not found" -ForegroundColor Red
        exit 1
    }
    Write-Host ""

    # Step 4: Deploy MySQL
    Write-Host "Step 4: Deploying MySQL" -ForegroundColor Blue
    $mysqlPath = Join-Path $ManifestDir "mysql.yaml"
    if (Test-Path $mysqlPath) {
        kubectl apply -f $mysqlPath -n $Namespace | Out-Null
        Write-Host "✓ MySQL deployment applied" -ForegroundColor Green

        # Wait for MySQL pod to be ready
        Start-Sleep -Seconds 5
        $mysqlPod = kubectl get pods -n $Namespace -l app=mysql -o jsonpath='{.items[0].metadata.name}' 2>$null

        if ($mysqlPod) {
            Write-Host "⏳ Waiting for MySQL pod to be ready..." -ForegroundColor Yellow

            # Wait for pod to be ready
            $ready = $false
            for ($i = 1; $i -le 60; $i++) {
                $status = kubectl get pod $mysqlPod -n $Namespace -o jsonpath='{.status.phase}' 2>$null
                if ($status -eq "Running") {
                    Write-Host "✓ MySQL pod is running" -ForegroundColor Green
                    $ready = $true
                    break
                }
                Write-Host "  Attempt $i/60: Pod not ready yet..." -ForegroundColor Gray
                Start-Sleep -Seconds 2
            }

            if ($ready) {
                # Wait for MySQL to be fully initialized
                Write-Host "⏳ Waiting for MySQL database to initialize..." -ForegroundColor Yellow
                Start-Sleep -Seconds 10

                # Test MySQL connectivity
                $mysqlReady = $false
                for ($i = 1; $i -le 30; $i++) {
                    try {
                        kubectl exec $mysqlPod -n $Namespace -- mysqladmin ping -u root -proot 2>$null | Out-Null
                        Write-Host "✓ MySQL is responding" -ForegroundColor Green
                        $mysqlReady = $true
                        break
                    } catch {
                        Write-Host "  Attempt $i/30: MySQL not ready yet..." -ForegroundColor Gray
                        Start-Sleep -Seconds 5
                    }
                }

                if (-not $mysqlReady) {
                    Write-Host "⚠️  MySQL may not be fully ready, but proceeding..." -ForegroundColor Yellow
                }
            }
        }
    } else {
        Write-Host "✗ mysql.yaml not found" -ForegroundColor Red
        exit 1
    }
    Write-Host ""

    # Step 5: Deploy application
    Write-Host "Step 5: Deploying Application" -ForegroundColor Blue
    $deploymentPath = Join-Path $ManifestDir "deployment.yaml"
    if (Test-Path $deploymentPath) {
        kubectl apply -f $deploymentPath -n $Namespace | Out-Null
        Write-Host "✓ Application deployment applied" -ForegroundColor Green
    } else {
        Write-Host "✗ deployment.yaml not found" -ForegroundColor Red
        exit 1
    }
    Write-Host ""

    # Step 6: Deploy service
    Write-Host "Step 6: Deploying Service" -ForegroundColor Blue
    $servicePath = Join-Path $ManifestDir "service.yaml"
    if (Test-Path $servicePath) {
        kubectl apply -f $servicePath -n $Namespace | Out-Null
        Write-Host "✓ Service deployed" -ForegroundColor Green
    } else {
        Write-Host "✗ service.yaml not found" -ForegroundColor Red
        exit 1
    }
    Write-Host ""

    # Step 7: Deploy ingress (optional)
    $ingressPath = Join-Path $ManifestDir "ingress.yaml"
    if (Test-Path $ingressPath) {
        Write-Host "Step 7: Deploying Ingress" -ForegroundColor Blue
        kubectl apply -f $ingressPath -n $Namespace | Out-Null
        Write-Host "✓ Ingress deployed" -ForegroundColor Green
        Write-Host ""
    }

    # Step 8: Wait for application rollout
    Write-Host "Step 8: Waiting for Application to be Ready" -ForegroundColor Blue
    try {
        kubectl rollout status deployment/scm-app -n $Namespace --timeout=600s 2>$null | Out-Null
        Write-Host "✓ Application is ready" -ForegroundColor Green
    } catch {
        Write-Host "⚠️  Application may not be fully ready, check status with: kubectl get pods -n $Namespace" -ForegroundColor Yellow
    }
    Write-Host ""

    # Step 9: Verify deployment
    Write-Host "Step 9: Verifying Deployment" -ForegroundColor Blue
    Write-Host ""
    Write-Host "Deployments:" -ForegroundColor Cyan
    kubectl get deployments -n $Namespace -o wide
    Write-Host ""
    Write-Host "Pods:" -ForegroundColor Cyan
    kubectl get pods -n $Namespace -o wide
    Write-Host ""
    Write-Host "Services:" -ForegroundColor Cyan
    kubectl get svc -n $Namespace -o wide
    Write-Host ""

    # Step 10: Summary
    Write-Host "═══════════════════════════════════════" -ForegroundColor Blue
    Write-Host "✓ Deployment Complete!" -ForegroundColor Green
    Write-Host "═══════════════════════════════════════" -ForegroundColor Blue
    Write-Host ""

    # Get pod info
    $appPod = kubectl get pods -n $Namespace -l app=scm-app -o jsonpath='{.items[0].metadata.name}' 2>$null
    if ($appPod) {
        Write-Host "Application Pod: $appPod" -ForegroundColor Yellow

        $podStatus = kubectl get pod $appPod -n $Namespace -o jsonpath='{.status.phase}' 2>$null
        if ($podStatus -eq "Running") {
            Write-Host "Status: Running" -ForegroundColor Green
        } else {
            Write-Host "Status: $podStatus" -ForegroundColor Yellow
        }
    }
    Write-Host ""

    Write-Host "Next Steps:" -ForegroundColor Blue
    Write-Host "1. Monitor logs: kubectl logs -f deployment/scm-app -n $Namespace"
    Write-Host "2. Port forward: kubectl port-forward svc/scm-app-service 8080:80 -n $Namespace"
    Write-Host "3. Access app: http://localhost:8080"
    Write-Host ""

    Write-Host "Useful Commands:" -ForegroundColor Blue
    Write-Host "  View all: kubectl get all -n $Namespace"
    Write-Host "  Pod logs: kubectl logs <pod-name> -n $Namespace"
    Write-Host "  Pod shell: kubectl exec -it <pod-name> -n $Namespace -- /bin/bash"
    Write-Host "  Describe: kubectl describe pod <pod-name> -n $Namespace"
    Write-Host "  Events: kubectl get events -n $Namespace --sort-by='.lastTimestamp'"
    Write-Host ""

} catch {
    Write-Host "Error during deployment: $_" -ForegroundColor Red
    exit 1
}

