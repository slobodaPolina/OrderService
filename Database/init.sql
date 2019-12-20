create table items (id serial primary key, name varchar(100), price bigint);

create table orders(id serial primary key, status varchar(50), username varchar(100));

create table orders_items(order_id bigint references orders(id), item_id bigint references items(id), amount bigint);

GRANT ALL PRIVILEGES ON TABLE orders to example;

GRANT ALL PRIVILEGES ON TABLE items to example;

GRANT ALL PRIVILEGES ON TABLE orders_items to example;
