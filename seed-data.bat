@echo off
title Expense Tracker - Seed Demo Data
echo ========================================================
echo Seeding Demo User and 6 Months of Transactions
echo ========================================================
powershell -ExecutionPolicy Bypass -File "%~dp0seed-data.ps1"
pause

