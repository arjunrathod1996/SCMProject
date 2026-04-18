# SCM Project - Kubernetes Deployment Verification Script (PowerShell)
# This script verifies that all Kubernetes resources are properly configured and running

param(
    [string]$Namespace = "dev"
)

Write-Host "🔍 Verifying SCM Project deployment in namespace: $Namespace" -ForegroundColor Cyan
Write-Host ""

# Function to print status
function Print-Status {
    param(
        [int]$Code,
        [string]$Message
    )
    if ($Code -eq 0) {
        Write-Host "✓ $Message" -ForegroundColor Green
    } else {
        Write-Host "✗ $Message" -ForegroundColor Red
    }
}

# 1. Check if namespace exists
Write-Host "1. Checking Namespace" -ForegroundColor Blue
try {
    $ns = kubectl get namespace $Namespace 2>$null
    Print-Status 0 "Namespace '$Namespace' exists"
} catch {
    Print-Status 1 "Namespace '$Namespace' does not exist"
    Write-Host "   Creating namespace..."
    kubectl create namespace $Namespace | Out-Null
}
Write-Host ""

# 2. Check secrets
Write-Host "2. Checking Secrets" -ForegroundColor Blue
try {
    $secret = kubectl get secret scm-secrets -n $Namespace 2>$null
    Print-Status 0 "Secret 'scm-secrets' exists"
    Write-Host "   Secret is configured"
} catch {
    Print-Status 1 "Secret 'scm-secrets' does not exist"
    Write-Host "   ⚠️  You need to apply k8s/secrets.yaml"
}
Write-Host ""

# 3. Check MySQL deployment
Write-Host "3. Checking MySQL Deployment" -ForegroundColor Blue
try {
    $mysql = kubectl get deployment mysql -n $Namespace 2>$null
    Print-Status 0 "MySQL deployment exists"

    $mysqlReady = kubectl get deployment mysql -n $Namespace -o jsonpath='{.status.readyReplicas}' 2>$null
    $mysqlDesired = kubectl get deployment mysql -n $Namespace -o jsonpath='{.spec.replicas}' 2>$null

    Write-Host "   Status: $mysqlReady/$mysqlDesired replicas ready"

    if ($mysqlReady -eq $mysqlDesired -and $mysqlReady -gt 0) {
        Print-Status 0 "MySQL is fully ready"
    } else {
        Print-Status 1 "MySQL is not fully ready"
    }
} catch {
    Print-Status 1 "MySQL deployment does not exist"
}
Write-Host ""

# 4. Check App deployment
Write-Host "4. Checking App Deployment" -ForegroundColor Blue
try {
    $app = kubectl get deployment scm-app -n $Namespace 2>$null
    Print-Status 0 "App deployment exists"

    $appReady = kubectl get deployment scm-app -n $Namespace -o jsonpath='{.status.readyReplicas}' 2>$null
    $appDesired = kubectl get deployment scm-app -n $Namespace -o jsonpath='{.spec.replicas}' 2>$null

    Write-Host "   Status: $appReady/$appDesired replicas ready"

    if ($appReady -eq $appDesired -and $appReady -gt 0) {
        Print-Status 0 "App is fully ready"
    } else {
        Print-Status 1 "App is not fully ready"
    }
} catch {
    Print-Status 1 "App deployment does not exist"
}
Write-Host ""

# 5. Check services
Write-Host "5. Checking Services" -ForegroundColor Blue
try {
    $svc = kubectl get svc scm-app-service -n $Namespace 2>$null
    Print-Status 0 "Service 'scm-app-service' exists"

    $serviceIp = kubectl get svc scm-app-service -n $Namespace -o jsonpath='{.spec.clusterIP}' 2>$null
    Write-Host "   Cluster IP: $serviceIp"
} catch {
    Print-Status 1 "Service 'scm-app-service' does not exist"
}

try {
    $mysql_svc = kubectl get svc mysql -n $Namespace 2>$null
    Print-Status 0 "Service 'mysql' exists"

    $mysqlIp = kubectl get svc mysql -n $Namespace -o jsonpath='{.spec.clusterIP}' 2>$null
    Write-Host "   Cluster IP: $mysqlIp"
} catch {
    Print-Status 1 "Service 'mysql' does not exist"
}
Write-Host ""

# 6. Check pods
Write-Host "6. Checking Pods" -ForegroundColor Blue
Write-Host "   All pods in namespace '$Namespace':"
try {
    kubectl get pods -n $Namespace -o wide 2>$null
} catch {
    Write-Host "   (no pods found)"
}
Write-Host ""

# 7. Check pod events
Write-Host "7. Recent Events" -ForegroundColor Blue
try {
    kubectl get events -n $Namespace --sort-by='.lastTimestamp' 2>$null | Select-Object -Last 5
} catch {
    Write-Host "   (no events)"
}
Write-Host ""

# 8. Summary
Write-Host "═══════════════════════════════════════" -ForegroundColor Blue
Write-Host "Deployment Verification Summary" -ForegroundColor Blue
Write-Host "═══════════════════════════════════════" -ForegroundColor Blue

$totalPods = (kubectl get pods -n $Namespace --no-headers 2>$null | Measure-Object -Line).Lines
$readyPods = (kubectl get pods -n $Namespace --field-selector=status.phase=Running --no-headers 2>$null | Measure-Object -Line).Lines

Write-Host "Namespace: $Namespace"
Write-Host "Pods: $readyPods/$totalPods running"
Write-Host ""

if ($readyPods -eq $totalPods -and $totalPods -gt 0) {
    Write-Host "✓ Deployment looks healthy!" -ForegroundColor Green
} else {
    Write-Host "⚠️  Some pods are not ready. Check logs with:" -ForegroundColor Yellow
    Write-Host "   kubectl logs -f pod/<pod-name> -n $Namespace"
    Write-Host "   kubectl describe pod <pod-name> -n $Namespace"
}
Write-Host ""

# 9. Useful commands
Write-Host "Useful Commands:" -ForegroundColor Blue
Write-Host "  View all resources: kubectl get all -n $Namespace"
Write-Host "  View logs: kubectl logs -f deployment/scm-app -n $Namespace"
Write-Host "  Port forward: kubectl port-forward svc/scm-app-service 8080:80 -n $Namespace"
Write-Host "  Exec into pod: kubectl exec -it <pod-name> -n $Namespace -- /bin/bash"
Write-Host "  Watch deployment: kubectl rollout status deployment/scm-app -n $Namespace"
Write-Host ""

