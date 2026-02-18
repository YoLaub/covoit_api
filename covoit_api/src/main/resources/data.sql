-- ============================================================
-- 1. TABLE : STATUS (Nécessaire pour ton entité Java User)
-- ============================================================
-- Attention : Assure-toi d'avoir créé la table 'status' si tu as migré vers une entité Status
INSERT INTO status (id_status, label) VALUES (1, 'ACTIVE') ON CONFLICT (id_status) DO NOTHING;
INSERT INTO status (id_status, label) VALUES (2, 'INACTIVE') ON CONFLICT (id_status) DO NOTHING;
INSERT INTO status (id_status, label) VALUES (3, 'BANNED') ON CONFLICT (id_status) DO NOTHING;
INSERT INTO status (id_status, label) VALUES (4, 'PENDING') ON CONFLICT (id_status) DO NOTHING;

-- ============================================================
-- 2. TABLE : ROLE_USER
-- ============================================================
INSERT INTO role_user (id_role, label) VALUES (1, 'ADMIN') ON CONFLICT (id_role) DO NOTHING;
INSERT INTO role_user (id_role, label) VALUES (2, 'USER') ON CONFLICT (id_role) DO NOTHING;
INSERT INTO role_user (id_role, label) VALUES (3, 'DRIVER') ON CONFLICT (id_role) DO NOTHING;

-- ============================================================
-- 3. TABLE : ICON (Options de confort/préférences)
-- ============================================================
INSERT INTO icon (id_icon, label) VALUES (1, 'Non-fumeur') ON CONFLICT (id_icon) DO NOTHING;
INSERT INTO icon (id_icon, label) VALUES (2, 'Animaux acceptés') ON CONFLICT (id_icon) DO NOTHING;
INSERT INTO icon (id_icon, label) VALUES (3, 'Musique') ON CONFLICT (id_icon) DO NOTHING;
INSERT INTO icon (id_icon, label) VALUES (4, 'Discussion') ON CONFLICT (id_icon) DO NOTHING;
INSERT INTO icon (id_icon, label) VALUES (5, 'Silence') ON CONFLICT (id_icon) DO NOTHING;
INSERT INTO icon (id_icon, label) VALUES (6, 'Bagages volumineux') ON CONFLICT (id_icon) DO NOTHING;

-- ============================================================
-- 4. TABLE : TYPE_NOTIF
-- ============================================================
INSERT INTO type_notif (id_type_notif, label) VALUES (1, 'TRIP_CONFIRMED') ON CONFLICT (id_type_notif) DO NOTHING;
INSERT INTO type_notif (id_type_notif, label) VALUES (2, 'TRIP_CANCELLED') ON CONFLICT (id_type_notif) DO NOTHING;
INSERT INTO type_notif (id_type_notif, label) VALUES (3, 'DRIVER_ARRIVED') ON CONFLICT (id_type_notif) DO NOTHING;
INSERT INTO type_notif (id_type_notif, label) VALUES (4, 'PAYMENT_RECEIVED') ON CONFLICT (id_type_notif) DO NOTHING;
INSERT INTO type_notif (id_type_notif, label) VALUES (5, 'NEW_MESSAGE') ON CONFLICT (id_type_notif) DO NOTHING;
INSERT INTO type_notif (id_type_notif, label) VALUES (6, 'SECURITY_ALERT') ON CONFLICT (id_type_notif) DO NOTHING;

-- ============================================================
-- 5. TABLES : NOTIFICATION (Templates)
-- ============================================================
INSERT INTO notification (id_notification, contain, id_type_notif) 
VALUES (1, 'Votre trajet a été confirmé avec succès.', 1) 
ON CONFLICT (id_notification) DO NOTHING;

INSERT INTO notification (id_notification, contain, id_type_notif) 
VALUES (2, 'Attention, le conducteur a annulé le trajet.', 2) 
ON CONFLICT (id_notification) DO NOTHING;

INSERT INTO notification (id_notification, contain, id_type_notif) 
VALUES (3, 'Bienvenue ! Veuillez confirmer votre email.', 6) 
ON CONFLICT (id_notification) DO NOTHING;

