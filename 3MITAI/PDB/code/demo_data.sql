INSERT INTO pdbSql.wall (id, created_at, updated_at, deleted) VALUES (1, '2020-12-04 13:03:52', '2020-12-04 13:03:52', 0);
INSERT INTO pdbSql.wall (id, created_at, updated_at, deleted) VALUES (2, '2020-12-04 13:04:44', '2020-12-04 13:04:44', 0);
INSERT INTO pdbSql.wall (id, created_at, updated_at, deleted) VALUES (3, '2020-12-04 15:45:11', '2020-12-04 15:45:11', 0);
INSERT INTO pdbSql.wall (id, created_at, updated_at, deleted) VALUES (15, '2020-12-11 14:51:55', '2020-12-11 14:51:55', 0);
INSERT INTO pdbSql.wall (id, created_at, updated_at, deleted) VALUES (107, '2020-12-12 13:47:10', '2020-12-12 15:03:26', 1);
INSERT INTO pdbSql.wall (id, created_at, updated_at, deleted) VALUES (108, '2020-12-12 13:59:19', '2020-12-12 13:59:19', 0);

INSERT INTO pdbSql.state (id, name, created_at, updated_at, deleted) VALUES (1, 'Slovensko', '2020-12-11 10:23:03', '2020-12-11 10:23:03', 0);

INSERT INTO pdbSql.user (id, name, surname, email, password_hash, profile_photo_id, gender, address, city, state_id, wall_id, created_at, updated_at, deleted) VALUES (1, 'Peter', 'Sveter', 'peter@sn.com', '$2a$10$b2sBGNUm6zmAvQrRdQ600OZlbx0jyFpiy1OiqW1iiuLvnL.FuS.w6', null, 'M', 'Kolejni 2', 'Brno', null, 1, '2020-12-04 13:03:52', '2020-12-04 13:03:52', 0);
INSERT INTO pdbSql.user (id, name, surname, email, password_hash, profile_photo_id, gender, address, city, state_id, wall_id, created_at, updated_at, deleted) VALUES (2, 'Lukas', 'Suster', 'lukas@sn.com', '$2a$10$YIWkS15WXQvBamoJefZK5enuId2jIiNqcr5wXgG0pLbe7mcvd6uta', null, 'M', 'Provaznikova 5', 'Bratislava', null, 2, '2020-12-04 13:04:44', '2020-12-04 13:04:44', 0);
INSERT INTO pdbSql.user (id, name, surname, email, password_hash, profile_photo_id, gender, address, city, state_id, wall_id, created_at, updated_at, deleted) VALUES (11, 'Franz', 'Kafka', 'kafka@sn.com', '$2a$10$gOK5pDHtu5P6qBJ7aORWLuWlPYD9RTSJ8FHBqE2k5qJW77MErqO/O', null, 'M', 'Zahrobni 5', 'Bratislava', null, 15, '2020-12-11 14:51:55', '2020-12-11 14:51:55', 0);

INSERT INTO pdbSql.page (id, name, admin_id, profile_photo_id, wall_id, created_at, updated_at, deleted) VALUES (1, 'Stranocka', 1, null, 3, '2020-12-04 15:45:11', '2020-12-04 15:45:11', 0);
INSERT INTO pdbSql.page (id, name, admin_id, profile_photo_id, wall_id, created_at, updated_at, deleted) VALUES (26, 'Plz do not look here', 1, null, 107, '2020-12-12 13:47:10', '2020-12-12 15:03:26', 1);
INSERT INTO pdbSql.page (id, name, admin_id, profile_photo_id, wall_id, created_at, updated_at, deleted) VALUES (27, 'This should be together', 1, null, 108, '2020-12-12 13:59:19', '2020-12-12 13:59:19', 0);

