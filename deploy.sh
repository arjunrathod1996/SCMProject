#!/bin/bash

# SCM Project - Automated Kubernetes Deployment Script
# This script fully automates the deployment process

set -e

NAMESPACE=${1:-dev}
MANIFEST_DIR="k8s"

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}"
echo "╔════════════════════════════════════════════════════════╗"
echo "║  SCM Project - Kubernetes Deployment Automation        ║"
echo "║  Namespace: $NAMESPACE"
echo "╚════════════════════════════════════════════════════════╝"
echo -e "${NC}"
echo ""

# Function to wait for resource
wait_for_resource() {
    local resource_type=$1
    local resource_name=$2
    local namespace=$3
    local timeout=${4:-300}

    echo -e "${YELLOW}⏳ Waiting for $resource_type/$resource_name to be ready (timeout: ${timeout}s)...${NC}"

    if kubectl wait --for=condition=ready $resource_type/$resource_name -n $namespace --timeout=${timeout}s 2>/dev/null; then
        echo -e "${GREEN}✓ $resource_type/$resource_name is ready${NC}"
        return 0
    else
        echo -e "${RED}✗ $resource_type/$resource_name failed to become ready${NC}"
        return 1
    fi
}

# Step 1: Create namespace
echo -e "${BLUE}Step 1: Creating/Verifying Namespace${NC}"
kubectl create namespace $NAMESPACE 2>/dev/null || true
echo -e "${GREEN}✓ Namespace $NAMESPACE ready${NC}"
echo ""

# Step 2: Apply namespace manifest if it exists
if [ -f "$MANIFEST_DIR/namespace.yaml" ]; then
    echo -e "${BLUE}Step 2: Applying Namespace Manifest${NC}"
    kubectl apply -f $MANIFEST_DIR/namespace.yaml
    echo -e "${GREEN}✓ Namespace manifest applied${NC}"
    echo ""
fi

# Step 3: Apply secrets
echo -e "${BLUE}Step 3: Deploying Secrets${NC}"
if [ -f "$MANIFEST_DIR/secrets.yaml" ]; then
    envsubst < $MANIFEST_DIR/secrets.yaml | kubectl apply -f - -n $NAMESPACE
    echo -e "${GREEN}✓ Secrets deployed${NC}"
else
    echo -e "${RED}✗ secrets.yaml not found${NC}"
    exit 1
fi
echo ""

# Step 4: Deploy MySQL
echo -e "${BLUE}Step 4: Deploying MySQL${NC}"
if [ -f "$MANIFEST_DIR/mysql.yaml" ]; then
    kubectl apply -f $MANIFEST_DIR/mysql.yaml -n $NAMESPACE
    echo -e "${GREEN}✓ MySQL deployment applied${NC}"

    # Wait for MySQL pod to be ready
    sleep 5
    MYSQL_POD=$(kubectl get pods -n $NAMESPACE -l app=mysql -o jsonpath='{.items[0].metadata.name}' 2>/dev/null || echo "")

    if [ ! -z "$MYSQL_POD" ]; then
        echo -e "${YELLOW}⏳ Waiting for MySQL pod to be ready...${NC}"
        kubectl wait --for=condition=ready pod/$MYSQL_POD -n $NAMESPACE --timeout=300s 2>/dev/null || true

        # Additional wait for MySQL to be fully initialized
        echo -e "${YELLOW}⏳ Waiting for MySQL database to initialize...${NC}"
        sleep 10

        # Test MySQL connectivity
        MYSQL_READY=false
        for i in {1..30}; do
            if kubectl exec $MYSQL_POD -n $NAMESPACE -- mysqladmin ping -u root -proot &>/dev/null; then
                echo -e "${GREEN}✓ MySQL is responding${NC}"
                MYSQL_READY=true
                break
            fi
            echo "  Attempt $i/30: MySQL not ready yet..."
            sleep 5
        done

        if [ "$MYSQL_READY" = false ]; then
            echo -e "${YELLOW}⚠️  MySQL may not be fully ready, but proceeding...${NC}"
        fi
    fi
