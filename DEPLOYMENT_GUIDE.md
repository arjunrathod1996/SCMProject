# SCM Project - Complete Deployment Guide

## 🎯 Overview

This document provides complete guidance for deploying the SCM Project to Kubernetes. All critical issues have been identified and fixed.

---

## 📋 What's Been Fixed

### Critical Issues Resolved ✅

1. **Spring Boot Profile Mismatch** - Dockerfile and K8s now use consistent configuration
2. **Elasticsearch Auto-Configuration** - Disabled for non-Elasticsearch deployments
3. **Aggressive Health Probes** - Added startup probe and adjusted probe timings
4. **MySQL Readiness** - Added health checks to ensure proper initialization
5. **Deployment Sequencing** - Proper dependency ordering in workflows
6. **GitHub Workflow Conflicts** - Consolidated to single authoritative workflow
7. **Ingress Configuration** - Added proper nginx setup and timeout handling

### Impact
- ✅ Pod no longer crashes repeatedly
- ✅ MySQL initializes before app connects
- ✅ App has 3+ minutes to start properly
- ✅ Consistent builds and deployments
- ✅ Better error handling and monitoring

---

## 🚀 Quick Start

### Option 1: Automated Deployment (Recommended)

**On Windows (PowerShell):**
```powershell
.\deploy.ps1 -Namespace dev
```

**On Linux/Mac (Bash):**
```bash
bash deploy.sh dev
```

### Option 2: Manual Deployment

```bash
# 1. Create namespace
kubectl create namespace dev

# 2. Deploy secrets
kubectl apply -f k8s/secrets.yaml -n dev

# 3. Deploy MySQL
kubectl apply -f k8s/mysql.yaml -n dev
kubectl wait --for=condition=ready pod -l app=mysql -n dev --timeout=300s

# 4. Deploy app
kubectl apply -f k8s/deployment.yaml -n dev
kubectl apply -f k8s/service.yaml -n dev

# 5. (Optional) Deploy ingress
kubectl apply -f k8s/ingress.yaml -n dev

# 6. Wait for rollout
kubectl rollout status deployment/scm-app -n dev --timeout=600s
```

---

## 📁 File Structure

```
SCMProject/
├── k8s/
│   ├── namespace.yaml        # ✨ NEW - Namespace definitions
│   ├── secrets.yaml          # Sensitive data (DB credentials)
│   ├── mysql.yaml            # ✅ FIXED - Added health probes
│   ├── deployment.yaml       # ✅ FIXED - Added startup probe
│   ├── service.yaml          # Service configuration
│   └── ingress.yaml          # ✅ FIXED - Added ingressClassName
├── Dockerfile                # ✅ FIXED - Spring profile corrected
├── .github/workflows/
│   ├── ci.yml               # ✅ FIXED - Consolidated workflow
│   ├── deploy.yml           # ✅ FIXED - Improved sequencing
│   └── maven.yml            # ⚠️ DEPRECATED - Use ci.yml
├── src/main/resources/
│   └── application.properties # ✅ FIXED - Elasticsearch disabled
├── deploy.sh                # ✨ NEW - Linux/Mac deployment script
├── deploy.ps1               # ✨ NEW - Windows deployment script
├── verify-deployment.sh     # ✨ NEW - Linux/Mac verification
├── verify-deployment.ps1    # ✨ NEW - Windows verification
├── KUBERNETES_FIXES.md      # ✨ NEW - Detailed documentation
└── DEPLOYMENT_QUICK_FIX.md  # ✨ NEW - Quick reference
```

---

## 🔍 Verification

### Step 1: Verify Files Were Modified Correctly

```bash
# Check Dockerfile
grep "SPRING_PROFILES_ACTIVE" Dockerfile

# Check application.properties
grep "spring.autoconfigure.exclude" src/main/resources/application.properties

# Check K8s deployment
grep "startupProbe:" k8s/deployment.yaml
```

Expected output: All should show the fixes described in DEPLOYMENT_QUICK_FIX.md

### Step 2: Run Verification Script

**Windows PowerShell:**
```powershell
.\verify-deployment.ps1 -Namespace dev
```

**Linux/Mac Bash:**
```bash
bash verify-deployment.sh dev
```

### Step 3: Check Pod Status

```bash
# All pods should be Running
kubectl get pods -n dev

# App should be fully ready
kubectl get deployment scm-app -n dev -o wide

# MySQL should be healthy
kubectl logs deployment/mysql -n dev
```

---

## 📊 Deployment Timeline

| Phase | Time | Action |
|-------|------|--------|
| 0s | Immediate | Pod starts, MySQL container initializes |
| 10-30s | Startup | MySQL database becomes ready |
| 30-60s | Startup | App container starts, Spring Boot initializes |
| 60-120s | Initialization | App loads configuration, connects to DB |
| 120s+ | Readiness | Readiness probe starts checking |
| 150s+ | Liveness | Liveness probe starts checking |
| ~180s | **Complete** | **Pod fully ready** ✅ |

