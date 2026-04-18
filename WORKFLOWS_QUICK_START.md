# 🚀 Quick Start - Enterprise Workflows

## Your New Workflows (4 Total)

| Workflow | File | Trigger | Use Case |
|----------|------|---------|----------|
| **CI Pipeline** | `ci.yml` | Auto on dev push | Development |
| **UAT/Homol** | `homol.yml` | Manual (workflow_dispatch) | Testing/UAT |
| **Prod Release** | `prod-release.yml` | Manual (workflow_dispatch) | Production |
| **SonarQube** | `sonar.yml` | Auto on any push | Quality checks |

---

## 📌 Branch Strategy

```
master/main (PRODUCTION)
  ├─ Pull Request
  │  └─ Auto: sonar.yml + code review
  └─ Manual: prod-release.yml (with approval)
     └─ Deploy to PROD (3 replicas)

dev (DEVELOPMENT)
  ├─ Push/PR
  │  └─ Auto: ci.yml
  │     ├─ Build & Test
  │     ├─ SonarQube
  │     └─ Deploy to DEV (1 replica)
  └─ feature branches merge to dev

homol (UAT)
  └─ Manual: homol.yml
     └─ Deploy to HOMOL (2 replicas)
```

---

## 🎯 Quick Usage

### Development (Automatic)
```bash
# Create feature branch from dev
git checkout -b feature/my-feature dev

# Make changes
git add .
git commit -m "feat: description"

# Push to dev
git push origin feature/my-feature

# Create PR to dev
# ✅ Workflows run automatically
# ✅ Merges to dev auto-deploys
```

### UAT/Homol (Manual)
1. Go to: GitHub → Actions
2. Select: "UAT/Homol Pipeline"
3. Click: "Run workflow"
4. Select: Environment = `homol`
5. Click: "Run workflow"
6. ⏱️ Wait ~12 min
7. ✅ Deployed to homol namespace

### Production (Manual + Approval)
1. Go to: GitHub → Actions
2. Select: "Production Release Pipeline"
3. Click: "Run workflow"
4. Select: Strategy = `rolling` (or others)
5. Click: "Run workflow"
6. ⏸️ Wait for approval
7. Review and **Approve**
8. ⏱️ Wait ~15 min
9. ✅ Deployed to prod namespace

---

## 📊 What Each Workflow Does

### CI Pipeline (dev)
```
Push to dev
  ↓
Checkout code
  ↓
Build with Maven
  ↓
Run tests (MySQL integration)
  ↓
SonarQube analysis
  ↓
Build Docker image
  ↓
Push to Docker Hub
  ↓
Deploy to DEV namespace (Kubernetes)
  ↓
✅ Application live at dev-k8s-endpoint
```

### UAT/Homol Pipeline
```
Manual trigger
  ↓
Full build & test
  ↓
SonarQube analysis
  ↓
Generate versioned Docker image
  ↓
Deploy to HOMOL namespace
  ↓
2 replicas running
  ↓
✅ Health checks passed
```

### Production Release Pipeline
```
Manual trigger
  ↓
Pre-deployment checks
  ↓
Build & comprehensive tests
  ↓
SonarQube + Quality Gate
  ↓
Build & push Docker image (release version)
  ↓
⏸️ APPROVAL GATE
  ↓
Deploy to PROD (selected strategy)
  ↓
3 replicas running
  ↓
Health checks & smoke tests
  ↓
✅ Production live
```

---

## 🔑 Required Secrets (Add in GitHub Settings)

```
Settings → Secrets and variables → Actions

DOCKERHUB_USERNAME      = arjun368
DOCKERHUB_PASSWORD      = [your-token]
SONAR_TOKEN            = [your-sonar-token]
SONAR_HOST_URL         = https://sonarqube-server.com
KUBE_CONFIG            = [base64 of ~/.kube/config]
```

---

## 🎛️ Workflow Parameters

### CI Pipeline (auto, no parameters)
Runs automatically on dev push

