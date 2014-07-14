# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table addfailed (
  id                        bigint not null,
  victim_id                 bigint,
  imagelocation             varchar(255),
  weblocation               varchar(255),
  exception                 text,
  datehappened              timestamp not null,
  constraint pk_addfailed primary key (id))
;

create table advertisements (
  id                        bigint not null,
  associatedid              bigint,
  associatedtype            bigint,
  content                   text,
  moment                    timestamp not null,
  constraint pk_advertisements primary key (id))
;

create table blacklist (
  id                        bigint not null,
  author_id                 bigint,
  site                      varchar(255),
  shortdescription          varchar(255),
  datecreated               timestamp not null,
  constraint pk_blacklist primary key (id))
;

create table blog (
  id                        bigint not null,
  author_id                 bigint,
  posted_at                 timestamp,
  content                   text,
  title                     varchar(255),
  constraint pk_blog primary key (id))
;

create table blog_comment (
  id                        bigint not null,
  author_id                 bigint,
  posted_at                 timestamp,
  content                   varchar(255),
  post_id                   bigint,
  constraint pk_blog_comment primary key (id))
;

create table blog_image (
  id                        bigint not null,
  blogid                    bigint,
  file_id                   bigint,
  is_video                  boolean,
  file_type                 varchar(255),
  ok                        varchar(255),
  constraint pk_blog_image primary key (id))
;

create table blog_labels (
  id                        bigint not null,
  tag                       varchar(255),
  constraint uq_blog_labels_tag unique (tag),
  constraint pk_blog_labels primary key (id))
;

create table blog_likes (
  id                        bigint not null,
  blog_id                   bigint,
  author_id                 bigint,
  constraint pk_blog_likes primary key (id))
;

create table cart (
  id                        bigint not null,
  uuid                      varchar(255),
  status                    varchar(255),
  user_id                   bigint,
  constraint pk_cart primary key (id))
;

create table category (
  id                        bigint not null,
  cname                     varchar(255),
  pcategory_id              bigint,
  toplevel                  bigint,
  constraint pk_category primary key (id))
;

create table collection_comment (
  id                        bigint not null,
  author_id                 bigint,
  posted_at                 timestamp,
  content                   varchar(255),
  collection_id             bigint,
  constraint pk_collection_comment primary key (id))
;

create table comment (
  id                        bigint not null,
  author_id                 bigint,
  posted_at                 timestamp,
  content                   varchar(255),
  post_id                   bigint,
  constraint pk_comment primary key (id))
;

create table contactsheet (
  inviter_id                bigint,
  contact                   varchar(255),
  hasinvited                boolean)
;

create table contactus (
  id                        bigint not null,
  querytype                 integer,
  email                     varchar(255),
  firstname                 varchar(255),
  lastname                  varchar(255),
  subject                   varchar(255),
  content                   text,
  datecreated               timestamp not null,
  constraint pk_contactus primary key (id))
;

create table contributor (
  id                        bigint not null,
  user_id                   bigint,
  constraint pk_contributor primary key (id))
;

create table dbreloader (
  id                        bigint not null,
  xx                        bigint,
  constraint pk_dbreloader primary key (id))
;

create table eventlog (
  id                        bigint not null,
  eventtype                 bigint,
  productinv_id             bigint,
  userinv_id                bigint,
  collectinv_id             bigint,
  storeinv_id               bigint,
  eventtime                 timestamp not null,
  constraint pk_eventlog primary key (id))
;

create table fsearch (
  id                        bigint not null,
  skey                      varchar(255),
  genlow                    bigint,
  genup                     bigint,
  prlow                     float,
  prhigh                    float,
  userid_id                 bigint,
  isprivate                 boolean,
  constraint pk_fsearch primary key (id))
;

create table feedback (
  id                        bigint not null,
  author_id                 bigint,
  email                     varchar(255),
  name                      varchar(255),
  content                   text,
  datecreated               timestamp not null,
  constraint pk_feedback primary key (id))
;

create table follow (
  id                        bigint not null,
  leader                    bigint,
  follower                  bigint,
  type                      integer,
  constraint pk_follow primary key (id))
;

create table image_beer (
  id                        bigint not null,
  url                       varchar(255),
  constraint pk_image_beer primary key (id))
;

