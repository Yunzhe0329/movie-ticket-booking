CREATE TABLE movie(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(200) NOT NULL ,
    director VARCHAR(200) NOT NULL,
    duration_min INTEGER NOT NULL,
    rating VARCHAR(10) NOT NULL ,
    release_date DATE NOT NULL ,
    poster_url VARCHAR(500),
    synopsis TEXT,

    CONSTRAINT chk_movie_duration CHECK (duration_min > 0),
    CONSTRAINT chk_movie_rating CHECK (rating IN('G', 'P', 'PG12', 'PG15', 'R'))
);