# Spark step by step

Nine small Spark programs in Java on half a year of made-up train departures from 40 Swiss stations. Each program adds a single concept. After each run, you find that concept in the History Server: jobs, stages, tasks, partitions, shuffles and cached data.

The slides are in [DS2026-ExerciseSession6.pdf](DS2026-ExerciseSession6.pdf).

## Setup

The folder runs the same Spark 4.1.3 standalone cluster as the course: 1 master, 3 workers (1 core and 512 MB for the executor each), HDFS, a History Server, and a `dev` container that is the driver.

In VS Code or Codespaces, choose "Reopen in Container". All containers start, and you get a terminal inside `dev` (root@dev).

Without VS Code, run `docker compose up -d --build`, then `docker compose exec dev bash`.

| Port | UI |
|---|---|
| 8080 | Spark master: registered workers and running applications |
| 18080 | History Server: jobs, stages and tasks of every finished run. A run appears a few seconds after it ends. |
| 9870 | HDFS NameNode: DataNodes, blocks, file browser |

Locally, open them at `http://localhost:<port>`. In Codespaces, open them from the Ports tab. If a port is missing there, run `bash .devcontainer/ports.sh`, then add the port with **Forward a Port**.

## Running a program

`exercise/` holds the programs with `TODO start` / `TODO end` blocks to fill in, and `solution/` holds the same programs filled in. `run.sh` takes a program by its name, with or without the folder number:

```bash
./run.sh platforms                # exercise/01_platforms/Platforms.java
./run.sh solution platforms       # solution/01_platforms/Platforms.java
./run.sh solution all             # every program of solution/, in folder order
```

| Folder | Concept |
|---|---|
| `01_platforms` | application, job, stage, task; a task per partition |
| `02_timetable` | transformations are lazy; every action starts a job |
| `03_departures` | a file becomes 1 or more partitions; the Input column |
| `04_latetrains` | narrow transformations run in the stage of the read |
| `05_delayminutes` | `reduceByKey` is wide: a shuffle and a second stage |
| `06_sumthreeways` | `groupByKey` against `reduceByKey`, and filtering before the shuffle |
| `07_stationnames` | `join` and the records an inner join drops |
| `08_worststations` | `sortByKey` against `top`, and skipped stages |
| `09_punctuality` | `cache()` |
| `10_delayreport` | Spark SQL queries over all departures, read in the DataFlint tab |

## Data

```
data/trains/2025-01.csv ... 2025-06.csv   date,time,line,station_id,delay_min
data/stations.csv                         station_id,name
```

`stations.csv` has no row for stations 39 and 40, and its station 41 has no departures. `java data/Generate.java` writes the files again; a fixed seed makes them identical every time.

## HDFS and DataFlint

Copy the data into HDFS:

```bash
hdfs dfs -mkdir -p /data
hdfs dfs -put -f /data/trains /data/stations.csv /data/
```

The word `hdfs` makes `run.sh` read from `hdfs://namenode:8020/data` instead of `/data`:

```bash
./run.sh solution departures hdfs
```

The History Server has a **DataFlint** tab for every application, with its duration, input, shuffle, memory, idle cores and alerts on a single page. `DelayReport` runs 3 Spark SQL queries over all departures: a report per station, the average delay per hour, and the worst streak of 10 departures per station, which shuffles every departure.

```bash
./run.sh solution delayreport
```
