create database gift charset = "utf8mb4";

create table projects
(
    id             int auto_increment primary key,
    repo_id        int          not null,
    name           varchar(255) not null,
    full_name      varchar(255) not null,
    stars          int          not null,
    author         varchar(255) not null,
    url            varchar(255) not null,
    description    text         null,
    size           int          not null,
    default_branch varchar(255) not null,
    readme         longtext     null,
    readme_cn      longtext     null,
    tags           varchar(255) null,
    constraint repo_id unique (repo_id)
);
