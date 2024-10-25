INSERT INTO user_account (id, firstname, lastname, username, password, roles, account_number, is_active)
VALUES (1, 'Ardeshir', 'Ahouri','ardeshir', '$2a$10$rMe5hLcmLB8Q1u0SbEGGXuVPWaj3nfhWssAijAe1sXuMjTkyo2SGK', 'ROLE_ADMIN', 'ACC_111', TRUE);

INSERT INTO user_account (id, firstname, lastname, username, password, roles, account_number, is_active)
VALUES (2, 'Artaxer', 'Ahouri','artaxer' ,'$2a$10$rMe5hLcmLB8Q1u0SbEGGXuVPWaj3nfhWssAijAe1sXuMjTkyo2SGK', 'ROLE_USER', 'ACC_222', TRUE);

INSERT INTO transaction_entity (id, amount, date, account_number, tracking_number, transaction_type, transfer_account_number, description)
VALUES (1, 5000, CURRENT_TIMESTAMP, 'ACC_111', 'T1', 'DEPOSIT', NULL, 'Initial deposit');

INSERT INTO transaction_entity (id, amount, date, account_number, tracking_number, transaction_type, transfer_account_number, description)
VALUES (2, 5000, CURRENT_TIMESTAMP, 'ACC_222', 'T2', 'DEPOSIT', NULL, 'Initial deposit');