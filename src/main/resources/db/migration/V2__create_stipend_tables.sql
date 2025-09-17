create table stipends (
                          id                    bigserial,
                          type_name             varchar(100) not null unique,
                          amount                numeric(10, 2) not null,
                          primary key (id)
);

create table stipend_settings (
                                  id                     bigserial,
                                  profkom_deduction_percent  numeric(5, 2),
                                  brsm_deduction_percent     numeric(5, 2),
                                  primary key (id)
);

-- Заполнение начальными данными
insert into stipends (type_name, amount)
values
    ('Социальная стипендия', 100.00),
    ('Именная стипендия', 200.00),
    ('Стипендия совета универа', 300.00),
    ('Президентская стипендия', 400.00),
    ('Учебная стипендия от 5 по 5,99', 50.00),
    ('Учебная стипендия от 6 по 7,99', 75.00),
    ('Учебная стипендия от 8 по 8,99', 100.00),
    ('Учебная стипендия от 9 до 10', 125.00);

insert into stipend_settings (profkom_deduction_percent, brsm_deduction_percent)
values
    (3.00, 1.00);