-- generator script for SMF MySQL database, should be run in ssh from an ITU server

DROP DATABASE IF EXISTS smf;

CREATE DATABASE IF NOT EXISTS smf;

USE smf;

SELECT 'Creating tables ...' AS 'Print_Hack';

DROP TABLE IF EXISTS Countries, Users, Posts, TextPosts, PicturePosts;

CREATE TABLE Countries (
countryID INT NOT NULL AUTO_INCREMENT,
countryName VARCHAR(100) NOT NULL,
PRIMARY KEY (countryID),
UNIQUE (countryName)
);

CREATE TABLE Users (
    userID INT NOT NULL AUTO_INCREMENT,
    userName VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(200) NOT NULL,
    countryID INT NOT NULL,
    birthYear int NOT NULL,
    UNIQUE (userName),
    UNIQUE (email),
    FOREIGN KEY (countryID) REFERENCES Countries (countryID),
    PRIMARY KEY (userID)
);

CREATE TABLE Posts(
    postID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userID INT NOT NULL FOREIGN KEY (userID) REFERENCES Users (userID) ON DELETE CASCADE,
    postType INT NOT NULL,
    tStamp BIGINT SIGNED NOT NULL,
    universalTimeStamps VARCHAR(35),
    localTimeStamps VARCHAR(35)
);

CREATE TABLE TextPosts(
    postID INT NOT NULL FOREIGN KEY (postID) REFERENCES Posts (postID) ON DELETE CASCADE,
    postText VARCHAR(145)
);


CREATE TABLE PicturePosts(
    postID INT NOT NULL FOREIGN KEY (postID) REFERENCES Posts (postID) ON DELETE CASCADE,
    picture BLOB NOT NULL
);

CREATE TABLE Likes(
    postID INT NOT NULL FOREIGN KEY (postID) REFERENCES Posts (postID) ON DELETE CASCADE,
    likes INT UNSIGNED NOT NULL DEFAULT 0,
    UNIQUE (postID)
);

CREATE TABLE LikeRelationship(
    postID INT NOT NULL FOREIGN KEY (postID) REFERENCES Posts (postID) ON DELETE CASCADE,
    userID INT NOT NULL FOREIGN KEY (userID) REFERENCES Users (userID) ON DELETE CASCADE,
    UNIQUE (postID,userID)
);


INSERT INTO Countries (countryName) VALUES
('Afghanistan'),('Albania'),('Algeria'),('Andorra'),
('Angola'),('Antigua & Barbuda'),('Argentina'),('Armenia'),
('Australia'),('Austria'),('Azerbaijan'),('Bahamas'),('Bahrain'),
('Bangladesh'),('Barbados'),('Belarus'),('Belgium'),('Belize'),
('Benin'),('Bhutan'),('Bolivia'),('Bosnia & Herzegovina'),
('Botswana'),('Brazil'),('Brunei'),('Bulgaria'),('Burkina Faso'),
('Burundi'),('Cabo Verde'),('Cambodia'),('Cameroon'),('Canada'),
('Central African Republic (CAR)'),('Chad'),('Chile'),('China'),
('Colombia'),('Comoros'),('Congo'), ('The Democratic Republic of the Congo'),
('Costa Rica'),("Cote d' Ivoire"),('Croatia'),('Cuba'),('Cyprus'),
('Czechia'),('Denmark'),('Djibouti'),('Dominica'),('Dominican Republic'),
('Ecuador'),('Egypt'),('El Salvador'),('Equatorial Guinea'),('Eritrea'),('Estonia'),
('Eswatini (Swaziland)'),('Ethiopia'),('Fiji'),('Finland'),('France'),('Gabon'),('Gambia'),
('Georgia'),('Germany'),('Ghana'),('Greece'),('Grenada'),('Guatemala'),('Guinea'),('Guinea-Bissau'),
('Guyana'),('Haiti'),('Honduras'),('Hungary'),('Iceland'),('India'),('Indonesia'),('Iran'),
('Iraq'),('Ireland'),('Israel'),('Italy'),('Jamaica'),('Japan'),('Jordan'),('Kazakhstan'),
('Kenya'),('Kiribati'),('Kosovo'),('Kuwait'),('Kyrgyzstan'),('Laos'),('Latvia'),('Lebanon'),('Lesotho'),
('Liberia'),('Libya'),('Liechtenstein'),('Lithuania'),('Luxembourg'),('Madagascar'),('Malawi'),
('Malaysia'),('Maldives'),('Mali'),('Malta'),('Marshall Islands'),('Mauritania'),('Mauritius'),
('Mexico'),('Micronesia'),('Moldova'),('Monaco'),('Mongolia'),('Montenegro'),('Morocco'),
('Mozambique'),('Myanmar (Burma)'),('Namibia'),('Nauru'),('Nepal'),('Netherlands'),('New Zealand'),
('Nicaragua'),('Niger'),('Nigeria'),('North Korea'),('North Macedonia (Macedonia)'),('Norway'),
('Oman'),('Pakistan'),('Palau'),('Palestine'),('Panama'),('Papua New Guinea'),('Paraguay'),('Peru'),
('Philippines'),('Poland'),('Portugal'),('Qatar'),('Romania'),('Russia'),('Rwanda'),('Saint Kitts & Nevis'),
('Saint Lucia'),('Saint Vincent & the Grenadines'),('Samoa'),('San Marino'),('Sao Tome & Principe'),
('Saudi Arabia'),('Senegal'),('Serbia'),('Seychelles'),('Sierra Leone'),('Singapore'),('Slovakia'),
('Slovenia'),('Solomon Islands'),('Somalia'),('South Africa'),('South Korea'),('South Sudan'),
('Spain'),('Sri Lanka'),('Sudan'),('Suriname'),('Sweden'),('Switzerland'),('Syria'),('Taiwan'),('Tajikistan'),
('Tanzania'),('Thailand'),('Timor-Leste'),('Togo'),('Tonga'),('Trinidad & Tobago'),('Tunisia'),('Turkey'),
('Turkmenistan'),('Tuvalu'),('Uganda'),('Ukraine'),('United Arab Emirates (UAE)'),('United Kingdom (UK)'),
('United States of America (USA)'),('Uruguay'),('Uzbekistan'),('Vanuatu'),('Vatican City'),('Venezuela'),
('Vietnam'),('Yemen'),('Zambia'),('Zimbabwe');


