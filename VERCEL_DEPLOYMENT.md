# 🚀 MediBook — Vercel Deployment Guide

MediBook has been pre-configured for **1-click free deployment on Vercel** with client-side SPA routing (`vercel.json`).

---

## 🛠️ Pre-Configured Files Created

- `frontend/vercel.json` — Ensures React Router single-page app routes work seamlessly without 404 errors on page refresh.
- `vercel.json` (Root) — Root build script configuration.

---

## Option 1: Deploy via Vercel Web Dashboard + GitHub (Recommended)

### Step 1: Push Project to GitHub
Open Command Prompt in `medibook`:
```cmd
cd C:\Users\kesav\.gemini\antigravity\scratch\medibook
git init
git add .
git commit -m "Deploy MediBook to Vercel"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/medibook.git
git push -u origin main
```

### Step 2: Import into Vercel
1. Go to [https://vercel.com/new](https://vercel.com/new) and log in with your GitHub account.
2. Select your `medibook` repository and click **Import**.
3. Configure Project Settings:
   - **Framework Preset**: `Vite`
   - **Root Directory**: `frontend`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
4. Click **Deploy**.
5. Within 60 seconds, Vercel will generate your live production URL (e.g., `https://medibook-frontend.vercel.app`)! 🎉

---

## Option 2: Direct Terminal Deployment via Vercel CLI

Open Command Prompt:
```cmd
cd C:\Users\kesav\.gemini\antigravity\scratch\medibook\frontend
cmd /c npx vercel
```

1. Prompt: *Set up and deploy?* → Type `y`
2. Prompt: *Which scope?* → Select your Vercel account
3. Prompt: *Link to existing project?* → Type `n`
4. Prompt: *What's your project's name?* → `medibook`
5. Prompt: *In which directory is your code located?* → `./`
6. Vercel will automatically detect **Vite** and complete your deployment!

---

## 🌐 Deploying Backend (Optional for Live Production)

- **Frontend on Vercel**: `https://medibook.vercel.app`
- **Backend Options**:
  - Deploy Spring Boot backend to **Render.com** (Free Web Service), **Railway.app**, or **Fly.io**.
  - Update `VITE_API_URL` environment variable in Vercel to point to your live backend URL!
