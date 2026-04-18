# SCM Project - Kubernetes Deployment & CI/CD Fixes

## Issues Fixed

### 1. **Dockerfile Spring Profile Mismatch**
- **Problem**: Dockerfile was set to `SPRING_PROFILES_ACTIVE=prod` but K8s deployment uses `default`
- **Fix**: Changed to `default` profile to match K8s environment variable
- **Location**: `Dockerfile` line 71

### 2. **Elasticsearch Auto-Configuration Error**
- **Problem**: App tries to auto-configure Elasticsearch which doesn't exist in K8s
- **Fix**: Disabled auto-configuration in `application.properties`
- **Location**: `src/main/resources/application.properties` line 35
- **Change**: Added `spring.autoconfigure.exclude=...ElasticsearchRestClientAutoConfiguration`

### 3. **Kubernetes Health Probes Configuration**
- **Problem**: Liveness and readiness probes were too aggressive, causing pod restarts
- **Problems**:
  - Initial delay too short (90s for readiness)
  - High failure threshold (3 consecutive failures)
  - Conflicting probe timings
- **Fixes**:
  - **Added startup probe** (30 attempts × 5 sec = 150 sec timeout) for initial startup
  - **Increased readiness initialDelaySeconds** to 120s
  - **Increased liveness initialDelaySeconds** to 180s
  - **Reduced readiness period** to 5s for faster detection
- **Location**: `k8s/deployment.yaml`

### 4. **MySQL Database Connectivity Issues**
- **Problem**: App was failing before MySQL was ready
- **Fixes**:
  - Added liveness and readiness probes to MySQL pod
  - Added delay before deploying app (wait for MySQL)
  - Initial delay: 30s, readiness: 10s
- **Location**: `k8s/mysql.yaml`

### 5. **GitHub Workflows Consolidation**
- **Problem**: Two conflicting CI workflows (ci.yml and maven.yml)
- **Fix**: 
  - Consolidated ci.yml with proper MySQL service and database credentials
  - Updated maven.yml to reference ci.yml as primary
  - Added proper MySQL wait logic
- **Location**: `.github/workflows/ci.yml`, `.github/workflows/maven.yml`

### 6. **Kubernetes Deployment Sequence**
- **Problem**: Resources deployed in wrong order without waiting for dependencies
- **Fixes**:
  - Deploy secrets first
  - Deploy and wait for MySQL readiness (300s timeout)
  - Deploy app with health probes
  - Wait for rollout completion (600s timeout)
- **Location**: `.github/workflows/deploy.yml`

### 7. **Ingress Configuration**
- **Problem**: Missing ingressClassName and timeout configurations
- **Fixes**:
  - Added `ingressClassName: nginx`
  - Added proxy timeout annotations (600s for long-running operations)
  - Better connection timeout handling
- **Location**: `k8s/ingress.yaml`

---

## Database Configuration

### K8s Secrets (`k8s/secrets.yaml`)
```yaml
db-username: scmuser
db-password: scmpassword
groq-api-key: [YOUR_GROQ_API_KEY_HERE]  # ⚠️ KEEP THIS SECRET - Do not commit real keys
```

### Connection String (K8s)
```
jdbc:mysql://mysql:3306/SEC
```

### MySQL Service Name
- Service name: `mysql`
- Port: `3306`
- Database: `SEC`

---

## Deployment Sequence

### 1. Create Namespaces
```bash
kubectl apply -f k8s/namespace.yaml
```

### 2. Deploy to DEV
```bash
kubectl apply -f k8s/secrets.yaml -n dev
kubectl apply -f k8s/mysql.yaml -n dev
# Wait for MySQL pod to be ready
kubectl wait --for=condition=ready pod -l app=mysql -n dev --timeout=300s
sleep 10
kubectl apply -f k8s/deployment.yaml -n dev
kubectl apply -f k8s/service.yaml -n dev
```

### 3. Monitor Logs
```bash
# Watch deployment progress
kubectl logs -f deployment/scm-app -n dev

# Check pod status
kubectl describe pod <pod-name> -n dev

# Check MySQL status
kubectl logs -f deployment/mysql -n dev
```

---

## Health Check Endpoints

The application exposes the following health endpoints:

```
GET http://scm-app-service:8080/actuator/health
GET http://scm-app-service:8080/actuator/health/liveness
GET http://scm-app-service:8080/actuator/health/readiness
```

