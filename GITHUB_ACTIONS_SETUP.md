# GitHub Actions Setup Guide

## Step-by-Step: Enable GitHub Actions

### 1. Enable Workflows

**Path**: Settings → Actions → General

- ✅ Check: "Allow all actions and reusable workflows"
- ✅ Check: "Allow GitHub Actions to create and approve pull requests"

### 2. Add Required Secrets

**Path**: Settings → Secrets and variables → Actions

#### Required Secrets:

**DOCKERHUB_USERNAME**
- Your Docker Hub account username
- Example: `arjun368`

**DOCKERHUB_PASSWORD**
- Your Docker Hub password or access token
- Go to: https://hub.docker.com/settings/security
- Create access token if needed

**KUBE_CONFIG**
- Your Kubernetes config file
- File location: `~/.kube/config` (on your machine)
- Or get from your cluster admin

How to get KUBE_CONFIG:
```bash
# Show content of kube config
cat ~/.kube/config

# Copy entire output and paste as secret value
```

**GROQ_API_KEY** (Optional)
- Your Groq API key
- Already configured in k8s/secrets.yaml

### 3. Verify Workflow Files

Check that these files exist:
```
✅ .github/workflows/ci.yml
✅ .github/workflows/deploy.yml
✅ .github/workflows/maven.yml
✅ .github/workflows/sonar.yml
```

All files should be in main branch.

### 4. Trigger Workflow

After setup, workflows trigger on:
- ✅ Push to any branch
- ✅ Pull request creation
- ✅ Manual trigger (via Actions tab)

### 5. Manual Trigger (If needed)

**Path**: Actions → Select Workflow → Run workflow

Click **Run workflow** to manually trigger.

---

## Expected Workflow Steps

### CI Workflow (ci.yml)
1. Checkout code
2. Setup Java
3. Run Maven build
4. Run tests
5. Build Docker image
6. Push to Docker Hub

### Deploy Workflow (deploy.yml)
1. Checkout code
2. Setup kubectl
3. Deploy to dev namespace (if pushing to dev branch)
4. Deploy to prod namespace (if pushing to main branch)

---

## Troubleshooting

### Workflows not running?

Check:
- [ ] Secrets are set correctly
- [ ] Branch is not protected
- [ ] Workflows are enabled
- [ ] Workflow YAML syntax is correct

### Build failing?

Check:
- [ ] Maven installed
- [ ] Java version compatible
- [ ] Dependencies available
- [ ] Tests passing

### Deploy failing?

Check:
- [ ] Kubernetes cluster accessible
- [ ] Credentials correct
- [ ] Namespaces exist
- [ ] Resources available

---

## Useful Commands

### Check if workflows are syntactically correct:
```bash
# Validate workflow
cat .github/workflows/ci.yml  # Should be valid YAML
```

### Re-run workflow:
On GitHub UI:
1. Go to Actions
2. Select workflow run
3. Click "Re-run failed jobs"

### View workflow logs:
1. Go to Actions tab
2. Click on workflow run
3. Click on job
4. View logs

---

## Next Steps

1. ✅ Enable workflows in Settings
2. ✅ Add Docker Hub secrets
3. ✅ Add Kubernetes secrets (optional)
4. ✅ Push new commit to PR
5. ✅ Watch Actions tab for workflow run
6. ✅ Check logs if any failures


