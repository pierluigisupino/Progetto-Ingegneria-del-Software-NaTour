CREATE TABLE ITINERARY(

  iterID SERIAL PRIMARY KEY,
  iterName VARCHAR(30) NOT NULL,
  description VARCHAR(150),
  difficulty SMALLINT NOT NULL,
  hours SMALLINT NOT NULL,
  minutes SMALLINT NOT NULL,
  startPoint POINT NOT NULL,
  waypoints json[],
  creatorID VARCHAR(100) NOT NULL,
  shareDate DATE NOT NULL,
  updateDate DATE,
  modifiedSince TIMESTAMP NOT NULL

  CONSTRAINT check_iterName CHECK
  (LENGTH(iterName) > 3),

  CONSTRAINT check_difficulty CHECK
  (difficulty >= 0),

  CONSTRAINT check_creatorID CHECK
  (LENGTH(creatorID) > 0),

  CONSTRAINT check_duration CHECK
  ((hours = 0 AND minutes BETWEEN 1 AND 59) OR (hours > 0 AND minutes BETWEEN 0 AND 59))

);

CREATE TABLE MESSAGE(

  body VARCHAR(2000) NOT NULL,
  sendDate DATE NOT NULL,
  sendTime TIME NOT NULL,
  sender VARCHAR(100) NOT NULL,
  receiver VARCHAR(100) NOT NULL,

  CONSTRAINT check_body CHECK
  (LENGTH(body) > 0),

  CONSTRAINT check_send_rec CHECK
  (sender IS DISTINCT FROM receiver)

);

CREATE TABLE ONLINEUSER(

  connectionid VARCHAR(200) NOT NULL,
  sub VARCHAR(100) NOT NULL,
  username VARCHAR(30) NOT NULL

)