DELIMITER $$
CREATE TRIGGER after_posts_insert
AFTER INSERT ON Posts
FOR EACH ROW
BEGIN
    IF NEW.postType = 0 THEN
        INSERT INTO TextPosts (postID) VALUES (NEW.postID);
        INSERT INTO Likes (postID) VALUES (NEW.postID);
    ELSE
        INSERT INTO PicturePosts (postID) VALUES (NEW.postID);
        INSERT INTO Likes (postID) VALUES (NEW.postID);
	END IF;
END $$
DELIMITER;

DELIMITER $$$
CREATE FUNCTION insertTextPost(
  inUserID INT,
  type INT,
  intStamp BIGINT SIGNED,
  inPostText VARCHAR(145),
  inUniversalTimeStamp VARCHAR(35),
  inLocalTimeStamp VARCHAR(35)
)
RETURNS BOOLEAN
DETERMINISTIC
BEGIN
  DECLARE returnVal boolean;
  INSERT INTO Posts (userID, postType, tStamp,universalTimeStamps,localTimeStamps)
    VALUES (inUserID,type,intStamp,inUniversalTimeStamp,inLocalTimeStamp);
  UPDATE TextPosts SET postText = inPostText
      WHERE postID = (SELECT postID FROM Posts WHERE tStamp = intStamp AND userID = inUserID);
    SET returnVal=true;
  RETURN returnVal;
END $$$
DELIMITER;


DELIMITER //
CREATE FUNCTION insertPicturePost(
  inUserID INT,
  type INT,
  intStamp BIGINT SIGNED,
  inPicture BLOB,
  inLocalTimeStamp VARCHAR(35),
  inUniversalTimeStamp VARCHAR(35)
)
RETURNS BOOLEAN
DETERMINISTIC
BEGIN
  DECLARE returnVal boolean;
  INSERT INTO Posts (userID, postType, tStamp,universalTimeStamps,localTimeStamps)
    VALUES (inUserID,type,intStamp,inUniversalTimeStamp,inLocalTimeStamp);

  UPDATE PicturePosts SET picture = inPicture
          WHERE postID = (SELECT postID FROM Posts WHERE tStamp = intStamp AND userID = inUserID);
    SET returnVal=true;
  RETURN returnVal;
END //
DELIMITER;

INSERT INTO Likes (postID) VALUES (SELECT postID FROM Posts);