create table image_gadgets (
  id                        bigint not null,
  url                       varchar(255),
  constraint pk_image_gadgets primary key (id))
;

create table image_glass_ware (
  id                        bigint not null,
  url                       varchar(255),
  constraint pk_image_glass_ware primary key (id))
;

create table image_liquor (
  id                        bigint not null,
  url                       varchar(255),
  constraint pk_image_liquor primary key (id))
;

create table image_mixology (
  id                        bigint not null,
  url                       varchar(255),
  constraint pk_image_mixology primary key (id))
;

create table image_toys (
  id                        bigint not null,
  url                       varchar(255),
  constraint pk_image_toys primary key (id))
;

create table image_wine (
  id                        bigint not null,
  url                       varchar(255),
  constraint pk_image_wine primary key (id))
;

create table infopage (
  id                        bigint not null,
  author_id                 bigint,
  lastedited                timestamp,
  content                   text,
  title                     varchar(255),
  datecreated               timestamp not null,
  constraint pk_infopage primary key (id))
;

create table ipblacklist (
  id                        bigint not null,
  author_id                 bigint,
  lowerip                   bigint,
  upperip                   bigint,
  range                     bigint,
  description               text,
  datecreated               timestamp not null,
  constraint pk_ipblacklist primary key (id))
;

create table item (
  id                        bigint not null,
  quantity                  integer,
  date                      timestamp,
  product_id                bigint,
  cart_id                   bigint,
  constraint pk_item primary key (id))
;

create table kvarrayproperty (
  id                        bigint not null,
  arraypropertyname         varchar(255),
  datavalue                 varchar(255),
  arrayindex                integer,
  constraint pk_kvarrayproperty primary key (id))
;

create table kvproperty (
  id                        bigint not null,
  propertyname              varchar(255),
  stringvalue               varchar(255),
  valuetype                 varchar(255),
  constraint uq_kvproperty_propertyname unique (propertyname),
  constraint pk_kvproperty primary key (id))
;

create table linked_account (
  id                        bigint not null,
  user_id                   bigint,
  provider_user_id          varchar(255),
  provider_key              varchar(255),
  constraint pk_linked_account primary key (id))
;

create table product (
  id                        bigint not null,
  productname               varchar(255),
  currency                  varchar(255),
  pricetag                  float,
  founder_id                bigint,
  siteurl                   varchar(255),
  image_location            varchar(255),
  gender                    bigint,
  views                     bigint,
  description               varchar(255),
  category_id               bigint,
  alive                     boolean,
  pstore_id                 bigint,
  timeofadd                 timestamp not null,
  constraint pk_product primary key (id))
;

create table reportabuse (
  id                        bigint not null,
  author_id                 bigint,
  contentid                 bigint,
  ratype                    integer,
  frompage                  varchar(255),
  complaintext              varchar(255),
  datecreated               timestamp not null,
  constraint pk_reportabuse primary key (id))
;

create table s3file (
  id                        bigint not null,
  bucketname                varchar(255),
  filequalifier             varchar(255),
  filename                  varchar(255),
  filestate                 varchar(255),
  modelref                  bigint,
  constraint pk_s3file primary key (id))
;

create table socommentsco (
  id                        bigint not null,
  commenter_id              bigint,
  collection_id             bigint,
  comment_id                bigint,
  moment                    timestamp not null,
  constraint pk_socommentsco primary key (id))
;

create table socommentspr (
  id                        bigint not null,
  commenter_id              bigint,
  product_id                bigint,
  comment_id                bigint,
  moment                    timestamp not null,
  constraint pk_socommentspr primary key (id))
;

create table sofollows (
  id                        bigint not null,
  follower_id               bigint,
  leader_id                 bigint,
  moment                    timestamp not null,
  constraint pk_sofollows primary key (id))
;

create table sorecommends (
  id                        bigint not null,
  recommender_id            bigint,
  leader_id                 bigint,
  product_id                bigint,
  moment                    timestamp not null,
  constraint pk_sorecommends primary key (id))
;

create table sosuggestsco (
  id                        bigint not null,
  suggester_id              bigint,
  product_id                bigint,
  collection_id             bigint,
  moment                    timestamp not null,
  constraint pk_sosuggestsco primary key (id))
;

create table security_role (
  id                        bigint not null,
  role_name                 varchar(255),
  constraint pk_security_role primary key (id))