**Total startup time: ~3 minutes**

---

## 🔧 Configuration Details

### Spring Boot Profiles

```
Profile  | Location | Used By | Database | Purpose
---------|----------|---------|----------|----------
default  | K8s      | K8s     | mysql:3306 | Kubernetes
local    | Local    | Docker  | localhost | Development
prod     | Production | Railway | Railway | Production
```

### Database Configuration

```properties
# K8s Environment
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/SEC
SPRING_DATASOURCE_USERNAME=scmuser (from secrets)
SPRING_DATASOURCE_PASSWORD=scmpassword (from secrets)
SPRING_AI_OPENAI_API_KEY=groq-api-key (from secrets)
```

### Health Endpoints

```
GET /actuator/health              # Overall health
GET /actuator/health/liveness     # Liveness check
GET /actuator/health/readiness    # Readiness check
```

---

## 🐛 Troubleshooting

### Issue: Pod CrashLoopBackOff

**Symptoms:**
```
Pod restarting repeatedly with exit code 255
```

**Solutions:**
```bash
# 1. Check previous logs
kubectl logs <pod-name> -n dev --previous

# 2. Check pod description
kubectl describe pod <pod-name> -n dev

# 3. Check MySQL is running
kubectl logs deployment/mysql -n dev

# 4. Wait longer and try again
kubectl delete pod <pod-name> -n dev
```

### Issue: Connection Refused on Port 8080

**Symptoms:**
```
Readiness probe failed: Get "http://10.244.0.12:8080/actuator/health/readiness": 
dial tcp 10.244.0.12:8080: connect: connection refused
```

**Solutions:**
```bash
# 1. App is still starting - this is normal during first 2 minutes
# 2. Check app logs
kubectl logs -f deployment/scm-app -n dev

# 3. Verify MySQL is ready
kubectl exec -it <mysql-pod> -n dev -- mysql -u root -proot -e "SELECT 1"

# 4. Check environment variables
kubectl exec <app-pod> -n dev -- env | grep SPRING
```

### Issue: MySQL Pod Not Starting

**Symptoms:**
```
MySQL pod stays in Pending or CrashLoopBackOff
```

**Solutions:**
```bash
# 1. Check MySQL logs
kubectl logs deployment/mysql -n dev

# 2. Check resources available
kubectl top nodes
kubectl describe node

# 3. Check resource requests in k8s/mysql.yaml
kubectl describe pod <mysql-pod> -n dev

# 4. Reduce resource requests if needed
# Edit k8s/mysql.yaml and reduce CPU/memory limits
```

### Issue: Secrets Not Found

**Symptoms:**
```
Error creating mysql: secrets "scm-secrets" not found
```

**Solutions:**
```bash
# 1. Check if secret exists
kubectl get secrets -n dev

# 2. Apply secrets
kubectl apply -f k8s/secrets.yaml -n dev

# 3. Verify secret contents
kubectl get secret scm-secrets -n dev -o yaml
```

---

## 🔄 Deployment Workflows

### CI Pipeline (GitHub Actions)

**Trigger:** Push to `main` or `dev` branch

**Steps:**
1. Checkout code
2. Set up JDK 17
3. Start MySQL service
4. Build and test with Maven
5. SonarCloud analysis (dev only)
6. Login to Docker Hub
7. Build and push Docker image

**Result:** Docker image pushed to `arjun368/scm-app:latest` and `arjun368/scm-app:<sha>`

### Deploy Pipeline (GitHub Actions)

**Trigger:** Manual workflow dispatch OR automatically on merge

**Steps:**
1. Checkout code
2. Setup kubectl
3. Configure kubeconfig
4. Deploy to namespace:
   - Create namespace
   - Apply secrets
   - Deploy MySQL (wait for readiness)
   - Deploy app (wait for rollout)
   - Deploy service
   - Deploy ingress

**Result:** Application running in K8s cluster

---

## 📝 Important Notes

### Security ⚠️

The `k8s/secrets.yaml` file contains:
- Database username and password
- Groq API key

**Never commit secrets to Git!**

**Best practices:**
```bash
# Option 1: Use GitHub Secrets (Recommended for CI/CD)
# Store values in GitHub repo settings
# Reference in workflows: ${{ secrets.GROQ_API_KEY }}

# Option 2: Use Sealed Secrets (Recommended for Production)
kubeseal -f k8s/secrets.yaml -w sealed-secrets.yaml

# Option 3: Use External Secrets Operator
# Reference secrets from HashiCorp Vault, AWS Secrets Manager, etc.
```

### Resource Limits

Current configuration:
```yaml
App:
  Requests: 200m CPU, 256Mi Memory
  Limits: 500m CPU, 512Mi Memory

MySQL:
  Requests: 500m CPU, 1Gi Memory
  Limits: 1000m CPU, 2Gi Memory
```

**Adjust if needed based on actual usage:**
```bash
# Monitor resource usage
kubectl top pods -n dev
kubectl top nodes
```

