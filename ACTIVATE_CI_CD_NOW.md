# ✅ GitHub Actions Activation Guide

## Your PR #30 - Activate CI/CD Workflows

**Date**: April 18, 2026  
**Status**: Ready to Activate

---

## 🚀 **Quick Setup (5 Minutes)**

### Step 1: Add Docker Hub Secrets (2 minutes)

**URL**: https://github.com/arjunrathod1996/SCMProject/settings/secrets/actions

Click **New repository secret** and add:

#### Secret 1: DOCKERHUB_USERNAME
```
Name: DOCKERHUB_USERNAME
Value: arjun368  # Your Docker Hub username
```

#### Secret 2: DOCKERHUB_PASSWORD  
```
Name: DOCKERHUB_PASSWORD
Value: [Your Docker Hub password or access token]
```

**How to get Docker Hub token:**
1. Go to: https://hub.docker.com/settings/security
2. Create **New Access Token**
3. Copy token and paste as secret value

#### Secret 3: KUBE_CONFIG (Optional for now)
```
Name: KUBE_CONFIG
Value: [Content of ~/.kube/config]
```

Only needed if you want auto-deploy from PR.

### Step 2: Enable Workflows (1 minute)

**URL**: https://github.com/arjunrathod1996/SCMProject/settings/actions

- ✅ Select: **"Allow all actions and reusable workflows"**
- ✅ Check: **"Allow GitHub Actions to create and approve pull requests"**
- Click: **Save**

### Step 3: Trigger Workflows (2 minutes)

**Option A: Push new commit** (Recommended)
```powershell
cd C:\Users\ratho\OneDrive\Documents\Git\SCMProject
echo "# CI/CD trigger" >> .github/workflows/README.md
git add .github/workflows/README.md
git commit -m "chore: Trigger GitHub Actions workflows"
git push origin feat/uodated_ci_cd
```

**Option B: Close and reopen PR**
1. Go to PR #30
2. Scroll to bottom
3. Click **Close pull request**
4. Reopen it immediately
5. Workflows should trigger

**Option C: Manual trigger**
1. Go to: **Actions** tab
2. Select **CI Pipeline**
3. Click **Run workflow**
4. Select branch: **feat/uodated_ci_cd**
5. Click **Run workflow**

---

## 📊 **What Happens After Activation**

### When you push to PR:

```
✅ GitHub Actions starts
  ├── Checkout code
  ├── Setup Java 17
  ├── Start MySQL service
  ├── Wait for MySQL ready
  ├── Run Maven build
  ├── Run unit tests
  ├── Run integration tests
  ├── Build Docker image
  ├── Push to Docker Hub
  └── Generate test reports
```

**Expected time**: 5-10 minutes

---

## ✅ **Verification Checklist**

### GitHub Settings
- [ ] Go to repo Settings
- [ ] Actions → General: Workflows enabled
- [ ] Secrets → Actions: DOCKERHUB_USERNAME added
- [ ] Secrets → Actions: DOCKERHUB_PASSWORD added

### PR #30
- [ ] Push new commit or reopen PR
- [ ] Go to **Checks** section
- [ ] See workflows starting
- [ ] Wait for completion

### Workflow Status
- [ ] CI Pipeline: Running/Passed
- [ ] All tests: Passed
- [ ] Docker image: Built
- [ ] Image: Pushed to Hub

---

## 📋 **Workflow Details**

### CI Pipeline (ci.yml)
**Triggers on:**
- Push to `main` or `dev` branch
- Any pull request

**Steps:**
1. Checkout code
2. Setup Java 17
3. Start MySQL 8.0 service
4. Build project with Maven
5. Run tests
6. Build Docker image
7. Push to Docker Hub

**Duration**: ~5-8 minutes

### Deploy Pipeline (deploy.yml)
**Triggers on:**
- Manual workflow dispatch
- (Optionally on push to main/dev)

**Steps:**
1. Checkout code
2. Setup kubectl
3. Deploy to Kubernetes (dev or prod)
4. Wait for deployment rollout
5. Verify deployment

**Duration**: ~3-5 minutes

---

## 🔧 **Troubleshooting**

