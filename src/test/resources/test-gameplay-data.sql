
INSERT INTO player(id, username, password) VALUES
(1, 'testuser1', '$2a$10$eP3FFlxW0RYWE9lO2Lb/ZeD8aswR.g07IBxTmAabZJYP9vOi/6a.e'), -- password1
(2, 'testuser2', '$2a$10$4RPXI3HNETyZ04X4ko.KcuaoG6vSbifrTgWSzg7Yf7vFf9kipwn..') -- password2
;

INSERT INTO game_template(id, name, min_players, max_players) VALUES
(1, 'game', 2, 2)
;

INSERT INTO game(id, name, game_template, creation_date, join_code, phase) VALUES
(5, 'testuser1s game', 1, '2020-05-01T20:04:00Z', 'code4', 'WAITING_FOR_SETUP_2_PLAYERS')
;

INSERT INTO game_player(game_id, player_id) VALUES
(5, 1),(5, 2)
;
