
INSERT INTO player(id, username, password) VALUES
(1, 'testuser1', '$2a$10$eP3FFlxW0RYWE9lO2Lb/ZeD8aswR.g07IBxTmAabZJYP9vOi/6a.e'), -- password1
(2, 'testuser2', '$2a$10$4RPXI3HNETyZ04X4ko.KcuaoG6vSbifrTgWSzg7Yf7vFf9kipwn..'), -- password2
(3, 'testuser3', '$2a$10$Hv0G6KNRtt7EO.pnPNxdr.kpxoaDEH4JJxeeH1P9pSKlylI4HhN1e') -- password3
;

INSERT INTO game_template(id, name, min_players, max_players) VALUES
(1, 'game', 2, 2)
;

INSERT INTO game(id, name, game_template, creation_date, join_code) VALUES
(1, 'testuser1s game', 1, '2020-05-01T00:01:00Z', 'code1'),
(2, 'testuser2s game', 1, '2020-05-01T00:02:00Z', 'code2'),
(3, 'testuser3s game', 1, '2020-04-29T00:03:00Z', 'code3'),
(4, 'testuser1s game', 1, '2020-05-01T20:04:00Z', 'code4'),
(5, 'testuser2s game', 1, '2020-05-01T00:05:00Z', 'code5')
;

INSERT INTO game_player(game_id, player_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 1), (4,2),
(5, 2), (5,3)
;

INSERT INTO favourite_setup(id, owner, description, setup_json) VALUES
(1, 1, 'setup1', '[["MARSHAL","FLAG"]]'),
(2, 2, 'setup2', '[]'),
(3, 1, 'setup3', '[]'),
(4, 2, 'setup4', '[]')
;