Required in `application.properties`:
```properties
management.endpoints.web.exposure.include=health
management.endpoint.health.probes.enabled=true
management.health.livenessstate.enabled=true
management.health.readinessstate.enabled=true
```

---

## Expected Timings

### App Startup
- **Startup Probe**: 0s initial + (30 failures × 5s) = ~150s max
- **Readiness**: After 120s initial delay
- **Liveness**: After 180s initial delay
- **Total startup time**: ~2.5-3 minutes

### MySQL Startup
- **Readiness Probe**: After 10s initial delay
- **Expected startup**: ~10-15 seconds

---

## Common Issues & Solutions

### Issue 1: "Connection refused" on port 8080
**Causes**:
- App hasn't started yet (wait longer)
- App crashed during startup
- Elasticsearch connection failure (fixed in this update)

**Solutions**:
```bash
# Check app logs
kubectl logs <pod-name> -n dev --previous

# Check if MySQL is running
kubectl get pods -n dev
kubectl describe pod mysql-xxx -n dev

# Check events
kubectl describe pod <pod-name> -n dev
```

### Issue 2: MySQL pod not becoming ready
**Causes**:
- Database initialization too slow
- Insufficient resources
- Secret not mounted correctly

**Solutions**:
```bash
# Check MySQL logs
kubectl logs -f deployment/mysql -n dev

# Check if secret exists
kubectl get secrets -n dev
kubectl describe secret scm-secrets -n dev

# Increase resource limits if needed (edit k8s/mysql.yaml)
```

### Issue 3: App works locally but fails in K8s
**Common reasons**:
- Different database credentials
- Elasticsearch enabled in default profile
- Spring profiles not matching

**Check**:
```bash
# Verify environment variables in pod
kubectl exec <pod-name> -n dev -- env | grep SPRING
```

---

## Files Modified

1. ✅ `Dockerfile` - Fixed Spring profile
2. ✅ `src/main/resources/application.properties` - Disabled Elasticsearch autoconfiguration
3. ✅ `k8s/deployment.yaml` - Added startup probe, adjusted probe timings
4. ✅ `k8s/mysql.yaml` - Added health probes
5. ✅ `k8s/ingress.yaml` - Added ingressClassName and timeouts
6. ✅ `.github/workflows/ci.yml` - Consolidated workflows
7. ✅ `.github/workflows/deploy.yml` - Improved deployment sequence
8. ✅ `.github/workflows/maven.yml` - Deprecated, use ci.yml
9. ✅ `k8s/namespace.yaml` - Created new file for namespace management

---

## Next Steps

### Immediate Actions
1. Push these changes to your repository
2. GitHub CI/CD will automatically build and push Docker image
3. Run manual deployment workflow or merge to main/dev

### Verification
```bash
# Check deployment status
kubectl get deployments -n dev
kubectl get pods -n dev

# Check service endpoints
kubectl get svc -n dev
kubectl get ingress -n dev

# Port forward to test
kubectl port-forward svc/scm-app-service 8080:80 -n dev
# Then visit http://localhost:8080
```

### Production Readiness
- [ ] Update `k8s/secrets.yaml` with production credentials
- [ ] Update `k8s/ingress.yaml` with production domain
- [ ] Review resource limits in `k8s/deployment.yaml` and `k8s/mysql.yaml`
- [ ] Set up monitoring/alerting
- [ ] Test failover scenarios

---

## Command Reference

### View All Resources
```bash
kubectl get all -n dev
```

### Stream Logs
```bash
kubectl logs -f deployment/scm-app -n dev
kubectl logs -f deployment/mysql -n dev
```

### Restart Deployment
```bash
kubectl rollout restart deployment/scm-app -n dev
kubectl rollout restart deployment/mysql -n dev
```

### Check Events
```bash
kubectl get events -n dev --sort-by='.lastTimestamp'
```

### Exec into Pod
```bash
kubectl exec -it <pod-name> -n dev -- /bin/bash
```

### Delete and Redeploy
```bash
kubectl delete -f k8s/ -n dev
kubectl apply -f k8s/namespace.yaml
# Then apply all YAML files again
```

---

## Security Notes

⚠️ **Warning**: The Groq API key in `k8s/secrets.yaml` is exposed
- Rotate this key immediately
- Use GitHub Secrets for management
- Consider using sealed-secrets or external-secrets operator

---

Generated: April 18, 2026

