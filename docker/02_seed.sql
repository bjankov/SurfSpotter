INSERT INTO countries (code, name)
VALUES ('AR', 'Argentina'),
       ('AU', 'Australia'),
       ('BB', 'Barbados'),
       ('BR', 'Brazil'),
       ('CA', 'Canada'),
       ('CL', 'Chile'),
       ('CR', 'Costa Rica'),
       ('HR', 'Croatia'),
       ('DO', 'Dominican Republic'),
       ('EC', 'Ecuador'),
       ('SV', 'El Salvador'),
       ('FJ', 'Fiji'),
       ('FR', 'France'),
       ('PF', 'French Polynesia'),
       ('GH', 'Ghana'),
       ('GR', 'Greece'),
       ('IS', 'Iceland'),
       ('IN', 'India'),
       ('ID', 'Indonesia'),
       ('IE', 'Ireland'),
       ('IL', 'Israel'),
       ('IT', 'Italy'),
       ('JM', 'Jamaica'),
       ('JP', 'Japan'),
       ('MV', 'Maldives'),
       ('MU', 'Mauritius'),
       ('MX', 'Mexico'),
       ('MA', 'Morocco'),
       ('NA', 'Namibia'),
       ('NZ', 'New Zealand'),
       ('NI', 'Nicaragua'),
       ('NO', 'Norway'),
       ('PA', 'Panama'),
       ('PE', 'Peru'),
       ('PH', 'Philippines'),
       ('PT', 'Portugal'),
       ('PR', 'Puerto Rico'),
       ('RE', 'Réunion'),
       ('SN', 'Senegal'),
       ('ZA', 'South Africa'),
       ('KR', 'South Korea'),
       ('ES', 'Spain'),
       ('LK', 'Sri Lanka'),
       ('TW', 'Taiwan'),
       ('TH', 'Thailand'),
       ('TR', 'Turkey'),
       ('GB', 'United Kingdom'),
       ('US', 'United States'),
       ('UY', 'Uruguay'),
       ('VN', 'Vietnam');

INSERT INTO surfing_schools (id, name)
VALUES (1, 'Jamie O''Brien Surf Experience'),
       (2, 'Tahiti Surf School'),
       (3, 'Bali Surf Class'),
       (4, 'J-Bay Surf Academy'),
       (5, 'Torquay Surfing Academy'),
       (6, 'Nazaré Water Fun'),
       (7, 'Hossegor Surf Club');

INSERT INTO coasts (id, name, country_code)
VALUES (1, 'North Shore', 'US'),
       (2, 'Tahiti Iti', 'PF'),
       (3, 'Bukit Peninsula', 'ID'),
       (4, 'Eastern Cape', 'ZA'),
       (5, 'Surf Coast', 'AU'),
       (6, 'Silver Coast', 'PT'),
       (7, 'Nouvelle-Aquitaine', 'FR');

INSERT INTO instructors (id, first_name, last_name, surfing_school_id)
VALUES
    (1, 'Jamie', 'O''Brien', 1),
    (2, 'Kaimana', 'Kahale', 1),
    (3, 'Noelani', 'Akana', 1),
    (4, 'Keoni', 'Paoa', 1),
    (5, 'Malia', 'Kalama', 1),

    (6, 'Matahi', 'Drollet', 2),
    (7, 'Heimanu', 'Tehei', 2),
    (8, 'Vaihere', 'Temauri', 2),
    (9, 'Teiva', 'Tetuanui', 2),
    (10, 'Maeva', 'Faatauira', 2),

    (11, 'Wayan', 'Suputra', 3),
    (12, 'Made', 'Putra', 3),
    (13, 'Kadek', 'Dewi', 3),
    (14, 'Nyoman', 'Arta', 3),
    (15, 'Komang', 'Sari', 3),

    (16, 'Cheron', 'Kraak', 4),
    (17, 'Sipho', 'Ndlovu', 4),
    (18, 'Thandeka', 'Mabaso', 4),
    (19, 'Pieter', 'Botha', 4),
    (20, 'Lerato', 'Dlamini', 4),

    (21, 'Cahill', 'Bell-Warren', 5),
    (22, 'Jack', 'Sheppard', 5),
    (23, 'Ella', 'Whitfield', 5),
    (24, 'Lachlan', 'Marsh', 5),
    (25, 'Mia', 'Coleman', 5),

    (26, 'Rafael', 'Sousa', 6),
    (27, 'Joao', 'Ferreira', 6),
    (28, 'Ines', 'Carvalho', 6),
    (29, 'Rui', 'Tavares', 6),
    (30, 'Catarina', 'Pereira', 6),

    (31, 'Joan', 'Duru', 7),
    (32, 'Antoine', 'Lefevre', 7),
    (33, 'Camille', 'Moreau', 7),
    (34, 'Mathieu', 'Bertrand', 7),
    (35, 'Lea', 'Girard', 7);

