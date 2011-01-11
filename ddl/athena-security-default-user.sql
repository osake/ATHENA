---------------------------------------------------------
--
-- This file is for development systems only, DO NOT RUN THIS FILE IN A PRODUCTION ENVIRONMENT
--
-- Inserts a default web user with username "athena_client" and password "password"
----------------------------------------------------------
insert into users (username, password, enabled) values ('athena_client','4a7906bfc2a30a2603ef04dfadc6fb82',1);
insert into authorities (username, authority) values ('athena_client', 'ROLE_CLIENT_APPLICATION');