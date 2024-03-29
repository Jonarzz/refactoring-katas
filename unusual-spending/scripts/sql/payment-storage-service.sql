-- basic inserts propagating payment-storage-service database
-- a tunnel to the database (port 1521) is required to query the DB from outside the cluster

insert into CATEGORY
values ('travel');
insert into CATEGORY
values ('groceries');

insert into CURRENCY (ALPHA_CODE, LANGUAGE_TAG)
values ('USD', 'en-US');
insert into CURRENCY (ALPHA_CODE, LANGUAGE_TAG)
values ('PLN', 'pl-PL');

insert into PAYMENT (ID, AMOUNT, CURRENCY, CATEGORY, PAYER_USERNAME, DESCRIPTION, TIME)
values (RANDOM_UUID(), 399.99, 'USD', 'travel', 'test_user', 'First payment desc', CURRENT_TIMESTAMP());
insert into PAYMENT (ID, AMOUNT, CURRENCY, CATEGORY, PAYER_USERNAME, DESCRIPTION, TIME)
values (RANDOM_UUID(), 12.35, 'PLN', 'groceries', 'test_user', 'Second payment desc', CURRENT_TIMESTAMP());
