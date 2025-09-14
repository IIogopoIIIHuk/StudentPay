create table users (
   id                    bigserial,
   username              varchar(30) not null unique,
   password              varchar(80) not null,
   email                 varchar(50) unique,
    name                  varchar(100),
    phone                 varchar(20),
    enabled               boolean default false,
    primary key (id)
);

create table roles (
    id                    serial,
    name                  varchar(50) not null,
    primary key (id)
);

CREATE TABLE users_roles (
    user_id               bigint not null,
    role_id               int not null,
    primary key (user_id, role_id),
    foreign key (user_id) references users (id),
    foreign key (role_id) references roles (id)
);

insert into roles (name)
values
    ('ROLE_USER'), ('ROLE_DEAN_EMPLOYEE'), ('ROLE_ACCOUNTANT');

insert into users (username, password, email, name, phone, enabled)
values
    ('student', '$2a$04$Fx/SX9.BAvtPlMyIIqqFx.hLY2Xp8nnhpzvEEVINvVpwIPbA3v/.i', 'student@gmail.com', 'Студент', '+375295784958', true), -- student (100)
    ('dean_employee', '$2a$04$Fx/SX9.BAvtPlMyIIqqFx.hLY2Xp8nnhpzvEEVINvVpwIPbA3v/.i', 'dean_employee@gmail.com', 'Сотрудник деканата', '++375573795849', true), -- dean (100)
    ('accountant', '$2a$04$Fx/SX9.BAvtPlMyIIqqFx.hLY2Xp8nnhpzvEEVINvVpwIPbA3v/.i', 'accountant@gmail.com', 'Бухгалтер', '+375295896783', true); -- accountant (100)

insert into users_roles (user_id, role_id)
values
    (1, 1),
    (2, 2),
    (3, 3);