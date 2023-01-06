create database eshop;

use eshop;

create table customers (
	-- primary key
    name varchar(32) not null,
    address varchar(128) not null,
    email varchar(128) not null,

    primary key(name)
);

INSERT INTO customers(name, address, email) 
VALUES
    ("fred", "201 Cobblestone Lane", "fredflintstone@bedrock.com"),
    ("sherlock", "221B Baker Street, London", "sherlock@consultingdetective.org"),
    ("spongebob", "124 Conch Street, Bikini Bottom", "spongebob@yahoo.com"),
    ("jessica", "698 Candlewood Land, Cabot Cove", "fletcher@gmail.com"),
    ("dursley", "4 Privet Drive, Little Whinging, Surrey", "dursley@gmail.com");


create table purchase_order (
    -- primary key 
    order_id varchar(8) not null,
    delivery_id varchar (8) not null,
    name varchar (32) not null,
    address varchar(128) not null,
    email varchar (128) not null,
    status varchar(128) not null,
    order_date date not null,

    -- foreign key
    primary key(order_id),
    constraint fk_name
	foreign key (name)
    references customers(name)

);

create table line_item (

    item_id int auto_increment not null,
    order_id varchar(8) not null,
    item varchar(128) not null,
    quantity int not null,

    primary key(item_id),
    constraint fk_order_id
    foreign key (order_id)
    references purchase_order(order_id)
);