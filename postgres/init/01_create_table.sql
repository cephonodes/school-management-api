create table facilitators (
    id serial not null,
    primary key (id)
);

create table classrooms (
        id serial not null,
        name varchar(50) not null,
        primary key (id)
);

create table students (
    id serial not null,
    name varchar(50) not null,
    login_id varchar(50) not null,
    classroom_id integer not null,
    primary key (id),
    foreign key (classroom_id) references classrooms(id)
);

create table facilitator_classroom_relation (
    id serial not null,
    facilitator_id integer not null,
    classroom_id integer not null,
    primary key (id),
    foreign key (facilitator_id) references facilitators(id),
    foreign key (classroom_id) references classrooms(id)
);