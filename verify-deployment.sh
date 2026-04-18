#!/bin/bash

# SCM Project - Kubernetes Deployment Verification Script
# This script verifies that all Kubernetes resources are properly configured and running

set -e

NAMESPACE=${1:-dev}
echo "🔍 Verifying SCM Project deployment in namespace: $NAMESPACE"
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print status
print_status() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓ $2${NC}"
    else
        echo -e "${RED}✗ $2${NC}"
    fi
}

# 1. Check if namespace exists
echo -e "${BLUE}1. Checking Namespace${NC}"
if kubectl get namespace $NAMESPACE &> /dev/null; then
    print_status 0 "Namespace '$NAMESPACE' exists"
else
    print_status 1 "Namespace '$NAMESPACE' does not exist"
    echo "   Creating namespace..."
    kubectl create namespace $NAMESPACE
fi
echo ""

# 2. Check secrets
echo -e "${BLUE}2. Checking Secrets${NC}"
if kubectl get secret scm-secrets -n $NAMESPACE &> /dev/null; then
    print_status 0 "Secret 'scm-secrets' exists"
    echo "   Keys:"
    kubectl get secret scm-secrets -n $NAMESPACE -o jsonpath='{.data}' | jq 'keys' 2>/dev/null || echo "   (unable to display)"
else
    print_status 1 "Secret 'scm-secrets' does not exist"
    echo "   ⚠️  You need to apply k8s/secrets.yaml"
fi
echo ""

# 3. Check MySQL deployment
echo -e "${BLUE}3. Checking MySQL Deployment${NC}"
if kubectl get deployment mysql -n $NAMESPACE &> /dev/null; then
    print_status 0 "MySQL deployment exists"
    MYSQL_READY=$(kubectl get deployment mysql -n $NAMESPACE -o jsonpath='{.status.readyReplicas}' 2>/dev/null || echo "0")
    MYSQL_DESIRED=$(kubectl get deployment mysql -n $NAMESPACE -o jsonpath='{.spec.replicas}' 2>/dev/null || echo "0")
    echo "   Status: $MYSQL_READY/$MYSQL_DESIRED replicas ready"

    if [ "$MYSQL_READY" -eq "$MYSQL_DESIRED" ] && [ "$MYSQL_READY" -gt 0 ]; then
        print_status 0 "MySQL is fully ready"
    else
        print_status 1 "MySQL is not fully ready"
    fi
else
    print_status 1 "MySQL deployment does not exist"
fi
echo ""

# 4. Check App deployment
echo -e "${BLUE}4. Checking App Deployment${NC}"
if kubectl get deployment scm-app -n $NAMESPACE &> /dev/null; then
    print_status 0 "App deployment exists"
    APP_READY=$(kubectl get deployment scm-app -n $NAMESPACE -o jsonpath='{.status.readyReplicas}' 2>/dev/null || echo "0")
    APP_DESIRED=$(kubectl get deployment scm-app -n $NAMESPACE -o jsonpath='{.spec.replicas}' 2>/dev/null || echo "0")
    echo "   Status: $APP_READY/$APP_DESIRED replicas ready"

    if [ "$APP_READY" -eq "$APP_DESIRED" ] && [ "$APP_READY" -gt 0 ]; then
        print_status 0 "App is fully ready"
    else
        print_status 1 "App is not fully ready"
    fi
else
    print_status 1 "App deployment does not exist"
fi
echo ""

# 5. Check services
echo -e "${BLUE}5. Checking Services${NC}"
if kubectl get svc scm-app-service -n $NAMESPACE &> /dev/null; then
    print_status 0 "Service 'scm-app-service' exists"
    SERVICE_IP=$(kubectl get svc scm-app-service -n $NAMESPACE -o jsonpath='{.spec.clusterIP}' 2>/dev/null)
    echo "   Cluster IP: $SERVICE_IP"
else
    print_status 1 "Service 'scm-app-service' does not exist"
fi

if kubectl get svc mysql -n $NAMESPACE &> /dev/null; then
    print_status 0 "Service 'mysql' exists"
    MYSQL_IP=$(kubectl get svc mysql -n $NAMESPACE -o jsonpath='{.spec.clusterIP}' 2>/dev/null)
    echo "   Cluster IP: $MYSQL_IP"
else
    print_status 1 "Service 'mysql' does not exist"
fi
echo ""

# 6. Check pods
echo -e "${BLUE}6. Checking Pods${NC}"
echo "   All pods in namespace '$NAMESPACE':"
kubectl get pods -n $NAMESPACE -o wide 2>/dev/null || echo "   (no pods found)"
echo ""

# 7. Check pod events
echo -e "${BLUE}7. Recent Events${NC}"
kubectl get events -n $NAMESPACE --sort-by='.lastTimestamp' | tail -5 2>/dev/null || echo "   (no events)"
echo ""

# 8. Health check
echo -e "${BLUE}8. Health Check${NC}"
MYSQL_POD=$(kubectl get pods -n $NAMESPACE -l app=mysql -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)
if [ ! -z "$MYSQL_POD" ]; then
    echo "   Testing MySQL pod: $MYSQL_POD"
    if kubectl exec $MYSQL_POD -n $NAMESPACE -- mysqladmin ping -u root -proot &> /dev/null; then
        print_status 0 "MySQL is responding"
    else
        print_status 1 "MySQL is not responding"
    fi
fi

APP_POD=$(kubectl get pods -n $NAMESPACE -l app=scm-app -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)
if [ ! -z "$APP_POD" ]; then
    echo "   Testing App pod: $APP_POD"
    if kubectl get pod $APP_POD -n $NAMESPACE -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' | grep -q "True"; then
        print_status 0 "App pod is ready"
    else
        print_status 1 "App pod is not ready"
    fi
fi
echo ""

# 9. Summary
echo -e "${BLUE}═══════════════════════════════════════${NC}"
echo -e "${BLUE}Deployment Verification Summary${NC}"
echo -e "${BLUE}═══════════════════════════════════════${NC}"

TOTAL_PODS=$(kubectl get pods -n $NAMESPACE --no-headers 2>/dev/null | wc -l)
READY_PODS=$(kubectl get pods -n $NAMESPACE --field-selector=status.phase=Running --no-headers 2>/dev/null | wc -l)

echo "Namespace: $NAMESPACE"
echo "Pods: $READY_PODS/$TOTAL_PODS running"
echo ""

if [ "$READY_PODS" -eq "$TOTAL_PODS" ] && [ "$TOTAL_PODS" -gt 0 ]; then
    echo -e "${GREEN}✓ Deployment looks healthy!${NC}"
else
    echo -e "${YELLOW}⚠️  Some pods are not ready. Check logs with:${NC}"
    echo "   kubectl logs -f pod/<pod-name> -n $NAMESPACE"
    echo "   kubectl describe pod <pod-name> -n $NAMESPACE"
fi
echo ""

# 10. Useful commands
echo -e "${BLUE}Useful Commands:${NC}"
echo "  View all resources: kubectl get all -n $NAMESPACE"
echo "  View logs: kubectl logs -f deployment/scm-app -n $NAMESPACE"
echo "  Port forward: kubectl port-forward svc/scm-app-service 8080:80 -n $NAMESPACE"
echo "  Exec into pod: kubectl exec -it <pod-name> -n $NAMESPACE -- /bin/bash"
echo "  Watch deployment: kubectl rollout status deployment/scm-app -n $NAMESPACE"
echo ""

