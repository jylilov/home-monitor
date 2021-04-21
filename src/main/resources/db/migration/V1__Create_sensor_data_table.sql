CREATE EXTENSION IF NOT EXISTS timescaledb VERSION '1.7.4';

create table sensor_data
(
    time        timestamp        not null,
    temperature double precision null,
    humidity    double precision null
);
select create_hypertable('sensor_data', 'time')
