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

# How To Run
## Requirements
This is a Scala project, built using `sbt`, so ideally we will require the `sbt` to be installed.
It can easily be installed using `sdkman` or `homebrew`, more details in https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Mac.html

```shell
#SDKMAN!
sdk install java $(sdk list java | grep -o "\b8\.[0-9]*\.[0-9]*\-tem" | head -1)
sdk install sbt

# Homebrew
brew install sbt
```

## Running The Tests
Once SBT is installed, to run the tests suite, simply use sbt from the root directory of the project.
```shell
sbt test
```
The command will build and run the tests

## Running the App with SBT
An option to run the application can be using directly `sbt`, for that we will need to scape the string argument.
```shell
sbt "run \"*/15 0 1,15 * 1-5 /usr/bin/find\""
```

```
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