else
    echo -e "${RED}✗ mysql.yaml not found${NC}"
    exit 1
fi
echo ""

# Step 5: Deploy application
echo -e "${BLUE}Step 5: Deploying Application${NC}"
if [ -f "$MANIFEST_DIR/deployment.yaml" ]; then
    kubectl apply -f $MANIFEST_DIR/deployment.yaml -n $NAMESPACE
    echo -e "${GREEN}✓ Application deployment applied${NC}"
else
    echo -e "${RED}✗ deployment.yaml not found${NC}"
    exit 1
fi
echo ""

# Step 6: Deploy service
echo -e "${BLUE}Step 6: Deploying Service${NC}"
if [ -f "$MANIFEST_DIR/service.yaml" ]; then
    kubectl apply -f $MANIFEST_DIR/service.yaml -n $NAMESPACE
    echo -e "${GREEN}✓ Service deployed${NC}"
else
    echo -e "${RED}✗ service.yaml not found${NC}"
    exit 1
fi
echo ""

# Step 7: Deploy ingress (optional)
if [ -f "$MANIFEST_DIR/ingress.yaml" ]; then
    echo -e "${BLUE}Step 7: Deploying Ingress${NC}"
    kubectl apply -f $MANIFEST_DIR/ingress.yaml -n $NAMESPACE
    echo -e "${GREEN}✓ Ingress deployed${NC}"
    echo ""
fi

# Step 8: Wait for application rollout
echo -e "${BLUE}Step 8: Waiting for Application to be Ready${NC}"
if kubectl rollout status deployment/scm-app -n $NAMESPACE --timeout=600s; then
    echo -e "${GREEN}✓ Application is ready${NC}"
else
    echo -e "${YELLOW}⚠️  Application may not be fully ready, check status with: kubectl get pods -n $NAMESPACE${NC}"
fi
echo ""

# Step 9: Verify deployment
echo -e "${BLUE}Step 9: Verifying Deployment${NC}"
echo ""
echo "Deployments:"
kubectl get deployments -n $NAMESPACE -o wide
echo ""
echo "Pods:"
kubectl get pods -n $NAMESPACE -o wide
echo ""
echo "Services:"
kubectl get svc -n $NAMESPACE -o wide
echo ""

# Step 10: Summary
echo -e "${BLUE}═══════════════════════════════════════${NC}"
echo -e "${GREEN}✓ Deployment Complete!${NC}"
echo -e "${BLUE}═══════════════════════════════════════${NC}"
echo ""

# Get pod info
APP_POD=$(kubectl get pods -n $NAMESPACE -l app=scm-app -o jsonpath='{.items[0].metadata.name}' 2>/dev/null || echo "")
if [ ! -z "$APP_POD" ]; then
    echo -e "${YELLOW}Application Pod: $APP_POD${NC}"

    # Check pod status
    POD_STATUS=$(kubectl get pod $APP_POD -n $NAMESPACE -o jsonpath='{.status.phase}')
    if [ "$POD_STATUS" = "Running" ]; then
        echo -e "${GREEN}Status: Running${NC}"
    else
        echo -e "${YELLOW}Status: $POD_STATUS${NC}"
    fi
fi
echo ""

echo -e "${BLUE}Next Steps:${NC}"
echo "1. Monitor logs: kubectl logs -f deployment/scm-app -n $NAMESPACE"
echo "2. Port forward: kubectl port-forward svc/scm-app-service 8080:80 -n $NAMESPACE"
echo "3. Access app: http://localhost:8080"
echo ""

echo -e "${BLUE}Useful Commands:${NC}"
echo "  View all: kubectl get all -n $NAMESPACE"
echo "  Pod logs: kubectl logs <pod-name> -n $NAMESPACE"
echo "  Pod shell: kubectl exec -it <pod-name> -n $NAMESPACE -- /bin/bash"
echo "  Describe: kubectl describe pod <pod-name> -n $NAMESPACE"
echo "  Events: kubectl get events -n $NAMESPACE --sort-by='.lastTimestamp'"
echo ""