;

create table store (
  id                        bigint not null,
  name                      varchar(255),
  storeurl                  varchar(255),
  location                  varchar(255),
  servicearea               varchar(255),
  firstappearance           timestamp not null,
  constraint pk_store primary key (id))
;

create table token_action (
  id                        bigint not null,
  token                     varchar(255),
  target_user_id            bigint,
  type                      varchar(2),
  created                   timestamp,
  expires                   timestamp,
  constraint ck_token_action_type check (type in ('EV','PR','EE')),
  constraint uq_token_action_token unique (token),
  constraint pk_token_action primary key (id))
;

create table users (
  id                        bigint not null,
  email                     varchar(255),
  name                      varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  last_login                timestamp,
  lastattempt               timestamp,
  failedattempt             integer,
  active                    boolean,
  email_validated           boolean,
  gender                    bigint,
  profileimage              varchar(255),
  website                   varchar(255),
  location                  varchar(255),
  biography                 varchar(255),
  registerip                varchar(255),
  registerlocation          varchar(255),
  registerstatus            integer,
  registeredon              timestamp not null,
  constraint uq_users_name unique (name),
  constraint pk_users primary key (id))
;

create table user_address (
  id                        bigint not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  address1                  varchar(255),
  address2                  varchar(255),
  city                      varchar(255),
  province                  varchar(255),
  zip                       integer,
  country                   varchar(255),
  phone                     varchar(255),
  uuid                      varchar(255),
  user_id                   bigint,
  constraint pk_user_address primary key (id))
;

create table user_collection (
  id                        bigint not null,
  colname                   varchar(255),
  isprivate                 boolean,
  coverimage                varchar(255),
  contributor_id            bigint,
  timeofadd                 timestamp not null,
  constraint pk_user_collection primary key (id))
;

create table userinfo (
  id                        bigint not null,
  loginuser_id              bigint,
  lastip                    varchar(255),
  lastlocation              varchar(255),
  lastlogin                 timestamp,
  constraint pk_userinfo primary key (id))
;

create table user_permission (
  id                        bigint not null,
  value                     varchar(255),
  constraint pk_user_permission primary key (id))
;

create table usersubscriptions (
  id                        bigint not null,
  subscriber_id             bigint,
  sofollows                 boolean,
  socommentpr               boolean,
  socommentco               boolean,
  sorecommends              boolean,
  sosuggestsco              boolean,
  advertisements            boolean,
  constraint pk_usersubscriptions primary key (id))
;


create table blog_blog_labels (
  blog_id                        bigint not null,
  blog_labels_id                 bigint not null,
  constraint pk_blog_blog_labels primary key (blog_id, blog_labels_id))
;

create table ownedproduct (
  contributor_id                 bigint not null,
  product_id                     bigint not null,
  constraint pk_ownedproduct primary key (contributor_id, product_id))
;

create table likedproduct (
  contributor_id                 bigint not null,
  product_id                     bigint not null,
  constraint pk_likedproduct primary key (contributor_id, product_id))
;

create table wantedproduct (
  contributor_id                 bigint not null,
  product_id                     bigint not null,
  constraint pk_wantedproduct primary key (contributor_id, product_id))
;

create table collectionadmins (
  contributor_id                 bigint not null,
  user_collection_id             bigint not null,
  constraint pk_collectionadmins primary key (contributor_id, user_collection_id))
;

create table store_user_collection (
  store_id                       bigint not null,
  user_collection_id             bigint not null,
  constraint pk_store_user_collection primary key (store_id, user_collection_id))
;

create table store_contributor (
  store_id                       bigint not null,
  contributor_id                 bigint not null,
  constraint pk_store_contributor primary key (store_id, contributor_id))
;

create table users_security_role (
  users_id                       bigint not null,
  security_role_id               bigint not null,
  constraint pk_users_security_role primary key (users_id, security_role_id))
;

create table users_user_permission (
  users_id                       bigint not null,
  user_permission_id             bigint not null,
  constraint pk_users_user_permission primary key (users_id, user_permission_id))
;

create table users_category (
  users_id                       bigint not null,
  category_id                    bigint not null,
  constraint pk_users_category primary key (users_id, category_id))
;

create table user_collection_product (
  user_collection_id             bigint not null,
  product_id                     bigint not null,
  constraint pk_user_collection_product primary key (user_collection_id, product_id))
