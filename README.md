# Crypto.com Android interview demo project

## About UI

- Total balance and List item are observed each related data
- Support swipe refresh that will re-trigger data refresh
- Launch data request asynchronously and handle exception separately
- List is sorted by usd balance
- Coin balance's rate is provided with most amount in rate array

## About Data

- In-memory Room database for caching display
- FakeService will simulate randomly exception and delay
- Each data class is handled individually with each repository
- Tiers will query only necessary currency to save memory

![](device-2022-01-07-130712.png)

