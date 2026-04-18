# 🚀 Enterprise-Grade CI/CD Workflows - Complete Guide

## Overview

Your SCM Project now has professional, enterprise-level CI/CD workflows following industry best practices and your company's standards.

---

## 📋 Workflow Summary

### 1. **CI Pipeline** (`ci.yml`)
**Trigger**: Automatic on push to `dev` branch or PR to `dev`

**Features**:
- ✅ Maven build & compile
- ✅ Automated tests
- ✅ SonarQube analysis
- ✅ Docker image build & push
- ✅ Auto-deploy to DEV namespace

**Status Checks**:
- Build compilation
- Unit tests
- Code quality gates

**Duration**: ~8-10 minutes

---

### 2. **UAT/Homol Workflow** (`homol.yml`)
**Trigger**: Manual workflow dispatch (workflow_dispatch)

**Features**:
- ✅ Full build & test
- ✅ SonarQube analysis
- ✅ Docker image build with versioning
- ✅ Deploy to Homol namespace
- ✅ Health checks & verification

**Parameters**:
- Environment selection (homol)
- Skip tests (optional)
- Skip SonarQube (optional)

**Status Checks**:
- Build passing
- Tests passing
- Code quality passing
- Pod readiness

**Duration**: ~10-12 minutes

---

### 3. **Production Release Workflow** (`prod-release.yml`)
**Trigger**: Manual workflow dispatch on `master` branch

**Features**:
- ✅ Pre-deployment checks
- ✅ Build & comprehensive tests
- ✅ SonarQube quality gate
- ✅ Docker image build with release version
- ✅ Approval gate (optional)
- ✅ Blue-Green / Rolling / Canary deployment
- ✅ Production health checks
- ✅ Smoke tests

**Parameters**:
- Deployment strategy (rolling/blue-green/canary)
- Skip tests (optional)
- Skip SonarQube (optional)
- Require approval (default: true)

**Status Checks**:
- All pre-deployment checks
- Build passing
- Tests passing
- Quality gate passed
- Approval received (if enabled)
- Pod readiness
- Health checks

**Duration**: ~15-20 minutes (+ approval time)

---

### 4. **SonarQube Quality Gate** (`sonar.yml`)
**Trigger**: Push/PR to any branch

**Features**:
- ✅ Dedicated SonarQube analysis
- ✅ Quality gate check
- ✅ PR comments with results
- ✅ Code coverage tracking

**Duration**: ~5-7 minutes

---

## 🔄 Workflow Structure

```
Master/Main Branch (Production)
├── Pull Request
│   ├── Run: sonar.yml ✅
│   └── Comment with results
└── Manual Trigger: prod-release.yml
    ├── Pre-deployment checks
    ├── Build & Tests
    ├── SonarQube analysis
    ├── Approval gate (⏸️ if enabled)
    └── Deploy to PROD with selected strategy

Dev Branch (Development)
├── Push
│   └── Run: ci.yml ✅
│       ├── Build & test
│       ├── SonarQube
│       └── Auto-deploy to DEV
└── Pull Request
    ├── Run: ci.yml
    └── Comment with status

Homol Branch (UAT)
└── Manual Trigger: homol.yml ✅
    ├── Build & test
    ├── SonarQube
    └── Deploy to HOMOL
```

---

## 📊 Feature Comparison

| Feature | CI (Dev) | Homol | Prod |
|---------|----------|-------|------|
| **Auto Trigger** | ✅ Push | ❌ Manual | ❌ Manual |
| **Manual Trigger** | ✅ | ✅ | ✅ |
| **Build & Test** | ✅ | ✅ | ✅ |
| **SonarQube** | ✅ | ✅ | ✅ |
| **Docker Build** | ✅ | ✅ | ✅ |
| **Approval Gate** | ❌ | ❌ | ✅ Optional |
| **Health Checks** | ✅ | ✅ | ✅ |
| **Replicas** | 1 | 2 | 3 |
| **Deployment Strategy** | Rolling | Rolling | Selectable |

---

## 🚀 How to Use

### Development Flow (Dev Branch)

**Step 1**: Create feature branch from dev
```bash
git checkout -b feature/my-feature dev
```

**Step 2**: Make changes and commit
```bash
git add .
git commit -m "feat: Add new feature"
```

**Step 3**: Push to dev
```bash
git push origin feature/my-feature
```

**Step 4**: Create PR to dev

**Step 5**: Workflows run automatically
- sonar.yml (quality check)
- ci.yml (build, test, deploy)

**Step 6**: After approval, merge to dev
- Auto-deploys to DEV namespace

---

### UAT/Homol Flow

**Step 1**: Ensure code is in dev branch and tested

**Step 2**: Go to GitHub Actions
```
https://github.com/arjunrathod1996/SCMProject/actions
```

**Step 3**: Select "UAT/Homol Pipeline" workflow

**Step 4**: Click "Run workflow"

**Step 5**: Select options:
- Environment: `homol`
- Skip tests: `false` (recommended)
- Skip SonarQube: `false` (recommended)

**Step 6**: Click "Run workflow"

**Step 7**: Monitor deployment (10-12 min)

**Result**: Deployed to `homol` namespace with 2 replicas

---

### Production Release Flow

**Step 1**: Ensure all tests pass on dev

**Step 2**: Merge to master/main
```bash
git checkout master
git merge dev
git push origin master
```

**Step 3**: Go to GitHub Actions
```
https://github.com/arjunrathod1996/SCMProject/actions
```

**Step 4**: Select "Production Release Pipeline" workflow

**Step 5**: Click "Run workflow"

