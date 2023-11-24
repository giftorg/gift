create table projects
(
    id          int auto_increment primary key,
    item_name   varchar(255) null,
    stars       int          null,
    login_name  varchar(255) null,
    repository  varchar(255) not null,
    description varchar(255) null,
    constraint repository unique (repository)
)