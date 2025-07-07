-- These instructions are ordered in the reverse order to the insert of data
DELETE FROM users_roles;
DELETE FROM note;
DELETE FROM tbl_user;
DELETE FROM role;

-- Insert into role
INSERT INTO role (id_role, name) VALUES 
(1, 'ROLE_USER'),
(2, 'ROLE_ADMIN'),
(3, 'ROLE_SUPER_ADMIN');

-- Insert into user
-- Insert into tbl_user (ids incrementados en +9)
INSERT INTO tbl_user (id_user, username, password, admin, enabled) VALUES 
(10, 'alejandro', 'ale123', true, true),
(11, 'fernando', 'fer123', true, true),
(12, 'celia', 'celia123', true, true),
(13, 'jorge', 'jorge123', false, true),
(14, 'rayas', '$2a$10$BDgkY07YMxPbXbnM3.Vl2ehN4Rz9ZpZ.n.zVtcoDLHLZRxAK3HNwa', false, true),
(15, 'pancha', 'pancha123', false, false);

-- Insert into note (referencias de id_user actualizadas en +9)
INSERT INTO note (id_note, content, created_at, updated_at, id_user) VALUES 
(1, 'This is the note No. 1', NULL, NULL, 10),
(2, 'This is the note No. 2', NULL, NULL, 11),
(3, 'This is the note No. 3', NULL, NULL, 11),
(4, 'This is the note No. 4', NULL, NULL, 11),
(5, 'This is the note No. 5', NULL, NULL, 12),
(6, 'This is the note No. 6', NULL, NULL, 12),
(7, 'This is the note No. 7', NULL, NULL, 13),
(8, 'This is the note No. 8', NULL, NULL, 13),
(9, 'This is the note No. 9', NULL, NULL, 15),
(10, 'This is the note No. 10', NULL, NULL, 15);

-- Insert into users_roles (id_user actualizados en +9)
INSERT INTO users_roles (id_user, id_role) VALUES 
(10, 1),
(10, 2),
(10, 3),
(11, 1),
(11, 2),
(12, 1),
(12, 2),
(13, 1),
(14, 1),
(15, 1);
