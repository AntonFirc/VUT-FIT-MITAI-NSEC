INSERT INTO pdb2020.page_follows (follows_id, user_email, created_at) VALUES (27, 'lukas@sn.com', '2020-12-12 18:59:08.787');
INSERT INTO pdb2020.page_follows (follows_id, user_email, created_at) VALUES (27, 'peter@sn.com', '2020-12-12 18:59:14.100');
INSERT INTO pdb2020.page_follows (follows_id, user_email, created_at) VALUES (1, 'lukas@sn.com', '2020-12-12 18:59:02.636');

INSERT INTO pdb2020.page_post (page_id, content_type, created_at, comments, content, likes, page_name) VALUES (27, 'text', '2020-12-12 19:00:00.938', [], 'I like Java.', [], 'This should be together');
INSERT INTO pdb2020.page_post (page_id, content_type, created_at, comments, content, likes, page_name) VALUES (27, 'text', '2020-12-12 19:00:30.306', [{ident:3,author_name:'Lukas Suster',author_profile_url:'/user/lukassuster.2',author_picture_url:NULL,content:'Fajne!',comment_likes:[{ident:4,author_name:'Peter Sveter',author_profile_url:'/user/petersveter.1',author_picture_url:NULL,created_at:1607800921839}],created_at:1607800876698}], 'What does Apache Kafka have in common with Alfonz Kafka?', [{ident:27,author_name:'Peter Sveter',author_profile_url:'/user/petersveter.1',author_picture_url:NULL,created_at:1607799686503}, {ident:28,author_name:'Lukas Suster',author_profile_url:'/user/lukassuster.2',author_picture_url:NULL,created_at:1607799692228}], 'This should be together');
INSERT INTO pdb2020.page_post (page_id, content_type, created_at, comments, content, likes, page_name) VALUES (1, 'text', '2020-12-04 16:28:04.460', [], 'Tato stranka je najlepsia na svete', [], 'Stranocka');
INSERT INTO pdb2020.page_post (page_id, content_type, created_at, comments, content, likes, page_name) VALUES (1, 'text', '2020-12-10 16:03:28.379', [], 'Ide to ?', [], 'Stranocka');

INSERT INTO pdb2020.page (id, admin_email, last_active, name, profile_path, profile_photo_path) VALUES (27, 'peter@sn.com', '2020-12-12 19:22:16.940', 'This should be together', '/page/thisshouldbetogether.27', null);
INSERT INTO pdb2020.page (id, admin_email, last_active, name, profile_path, profile_photo_path) VALUES (1, 'peter@sn.com', null, 'Stranocka', '/page/stranocka.1', null);

INSERT INTO pdb2020.profile_link_dictionary (path, page_id, user_email) VALUES ('/user/lukassuster.2', null, 'lukas@sn.com');
INSERT INTO pdb2020.profile_link_dictionary (path, page_id, user_email) VALUES ('/page/thisshouldbetogether.27', 27, null);
INSERT INTO pdb2020.profile_link_dictionary (path, page_id, user_email) VALUES ('/page/stranocka.1', 1, null);
INSERT INTO pdb2020.profile_link_dictionary (path, page_id, user_email) VALUES ('/user/petersveter.1', null, 'peter@sn.com');
INSERT INTO pdb2020.profile_link_dictionary (path, page_id, user_email) VALUES ('/user/franzkafka.11', null, 'kafka@sn.com');

INSERT INTO pdb2020.user_follower (user_email, created_at, follower_email, follower_id) VALUES ('peter@sn.com', '2020-12-12 18:59:14.100', null, 27);
INSERT INTO pdb2020.user_follower (user_email, created_at, follower_email, follower_id) VALUES ('lukas@sn.com', '2020-12-10 15:42:46.705', 'peter@sn.com', null);
INSERT INTO pdb2020.user_follower (user_email, created_at, follower_email, follower_id) VALUES ('lukas@sn.com', '2020-12-12 18:59:02.636', null, 1);
INSERT INTO pdb2020.user_follower (user_email, created_at, follower_email, follower_id) VALUES ('lukas@sn.com', '2020-12-12 18:59:08.787', null, 27);

INSERT INTO pdb2020.user_follows (follows_email, user_email, created_at) VALUES ('peter@sn.com', 'lukas@sn.com', '2020-12-10 15:42:46.669');