;
create sequence addfailed_seq;

create sequence advertisements_seq;

create sequence blacklist_seq;

create sequence blog_seq;

create sequence blog_comment_seq;

create sequence blog_image_seq;

create sequence blog_labels_seq;

create sequence blog_likes_seq;

create sequence cart_seq;

create sequence category_seq;

create sequence collection_comment_seq;

create sequence comment_seq;

create sequence contactus_seq;

create sequence contributor_seq;

create sequence dbreloader_seq;

create sequence eventlog_seq;

create sequence fsearch_seq;

create sequence feedback_seq;

create sequence follow_seq;

create sequence image_beer_seq;

create sequence image_gadgets_seq;

create sequence image_glass_ware_seq;

create sequence image_liquor_seq;

create sequence image_mixology_seq;

create sequence image_toys_seq;

create sequence image_wine_seq;

create sequence infopage_seq;

create sequence ipblacklist_seq;

create sequence item_seq;

create sequence kvarrayproperty_seq;

create sequence kvproperty_seq;

create sequence linked_account_seq;

create sequence product_seq;

create sequence reportabuse_seq;

create sequence s3file_seq;

create sequence socommentsco_seq;

create sequence socommentspr_seq;

create sequence sofollows_seq;

create sequence sorecommends_seq;

create sequence sosuggestsco_seq;

create sequence security_role_seq;

create sequence store_seq;

create sequence token_action_seq;

create sequence users_seq;

create sequence user_address_seq;

create sequence user_collection_seq;

create sequence userinfo_seq;

create sequence user_permission_seq;

create sequence usersubscriptions_seq;