INSERT INTO pdbSql.user_page (id, user_id, page_id, is_admin, created_at, updated_at, deleted) VALUES (1, 1, 1, 1, '2020-12-04 15:45:11', '2020-12-04 15:45:11', 0);
INSERT INTO pdbSql.user_page (id, user_id, page_id, is_admin, created_at, updated_at, deleted) VALUES (2, 1, 26, 1, '2020-12-12 13:47:10', '2020-12-12 15:03:26', 1);
INSERT INTO pdbSql.user_page (id, user_id, page_id, is_admin, created_at, updated_at, deleted) VALUES (3, 1, 27, 1, '2020-12-12 13:59:19', '2020-12-12 13:59:19', 0);

INSERT INTO pdbSql.profile_link_dictionary (id, path, page_id, user_id, created_at, updated_at, deleted) VALUES (1, '/user/petersveter.1', null, 1, '2020-12-04 13:03:52', '2020-12-04 13:03:52', 0);
INSERT INTO pdbSql.profile_link_dictionary (id, path, page_id, user_id, created_at, updated_at, deleted) VALUES (2, '/user/lukassuster.2', null, 2, '2020-12-04 13:04:44', '2020-12-04 13:04:44', 0);
INSERT INTO pdbSql.profile_link_dictionary (id, path, page_id, user_id, created_at, updated_at, deleted) VALUES (3, '/page/stranocka.1', 1, null, '2020-12-04 15:45:11', '2020-12-04 15:45:11', 0);
INSERT INTO pdbSql.profile_link_dictionary (id, path, page_id, user_id, created_at, updated_at, deleted) VALUES (83, '/page/plz do not look here.26', 26, null, '2020-12-12 13:47:10', '2020-12-12 13:47:10', 1);

INSERT INTO pdbSql.subscribed_to (id, subscriber_id, subscribed_to_user, subscribed_to_page, created_at, updated_at, deleted) VALUES (20, 2, 1, null, '2020-12-10 16:42:46', '2020-12-10 16:42:46', 0);
INSERT INTO pdbSql.subscribed_to (id, subscriber_id, subscribed_to_user, subscribed_to_page, created_at, updated_at, deleted) VALUES (61, 2, null, 1, '2020-12-12 19:59:02', '2020-12-12 19:59:02', 0);
INSERT INTO pdbSql.subscribed_to (id, subscriber_id, subscribed_to_user, subscribed_to_page, created_at, updated_at, deleted) VALUES (62, 2, null, 27, '2020-12-12 19:59:08', '2020-12-12 19:59:08', 0);
INSERT INTO pdbSql.subscribed_to (id, subscriber_id, subscribed_to_user, subscribed_to_page, created_at, updated_at, deleted) VALUES (63, 1, null, 27, '2020-12-12 19:59:14', '2020-12-12 19:59:14', 0);

INSERT INTO pdbSql.post (id, content_type, content, user_id, page_id, wall_id, created_at, updated_at, deleted) VALUES (1, 'text', 'Moj prvy prispevok!', 1, null, 1, '2020-12-04 13:07:23.972', '2020-12-04 13:07:23.000', 0);
INSERT INTO pdbSql.post (id, content_type, content, user_id, page_id, wall_id, created_at, updated_at, deleted) VALUES (4, 'text', 'Tato stranka je najlepsia na svete', null, 1, 3, '2020-12-04 17:28:04.000', '2020-12-04 16:28:04.000', 0);
INSERT INTO pdbSql.post (id, content_type, content, user_id, page_id, wall_id, created_at, updated_at, deleted) VALUES (30, 'text', 'Seriously?', null, 26, 107, '2020-12-12 14:47:42.853', '2020-12-12 14:47:42.853', 0);
INSERT INTO pdbSql.post (id, content_type, content, user_id, page_id, wall_id, created_at, updated_at, deleted) VALUES (31, 'text', 'I like Java.', null, 27, 108, '2020-12-12 20:00:00.938', '2020-12-12 20:00:00.938', 0);
INSERT INTO pdbSql.post (id, content_type, content, user_id, page_id, wall_id, created_at, updated_at, deleted) VALUES (32, 'text', 'What does Apache Kafka have in common with Alfonz Kafka?', null, 27, 108, '2020-12-12 20:00:30.306', '2020-12-12 20:00:30.306', 0);