INSERT INTO pdb2020.user_post (user_email, content_type, created_at, comments, content, likes, user_profile_path) VALUES ('peter@sn.com', 'text', '2020-12-04 13:07:23.972', [], 'Moj prvy prispevok!', [], '/profile/petersveter.1');
INSERT INTO pdb2020.user_post (user_email, content_type, created_at, comments, content, likes, user_profile_path) VALUES ('peter@sn.com', 'text', '2020-12-09 18:44:30.373', [{ident:2,author_name:'Lukas Suster',author_profile_url:'/user/lukassuster.2',author_picture_url:NULL,content:'Second comment ever !',comment_likes:[{ident:2,author_name:'Peter Sveter',author_profile_url:'/user/petersveter.1',author_picture_url:NULL,created_at:1607676757990},{ident:3,author_name:'Peter Sveter',author_profile_url:'/user/petersveter.1',author_picture_url:NULL,created_at:1607676872062},{ident:2,author_name:'Peter Sveter',author_profile_url:'/user/petersveter.1',author_picture_url:NULL,created_at:1607676757990},{ident:3,author_name:'Peter Sveter',author_profile_url:'/user/petersveter.1',author_picture_url:NULL,created_at:1607676872062}],created_at:1607626964935}], 'Funguj boha ti krista tam uz', [{ident:25,author_name:'Lukas Suster',author_profile_url:'/user/lukassuster.2',author_picture_url:NULL,created_at:1607613640058}], '/user/petersveter.1');

INSERT INTO pdb2020.user (email, created_at, last_active, name, owned_pages, password_hash, profile_path, profile_photo_path, status, surname) VALUES ('peter@sn.com', '2020-12-05 13:35:25.667', '2020-12-05 13:35:25.667', 'Peter', [1, 26, 27], '$2a$10$b2sBGNUm6zmAvQrRdQ600OZlbx0jyFpiy1OiqW1iiuLvnL.FuS.w6', '/user/petersveter.1', null, true, 'Sveter');
INSERT INTO pdb2020.user (email, created_at, last_active, name, owned_pages, password_hash, profile_path, profile_photo_path, status, surname) VALUES ('kafka@sn.com', null, null, 'Franz', [], '$2a$10$gOK5pDHtu5P6qBJ7aORWLuWlPYD9RTSJ8FHBqE2k5qJW77MErqO/O', '/user/franzkafka.11', null, false, 'Kafka');
INSERT INTO pdb2020.user (email, created_at, last_active, name, owned_pages, password_hash, profile_path, profile_photo_path, status, surname) VALUES ('lukas@sn.com', '2020-12-05 13:35:33.957', '2020-12-05 13:35:33.957', 'Lukas', [], '$2a$10$YIWkS15WXQvBamoJefZK5enuId2jIiNqcr5wXgG0pLbe7mcvd6uta', '/user/lukassuster.2', null, true, 'Suster');

INSERT INTO pdb2020.chat (id, name, updated_at) VALUES (2, 'Group chat', '2020-12-13 10:22:53.000');
INSERT INTO pdb2020.chat (id, name, updated_at) VALUES (1, null, '2020-12-13 10:21:18.000');

INSERT INTO pdb2020.user_chat (user_email, chat_id, created_at) VALUES ('peter@sn.com', 1, '2020-12-13 10:21:51.000');
INSERT INTO pdb2020.user_chat (user_email, chat_id, created_at) VALUES ('peter@sn.com', 2, '2020-12-13 10:23:14.000');
INSERT INTO pdb2020.user_chat (user_email, chat_id, created_at) VALUES ('kafka@sn.com', 2, '2020-12-13 10:23:33.000');
INSERT INTO pdb2020.user_chat (user_email, chat_id, created_at) VALUES ('lukas@sn.com', 1, '2020-12-13 10:21:57.000');
INSERT INTO pdb2020.user_chat (user_email, chat_id, created_at) VALUES ('lukas@sn.com', 2, '2020-12-13 10:23:23.000');

INSERT INTO pdb2020.chat_user (chat_id, user_email, created_at) VALUES (2, 'kafka@sn.com', '2020-12-13 10:23:33.000');
INSERT INTO pdb2020.chat_user (chat_id, user_email, created_at) VALUES (2, 'lukas@sn.com', '2020-12-13 10:23:23.000');
INSERT INTO pdb2020.chat_user (chat_id, user_email, created_at) VALUES (2, 'peter@sn.com', '2020-12-13 10:23:14.000');
INSERT INTO pdb2020.chat_user (chat_id, user_email, created_at) VALUES (1, 'lukas@sn.com', '2020-12-13 10:21:57.000');
INSERT INTO pdb2020.chat_user (chat_id, user_email, created_at) VALUES (1, 'peter@sn.com', '2020-12-13 10:21:51.000');

INSERT INTO pdb2020.chat_message (chat_id, from_user, created_at, message) VALUES (1, 'lukas@sn.com', '2020-12-13 10:22:36.000', 'Message 2');
INSERT INTO pdb2020.chat_message (chat_id, from_user, created_at, message) VALUES (1, 'peter@sn.com', '2020-12-13 10:22:16.000', 'Message 1');