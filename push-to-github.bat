@echo off
title Push Expense Tracker to GitHub
echo ========================================================
echo Pushing Expense Tracker to GitHub:
echo https://github.com/adhiraj1235-wq/EXPENSE-TRACKER-
echo ========================================================
echo.

set GIT_EXE="C:\Users\Adhiraj\.tools\git\cmd\git.exe"

echo 1. Checking git status...
%GIT_EXE% status

echo.
echo 2. Pushing to origin main...
echo (If prompted for password, enter your GitHub Personal Access Token)
%GIT_EXE% push -u origin main --force

echo.
if %ERRORLEVEL% equ 0 (
    echo ========================================================
    echo SUCCESS! Your project has been pushed to GitHub:
    echo https://github.com/adhiraj1235-wq/EXPENSE-TRACKER-
    echo ========================================================
) else (
    echo.
    echo ========================================================
    echo Push encountered an error or needs credentials.
    echo Tip: GitHub requires a Personal Access Token (PAT)
    echo Generate one at: https://github.com/settings/tokens
    echo Select scopes: 'repo'
    echo ========================================================
)

pause
