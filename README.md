# Build your own Crontab-tool

Implementation of the https://codingchallenges.fyi/challenges/challenge-cron/ using Scala


## Implementation Details
The parser itself is using a Monadic Parser, based on Graham Hutton and Erik Meijer paper 
https://people.cs.nott.ac.uk/pszgmh/monparsing.pdf although a simplified version at this point, 
I am not using a StateMonad which could be nice to have to be more detailed when reporting parser errors.

## Features
Providing the crontab expression (ie: `*/15 0 1,15 * 1-5 /usr/bin/find`)
- Validates the crontab
- Details an explanation on shape of a table
- Displays a human-readable explanation
- Displays the next 5 instances of the cron

```shell
cronTabTool */15 0 1,15 * 1-5 /usr/bin/find

minutes       0 15 30 45
hours         0
day of month  1 15
month         1 2 3 4 5 6 7 8 9 10 11 12
day of week   1 2 3 4 5
command       /usr/bin/find

Every 15 minutes, at 0 hours, Monday through Friday

Friday, 1 November 2024, 00:00:00 Greenwich Mean Time
Friday, 1 November 2024, 00:15:00 Greenwich Mean Time
Friday, 1 November 2024, 00:30:00 Greenwich Mean Time
Friday, 1 November 2024, 00:45:00 Greenwich Mean Time
Friday, 15 November 2024, 00:00:00 Greenwich Mean Time
```


