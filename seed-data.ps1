# Seed script for Expense Tracker via API Gateway (Port 8080)
Write-Host "Connecting to API Gateway at http://localhost:8080..." -ForegroundColor Cyan

try {
    # 1. Register or Login Demo User
    $loginBody = '{"email":"adhiraj@example.com","password":"password123"}'
    try {
        $loginRes = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body $loginBody
        $token = $loginRes.token
        Write-Host "Demo user logged in successfully!" -ForegroundColor Green
    } catch {
        Write-Host "Registering demo user adhiraj@example.com..." -ForegroundColor Yellow
        $regBody = '{"name":"Adhiraj Sharma","email":"adhiraj@example.com","password":"password123"}'
        $regRes = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method Post -ContentType "application/json" -Body $regBody
        $token = $regRes.token
        Write-Host "Registered and logged in!" -ForegroundColor Green
    }

    $headers = @{ "Authorization" = "Bearer $token"; "Content-Type" = "application/json" }

    # 2. Add 6 Months of Sample Transactions (May - October 2026)
    $txs = @(
      @{ type="INCOME";  amount=55000; category="Salary";        description="May Salary";                 date="2026-05-01" },
      @{ type="EXPENSE"; amount=15000; category="Housing";       description="Apartment Rent - May";        date="2026-05-02" },
      @{ type="EXPENSE"; amount=8500;  category="Groceries";     description="Groceries - BigBasket";       date="2026-05-05" },
      @{ type="EXPENSE"; amount=2500;  category="Transport";     description="Fuel + Cab - May";           date="2026-05-10" },
      @{ type="EXPENSE"; amount=3200;  category="Food";          description="Dining Out & Swiggy";         date="2026-05-15" },
      @{ type="EXPENSE"; amount=1800;  category="Utilities";     description="Electricity & Broadband";     date="2026-05-20" },
      @{ type="EXPENSE"; amount=1500;  category="Entertainment"; description="Movies & Streaming";          date="2026-05-25" },
      @{ type="INCOME";  amount=5000;  category="Freelance";     description="Web Design Gig";             date="2026-05-28" },

      @{ type="INCOME";  amount=55000; category="Salary";        description="June Salary";                date="2026-06-01" },
      @{ type="EXPENSE"; amount=15000; category="Housing";       description="Apartment Rent - June";       date="2026-06-02" },
      @{ type="EXPENSE"; amount=7800;  category="Groceries";     description="Groceries - June";            date="2026-06-05" },
      @{ type="EXPENSE"; amount=3200;  category="Transport";     description="Fuel & Metro - June";         date="2026-06-10" },
      @{ type="EXPENSE"; amount=4100;  category="Food";          description="Cafes & Dinners";             date="2026-06-15" },
      @{ type="EXPENSE"; amount=1900;  category="Utilities";     description="Utilities - June";            date="2026-06-20" },
      @{ type="EXPENSE"; amount=2200;  category="Entertainment"; description="Concert Tickets";             date="2026-06-25" },

      @{ type="INCOME";  amount=55000; category="Salary";        description="July Salary";                date="2026-07-01" },
      @{ type="INCOME";  amount=8000;  category="Freelance";     description="App Development Contract";   date="2026-07-03" },
      @{ type="EXPENSE"; amount=15000; category="Housing";       description="Apartment Rent - July";       date="2026-07-02" },
      @{ type="EXPENSE"; amount=9200;  category="Groceries";     description="Supermarket Groceries";       date="2026-07-05" },
      @{ type="EXPENSE"; amount=2800;  category="Transport";     description="Fuel - July";                 date="2026-07-10" },
      @{ type="EXPENSE"; amount=3500;  category="Food";          description="Restaurants & Weekend Food";  date="2026-07-15" },
      @{ type="EXPENSE"; amount=5000;  category="Education";     description="Online Certification Course"; date="2026-07-22" },

      @{ type="INCOME";  amount=55000; category="Salary";        description="August Salary";              date="2026-08-01" },
      @{ type="EXPENSE"; amount=15000; category="Housing";       description="Apartment Rent - August";     date="2026-08-02" },
      @{ type="EXPENSE"; amount=8800;  category="Groceries";     description="Groceries - August";          date="2026-08-05" },
      @{ type="EXPENSE"; amount=3100;  category="Transport";     description="Ola Rides & Fuel";            date="2026-08-10" },
      @{ type="EXPENSE"; amount=2900;  category="Food";          description="Restaurants - August";        date="2026-08-15" },
      @{ type="EXPENSE"; amount=3500;  category="Entertainment"; description="Gaming & OTT - August";       date="2026-08-25" },
      @{ type="EXPENSE"; amount=2500;  category="Healthcare";    description="Health Checkup";              date="2026-08-27" },

      @{ type="INCOME";  amount=60000; category="Salary";        description="September Salary (Increment)";date="2026-09-01" },
      @{ type="EXPENSE"; amount=15000; category="Housing";       description="Apartment Rent - September";  date="2026-09-02" },
      @{ type="EXPENSE"; amount=9500;  category="Groceries";     description="Groceries - September";       date="2026-09-05" },
      @{ type="EXPENSE"; amount=2600;  category="Transport";     description="Fuel - September";            date="2026-09-10" },
      @{ type="EXPENSE"; amount=4200;  category="Food";          description="Festive Dinners";             date="2026-09-15" },
      @{ type="EXPENSE"; amount=8000;  category="Entertainment"; description="Festive Trip - September";    date="2026-09-28" },

      @{ type="INCOME";  amount=60000; category="Salary";        description="October Salary";             date="2026-10-01" },
      @{ type="INCOME";  amount=12000; category="Freelance";     description="Consulting Gig - October";   date="2026-10-05" },
      @{ type="EXPENSE"; amount=15000; category="Housing";       description="Apartment Rent - October";    date="2026-10-02" },
      @{ type="EXPENSE"; amount=10200; category="Groceries";     description="Diwali Sweets & Groceries";   date="2026-10-05" },
      @{ type="EXPENSE"; amount=3800;  category="Transport";     description="Diwali Travel & Fuel";        date="2026-10-10" },
      @{ type="EXPENSE"; amount=5500;  category="Food";          description="Family Dinners - October";    date="2026-10-15" },
      @{ type="EXPENSE"; amount=12000; category="Entertainment"; description="Diwali Gifts & Shopping";     date="2026-10-25" }
    )

    Write-Host "Inserting $($txs.Count) transactions..." -ForegroundColor Cyan
    foreach ($t in $txs) {
        $null = Invoke-RestMethod -Uri "http://localhost:8080/api/transactions" -Method Post -Headers $headers -Body ($t | ConvertTo-Json)
    }
    Write-Host "Transactions seeded successfully!" -ForegroundColor Green

    # 3. Add Budgets for Current / Target Month
    $now = Get-Date
    $m = $now.Month
    $y = $now.Year
    $budgets = @(
      @{ category="Housing";       budgetLimit=15000; month=$m; year=$y },
      @{ category="Groceries";     budgetLimit=10000; month=$m; year=$y },
      @{ category="Transport";     budgetLimit=4000;  month=$m; year=$y },
      @{ category="Food";          budgetLimit=5000;  month=$m; year=$y },
      @{ category="Entertainment"; budgetLimit=8000;  month=$m; year=$y }
    )

    foreach ($b in $budgets) {
        try {
            $null = Invoke-RestMethod -Uri "http://localhost:8080/api/budgets" -Method Post -Headers $headers -Body ($b | ConvertTo-Json)
        } catch {}
    }
    Write-Host "Budgets seeded for month $m/$y!" -ForegroundColor Green

    # 4. Show Summary
    $summary = Invoke-RestMethod -Uri "http://localhost:8080/api/analytics/summary" -Method Get -Headers $headers
    Write-Host "`n========================================================" -ForegroundColor Yellow
    Write-Host "Demo Data Ready!" -ForegroundColor Yellow
    Write-Host "Total Income:  ₹$($summary.totalIncome)" -ForegroundColor Green
    Write-Host "Total Expense: ₹$($summary.totalExpense)" -ForegroundColor Red
    Write-Host "Net Balance:   ₹$($summary.balance)" -ForegroundColor Cyan
    Write-Host "Login with: adhiraj@example.com / password123" -ForegroundColor White
    Write-Host "========================================================`n" -ForegroundColor Yellow
} catch {
    Write-Host "Error seeding data: $_" -ForegroundColor Red
    Write-Host "Make sure all services are running first (run run.bat)!" -ForegroundColor Yellow
}

