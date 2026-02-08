-- Création des tables avec IF NOT EXISTS

CREATE TABLE IF NOT EXISTS icon(
   Id_icon SERIAL,
   label VARCHAR(50) NOT NULL,
   PRIMARY KEY(Id_icon),
   UNIQUE(label)
);

CREATE TABLE IF NOT EXISTS brand(
   Id_brand SERIAL,
   label VARCHAR(50) NOT NULL,
   PRIMARY KEY(Id_brand),
   UNIQUE(label)
);

CREATE TABLE IF NOT EXISTS model(
   Id_model SERIAL,
   label VARCHAR(255) NOT NULL,
   Id_brand INT NOT NULL,
   PRIMARY KEY(Id_model),
   FOREIGN KEY(Id_brand) REFERENCES brand(Id_brand) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS role_user(
   Id_role SERIAL,
   label VARCHAR(50) NOT NULL,
   PRIMARY KEY(Id_role),
   UNIQUE(label)
);

CREATE TABLE IF NOT EXISTS location(
   Id_location SERIAL,
   label_address VARCHAR(255) NOT NULL,
   city VARCHAR(100) NOT NULL,
   postcode VARCHAR(10) NOT NULL,
   latitude DECIMAL(10,8) NOT NULL CHECK(latitude BETWEEN -90 AND 90),
   longitude DECIMAL(11,8) NOT NULL CHECK(longitude BETWEEN -180 AND 180),
   PRIMARY KEY(Id_location)
);

CREATE TABLE IF NOT EXISTS type_notif(
   Id_type_notif SERIAL,
   label VARCHAR(255) NOT NULL,
   PRIMARY KEY(Id_type_notif),
   UNIQUE(label)
);

CREATE TABLE IF NOT EXISTS user_profil(
   Id_User SERIAL,
   pseudo VARCHAR(255) NOT NULL,
   email VARCHAR(255) NOT NULL,
   password VARCHAR(255) NOT NULL,
   token VARCHAR(255),
   reset_password_token VARCHAR(255),
   reset_password_expires_at DATE,
   last_login_at TIMESTAMP,
   etat BOOLEAN NOT NULL DEFAULT TRUE,
   Id_role INT NOT NULL,
   PRIMARY KEY(Id_User),
   UNIQUE(pseudo),
   UNIQUE(email),
   UNIQUE(token),
   UNIQUE(reset_password_token),
   FOREIGN KEY(Id_role) REFERENCES role_user(Id_role) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS route(
   Id_route SERIAL,
   place SMALLINT NOT NULL CHECK(place > 0),
   date_ DATE NOT NULL,
   hour_ TIME NOT NULL,
   distance INT NOT NULL CHECK(distance >= 0),
   Id_icon INT NOT NULL,
   PRIMARY KEY(Id_route),
   FOREIGN KEY(Id_icon) REFERENCES icon(Id_icon) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS vehicule(
   Id_vehicule SERIAL,
   place SMALLINT NOT NULL CHECK(place > 0),
   additional_info TEXT,
   Id_model INT NOT NULL,
   Id_User INT NOT NULL,
   PRIMARY KEY(Id_vehicule),
   FOREIGN KEY(Id_model) REFERENCES model(Id_model) ON DELETE RESTRICT,
   FOREIGN KEY(Id_User) REFERENCES user_profil(Id_User) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS historical(
   Id_historical SERIAL,
   comment TEXT,
   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   Id_route INT NOT NULL,
   PRIMARY KEY(Id_historical),
   FOREIGN KEY(Id_route) REFERENCES route(Id_route) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS notification(
   Id_notification SERIAL,
   contain TEXT NOT NULL,
   Id_type_notif INT,
   PRIMARY KEY(Id_notification),
   UNIQUE(contain),
   FOREIGN KEY(Id_type_notif) REFERENCES type_notif(Id_type_notif) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS user_route(
   Id_User INT,
   Id_route INT,
   role_in_route VARCHAR(50),
   status VARCHAR(50) NOT NULL,
   PRIMARY KEY(Id_User, Id_route),
   FOREIGN KEY(Id_User) REFERENCES user_profil(Id_User) ON DELETE CASCADE,
   FOREIGN KEY(Id_route) REFERENCES route(Id_route) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_notification(
   Id_User INT,
   Id_notification INT,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   is_read BOOLEAN DEFAULT FALSE,
   PRIMARY KEY(Id_User, Id_notification),
   FOREIGN KEY(Id_User) REFERENCES user_profil(Id_User) ON DELETE CASCADE,
   FOREIGN KEY(Id_notification) REFERENCES notification(Id_notification) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS route_location(
   Id_route INT,
   Id_location INT,
   type VARCHAR(50) NOT NULL CHECK(type IN ('start', 'end', 'waypoint')),
   PRIMARY KEY(Id_route, Id_location),
   FOREIGN KEY(Id_route) REFERENCES route(Id_route) ON DELETE CASCADE,
   FOREIGN KEY(Id_location) REFERENCES location(Id_location) ON DELETE RESTRICT
);

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_user_email ON user_profil(email);
CREATE INDEX IF NOT EXISTS idx_user_token ON user_profil(token);
CREATE INDEX IF NOT EXISTS idx_user_etat ON user_profil(etat);
CREATE INDEX IF NOT EXISTS idx_route_date ON route(date_);
CREATE INDEX IF NOT EXISTS idx_location_coords ON location(latitude, longitude);
CREATE INDEX IF NOT EXISTS idx_user_route_status ON user_route(status);
CREATE INDEX IF NOT EXISTS idx_user_notification_read ON user_notification(is_read);
CREATE INDEX IF NOT EXISTS idx_notification_type ON notification(Id_type_notif);