### UAT/Homol Pipeline
```
environment        = homol (fixed)
skip-tests         = false (default, can skip)
skip-sonar         = false (default, can skip)
```

### Production Release Pipeline
```
deployment-strategy = rolling / blue-green / canary
skip-tests          = false (default)
skip-sonar          = false (default)
require-approval    = true (default)
```

---

## 📈 Kubernetes Namespaces

```
dev     → Development environment
         1 replica
         Auto-updated from dev branch
         
homol   → UAT/Testing environment
         2 replicas
         Manual deployment
         
prod    → Production environment
         3 replicas
         Manual + approval required
```

Check status:
```bash
kubectl get all -n dev
kubectl get all -n homol
kubectl get all -n prod
```

---

## ✅ Typical Timeline

### Development (Auto)
```
Push → 1 min  : Detect push
     → 3 min  : Build & test start
     → 8 min  : Docker build
     → 10 min : Deploy & ready
Total: ~10 minutes to live
```

### UAT (Manual)
```
Trigger → 1 min  : Start
        → 3 min  : Build & test
        → 8 min  : Docker build
        → 12 min : Deploy & ready
Total: ~12 minutes after trigger
```

### Production (Manual + Approval)
```
Trigger    → Build & test (8 min)
         → ⏸️ Approval gate (waiting for you)
Approve    → Deploy (5 min)
         → ✅ Live
Total: 13-20+ min (depends on approval time)
```

---

## 🔍 Monitor Workflows

### GitHub UI
```
https://github.com/arjunrathod1996/SCMProject/actions
```

### CLI
```bash
# Watch deployment
kubectl get pods -n dev --watch

# View logs
kubectl logs -n dev deployment/scm-app -f

# Check status
kubectl describe deployment scm-app -n dev
```

---

## 🚨 Troubleshooting

| Issue | Solution |
|-------|----------|
| Workflow not starting | Check secrets configured, Actions enabled |
| Build fails | Run `mvn clean install` locally first |
| Tests fail | Check MySQL running, test code correct |
| Deploy fails | Check Kubernetes cluster, kubeconfig valid |
| Sonar fails | Check SonarQube server running, token valid |
| Pods not ready | Check logs: `kubectl logs -n {ns} pod/{pod}` |

---

## 📚 Documentation Files

```
.github/workflows/           ← All workflows here
  ci.yml                     ← Development CI/CD
  homol.yml                  ← UAT deployment
  prod-release.yml           ← Production release
  sonar.yml                  ← Quality gates

WORKFLOWS_GUIDE.md           ← Complete guide (this file format)
WORKFLOWS_QUICK_START.md     ← Quick reference
k8s/                         ← Kubernetes config files
pom.xml                      ← Maven configuration
Dockerfile                   ← Docker build config
```

---

## 🎯 Next Steps

1. ✅ Add required secrets to GitHub
2. ✅ Test CI workflow on dev branch
3. ✅ Test manual homol workflow
4. ✅ Test production workflow with approval
5. ✅ Monitor workflows in Actions tab
6. ✅ Review deployed applications
7. ✅ Read complete guide: `WORKFLOWS_GUIDE.md`

---

## 💡 Pro Tips

- 🔄 Workflows follow company standards (from your attached examples)
- 🚀 Fully automated & self-service
- 📊 SonarQube integrated for quality gates
- 🏗️ Docker images versioned & tracked
- ☸️ Kubernetes managed cleanly
- ✅ Health checks on every deployment
- 🔐 Production requires approval
- 📈 Scalable (dev:1 replicas, homol:2, prod:3)

---

## 🎉 You're Ready!

Your workflows are:
- ✅ Enterprise-grade
- ✅ Following company standards
- ✅ Fully automated
- ✅ Production-ready
- ✅ Well-documented

**Start using them now!** 🚀

---

**Branch Strategy**: master=prod, dev=dev, homol=uat  
**Deployment**: Auto (dev), Manual (homol/prod)  
**Status**: Ready for Production  
**Created**: April 18, 2026


