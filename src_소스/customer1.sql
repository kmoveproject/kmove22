create table customer1(
    code number(10) primary key,
    name varchar(30) not null,
    email varchar(30),
    tel varchar(35)
)

insert into customer1 values (
     1, '강지아', 'jeea@shop.com', '02-137-1484');
insert into customer1 values (
     2, '이장미', 'rose@shop.com', '02-523-8715');
insert into customer1 values (
     3, '김백합', 'lily@shop.com', '02-789-2359');

select * from customer1;