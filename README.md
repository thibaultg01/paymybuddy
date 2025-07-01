
# Pay My Buddy - MPD

## Schéma relationnel

### Table `User`
| Colonne     | Type           | Contraintes                |
|-------------|----------------|----------------------------|
| id          | INT            | PRIMARY KEY, AUTO_INCREMENT |
| email       | VARCHAR(255)   | NOT NULL, UNIQUE           |
| password    | VARCHAR(255)   | NOT NULL                   |
| username    | VARCHAR(255)   | NOT NULL, UNIQUE           |
| first_name  | VARCHAR(100)   |                            |
| last_name   | VARCHAR(100)   |                            |
| balance     | DECIMAL(10,2)  | NOT NULL, DEFAULT 0.00     |

---

### Table `Relation`
| Colonne     | Type           | Contraintes                |
|-------------|----------------|----------------------------|
| user_id     | INT            | PRIMARY KEY, FOREIGN KEY → User(id) |
| friend_id   | INT            | PRIMARY KEY, FOREIGN KEY → User(id) |

---

### Table `Transaction`
| Colonne     | Type           | Contraintes                |
|-------------|----------------|----------------------------|
| id          | INT            | PRIMARY KEY, AUTO_INCREMENT |
| sender_id   | INT            | FOREIGN KEY → User(id)     |
| receiver_id | INT            | FOREIGN KEY → User(id)     |
| amount      | DECIMAL(10,2)  | NOT NULL                   |
| description | TEXT           |                            |
| timestamp   | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP  |

---