-- ============================================================
-- 6. TABLES : BRAND (Marques)
-- ============================================================
INSERT INTO brand (id_brand, label) VALUES (1, 'Peugeot') ON CONFLICT (id_brand) DO NOTHING;
INSERT INTO brand (id_brand, label) VALUES (2, 'Renault') ON CONFLICT (id_brand) DO NOTHING;
INSERT INTO brand (id_brand, label) VALUES (3, 'Citroën') ON CONFLICT (id_brand) DO NOTHING;
INSERT INTO brand (id_brand, label) VALUES (4, 'Volkswagen') ON CONFLICT (id_brand) DO NOTHING;
INSERT INTO brand (id_brand, label) VALUES (5, 'Tesla') ON CONFLICT (id_brand) DO NOTHING;
INSERT INTO brand (id_brand, label) VALUES (6, 'Toyota') ON CONFLICT (id_brand) DO NOTHING;
INSERT INTO brand (id_brand, label) VALUES (7, 'BMW') ON CONFLICT (id_brand) DO NOTHING;
INSERT INTO brand (id_brand, label) VALUES (8, 'Audi') ON CONFLICT (id_brand) DO NOTHING;

-- ============================================================
-- 7. TABLES : MODEL (Modèles liés aux marques)
-- ============================================================
-- Peugeot (ID 1)
INSERT INTO model (label, id_brand) VALUES ('208', 1) ON CONFLICT DO NOTHING;
INSERT INTO model (label, id_brand) VALUES ('308', 1) ON CONFLICT DO NOTHING;
INSERT INTO model (label, id_brand) VALUES ('3008', 1) ON CONFLICT DO NOTHING;
INSERT INTO model (label, id_brand) VALUES ('508', 1) ON CONFLICT DO NOTHING;

-- Renault (ID 2)
INSERT INTO model (label, id_brand) VALUES ('Clio', 2) ON CONFLICT DO NOTHING;
INSERT INTO model (label, id_brand) VALUES ('Megane', 2) ON CONFLICT DO NOTHING;
INSERT INTO model (label, id_brand) VALUES ('Captur', 2) ON CONFLICT DO NOTHING;
INSERT INTO model (label, id_brand) VALUES ('Arkana', 2) ON CONFLICT DO NOTHING;

-- Citroën (ID 3)
INSERT INTO model (label, id_brand) VALUES ('C3', 3) ON CONFLICT DO NOTHING;
INSERT INTO model (label, id_brand) VALUES ('C4', 3) ON CONFLICT DO NOTHING;

-- Volkswagen (ID 4)
INSERT INTO model (label, id_brand) VALUES ('Golf', 4) ON CONFLICT DO NOTHING;
INSERT INTO model (label, id_brand) VALUES ('Polo', 4) ON CONFLICT DO NOTHING;
INSERT INTO model (label, id_brand) VALUES ('Tiguan', 4) ON CONFLICT DO NOTHING;

-- Tesla (ID 5)
INSERT INTO model (label, id_brand) VALUES ('Model 3', 5) ON CONFLICT DO NOTHING;
INSERT INTO model (label, id_brand) VALUES ('Model Y', 5) ON CONFLICT DO NOTHING;


-- ============================================================
-- 9. TABLE : USER_ACCOUNT (Admin par défaut)
-- ============================================================
-- Mot de passe : "Admin@1234" encodé en BCrypt
INSERT INTO user_account (id_account, email, password, id_status, id_role)
VALUES (
           3,
           'admin@covoit.fr',
           '$2a$10$yxoVAcvtXyKQavRDUIzHAeGFP/13nS/pTqX92HrprBjw5AXj6I7dS',
           1,
           1
       ) ON CONFLICT (id_account) DO NOTHING;

SELECT setval('user_account_id_account_seq', (SELECT MAX(id_account) FROM user_account));

-- ============================================================
-- 8. RESET DES SÉQUENCES (Important pour Postgres)
-- ============================================================
-- Si on insère manuellement des IDs (ex: 1, 2, 3), la séquence automatique (SERIAL)
-- ne le sait pas et essaiera de réutiliser "1" au prochain insert, ce qui plantera.
-- On remet les compteurs à la valeur max insérée.

SELECT setval('status_id_status_seq', (SELECT MAX(id_status) FROM status));
SELECT setval('role_user_id_role_seq', (SELECT MAX(id_role) FROM role_user));
SELECT setval('icon_id_icon_seq', (SELECT MAX(id_icon) FROM icon));
SELECT setval('type_notif_id_type_notif_seq', (SELECT MAX(id_type_notif) FROM type_notif));
SELECT setval('notification_id_notification_seq', (SELECT MAX(id_notification) FROM notification));
SELECT setval('brand_id_brand_seq', (SELECT MAX(id_brand) FROM brand));
SELECT setval('model_id_model_seq', (SELECT MAX(id_model) FROM model));