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
INSERT INTO tbl_user (id_user, username, password, admin, enabled) VALUES 
(1, 'alejandro', 'ale123', true, true),
(2, 'fernando', 'fer123', true, true),
(3, 'celia', 'celia123', true, true),
(4, 'jorge', 'jorge123', false, true),
(5, 'rayas', 'rayas123', false, true),
(6, 'pancha', 'pancha123', false, false);

-- Insert into note
INSERT INTO note (id_note, content, created_at, updated_at, id_user) VALUES 
(1, 'This is the note No. 1', NULL, NULL, 1),
(2, 'This is the note No. 2', NULL, NULL, 2),
(3, 'This is the note No. 3', NULL, NULL, 2),
(4, 'This is the note No. 4', NULL, NULL, 2),
(5, 'This is the note No. 5', NULL, NULL, 3),
(6, 'This is the note No. 6', NULL, NULL, 3),
(7, 'This is the note No. 7', NULL, NULL, 4),
(8, 'This is the note No. 8', NULL, NULL, 4),
(9, 'This is the note No. 9', NULL, NULL, 6),
(10, 'This is the note No. 10', NULL, NULL, 6);

-- Insert into users_roles
INSERT INTO users_roles (id_user, id_role) VALUES 
(1, 1),
(1, 2),
(1, 3),
(2, 1),
(2, 2),
(3, 1),
(3, 2),
(4, 1),
(5, 1),
(6, 1);