alter table addfailed add constraint fk_addfailed_victim_1 foreign key (victim_id) references users (id);
create index ix_addfailed_victim_1 on addfailed (victim_id);
alter table blacklist add constraint fk_blacklist_author_2 foreign key (author_id) references contributor (id);
create index ix_blacklist_author_2 on blacklist (author_id);
alter table blog add constraint fk_blog_author_3 foreign key (author_id) references contributor (id);
create index ix_blog_author_3 on blog (author_id);
alter table blog_comment add constraint fk_blog_comment_author_4 foreign key (author_id) references contributor (id);
create index ix_blog_comment_author_4 on blog_comment (author_id);
alter table blog_comment add constraint fk_blog_comment_post_5 foreign key (post_id) references blog (id);
create index ix_blog_comment_post_5 on blog_comment (post_id);
alter table blog_image add constraint fk_blog_image_file_6 foreign key (file_id) references s3file (id);
create index ix_blog_image_file_6 on blog_image (file_id);
alter table blog_likes add constraint fk_blog_likes_blog_7 foreign key (blog_id) references blog (id);
create index ix_blog_likes_blog_7 on blog_likes (blog_id);
alter table blog_likes add constraint fk_blog_likes_author_8 foreign key (author_id) references contributor (id);
create index ix_blog_likes_author_8 on blog_likes (author_id);
alter table cart add constraint fk_cart_user_9 foreign key (user_id) references users (id);
create index ix_cart_user_9 on cart (user_id);
alter table category add constraint fk_category_pcategory_10 foreign key (pcategory_id) references category (id);
create index ix_category_pcategory_10 on category (pcategory_id);
alter table collection_comment add constraint fk_collection_comment_author_11 foreign key (author_id) references contributor (id);
create index ix_collection_comment_author_11 on collection_comment (author_id);
alter table collection_comment add constraint fk_collection_comment_collect_12 foreign key (collection_id) references user_collection (id);
create index ix_collection_comment_collect_12 on collection_comment (collection_id);
alter table comment add constraint fk_comment_author_13 foreign key (author_id) references contributor (id);
create index ix_comment_author_13 on comment (author_id);
alter table comment add constraint fk_comment_post_14 foreign key (post_id) references product (id);
create index ix_comment_post_14 on comment (post_id);
alter table contactsheet add constraint fk_contactsheet_inviter_15 foreign key (inviter_id) references users (id);
create index ix_contactsheet_inviter_15 on contactsheet (inviter_id);
alter table contributor add constraint fk_contributor_user_16 foreign key (user_id) references users (id);
create index ix_contributor_user_16 on contributor (user_id);
alter table eventlog add constraint fk_eventlog_productinv_17 foreign key (productinv_id) references product (id);
create index ix_eventlog_productinv_17 on eventlog (productinv_id);
alter table eventlog add constraint fk_eventlog_userinv_18 foreign key (userinv_id) references contributor (id);
create index ix_eventlog_userinv_18 on eventlog (userinv_id);
alter table eventlog add constraint fk_eventlog_collectinv_19 foreign key (collectinv_id) references user_collection (id);
create index ix_eventlog_collectinv_19 on eventlog (collectinv_id);
alter table eventlog add constraint fk_eventlog_storeinv_20 foreign key (storeinv_id) references store (id);
create index ix_eventlog_storeinv_20 on eventlog (storeinv_id);
alter table fsearch add constraint fk_fsearch_userid_21 foreign key (userid_id) references contributor (id);
create index ix_fsearch_userid_21 on fsearch (userid_id);
alter table feedback add constraint fk_feedback_author_22 foreign key (author_id) references contributor (id);
create index ix_feedback_author_22 on feedback (author_id);
alter table infopage add constraint fk_infopage_author_23 foreign key (author_id) references contributor (id);
create index ix_infopage_author_23 on infopage (author_id);
alter table ipblacklist add constraint fk_ipblacklist_author_24 foreign key (author_id) references users (id);
create index ix_ipblacklist_author_24 on ipblacklist (author_id);
alter table item add constraint fk_item_product_25 foreign key (product_id) references product (id);
create index ix_item_product_25 on item (product_id);
alter table item add constraint fk_item_cart_26 foreign key (cart_id) references cart (id);
create index ix_item_cart_26 on item (cart_id);
alter table linked_account add constraint fk_linked_account_user_27 foreign key (user_id) references users (id);
create index ix_linked_account_user_27 on linked_account (user_id);
alter table product add constraint fk_product_Founder_28 foreign key (founder_id) references users (id);
create index ix_product_Founder_28 on product (founder_id);
alter table product add constraint fk_product_category_29 foreign key (category_id) references category (id);
create index ix_product_category_29 on product (category_id);
alter table product add constraint fk_product_pstore_30 foreign key (pstore_id) references store (id);
create index ix_product_pstore_30 on product (pstore_id);
alter table reportabuse add constraint fk_reportabuse_author_31 foreign key (author_id) references contributor (id);
create index ix_reportabuse_author_31 on reportabuse (author_id);
alter table socommentsco add constraint fk_socommentsco_commenter_32 foreign key (commenter_id) references contributor (id);
create index ix_socommentsco_commenter_32 on socommentsco (commenter_id);
alter table socommentsco add constraint fk_socommentsco_collection_33 foreign key (collection_id) references user_collection (id);
create index ix_socommentsco_collection_33 on socommentsco (collection_id);
alter table socommentsco add constraint fk_socommentsco_comment_34 foreign key (comment_id) references collection_comment (id);
create index ix_socommentsco_comment_34 on socommentsco (comment_id);
alter table socommentspr add constraint fk_socommentspr_commenter_35 foreign key (commenter_id) references contributor (id);
create index ix_socommentspr_commenter_35 on socommentspr (commenter_id);
alter table socommentspr add constraint fk_socommentspr_product_36 foreign key (product_id) references product (id);
create index ix_socommentspr_product_36 on socommentspr (product_id);
alter table socommentspr add constraint fk_socommentspr_comment_37 foreign key (comment_id) references comment (id);
create index ix_socommentspr_comment_37 on socommentspr (comment_id);
alter table sofollows add constraint fk_sofollows_follower_38 foreign key (follower_id) references contributor (id);
create index ix_sofollows_follower_38 on sofollows (follower_id);
alter table sofollows add constraint fk_sofollows_leader_39 foreign key (leader_id) references contributor (id);
create index ix_sofollows_leader_39 on sofollows (leader_id);
alter table sorecommends add constraint fk_sorecommends_recommender_40 foreign key (recommender_id) references contributor (id);
create index ix_sorecommends_recommender_40 on sorecommends (recommender_id);
alter table sorecommends add constraint fk_sorecommends_leader_41 foreign key (leader_id) references contributor (id);
create index ix_sorecommends_leader_41 on sorecommends (leader_id);
alter table sorecommends add constraint fk_sorecommends_product_42 foreign key (product_id) references product (id);
create index ix_sorecommends_product_42 on sorecommends (product_id);
alter table sosuggestsco add constraint fk_sosuggestsco_suggester_43 foreign key (suggester_id) references contributor (id);
create index ix_sosuggestsco_suggester_43 on sosuggestsco (suggester_id);
alter table sosuggestsco add constraint fk_sosuggestsco_product_44 foreign key (product_id) references product (id);
create index ix_sosuggestsco_product_44 on sosuggestsco (product_id);
alter table sosuggestsco add constraint fk_sosuggestsco_collection_45 foreign key (collection_id) references user_collection (id);
create index ix_sosuggestsco_collection_45 on sosuggestsco (collection_id);
alter table token_action add constraint fk_token_action_targetUser_46 foreign key (target_user_id) references users (id);
create index ix_token_action_targetUser_46 on token_action (target_user_id);
alter table user_address add constraint fk_user_address_user_47 foreign key (user_id) references users (id);
create index ix_user_address_user_47 on user_address (user_id);
alter table user_collection add constraint fk_user_collection_contributo_48 foreign key (contributor_id) references contributor (id);
create index ix_user_collection_contributo_48 on user_collection (contributor_id);
alter table userinfo add constraint fk_userinfo_loginuser_49 foreign key (loginuser_id) references users (id);
create index ix_userinfo_loginuser_49 on userinfo (loginuser_id);
alter table usersubscriptions add constraint fk_usersubscriptions_subscrib_50 foreign key (subscriber_id) references contributor (id);
create index ix_usersubscriptions_subscrib_50 on usersubscriptions (subscriber_id);



