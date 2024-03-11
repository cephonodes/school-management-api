create table facilitators (
    id serial not null,
    primary key (id)
);
create index facilitators_id_index on facilitators (id);

create table classrooms (
        id serial not null,
        name varchar(50) not null,
        primary key (id)
);
create index classrooms_id_index on classrooms (id);

create table students (
    id serial not null,
    name varchar(50) not null,
    login_id varchar(50) not null,
    classroom_id integer not null,
    primary key (id),
    foreign key (classroom_id) references classrooms(id)
);
create index students_name_index on students (name);
create unique index students_login_id_index on students (login_id);

create table facilitator_classroom_relation (
    id serial not null,
    facilitator_id integer not null,
    classroom_id integer not null,
    primary key (id),
    foreign key (facilitator_id) references facilitators(id),
    foreign key (classroom_id) references classrooms(id)
);
create unique index facilitator_classroom_relation_foreign_keys_index on facilitator_classroom_relation (facilitator_id, classroom_id);