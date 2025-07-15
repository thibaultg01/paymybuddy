
USE pay_my_buddy;

DELETE FROM Transaction WHERE id > 0;
DELETE FROM Relation WHERE user_id > 0;
DELETE FROM User WHERE id > 0;

INSERT INTO User (id, email, password, username, first_name, last_name, balance) VALUES
(1, 'alice@example.com', 'passeport123','AliceTest', 'Alice', 'Dupont', 100.00),
(2, 'jack@example.com', 'secretmotdepasse','JackTest', 'Jack', 'Martin', 50.00),
(3, 'carol@example.com', 'carolmdp', 'CarolTest', 'Carol', 'Durand', 75.00);

INSERT INTO Relation (user_id, friend_id) VALUES
(1, 2),  -- Alice ajoute Jack
(2, 1),  -- Bob ajoute Alice
(1, 3);  -- Alice ajoute Carol

INSERT INTO Transaction (sender_id, receiver_id, amount, description) VALUES
(1, 2, 10.00, 'repas'),
(2, 1, 5.00, 'taxi'),
(1, 3, 15.50, 'anniversaire');



