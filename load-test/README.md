# n11.com Search Load Test


## Test scenarios

| Scenario | Search Term | Verification |
|---------|-------------|-----------|
| Search_Normal | "telefon" | HTTP 200 + response time < 3s |
| Search_Empty | empty string | HTTP 200 |
| Search_LongKeyword | 59 crachter lenght string | HTTP 200 |

## Configuration

- 1 user (thread)
- 1 iteration
- HTTP Header: User-Agent, Accept, Accept-Language, Referer
- HTTP Cache Manager active
- Endpoint: `GET https://www.n11.com/arama?q={keyword}`

## Requirements

- JMeter 5.6+

## Run

with GUI:
```
jmeter -t n11_load_test.jmx
```



## Reports


- View Results Tree (detail results)
- Summary Report (special metrics)
