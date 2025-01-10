
-- create database note_manager;

use note_manager;

-- User entity
select *
from user;

-- Note entity
select *
from note;


-- Role entity

/*
	To insert roles in the table
    
    insert into role (name) values ("ROLE_USER");
    insert into role (name) values ("ROLE_ADMIN");
    insert into role (name) values ("ROLE_SUPER_ADMIN");
    
    insert into users_roles (id_role, id_user) values (3, 10);
    
	mega
*/

select *
from role;

select *
from users_roles;

-- Custom Queries

-- Select all of the users who only have the role called 'user' 
select distinct id_user, username, enabled
from user
where id_user not in (select id_user
						from users_roles
						where id_role = 2 or id_role = 3);