INSERT INTO surf_spots (id, name, latitude, longitude, coast_id, wave_type, wave_height, difficulty, wind_direction, image_path)
VALUES (1, 'Pipeline', 21.6640, -158.0539, 1, 'REEF_BREAK', 5.0, 'EXPERT', 90, 'pipeline.jpg'),
       (2, 'Teahupoo', -17.8470, -149.2670, 2, 'REEF_BREAK', 6.5, 'EXPERT', 45, 'teahupoo.jpg'),
       (3, 'Uluwatu', -8.8149, 115.0884, 3, 'REEF_BREAK', 3.0, 'ADVANCED', 135, 'uluwatu.jpg'),
       (4, 'Jeffreys Bay', -34.0333, 24.9167, 4, 'POINT_BREAK', 3.5, 'ADVANCED', 270, 'jeffreys-bay.jpg'),
       (5, 'Bells Beach', -38.3670, 144.2830, 5, 'POINT_BREAK', 4.0, 'ADVANCED', 315, 'bells-beach.jpg'),
       (6, 'Nazare', 39.6010, -9.0830, 6, 'BEACH_BREAK', 24.0, 'EXPERT', 90, 'nazare.jpg'),
       (7, 'Hossegor', 43.6667, -1.4333, 7, 'BEACH_BREAK', 2.5, 'INTERMEDIATE', 90, 'hossegor.jpg');

INSERT INTO surf_spot_schools(surf_spot_id, school_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 5),
       (6, 6),
       (7, 7);

INSERT INTO surf_spot_months (surf_spot_id, month_name)
VALUES
    (1, 'NOVEMBER'),
    (1, 'DECEMBER'),
    (1, 'JANUARY'),
    (1, 'FEBRUARY'),

    (2, 'APRIL'),
    (2, 'MAY'),
    (2, 'JUNE'),
    (2, 'JULY'),
    (2, 'AUGUST'),

    (3, 'MAY'),
    (3, 'JUNE'),
    (3, 'JULY'),
    (3, 'AUGUST'),
    (3, 'SEPTEMBER'),

    (4, 'JUNE'),
    (4, 'JULY'),
    (4, 'AUGUST'),
    (4, 'SEPTEMBER'),

    (5, 'MARCH'),
    (5, 'APRIL'),
    (5, 'MAY'),

    (6, 'OCTOBER'),
    (6, 'NOVEMBER'),
    (6, 'DECEMBER'),
    (6, 'JANUARY'),
    (6, 'FEBRUARY'),

    (7, 'SEPTEMBER'),
    (7, 'OCTOBER');

INSERT INTO users (id, username, email, password_hash)
VALUES (1, 'admin', 'admin@surfspot.com', '$2a$12$ylGEN5C3SY4lROgwj4YN2OuQ2ET/ueSrOxmMiTiS2z6A0uqkekWUu');

INSERT INTO roles (id, name)
VALUES (1, 'ADMIN'),
       (2, 'USER');

INSERT INTO user_roles (user_id, role_id)
VALUES (1, 1);

INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'SURF_SPOT_CREATE'),
       (1, 'SURF_SPOT_VIEW'),
       (1, 'SURF_SPOT_MODIFY'),
       (1, 'SURF_SPOT_DELETE'),
       (1, 'MANAGE_USERS'),
       (1, 'MANAGE_COUNTRIES'),
       (2, 'SURF_SPOT_CREATE'),
       (2, 'SURF_SPOT_VIEW'),
       (2, 'SURF_SPOT_MODIFY'),
       (2, 'SURF_SPOT_DELETE');

-- Postavljanje ID generatora na najveći postavljeni ID + 1
SELECT setval(pg_get_serial_sequence('users', 'id'), coalesce(max(id), 1))
FROM users;
SELECT setval(pg_get_serial_sequence('roles', 'id'), coalesce(max(id), 1))
FROM roles;
SELECT setval(pg_get_serial_sequence('surf_spots', 'id'), coalesce(max(id), 1))
FROM surf_spots;
SELECT setval(pg_get_serial_sequence('coasts', 'id'), coalesce(max(id), 1))
FROM coasts;
SELECT setval(pg_get_serial_sequence('instructors', 'id'), coalesce(max(id), 1))
FROM instructors;
SELECT setval(pg_get_serial_sequence('surfing_schools', 'id'), coalesce(max(id), 1))
FROM surfing_schools;
