# Crypto.com Android interview demo project

## About UI

- Total balance and List item is observe each related data
- Support swipe refresh that will re-trigger data refresh
- Launch data request asynchronously and handle exception separately

## About Data

- In-memory Room database for caching display
- FakeService will simulate randomly exception and delay
- Each data class is handled individually with each repository
- Tiers will query only necessary currency to save memory

![](device-2022-01-07-115720.png)

