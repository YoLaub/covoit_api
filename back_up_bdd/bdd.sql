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

CREATE TABLE IF NOT EXISTS status(
   Id_status SERIAL,
   label VARCHAR(50) NOT NULL,
   PRIMARY KEY(Id_status),
   UNIQUE(label)
);

CREATE TABLE IF NOT EXISTS location(
   Id_location SERIAL,
   street_number VARCHAR(20),
   street_name VARCHAR(255) NOT NULL,
   postal_code VARCHAR(10) NOT NULL,
   city_name VARCHAR(100) NOT NULL,
   latitude DECIMAL(10,8) CHECK(latitude BETWEEN -90 AND 90),
   longitude DECIMAL(11,8) CHECK(longitude BETWEEN -180 AND 180),
   PRIMARY KEY(Id_location)
);

CREATE TABLE IF NOT EXISTS type_notif(
   Id_type_notif SERIAL,
   label VARCHAR(255) NOT NULL,
   PRIMARY KEY(Id_type_notif),
   UNIQUE(label)
);

CREATE TABLE IF NOT EXISTS user_account(
   Id_account SERIAL,
   email VARCHAR(255) NOT NULL,
   password VARCHAR(255) NOT NULL,
   token VARCHAR(255),
   reset_password_token VARCHAR(255),
   reset_password_expires_at TIMESTAMP,
   Id_status INT NOT NULL,
   Id_role INT NOT NULL,
   PRIMARY KEY(Id_account),
   UNIQUE(email),
   UNIQUE(token),
   UNIQUE(reset_password_token),
   FOREIGN KEY(Id_role) REFERENCES role_user(Id_role) ON DELETE RESTRICT
   FOREIGN KEY(Id_status) REFERENCES status(Id_status) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS user_profil(
   Id_profil SERIAL,
   firstname VARCHAR(100) NOT NULL,
   lastname VARCHAR(100) NOT NULL,
   phone VARCHAR(20) NOT NULL,
   Id_account INT NOT NULL,
   PRIMARY KEY(Id_profil),
   UNIQUE(Id_account),
   FOREIGN KEY(Id_account) REFERENCES user_account(Id_account) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS vehicule(
   Id_vehicule SERIAL,
   seats SMALLINT NOT NULL CHECK(seats > 0),
   carregistration VARCHAR(20),
   additional_info TEXT,
   Id_model INT NOT NULL,
   Id_profil INT NOT NULL,
   PRIMARY KEY(Id_vehicule),
   UNIQUE(carregistration),
   FOREIGN KEY(Id_model) REFERENCES model(Id_model) ON DELETE RESTRICT,
   FOREIGN KEY(Id_profil) REFERENCES user_profil(Id_profil) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS route(
   Id_route SERIAL,
   kms INT NOT NULL CHECK(kms >= 0),
   available_seats SMALLINT NOT NULL CHECK(available_seats >= 0),
   trip_datetime TIMESTAMP NOT NULL,
   Id_profil INT NOT NULL,
   Id_icon INT,
   PRIMARY KEY(Id_route),
   FOREIGN KEY(Id_profil) REFERENCES user_profil(Id_profil) ON DELETE CASCADE,
   FOREIGN KEY(Id_icon) REFERENCES icon(Id_icon) ON DELETE SET NULL
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
   Id_profil INT,
   Id_route INT,
   role_in_route VARCHAR(50) NOT NULL DEFAULT 'passenger',
   status VARCHAR(50) NOT NULL DEFAULT 'pending',
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY(Id_profil, Id_route),
   FOREIGN KEY(Id_profil) REFERENCES user_profil(Id_profil) ON DELETE CASCADE,
   FOREIGN KEY(Id_route) REFERENCES route(Id_route) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_notification(
   Id_user_notification SERIAL, 
   Id_account INT NOT NULL,
   Id_notification INT NOT NULL,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   is_read BOOLEAN DEFAULT FALSE,
   PRIMARY KEY(Id_user_notification), 
   FOREIGN KEY(Id_account) REFERENCES user_account(Id_account) ON DELETE CASCADE,
   FOREIGN KEY(Id_notification) REFERENCES notification(Id_notification) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS route_location(
   Id_route INT,
   Id_location INT,
   type VARCHAR(50) NOT NULL CHECK(type IN ('starting', 'arrival')),
   PRIMARY KEY(Id_route, Id_location),
   FOREIGN KEY(Id_route) REFERENCES route(Id_route) ON DELETE CASCADE,
   FOREIGN KEY(Id_location) REFERENCES location(Id_location) ON DELETE RESTRICT
);

-- Index corrigés
CREATE INDEX IF NOT EXISTS idx_account_email ON user_account(email);
CREATE INDEX IF NOT EXISTS idx_account_token ON user_account(token);
CREATE INDEX IF NOT EXISTS idx_account_status ON user_account(status);
CREATE INDEX IF NOT EXISTS idx_profil_account ON user_profil(Id_account);
CREATE INDEX IF NOT EXISTS idx_profil_name ON user_profil(lastname, firstname);
CREATE INDEX IF NOT EXISTS idx_route_datetime ON route(trip_datetime);
CREATE INDEX IF NOT EXISTS idx_route_profil ON route(Id_profil);
CREATE INDEX IF NOT EXISTS idx_location_city ON location(city_name);
CREATE INDEX IF NOT EXISTS idx_location_coords ON location(latitude, longitude);
CREATE INDEX IF NOT EXISTS idx_user_route_status ON user_route(status);
CREATE INDEX IF NOT EXISTS idx_user_route_profil ON user_route(Id_profil);
CREATE INDEX IF NOT EXISTS idx_user_notification_read ON user_notification(is_read);
CREATE INDEX IF NOT EXISTS idx_user_notification_account ON user_notification(Id_account);
CREATE INDEX IF NOT EXISTS idx_notification_type ON notification(Id_type_notif);
CREATE INDEX IF NOT EXISTS idx_vehicule_registration ON vehicule(carregistration);
CREATE INDEX IF NOT EXISTS idx_vehicule_profil ON vehicule(Id_profil);