### Monitoring

Recommended to set up:
- Prometheus for metrics collection
- Grafana for visualization
- AlertManager for notifications
- Loki for log aggregation

---

## 📚 Additional Resources

### Scripts Provided

| Script | Purpose | Platform |
|--------|---------|----------|
| `deploy.sh` | Full automated deployment | Linux/Mac |
| `deploy.ps1` | Full automated deployment | Windows |
| `verify-deployment.sh` | Verify deployment health | Linux/Mac |
| `verify-deployment.ps1` | Verify deployment health | Windows |

### Documentation Provided

| Document | Purpose |
|----------|---------|
| `DEPLOYMENT_QUICK_FIX.md` | Quick reference of all fixes |
| `KUBERNETES_FIXES.md` | Detailed technical documentation |
| `README.md` | This file - Complete guide |

---

## 🎓 Learning Resources

### Kubernetes Concepts

- **Deployment**: Manages pod replicas
- **Service**: Exposes pods network access
- **ConfigMap**: Configuration data
- **Secret**: Sensitive data (credentials)
- **Ingress**: External HTTP(S) access
- **Health Probes**:
  - **Startup Probe**: Checks if app started
  - **Readiness Probe**: Checks if app ready for traffic
  - **Liveness Probe**: Checks if app alive, restarts if not

### Spring Boot Actuator

```
/actuator/health                    # Overall health
/actuator/health/liveness           # Is app alive?
/actuator/health/readiness          # Is app ready for traffic?
/actuator/health/db                 # Database health
/actuator/metrics                   # Prometheus metrics
```

---

## ✅ Pre-Deployment Checklist

- [ ] All code changes committed and pushed
- [ ] GitHub Actions CI pipeline completed successfully
- [ ] Docker image built and pushed
- [ ] Kubernetes cluster is accessible
- [ ] Kubeconfig properly configured
- [ ] Secrets file updated with correct credentials
- [ ] Resource quotas available in cluster
- [ ] Ingress controller installed (if using ingress)
- [ ] Domain name configured (if using ingress)

---

## 🚀 Deployment Checklist

- [ ] Run deployment script (automated or manual)
- [ ] Verify all pods are running
- [ ] Check application logs for errors
- [ ] Test health endpoints
- [ ] Verify database connectivity
- [ ] Test application functionality
- [ ] Monitor resource usage
- [ ] Check ingress routing (if applicable)

---

## 📞 Support & Debugging

### Get Detailed Logs

```bash
# Check what went wrong
kubectl describe pod <pod-name> -n dev

# See last 100 lines of app logs
kubectl logs deployment/scm-app -n dev --tail=100

# Stream logs in real-time
kubectl logs -f deployment/scm-app -n dev

# Get logs from previous crashed container
kubectl logs <pod-name> -n dev --previous

# Get logs with timestamps
kubectl logs deployment/scm-app -n dev --timestamps=true
```

### Debug Pod

```bash
# Execute shell in pod
kubectl exec -it <pod-name> -n dev -- /bin/sh

# Run specific command
kubectl exec <pod-name> -n dev -- env

# Check file permissions
kubectl exec <pod-name> -n dev -- ls -la /app
```

### Check Cluster Resources

```bash
# Node resources
kubectl top nodes
kubectl describe nodes

# Pod resources
kubectl top pods -n dev

# Check for resource pressure
kubectl get nodes -o wide
```

---

## 📞 Additional Help

If you encounter issues:

1. **Check logs first**: `kubectl logs -f deployment/scm-app -n dev`
2. **Review fixes**: Read `KUBERNETES_FIXES.md`
3. **Run verification**: `./verify-deployment.ps1 -Namespace dev`
4. **Check events**: `kubectl get events -n dev --sort-by='.lastTimestamp'`
5. **Describe pod**: `kubectl describe pod <pod-name> -n dev`

---

## 📄 Quick Command Reference

```bash
# Deployment operations
kubectl apply -f k8s/deployment.yaml -n dev          # Deploy
kubectl delete deployment scm-app -n dev              # Delete
kubectl rollout restart deployment/scm-app -n dev     # Restart
kubectl rollout status deployment/scm-app -n dev      # Status

# Pod operations
kubectl get pods -n dev                               # List pods
kubectl describe pod <pod-name> -n dev                # Pod details
kubectl logs <pod-name> -n dev                        # View logs
kubectl exec -it <pod-name> -n dev -- /bin/bash      # Shell access

# Service operations
kubectl get svc -n dev                                # List services
kubectl port-forward svc/scm-app-service 8080:80 -n dev  # Port forward

# Debugging
kubectl get events -n dev                             # Cluster events
kubectl top pods -n dev                               # Resource usage
kubectl describe node <node-name>                     # Node details
```

---

**Last Updated**: April 18, 2026  
**Status**: ✅ All issues resolved - Ready for production deployment  
**Maintainer**: SCM Project Team

