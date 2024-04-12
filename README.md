### Table of contents
- [Description](#description)
- [Supported jobs](#supported-jobs)
- [Usage](#usage)
- [Installation](#installation)

## Description
MIASI_CRON is a task manager implemented with ANTLR4 and pure Java 17. You can organize different jobs (cmds and files blocks) into tasks which can be run 
with different time configs (repeat_once, repeat_at, repeat_every and repeat cron).

## Supported jobs
Currently only windows is suported (support for linux is planned for future development).
Suported job types:
-  powershell comands (cmds block)
-  bash scripts (files block)
-  python scripts (files block)
-  support for other job types is planned for future releases

## Usage
Example config file
```
# Task definitions
task {
    name: "example1"
    output: "./task1_out.txt"
    cmds: ["echo hello from terminal"]
    files: ["src/main/resources/input/script.sh"]
    files: ["src/main/resources/input/main.py"]
}

task {
    name: "example2"
    output: "./task2_out.txt"
    cmds: ["echo hello from terminal"]
    files: ["src/main/resources/input/script.sh"]
    files: ["src/main/resources/input/main.py"]
}

task {
    name: "example3"
    output: "./task3_out.txt"
    files: ["src/main/resources/input/main.py"]
    cmds: ["echo hello from terminal"]
    files: ["src/main/resources/input/script.sh"]
}

task {
    name: "example4"
    output: "./task4_out.txt"
    files: ["src/main/resources/input/script.sh"]
    cmds: ["echo hello from terminal"]
    files: ["src/main/resources/input/main.py"]
}

# other task definitions ...


# Run configuration
run {
    repeat_once {
        tasks: ["example1"]
    }
    repeat_at {
        tasks: ["example2"]
        date: 2024-03-26
        time: 22:30:00
    }
    repeat_every {
        # possible values: [second, minute, hour, day, week, month]
        unit: minute
        value: 1
        tasks: ["example3"]
    }
    repeat_cron {
        cron: "* * * * *"
        tasks: ["example4"]
    }
}
```
Each task definition supports unlimited number of jobs. Each job is a list of either powershell commands, bash scripts or python scripts. 
Jobs and are run sequentially in order of definition in config file.

```
task {
    name: "example4"
    output: "./task4_out.txt"
    files: ["src/main/resources/input/script.sh"]
    cmds: ["echo hello from terminal"]
    files: ["src/main/resources/input/main.py"]
}
```


Tasks can be grouped with different config blocks. Each task declared in a block is a different thread and runs in parallel. Right now the limit of configured tasks is set to 16.
```
repeat_cron {
        cron: "* * * * *"
        tasks: ["example4", "example3"]
}
```



# Installation
1. Install java
2. Copy repository
3. Install dependencies with provided maven pom.xml
4. Run with Java jre

