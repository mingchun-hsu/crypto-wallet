# Crypto wallet

<img src="Screenshot.png" align="right" width="320"/>

This project demonstrates modern Android development with Hilt, Coroutines, Flow, Jetpack (Room, ViewModel), and Material Design based on MVVM architecture.

## About Data

- In-memory Room database for caching, and provide flow.
- FakeService will simulate randomly exception and delay.
- Each data class handled individually with each repository.
- Tiers queried only for necessary currency for saving memory.

## About UI

- Total balance and items are collected on each related data flow.
- Support swipe refresh that will re-trigger the data refresh.
- Launch data request asynchronously and handle exception individually.
- List is sorted by usd balance.
- Coin balance's rate is provided with most amount in rate array.
