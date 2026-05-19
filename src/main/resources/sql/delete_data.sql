-- Briše sve i automatski rješava Foreign Key ovisnosti
TRUNCATE TABLE
    role_permissions,
    user_roles,
    surf_spot_months,
    instructors,
    surfing_schools,
    surf_spots,
    coasts,
    countries,
    roles,
    users
    RESTART IDENTITY CASCADE;