create extension if not exists timescaledb version '1.7.4';

create table if not exists sensor_data
(
    time        timestamp        not null,
    temperature double precision null,
    humidity    double precision null
);
select create_hypertable('sensor_data', 'time')
