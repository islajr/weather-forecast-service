CREATE TABLE city
(
    id            BIGSERIAL NOT NULL,
    name          VARCHAR(30) NOT NULL UNIQUE,
    country       VARCHAR(30) NOT NULL,
    latitude      DOUBLE PRECISION NOT NULL,
    longitude     DOUBLE PRECISION NOT NULL,
    last_searched TIMESTAMP WITH TIME ZONE,
    search_count  BIGINT,
    created_at    TIMESTAMP WITH TIME ZONE,
    updated_at    TIMESTAMP WITH TIME ZONE,
    CONSTRAINT pk_city PRIMARY KEY (id)
);

CREATE TABLE current_weather
(
    id          VARCHAR(50) NOT NULL,
    city_id     BIGINT NOT NULL,
    timestamp   TIMESTAMP WITH TIME ZONE NOT NULL,
    temperature DOUBLE PRECISION,
    humidity    INT,
    wind_speed  DOUBLE PRECISION,
    wind_direction INT,
    pressure INT,
    conditions VARCHAR(100),
    description VARCHAR(100),
    created_at  TIMESTAMP WITH TIME ZONE,
    updated_at  TIMESTAMP WITH TIME ZONE,
    CONSTRAINT  pk_currentweather PRIMARY KEY (id)
);

CREATE TABLE forecast
(
    id            VARCHAR(255)     NOT NULL,
    city_id       BIGINT           NOT NULL,
    forecast_date TIMESTAMP WITH TIME ZONE  NOT NULL,
    temperature DOUBLE PRECISION,
    wind_speed DOUBLE PRECISION,
    wind_direction INT,
    pressure INT,
    humidity INT,
    conditions VARCHAR(100),
    description VARCHAR(100),
    rain_volume   DOUBLE PRECISION,
    probability   FLOAT,
    created_at    TIMESTAMP WITH TIME ZONE,
    updated_at    TIMESTAMP WITH TIME ZONE,
    CONSTRAINT pk_forecast PRIMARY KEY (id)
);

ALTER TABLE current_weather
    ADD CONSTRAINT uc_currentweather_city UNIQUE (city_id);

ALTER TABLE current_weather
    ADD CONSTRAINT FK_CURRENTWEATHER_ON_CITY FOREIGN KEY (city_id) REFERENCES city (id);

ALTER TABLE forecast
    ADD CONSTRAINT FK_FORECAST_ON_CITY FOREIGN KEY (city_id) REFERENCES city (id);