alter table blog_blog_labels add constraint fk_blog_blog_labels_blog_01 foreign key (blog_id) references blog (id);

alter table blog_blog_labels add constraint fk_blog_blog_labels_blog_labe_02 foreign key (blog_labels_id) references blog_labels (id);

alter table ownedproduct add constraint fk_ownedproduct_contributor_01 foreign key (contributor_id) references contributor (id);

alter table ownedproduct add constraint fk_ownedproduct_product_02 foreign key (product_id) references product (id);

alter table likedproduct add constraint fk_likedproduct_contributor_01 foreign key (contributor_id) references contributor (id);

alter table likedproduct add constraint fk_likedproduct_product_02 foreign key (product_id) references product (id);

alter table wantedproduct add constraint fk_wantedproduct_contributor_01 foreign key (contributor_id) references contributor (id);

alter table wantedproduct add constraint fk_wantedproduct_product_02 foreign key (product_id) references product (id);

alter table collectionadmins add constraint fk_collectionadmins_contribut_01 foreign key (contributor_id) references contributor (id);

alter table collectionadmins add constraint fk_collectionadmins_user_coll_02 foreign key (user_collection_id) references user_collection (id);

alter table store_user_collection add constraint fk_store_user_collection_stor_01 foreign key (store_id) references store (id);

alter table store_user_collection add constraint fk_store_user_collection_user_02 foreign key (user_collection_id) references user_collection (id);

alter table store_contributor add constraint fk_store_contributor_store_01 foreign key (store_id) references store (id);

alter table store_contributor add constraint fk_store_contributor_contribu_02 foreign key (contributor_id) references contributor (id);

alter table users_security_role add constraint fk_users_security_role_users_01 foreign key (users_id) references users (id);

alter table users_security_role add constraint fk_users_security_role_securi_02 foreign key (security_role_id) references security_role (id);

alter table users_user_permission add constraint fk_users_user_permission_user_01 foreign key (users_id) references users (id);

alter table users_user_permission add constraint fk_users_user_permission_user_02 foreign key (user_permission_id) references user_permission (id);

alter table users_category add constraint fk_users_category_users_01 foreign key (users_id) references users (id);

alter table users_category add constraint fk_users_category_category_02 foreign key (category_id) references category (id);

alter table user_collection_product add constraint fk_user_collection_product_us_01 foreign key (user_collection_id) references user_collection (id);

alter table user_collection_product add constraint fk_user_collection_product_pr_02 foreign key (product_id) references product (id);

# --- !Downs

drop table if exists addfailed cascade;

drop table if exists advertisements cascade;

drop table if exists blacklist cascade;

drop table if exists blog cascade;