**Step 6**: Select options:
- Strategy: `rolling` / `blue-green` / `canary`
- Skip tests: `false` (recommended)
- Skip SonarQube: `false` (recommended)
- Require approval: `true` (recommended)

**Step 7**: Workflow starts

**Step 8**: Review details and approve when ready
- Click environment approval link
- Review deployment plan
- Click "Approve and deploy"

**Step 9**: Deployment continues (5-10 min)

**Result**: Deployed to `prod` namespace with 3 replicas

---

## 🔐 Required Secrets & Variables

### GitHub Secrets (Settings → Secrets and variables → Actions)

```
DOCKERHUB_USERNAME          # Your Docker Hub username
DOCKERHUB_PASSWORD          # Your Docker Hub password/token
SONAR_TOKEN                 # SonarQube authentication token
SONAR_HOST_URL              # SonarQube server URL
KUBE_CONFIG                 # Kubernetes config (base64 encoded)
```

### GitHub Variables (Settings → Secrets and variables → Variables)

```
Optional but recommended:
SONAR_PROJECT_KEY           # Default: scm-project-{branch}
SONAR_PROJECT_NAME          # Default: SCM-App-{branch}
```

---

## 📈 Deployment Strategies

### Rolling Update (Default)
- Gradually replaces old pods with new ones
- No downtime
- Slower rollout (~5 min)
- Easy rollback

**When to use**: Regular updates, testing

### Blue-Green Deployment
- Runs two complete environments
- Switches traffic at once
- Zero downtime
- Quick rollback

**When to use**: Critical updates, major releases

### Canary Deployment
- Deploys to 5% of traffic first
- Monitors for errors
- Gradually increases traffic
- Automatic rollback on failure

**When to use**: High-risk deployments, new features

---

## 🔍 Monitoring & Logs

### View Workflow Run
```
https://github.com/arjunrathod1996/SCMProject/actions
```

### View Kubernetes Deployment
```bash
# Dev
kubectl get deployments -n dev
kubectl get pods -n dev
kubectl logs -n dev deployment/scm-app -f

# Homol
kubectl get deployments -n homol
kubectl get pods -n homol
kubectl logs -n homol deployment/scm-app -f

# Prod
kubectl get deployments -n prod
kubectl get pods -n prod
kubectl logs -n prod deployment/scm-app -f
```

### View Rollout Status
```bash
kubectl rollout status deployment/scm-app -n {namespace}
```

### Rollback if needed
```bash
kubectl rollout undo deployment/scm-app -n {namespace}
```

---

## ⚙️ Configuration Files

Your workflows use these configuration files:

```
.github/workflows/
├── ci.yml                  # Development CI/CD
├── homol.yml              # UAT/Homol deployment
├── prod-release.yml       # Production release
└── sonar.yml              # SonarQube quality gate

k8s/
├── namespace.yaml         # Kubernetes namespaces
├── secrets.yaml           # Database & API credentials
├── deployment.yaml        # Application deployment
├── mysql.yaml            # MySQL database
└── service.yaml          # Service configuration
```

---

## 🛠️ Customization Guide

### Change Docker Registry
Edit workflows, find `REGISTRY: docker.io` and update

### Change Kubernetes Namespace
Edit `k8s/namespace.yaml` and update namespace names

### Change Replica Counts
Edit workflows, search for `replicas:` and update

### Change Health Check Endpoints
Edit `deployment.yaml`, update probe paths:
```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness  # Change this
```

### Change SonarQube Settings
Edit `sonar.yml`, update these properties:
```yaml
-Dsonar.projectKey=scm-project-prod
-Dsonar.projectName=SCM-App
```

---

## ⚠️ Best Practices

✅ **DO**:
- Always run on dev first
- Use homol for UAT testing
- Require approval for production
- Monitor logs after deployment
- Use meaningful commit messages
- Test locally before pushing

❌ **DON'T**:
- Deploy directly to prod without testing
- Skip tests in production
- Deploy during business hours (if possible)
- Ignore SonarQube warnings
- Commit secrets to repository

---

## 🔧 Troubleshooting

### Workflow not triggering
- Check branch name matches trigger
- Verify secrets are configured
- Check GitHub Actions is enabled

### Build fails
- Check Maven cache
- Verify pom.xml syntax
- Check Java version compatibility

### Tests fail
- Check database connection
- Review test logs
- Run tests locally first

### Deployment fails
- Check Kubernetes cluster accessible
- Verify kubeconfig valid
- Check namespace exists
- Review pod logs

### SonarQube fails
- Check SonarQube server running
- Verify token valid
- Check project key correct

---

## 📞 Support & Reference

### Documentation
- [GitHub Actions](https://docs.github.com/en/actions)
- [Kubernetes](https://kubernetes.io/docs/)
- [SonarQube](https://docs.sonarqube.org/)
- [Maven](https://maven.apache.org/)

### Your Project Files
- Workflows: `.github/workflows/`
- K8s Config: `k8s/`
- Application: `src/`
- Build Config: `pom.xml`

---

## ✨ Summary

Your project now has:
- ✅ Automatic CI/CD for development
- ✅ Manual UAT deployment
- ✅ Production release with approval gate
- ✅ SonarQube quality gates
- ✅ Docker image management
- ✅ Kubernetes deployment automation
- ✅ Health checks & monitoring
- ✅ Multiple deployment strategies

**Status**: 🚀 Production-Ready

---

**Created**: April 18, 2026  
**Framework**: Enterprise-grade CI/CD  
**Standards**: Following company best practices  
**Team**: Fully automated, self-service deployment