INSERT INTO pdbSql.post_like (id, user_id, post_id, created_at, updated_at, deleted) VALUES (25, 2, 31, '2020-12-10 15:20:40', '2020-12-10 15:20:40', 0);
INSERT INTO pdbSql.post_like (id, user_id, post_id, created_at, updated_at, deleted) VALUES (27, 1, 32, '2020-12-12 19:01:26', '2020-12-12 19:01:26', 0);
INSERT INTO pdbSql.post_like (id, user_id, post_id, created_at, updated_at, deleted) VALUES (28, 2, 32, '2020-12-12 19:01:32', '2020-12-12 19:01:32', 0);

INSERT INTO pdbSql.comment (id, content, user_id, page_id, post_id, created_at, updated_at, deleted) VALUES (2, 'Second comment ever !', 2, null, 4, '2020-12-10 19:02:44', '2020-12-10 19:02:44', 0);
INSERT INTO pdbSql.comment (id, content, user_id, page_id, post_id, created_at, updated_at, deleted) VALUES (3, 'Fajne!', 2, null, 32, '2020-12-12 19:21:16', '2020-12-12 19:21:16', 0);

INSERT INTO pdbSql.comment_like (id, user_id, comment_id, created_at, updated_at, deleted) VALUES (3, 1, 2, '2020-12-11 08:54:32', '2020-12-11 08:54:32', 0);
INSERT INTO pdbSql.comment_like (id, user_id, comment_id, created_at, updated_at, deleted) VALUES (4, 1, 3, '2020-12-12 19:22:01', '2020-12-12 19:22:01', 0);

INSERT INTO pdbSql.chat (id, name, created_at, updated_at, deleted) VALUES (1, null, '2020-12-13 10:21:18', '2020-12-13 10:21:18', 0);
INSERT INTO pdbSql.chat (id, name, created_at, updated_at, deleted) VALUES (2, 'Group chat', '2020-12-13 10:22:53', '2020-12-13 10:22:53', 0);

INSERT INTO pdbSql.user_chat (id, user_id, chat_id, created_at, updated_at, deleted) VALUES (1, 1, 1, '2020-12-13 10:21:51', '2020-12-13 10:21:51', 0);
INSERT INTO pdbSql.user_chat (id, user_id, chat_id, created_at, updated_at, deleted) VALUES (2, 2, 1, '2020-12-13 10:21:57', '2020-12-13 10:21:57', 0);
INSERT INTO pdbSql.user_chat (id, user_id, chat_id, created_at, updated_at, deleted) VALUES (3, 1, 2, '2020-12-13 10:23:14', '2020-12-13 10:23:14', 0);
INSERT INTO pdbSql.user_chat (id, user_id, chat_id, created_at, updated_at, deleted) VALUES (4, 2, 2, '2020-12-13 10:23:23', '2020-12-13 10:23:23', 0);
INSERT INTO pdbSql.user_chat (id, user_id, chat_id, created_at, updated_at, deleted) VALUES (5, 11, 2, '2020-12-13 10:23:33', '2020-12-13 10:23:33', 0);

INSERT INTO pdbSql.message (id, content, author_id, chat_id, created_at, updated_at, deleted) VALUES (1, 'Message 1', 1, 1, '2020-12-13 10:22:16', '2020-12-13 10:22:16', 0);
INSERT INTO pdbSql.message (id, content, author_id, chat_id, created_at, updated_at, deleted) VALUES (2, 'Message 2', 2, 1, '2020-12-13 10:22:36', '2020-12-13 10:22:36', 0);
