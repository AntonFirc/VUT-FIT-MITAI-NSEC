create or replace table chat
(
	id bigint auto_increment
		primary key,
	name varchar(32) null,
	created_at timestamp(3) default current_timestamp() not null,
	updated_at timestamp(3) default current_timestamp() not null,
	deleted tinyint(1) default 0 null
);

create or replace table state
(
	id int auto_increment
		primary key,
	name varchar(64) not null,
	created_at timestamp(3) default current_timestamp() not null,
	updated_at timestamp(3) default current_timestamp() not null,
	deleted tinyint(1) default 0 null
);

create or replace table wall
(
	id bigint auto_increment
		primary key,
	created_at timestamp(3) default current_timestamp() not null,
	updated_at timestamp(3) default current_timestamp() not null,
	deleted tinyint(1) default 0 null
);

create or replace table page
(
	id bigint auto_increment
		primary key,
	name varchar(32) not null,
	admin_id bigint not null,
	profile_photo_id bigint null,
	wall_id bigint not null,
	created_at timestamp(3) default current_timestamp() not null,
	updated_at timestamp(3) default current_timestamp() not null,
	deleted tinyint(1) default 0 null,
	constraint fk_page_wall_id
		foreign key (wall_id) references wall (id)
);

create or replace table photo
(
	id bigint auto_increment
		primary key,
	path varchar(256) not null,
	user_id bigint,
	page_id bigint,
	created_at timestamp(3) default current_timestamp(),
	updated_at timestamp(3) default current_timestamp(),
	deleted tinyint(1) default 0 null,
	constraint fk_photo_page_id
		foreign key (page_id) references page (id)
);

alter table page
	add constraint fk_page_profile_photo_id
		foreign key (profile_photo_id) references photo (id);

create or replace table user
(
	id bigint auto_increment
		primary key,
	name varchar(32) not null,
	surname varchar(32) not null,
	email varchar(64) not null,
	password_hash varchar(60) not null,
	profile_photo_id bigint null,
	gender enum('M', 'F') not null,
	address varchar(64) null,
	city varchar(32) null,
	state_id int null,
	wall_id bigint not null,
	created_at timestamp(3) default current_timestamp() not null,
	updated_at timestamp(3) default current_timestamp() not null,
	deleted tinyint(1) default 0 null,
	constraint fk_user_profile_photo_id
		foreign key (profile_photo_id) references photo (id),
	constraint fk_user_state_id
		foreign key (state_id) references state (id),
	constraint fk_user_wall_id
		foreign key (wall_id) references wall (id)
);

create or replace table message
(
	id bigint auto_increment
		primary key,
	content text not null,
	author_id bigint not null,
	chat_id bigint not null,
	created_at timestamp(3) default current_timestamp() not null,
	updated_at timestamp(3) default current_timestamp() not null,
	deleted tinyint(1) default 0 null,
	constraint fk_message_author_id
		foreign key (author_id) references user (id),
	constraint fk_message_chat_id
		foreign key (chat_id) references chat (id)
);

alter table page
	add constraint fk_page_admin_id
		foreign key (admin_id) references user (id);

alter table photo
	add constraint fk_photo_user_id
		foreign key (user_id) references user (id);

create or replace table post
(
	id bigint auto_increment
		primary key,
	content_type enum('text', 'image') not null,
	content text not null,
	user_id bigint null,
	page_id bigint null,
	wall_id bigint null,
	created_at timestamp(3) default current_timestamp(3) null,
	updated_at timestamp(3) default current_timestamp(3) null,
	deleted tinyint(1) default 0 null,
	constraint fk_post_page_id
		foreign key (page_id) references page (id),
	constraint fk_post_user_id
		foreign key (user_id) references user (id),
	constraint fk_post_wall_id
		foreign key (wall_id) references wall (id)
);

create or replace table comment
(
	id bigint auto_increment
		primary key,
	content text not null,
	user_id bigint null,
	page_id bigint null,
	post_id bigint not null,
	created_at timestamp(3) default current_timestamp() not null,
	updated_at timestamp(3) default current_timestamp() not null,
	deleted tinyint(1) default 0 null,
	constraint fk_comment_page_id
		foreign key (page_id) references page (id),
	constraint fk_comment_post_id
		foreign key (post_id) references post (id),
	constraint fk_comment_user_id
		foreign key (user_id) references user (id)
);

create or replace table comment_like
(
	id bigint auto_increment
		primary key,
	user_id bigint not null,
	comment_id bigint not null,
	created_at timestamp(3) default current_timestamp() not null,
	updated_at timestamp(3) default current_timestamp() not null,
	deleted tinyint(1) default 0 null,
	constraint fk_comment_like_comment_id
		foreign key (comment_id) references comment (id),
	constraint fk_comment_like_user_id
		foreign key (user_id) references user (id)
);

create or replace table post_like
(
	id bigint auto_increment
		primary key,
	user_id bigint not null,
	post_id bigint not null,
	created_at timestamp(3) default current_timestamp() not null,
	updated_at timestamp(3) default current_timestamp() not null,
	deleted tinyint(1) default 0 null,
	constraint fk_post_like_post_id
		foreign key (post_id) references post (id),
	constraint fk_post_like_user_id
		foreign key (user_id) references user (id)
);

create or replace table profile_link_dictionary
(
	id bigint auto_increment
		primary key,
	path varchar(128) not null,
	page_id bigint null,
	user_id bigint null,
	created_at timestamp(3) default current_timestamp() not null,
	updated_at timestamp(3) default current_timestamp() not null,
	deleted tinyint(1) default 0 null,
	constraint fk_pld_page_id
		foreign key (page_id) references page (id),
	constraint fk_pld_user_id
		foreign key (user_id) references user (id)
);

create or replace table subscribed_to
(
	id bigint auto_increment
		primary key,
	subscriber_id bigint not null,
	subscribed_to_user bigint null,
	subscribed_to_page bigint null,
	created_at timestamp(3) default current_timestamp() not null,
	updated_at timestamp(3) default current_timestamp() not null,
	deleted tinyint(1) default 0 null,
	constraint fk_subscribed_to_subscribed_to_page
		foreign key (subscribed_to_page) references page (id),
	constraint fk_subscribed_to_subscribed_to_user
		foreign key (subscribed_to_user) references user (id),
	constraint fk_subscribed_to_subscriber_id
		foreign key (subscriber_id) references user (id)
);

create or replace table user_chat
(
	id bigint auto_increment
		primary key,
	user_id bigint not null,
	chat_id bigint not null,
	created_at timestamp(3) default current_timestamp() not null,
	updated_at timestamp(3) default current_timestamp() not null,
	deleted tinyint(1) default 0 null,
	constraint fk_user_chat_chat_id
		foreign key (chat_id) references chat (id),
	constraint fk_user_chat_user_id
		foreign key (user_id) references user (id)
);

create or replace table user_page
(
	id bigint auto_increment
		primary key,
	user_id bigint not null,
	page_id bigint not null,
	is_admin tinyint(1) default 1 null,
	created_at timestamp(3) default current_timestamp() not null,
	updated_at timestamp(3) default current_timestamp() not null,
	deleted tinyint(1) default 0 null,
	constraint fk_user_page_page_id
		foreign key (page_id) references page (id),
	constraint fk_user_page_user_id
		foreign key (user_id) references user (id)
);

