CREATE TABLE ITINERARY(

  iterID SERIAL PRIMARY KEY,
  iterName VARCHAR(30) NOT NULL,
  description VARCHAR(100),
  difficulty VARCHAR(50) NOT NULL,
  hours SMALLINT NOT NULL,
  minutes SMALLINT NOT NULL,
  startPoint POINT NOT NULL,
  waypoints json[],
  creatorID VARCHAR(100) NOT NULL,
  shareDate DATE NOT NULL,
  updateDate DATE,

  CONSTRAINT check_iterName CHECK
  (LENGTH(iterName) > 0),

  CONSTRAINT check_difficulty CHECK
  (LENGTH(difficulty) > 0),

  CONSTRAINT check_creatorID CHECK
  (LENGTH(creatorID) > 0),

  CONSTRAINT check_duration CHECK
  (hours = 0 AND minutes BETWEEN 1 AND 59) OR (hours > 0 AND minutes BETWEEN 0 AND 59)

)
