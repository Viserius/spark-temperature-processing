#!/bin/sh
CQL="CREATE KEYSPACE IF NOT EXISTS temperatures WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '2'} AND durable_writes = true;
CREATE table IF NOT EXISTS temperatures.measurements (station INT, date TIMESTAMP, hour INT, temperature INT, PRIMARY KEY ((station, date), hour));
CREATE table IF NOT EXISTS temperatures.predictions (station INT, date TIMESTAMP, hour INT, temperature INT, PRIMARY KEY ((station, date), hour));
CREATE table IF NOT EXISTS temperatures.averages (station INT, daily_average DOUBLE, weekly_average DOUBLE, monthly_average DOUBLE, PRIMARY KEY (station));
CREATE table IF NOT EXISTS temperatures.batch_averages_daily (station INT, date TIMESTAMP, temperature DOUBLE, PRIMARY KEY ((station), date));
CREATE table IF NOT EXISTS temperatures.batch_averages_weekly (station INT, date TIMESTAMP, temperature DOUBLE, PRIMARY KEY ((station), date));
CREATE table IF NOT EXISTS temperatures.batch_averages_monthly (station INT, date TIMESTAMP, temperature DOUBLE, PRIMARY KEY ((station), date));
CREATE table IF NOT EXISTS temperatures.batch_averages_all (station INT, date TIMESTAMP, temperature DOUBLE, PRIMARY KEY ((station), date));"

until echo $CQL | cqlsh;
do echo "cqlsh: Cassandra is unavailable to initialize - will retry later";
sleep 2;
done &

echo "Finished setting up keyspace"

exec /docker-entrypoint.sh "$@"