### Workflows not appearing?

**Check:**
1. [ ] Secrets configured (DOCKERHUB_USERNAME, DOCKERHUB_PASSWORD)
2. [ ] Workflows enabled in Settings
3. [ ] Workflow YAML files valid
4. [ ] Commit pushed to GitHub

**Solution:**
```powershell
# Force push
git push --force origin feat/uodated_ci_cd
```

### Build failing?

**Check workflow logs:**
1. Go to PR #30
2. Click **Checks** section
3. Click **Details** on failed job
4. View build logs

**Common issues:**
- ❌ Maven dependencies not downloading → Check internet
- ❌ Tests failing → Check test code
- ❌ Docker Hub auth failing → Check secrets

### Tests failing?

**Check:**
- [ ] Database connection working (MySQL service)
- [ ] Test database populated
- [ ] All dependencies available
- [ ] Correct Java version (17)

---

## 🎯 **Next Steps (In Order)**

1. ✅ Add Docker Hub secrets (2 min)
2. ✅ Enable workflows in Settings (1 min)
3. ✅ Push new commit to trigger workflows (2 min)
4. ✅ Monitor workflow in Actions tab (5-8 min)
5. ✅ Check workflow results
6. ✅ Fix any failures
7. ✅ Merge PR when all checks pass

---

## 📚 **Reference Files**

- **GITHUB_ACTIONS_SETUP.md** - Detailed setup guide
- **.github/workflows/ci.yml** - CI workflow definition
- **.github/workflows/deploy.yml** - Deployment workflow
- **pom.xml** - Maven configuration

---

## 💡 **Pro Tips**

### Tip 1: View Workflow Runs
```
https://github.com/arjunrathod1996/SCMProject/actions
```

### Tip 2: Re-run Failed Jobs
1. Go to workflow run
2. Click **Re-run failed jobs**
3. Workflows start again

### Tip 3: View Full Logs
1. Click on failed job
2. Expand log sections
3. See detailed error messages

### Tip 4: Docker Hub Verification
After workflow succeeds:
```bash
docker pull arjun368/scm-app:latest
docker images  # Should show your image
```

---

## 🚀 **Your Action Items (Right Now)**

### DO THIS IN 5 MINUTES:

1. **Add Docker credentials to GitHub Secrets**
   - https://github.com/arjunrathod1996/SCMProject/settings/secrets/actions
   - Add: DOCKERHUB_USERNAME
   - Add: DOCKERHUB_PASSWORD

2. **Enable Workflows**
   - https://github.com/arjunrathod1996/SCMProject/settings/actions
   - Select: "Allow all actions"

3. **Trigger Workflow**
   ```powershell
   git push origin feat/uodated_ci_cd
   ```

4. **Monitor**
   - Go to PR #30
   - Click Checks
   - Watch workflow run

---

## ✅ **Expected Results**

### After 5-8 minutes:

✅ **Checks section shows:**
- CI Pipeline: ✅ Passed
- Build: ✅ Success
- Tests: ✅ All passed
- Docker: ✅ Image pushed

✅ **Docker Hub shows:**
- New image: `arjun368/scm-app:latest`
- Or tagged with commit SHA

✅ **Ready to:**
- Review PR
- Merge to main/dev
- Deploy to production

---

## 📞 **Need Help?**

### Check logs in Actions tab:
1. PR #30 → Checks section
2. Click workflow name
3. View detailed logs

### Common error messages:
- **"Maven dependency not found"** → Network issue
- **"Tests failed"** → Code issue
- **"Docker login failed"** → Secret issue
- **"Kubernetes deploy failed"** → Config issue

---

## ✨ **Summary**

| Task | Time | Status |
|------|------|--------|
| Add Docker secrets | 2 min | → DO NOW |
| Enable workflows | 1 min | → DO NOW |
| Push commit | 2 min | → DO NOW |
| Run workflows | 5-8 min | → Wait |
| Verify results | 2 min | → Check |

**Total time to complete**: ~10 minutes

---

**Status**: Ready to Activate  
**Next**: Follow 3 action items above  
**Result**: Fully automated CI/CD!