drop table if exists blog_blog_labels cascade;

drop table if exists blog_comment cascade;

drop table if exists blog_image cascade;

drop table if exists blog_labels cascade;

drop table if exists blog_likes cascade;

drop table if exists cart cascade;

drop table if exists category cascade;

drop table if exists collection_comment cascade;

drop table if exists comment cascade;

drop table if exists contactsheet cascade;

drop table if exists contactus cascade;

drop table if exists contributor cascade;

drop table if exists ownedproduct cascade;

drop table if exists likedproduct cascade;

drop table if exists wantedproduct cascade;

drop table if exists collectionadmins cascade;

drop table if exists dbreloader cascade;

drop table if exists eventlog cascade;

drop table if exists fsearch cascade;

drop table if exists feedback cascade;

drop table if exists follow cascade;

drop table if exists image_beer cascade;

drop table if exists image_gadgets cascade;

drop table if exists image_glass_ware cascade;

drop table if exists image_liquor cascade;

drop table if exists image_mixology cascade;

drop table if exists image_toys cascade;

drop table if exists image_wine cascade;

drop table if exists infopage cascade;

drop table if exists ipblacklist cascade;

drop table if exists item cascade;

drop table if exists kvarrayproperty cascade;

drop table if exists kvproperty cascade;

drop table if exists linked_account cascade;

drop table if exists product cascade;

drop table if exists user_collection_product cascade;

drop table if exists reportabuse cascade;

drop table if exists s3file cascade;

drop table if exists socommentsco cascade;

drop table if exists socommentspr cascade;

drop table if exists sofollows cascade;

drop table if exists sorecommends cascade;

drop table if exists sosuggestsco cascade;

drop table if exists security_role cascade;

drop table if exists store cascade;

drop table if exists store_user_collection cascade;

drop table if exists store_contributor cascade;

drop table if exists token_action cascade;

drop table if exists users cascade;

drop table if exists users_security_role cascade;

drop table if exists users_user_permission cascade;

drop table if exists users_category cascade;

drop table if exists user_address cascade;

drop table if exists user_collection cascade;

drop table if exists userinfo cascade;

drop table if exists user_permission cascade;

drop table if exists usersubscriptions cascade;

drop sequence if exists addfailed_seq;

drop sequence if exists advertisements_seq;

drop sequence if exists blacklist_seq;

drop sequence if exists blog_seq;

drop sequence if exists blog_comment_seq;

drop sequence if exists blog_image_seq;

drop sequence if exists blog_labels_seq;

drop sequence if exists blog_likes_seq;

drop sequence if exists cart_seq;

drop sequence if exists category_seq;

drop sequence if exists collection_comment_seq;

drop sequence if exists comment_seq;

drop sequence if exists contactus_seq;

drop sequence if exists contributor_seq;

drop sequence if exists dbreloader_seq;

drop sequence if exists eventlog_seq;

drop sequence if exists fsearch_seq;

drop sequence if exists feedback_seq;

drop sequence if exists follow_seq;

drop sequence if exists image_beer_seq;

drop sequence if exists image_gadgets_seq;

drop sequence if exists image_glass_ware_seq;

drop sequence if exists image_liquor_seq;

drop sequence if exists image_mixology_seq;

drop sequence if exists image_toys_seq;

drop sequence if exists image_wine_seq;

drop sequence if exists infopage_seq;

drop sequence if exists ipblacklist_seq;

drop sequence if exists item_seq;

drop sequence if exists kvarrayproperty_seq;

drop sequence if exists kvproperty_seq;

drop sequence if exists linked_account_seq;

drop sequence if exists product_seq;

drop sequence if exists reportabuse_seq;

drop sequence if exists s3file_seq;

drop sequence if exists socommentsco_seq;

drop sequence if exists socommentspr_seq;

drop sequence if exists sofollows_seq;

drop sequence if exists sorecommends_seq;

drop sequence if exists sosuggestsco_seq;

drop sequence if exists security_role_seq;

drop sequence if exists store_seq;

drop sequence if exists token_action_seq;

drop sequence if exists users_seq;

drop sequence if exists user_address_seq;

drop sequence if exists user_collection_seq;

drop sequence if exists userinfo_seq;

drop sequence if exists user_permission_seq;

drop sequence if exists usersubscriptions_seq;

