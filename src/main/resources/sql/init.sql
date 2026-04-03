CREATE DATABASE surf_spot_db;

CREATE TABLE countries (
    code CHAR(2) PRIMARY KEY NOT NULL,
    name TEXT NOT NULL
);

CREATE TABLE surfing_schools (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE coasts (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL,
    country_code CHAR(2) NOT NULL,

    CONSTRAINT fk_country
    FOREIGN KEY (country_code)
    REFERENCES countries(code)
);

CREATE TABLE instructors (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    surfing_school_id BIGSERIAL,

    CONSTRAINT fk_surfing_school
    FOREIGN KEY (surfing_school_id)
    REFERENCES surfing_schools(id)
);

CREATE TABLE surf_spots (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL,
    latitude DECIMAL(9, 6) NOT NULL,
    longitude DECIMAL(9, 6) NOT NULL,
    country_code CHAR(2) NOT NULL,
    coast_id BIGINT NOT NULL,
    wave_type TEXT NOT NULL,
    wave_height DECIMAL,
    difficulty TEXT,
    wind_direction INT
        CHECK ( wind_direction >= 0 AND wind_direction < 360),

    CONSTRAINT fk_country
    FOREIGN KEY (country_code)
    REFERENCES countries(code),

    CONSTRAINT fk_coast
    FOREIGN KEY (coast_id)
    REFERENCES coasts(id)
);

CREATE TABLE surf_spot_months (
    surf_spot_id BIGINT NOT NULL,
    month_name TEXT NOT NULL,

    PRIMARY KEY (surf_spot_id, month_name),

    CONSTRAINT fk_surf_spot
    FOREIGN KEY (surf_spot_id)
    REFERENCES surf_spots(id)
);