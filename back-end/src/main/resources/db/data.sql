-- Insert roles
INSERT INTO user_role_types (id, name) VALUES (1, 'ROLE_USER') ON CONFLICT (id) DO NOTHING;
INSERT INTO user_role_types (id, name) VALUES (2, 'ROLE_MODERATOR') ON CONFLICT (id) DO NOTHING;
INSERT INTO user_role_types (id, name) VALUES (3, 'ROLE_ADMIN') ON CONFLICT (id) DO NOTHING;

-- Insert genders
INSERT INTO user_gender_types (id, name) VALUES (1, 'MALE') ON CONFLICT (id) DO NOTHING;
INSERT INTO user_gender_types (id, name) VALUES (2, 'FEMALE') ON CONFLICT (id) DO NOTHING;
INSERT INTO user_gender_types (id, name) VALUES (3, 'OTHER') ON CONFLICT (id) DO NOTHING;

-- Insert connection types
INSERT INTO connection_types (id, name) VALUES (1, 'SEEN') ON CONFLICT (id) DO NOTHING;
INSERT INTO connection_types (id, name) VALUES (2, 'OPENED_PROFILE') ON CONFLICT (id) DO NOTHING;
INSERT INTO connection_types (id, name) VALUES (3, 'JUST_FRIENDS') ON CONFLICT (id) DO NOTHING;
INSERT INTO connection_types (id, name) VALUES (4, 'MAYBE_MORE') ON CONFLICT (id) DO NOTHING;
INSERT INTO connection_types (id, name) VALUES (5, 'BLOCKED') ON CONFLICT (id) DO NOTHING;
INSERT INTO connection_types (id, name) VALUES (6, 'PENDING') ON CONFLICT (id) DO NOTHING;

-- Insert activity log types
INSERT INTO activity_log_types (id, name) VALUES (1, 'CREATED') ON CONFLICT (id) DO NOTHING;
INSERT INTO activity_log_types (id, name) VALUES (2, 'VERIFIED') ON CONFLICT (id) DO NOTHING;
INSERT INTO activity_log_types (id, name) VALUES (3, 'LOGIN') ON CONFLICT (id) DO NOTHING;
INSERT INTO activity_log_types (id, name) VALUES (4, 'LOGOUT') ON CONFLICT (id) DO NOTHING;

-- Insert attribute change types
INSERT INTO attribute_change_types (id, name) VALUES (1, 'CREATED') ON CONFLICT (id) DO NOTHING;
INSERT INTO attribute_change_types (id, name) VALUES (2, 'GENDER') ON CONFLICT (id) DO NOTHING;
INSERT INTO attribute_change_types (id, name) VALUES (3, 'BIRTHDATE') ON CONFLICT (id) DO NOTHING;
INSERT INTO attribute_change_types (id, name) VALUES (4, 'LOCATION') ON CONFLICT (id) DO NOTHING;

-- Insert preference change types
INSERT INTO preference_change_types (id, name) VALUES (1, 'CREATED') ON CONFLICT (id) DO NOTHING;
INSERT INTO preference_change_types (id, name) VALUES (2, 'GENDER') ON CONFLICT (id) DO NOTHING;
INSERT INTO preference_change_types (id, name) VALUES (3, 'AGE_MIN') ON CONFLICT (id) DO NOTHING;
INSERT INTO preference_change_types (id, name) VALUES (4, 'AGE_MAX') ON CONFLICT (id) DO NOTHING;
INSERT INTO preference_change_types (id, name) VALUES (5, 'DISTANCE') ON CONFLICT (id) DO NOTHING;
INSERT INTO preference_change_types (id, name) VALUES (6, 'TOLERANCE') ON CONFLICT (id) DO NOTHING;

-- Insert profile change types
INSERT INTO profile_change_types (id, name) VALUES (1, 'CREATED') ON CONFLICT (id) DO NOTHING;
INSERT INTO profile_change_types (id, name) VALUES (2, 'AGE') ON CONFLICT (id) DO NOTHING;
INSERT INTO profile_change_types (id, name) VALUES (3, 'BIO') ON CONFLICT (id) DO NOTHING;
INSERT INTO profile_change_types (id, name) VALUES (4, 'PHOTO') ON CONFLICT (id) DO NOTHING;
INSERT INTO profile_change_types (id, name) VALUES (5, 'INTERESTS') ON CONFLICT (id) DO NOTHING;

-- Insert hobby categories
INSERT INTO hobby (id, name, category, sub_category) VALUES (1, '3D printing', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (2, 'Acrobatics', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (3, 'Acting', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (4, 'Amateur radio', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (5, 'Animation', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (6, 'Aquascaping', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (7, 'Astrology', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (8, 'Astronomy', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (9, 'Baking', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (10, 'Baton twirling', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (11, 'Blogging', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (12, 'Building', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (13, 'Board/tabletop games', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (14, 'Book discussion clubs', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (15, 'Book restoration', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (16, 'Bowling', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (17, 'Brazilian jiu-jitsu', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (18, 'Breadmaking', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (19, 'Bullet journaling', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (20, 'Cabaret', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (21, 'Calligraphy', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (22, 'Candle making', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (23, 'Candy making', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (24, 'Car fixing & building', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (25, 'Card games', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (26, 'Cheesemaking', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (27, 'Cleaning', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (28, 'Clothesmaking', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (29, 'Coffee roasting', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (30, 'Collecting', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (31, 'Coloring', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (32, 'Computer programming', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (33, 'Confectionery', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (34, 'Cooking', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (35, 'Cosplaying', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (36, 'Couponing', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (37, 'Craft', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (38, 'Creative writing', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (39, 'Crocheting', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (40, 'Cross-stitch', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (41, 'Crossword puzzles', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (42, 'Cryptography', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (43, 'Cue sports', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (44, 'Dance', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (45, 'Digital arts', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (46, 'Distro Hopping', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (47, 'DJing', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (48, 'Do it yourself', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (49, 'Drama', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (50, 'Drawing', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (51, 'Drink mixing', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (52, 'Drinking', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (53, 'Electronic games', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (54, 'Electronics', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (55, 'Embroidery', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (56, 'Experimenting', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (57, 'Fantasy sports', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (58, 'Fashion', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (59, 'Fashion design', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (60, 'Fishkeeping', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (61, 'Filmmaking', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (62, 'Flower arranging', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (63, 'Fly tying', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (64, 'Foreign language learning', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (65, 'Furniture building', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (66, 'Gaming', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (67, 'Genealogy', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (68, 'Gingerbread house making', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (69, 'Glassblowing', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (70, 'Graphic design', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (71, 'Gunsmithing', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (72, 'Gymnastics', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (73, 'Hacking', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (74, 'Herp keeping', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (75, 'Home improvement', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (76, 'Homebrewing', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (77, 'Houseplant care', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (78, 'Hula hooping', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (79, 'Humor', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (80, 'Hydroponics', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (81, 'Ice skating', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (82, 'Jewelry making', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (83, 'Jigsaw puzzles', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (84, 'Journaling', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (85, 'Juggling', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (86, 'Karaoke', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (87, 'Karate', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (88, 'Kendama', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (89, 'Knife making', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (90, 'Knitting', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (91, 'Knot tying', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (92, 'Kombucha brewing', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (93, 'Lace making', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (94, 'Lapidary', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (95, 'Leather crafting', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (96, 'Lego building', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (97, 'Lock picking', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (98, 'Listening to music', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (99, 'Listening to podcasts', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (100, 'Machining', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (101, 'Macrame', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (102, 'Magic', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (103, 'Makeup', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (104, 'Mazes (indoor/outdoor)', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (105, 'Metalworking', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (106, 'Model building', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (107, 'Model engineering', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (108, 'Nail art', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (109, 'Needlepoint', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (110, 'Origami', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (111, 'Painting', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (112, 'Palmistry', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (113, 'Pet adoption & fostering', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (114, 'Philately', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (115, 'Photography', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (116, 'Practical jokes', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (117, 'Pressed flower craft', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (118, 'Playing musical instruments', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (119, 'Poi', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (120, 'Pottery', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (121, 'Powerlifting', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (122, 'Puzzles', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (123, 'Quilling', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (124, 'Quilting', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (125, 'Quizzes', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (126, 'Radio-controlled model', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (127, 'Rail transport modeling', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (128, 'Rapping', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (129, 'Reading', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (130, 'Refinishing', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (131, 'Reiki', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (132, 'Robot combat', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (133, 'Rubik''s Cube', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (134, 'Scrapbooking', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (135, 'Sculpting', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (136, 'Sewing', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (137, 'Shoemaking', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (138, 'Singing', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (139, 'Sketching', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (140, 'Skipping rope', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (141, 'Slot car', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (142, 'Soapmaking', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (143, 'Social media', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (144, 'Spreadsheets', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (145, 'Stand-up comedy', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (146, 'Stamp collecting', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (147, 'Table tennis', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (148, 'Tarot', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (149, 'Taxidermy', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (150, 'Thrifting', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (151, 'Video editing', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (152, 'Video game developing', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (153, 'Video gaming', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (154, 'Watching movies', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (155, 'Watching television', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (156, 'Videography', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (157, 'Virtual reality', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (158, 'Waxing', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (159, 'Weaving', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (160, 'Weight training', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (161, 'Welding', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (162, 'Whittling', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (163, 'Wikipedia editing', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (164, 'Winemaking', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (165, 'Wood carving', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (166, 'Woodworking', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (167, 'Worldbuilding', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (168, 'Writing', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (169, 'Word searches', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (170, 'Yo-yoing', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (171, 'Yoga', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (172, 'Zumba', 'General', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (173, 'Amusement park visiting', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (174, 'Air sports', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (175, 'Airsoft', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (176, 'Amateur geology', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (177, 'Archery', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (178, 'Astronomy', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (179, 'Backpacking', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (180, 'Badminton', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (181, 'BASE jumping', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (182, 'Baseball', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (183, 'Basketball', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (184, 'Beekeeping', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (185, 'Birdwatching', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (186, 'Blacksmithing', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (187, 'BMX', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (188, 'Board sports', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (189, 'Bodybuilding', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (190, 'Bonsai', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (191, 'Butterfly watching', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (192, 'Bus riding', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (193, 'Camping', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (194, 'Canoeing', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (195, 'Canyoning', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (196, 'Car riding', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (197, 'Caving', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (198, 'Composting', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (199, 'Cycling', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (200, 'Dowsing', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (201, 'Driving', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (202, 'Farming', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (203, 'Fishing', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (204, 'Flag football', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (205, 'Flower growing', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (206, 'Flying', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (207, 'Flying disc', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (208, 'Foraging', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (209, 'Fossicking', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (210, 'Freestyle football', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (211, 'Gardening', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (212, 'Geocaching', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (213, 'Ghost hunting', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (214, 'Gold prospecting', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (215, 'Graffiti', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (216, 'Handball', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (217, 'Herbalism', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (218, 'Herping', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (219, 'High-power rocketry', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (220, 'Hiking', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (221, 'Hobby horsing', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (222, 'Hobby tunneling', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (223, 'Hooping', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (224, 'Horseback riding', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (225, 'Hunting', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (226, 'Inline skating', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (227, 'Jogging', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (228, 'Jumping rope', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (229, 'Kayaking', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (230, 'Kite flying', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (231, 'Kitesurfing', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (232, 'Lacrosse', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (233, 'LARPing', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (234, 'Letterboxing', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (235, 'Longboarding', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (236, 'Martial arts', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (237, 'Metal detecting', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (238, 'Meteorology', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (239, 'Motor sports', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (240, 'Mountain biking', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (241, 'Mountaineering', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (242, 'Museum visiting', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (243, 'Mushroom hunting', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (244, 'Netball', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (245, 'Nordic skating', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (246, 'Orienteering', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (247, 'Paintball', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (248, 'Parkour', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (249, 'Photography', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (250, 'Podcast hosting', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (251, 'Polo', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (252, 'Public transport riding', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (253, 'Rafting', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (254, 'Railway journeys', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (255, 'Rappelling', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (256, 'Road biking', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (257, 'Rock climbing', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (258, 'Roller skating', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (259, 'Rugby', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (260, 'Running', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (261, 'Radio-controlled model', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (262, 'Sailing', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (263, 'Sand art', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (264, 'Scouting', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (265, 'Scuba diving', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (266, 'Sculling', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (267, 'Shooting', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (268, 'Shopping', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (269, 'Shuffleboard', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (270, 'Skateboarding', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (271, 'Skiing', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (272, 'Skimboarding', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (273, 'Skydiving', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (274, 'Slacklining', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (275, 'Snowboarding', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (276, 'Snowmobiling', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (277, 'Snowshoeing', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (278, 'Soccer', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (279, 'Stone skipping', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (280, 'Sun bathing', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (281, 'Surfing', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (282, 'Survivalism', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (283, 'Swimming', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (284, 'Taekwondo', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (285, 'Tai chi', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (286, 'Tennis', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (287, 'Topiary', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (288, 'Tourism', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (289, 'Thru-hiking', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (290, 'Trade fair visiting', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (291, 'Travel', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (292, 'Urban exploration', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (293, 'Vacation', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (294, 'Vegetable farming', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (295, 'Videography', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (296, 'Vehicle restoration', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (297, 'Walking', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (298, 'Water sports', 'General', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (299, 'Astronomy', 'Educational', '') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (300, 'Biology', 'Educational', '') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (301, 'Chemistry', 'Educational', '') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (302, 'Electrochemistry', 'Educational', '') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (303, 'Physics', 'Educational', '') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (304, 'Psychology', 'Educational', '') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (305, 'Sports science', 'Educational', '') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (306, 'Geography', 'Educational', '') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (307, 'History', 'Educational', '') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (308, 'Mathematics', 'Educational', '') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (309, 'Railway studies', 'Educational', '') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (310, 'Action figure', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (311, 'Antiquing', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (312, 'Ant-keeping', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (313, 'Art collecting', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (314, 'Book collecting', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (315, 'Button collecting', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (316, 'Cartophily', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (317, 'Coin collecting', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (318, 'Comic book collecting', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (319, 'Deltiology', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (320, 'Die-cast toy', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (321, 'Digital hoarding', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (322, 'Dolls', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (323, 'Element collecting', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (324, 'Ephemera collecting', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (325, 'Fusilately', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (326, 'Knife collecting', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (327, 'Lotology', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (329, 'Fingerprint collecting', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (330, 'Perfume', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (331, 'Phillumeny', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (332, 'Radio-controlled model', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (333, 'Rail transport modelling', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (334, 'Record collecting', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (335, 'Rock tumbling', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (336, 'Scutelliphily', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (337, 'Shoes', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (338, 'Slot car', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (339, 'Sports memorabilia', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (340, 'Stamp collecting', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (341, 'Stuffed toy collecting', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (342, 'Tea bag collecting', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (343, 'Ticket collecting', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (344, 'Toys', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (345, 'Transit map collecting', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (346, 'Video game collecting', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (347, 'Vintage cars', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (348, 'Vintage clothing', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (349, 'Vinyl Records', 'Collection', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (350, 'Antiquities', 'Collection', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (351, 'Auto audiophilia', 'Collection', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (352, 'Flower collecting and pressing', 'Collection', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (353, 'Fossil hunting', 'Collection', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (354, 'Insect collecting', 'Collection', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (355, 'Magnet fishing', 'Collection', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (356, 'Metal detecting', 'Collection', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (357, 'Mineral collecting', 'Collection', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (358, 'Rock balancing', 'Collection', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (359, 'Sea glass collecting', 'Collection', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (360, 'Seashell collecting', 'Collection', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (361, 'Stone collecting', 'Collection', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (362, 'Animal fancy', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (363, 'Axe throwing', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (364, 'Backgammon', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (365, 'Badminton', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (366, 'Baton twirling', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (367, 'Beauty pageants', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (368, 'Billiards', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (369, 'Bowling', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (370, 'Boxing', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (371, 'Bridge', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (372, 'Checkers (draughts)', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (373, 'Cheerleading', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (374, 'Chess', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (375, 'Color guard', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (376, 'Cribbage', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (377, 'Curling', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (378, 'Dancing', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (379, 'Darts', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (380, 'Debate', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (381, 'Dominoes', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (382, 'Eating', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (383, 'Esports', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (384, 'Fencing', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (385, 'Go', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (386, 'Gymnastics', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (387, 'Ice hockey', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (388, 'Ice skating', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (389, 'Judo', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (390, 'Jujitsu', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (391, 'Kabaddi', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (392, 'Knowledge/word games', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (393, 'Laser tag', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (394, 'Longboarding', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (395, 'Mahjong', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (396, 'Marbles', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (397, 'Martial arts', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (398, 'Model United Nations', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (399, 'Poker', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (400, 'Pool', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (401, 'Role-playing games', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (402, 'Shogi', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (403, 'Slot car racing', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (404, 'Speedcubing', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (405, 'Sport stacking', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (406, 'Table football', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (407, 'Table tennis', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (408, 'Volleyball', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (409, 'Weightlifting', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (410, 'Wrestling', 'Competitive', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (411, 'Airsoft', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (412, 'Archery', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (413, 'Association football', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (414, 'Australian rules football', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (415, 'Auto racing', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (416, 'Baseball', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (417, 'Beach volleyball', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (418, 'Breakdancing', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (419, 'Climbing', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (420, 'Cricket', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (421, 'Croquet', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (422, 'Cycling', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (423, 'Disc golf', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (424, 'Dog sport', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (425, 'Equestrianism', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (426, 'Exhibition drill', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (427, 'Field hockey', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (428, 'Figure skating', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (429, 'Fishing', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (430, 'Footbag', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (431, 'Frisbee', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (432, 'Golfing', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (433, 'Handball', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (434, 'Horseback riding', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (435, 'Horseshoes', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (436, 'Iceboat racing', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (437, 'Jukskei', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (438, 'Kart racing', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (439, 'Knife throwing', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (440, 'Lacrosse', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (441, 'Longboarding', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (442, 'Long-distance running', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (443, 'Marching band', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (444, 'Model aircraft', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (445, 'Orienteering', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (446, 'Pickleball', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (447, 'Quidditch', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (448, 'Race walking', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (449, 'Racquetball', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (450, 'Radio-controlled car racing', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (451, 'Roller derby', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (452, 'Rugby league football', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (453, 'Sculling', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (454, 'Shooting sport', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (455, 'Skateboarding', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (456, 'Skiing', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (457, 'Sled dog racing', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (458, 'Softball', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (459, 'Speed skating', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (460, 'Squash', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (461, 'Surfing', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (462, 'Swimming', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (463, 'Table tennis', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (464, 'Tennis', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (465, 'Tennis polo', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (466, 'Tether car', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (467, 'Tour skating', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (468, 'Tourism', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (469, 'Trapshooting', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (470, 'Triathlon', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (471, 'Ultimate frisbee', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (472, 'Volleyball', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (473, 'Water polo', 'Competitive', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (474, 'Fishkeeping', 'Observation', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (475, 'Learning', 'Observation', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (476, 'Meditation', 'Observation', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (477, 'Microscopy', 'Observation', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (478, 'Reading', 'Observation', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (479, 'Research', 'Observation', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (480, 'Shortwave listening', 'Observation', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (481, 'Audiophile', 'Observation', 'Indoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (482, 'Aircraft spotting', 'Observation', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (483, 'Amateur astronomy', 'Observation', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (484, 'Birdwatching', 'Observation', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (485, 'Bus spotting', 'Observation', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (486, 'Geocaching', 'Observation', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (487, 'Gongoozling', 'Observation', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (488, 'Herping', 'Observation', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (489, 'Hiking', 'Observation', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (490, 'Meteorology', 'Observation', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (491, 'Photography', 'Observation', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (492, 'Satellite watching', 'Observation', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (493, 'Trainspotting', 'Observation', 'Outdoors') ON CONFLICT (id) DO NOTHING;
INSERT INTO hobby (id, name, category, sub_category) VALUES (494, 'Whale watching', 'Observation', 'Outdoors') ON CONFLICT (id) DO NOTHING;

-- ----------------------------------------------------------------------
-- Sample data inserts for testing
-- ----------------------------------------------------------------------

-- Insert sample users into the "users" table.
INSERT INTO users (id, email, number, state) 
VALUES 
  (1, 'john.doe@example.com', '+37253414494', 'ACTIVE'),
  (2, 'jane.smith@example.com', '+37255433546', 'ACTIVE'),
  (3, 'alice.johnson@example.com', '+37255554445', 'ACTIVE'),
  (4, 'toomas.saar@example.com', '+37255554444', 'ACTIVE'),
  (5, 'madis.paidest@example.com', '+37255554443', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- Update the users_id_seq sequence, because we don't use JPA, JPA doesn't know where the sequence is
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- Insert user authentication data with a precomputed fixed BCrypt hash for the password "123456"
INSERT INTO user_auth_data (user_id, password, recovery)
VALUES 
  (1, '$2a$10$sNInvglURuXPEWjojzv/.uPsHsCxBmtUkeevnn0K7BnWdJCrvBwcK', 123456),
  (2, '$2a$10$sNInvglURuXPEWjojzv/.uPsHsCxBmtUkeevnn0K7BnWdJCrvBwcK', 123456),
  (3, '$2a$10$sNInvglURuXPEWjojzv/.uPsHsCxBmtUkeevnn0K7BnWdJCrvBwcK', 123456),
  (4, '$2a$10$sNInvglURuXPEWjojzv/.uPsHsCxBmtUkeevnn0K7BnWdJCrvBwcK', 123456),
  (5, '$2a$10$sNInvglURuXPEWjojzv/.uPsHsCxBmtUkeevnn0K7BnWdJCrvBwcK', 123456)
ON CONFLICT (user_id) DO NOTHING;

-- Insert corresponding user profiles into the "user_profile" table.
INSERT INTO user_profile (user_id, first_name, last_name, alias, city) 
VALUES 
  (1, 'John', 'Doe', 'johnny', 'Tallinn'),
  (2, 'Jane', 'Smith', 'jane', 'Tartu'),
  (3, 'Alice', 'Johnson', 'alice', 'Pärnu'),
  (4, 'Toomas', 'Saar', 'toomas', 'Paide'),
  (5, 'Madis', 'Paidest', 'madis', 'Tallinn')
ON CONFLICT (user_id) DO NOTHING;

-- Insert sample user attributes into the "user_attributes" table.
-- NOTE: "gender_id" values reference user_gender_types (1 = MALE, 2 = FEMALE)
--       and location is stored as an array literal (PostgreSQL syntax).
INSERT INTO user_attributes (user_id, gender_id, birthdate, location) 
VALUES 
  (1, 1, '1990-01-01', '{58.3859,24.5002}'),
  (2, 1, '1992-05-10', '{58.3859,24.5002}'),
  (3, 2, '1988-11-20', '{58.3859,24.5002}'),
  (4, 2, '1990-01-01', '{58.3859,24.5002}'),
  (5, 1, '1990-01-01', '{58.3859,24.5002}')
ON CONFLICT (user_id) DO NOTHING;

-- Insert sample user preferences into the "user_preferences" table.
-- Here we assume the gender preference (stored as gender_id) indicates the
-- preferred gender for matching (e.g. John (id 1) prefers females which is id 2).
INSERT INTO user_preferences (user_id, gender_id, age_min, age_max, distance, probability_tolerance)
VALUES 
  (1, 2, 18, 100, 30, 0.5),  -- John prefers females
  (2, 2, 18, 100, 30, 0.5),  -- Jane prefers males
  (3, 1, 18, 100, 30, 0.5),   -- Alice prefers males
  (4, 1, 18, 100, 30, 0.5),
  (5, 2, 18, 100, 30, 0.5)
ON CONFLICT (user_id) DO NOTHING;

-- Insert sample user scores into the "user_scores" table.
INSERT INTO user_scores (user_id, current_score, vibe_probability, current_blind)
VALUES 
  (1, 1000, 1.0, 1000),
  (2, 1000, 1.0, 1000),
  (3, 1000, 1.0, 1000),
  (4, 1000, 1.0, 1000),
  (5, 1000, 1.0, 1000)
ON CONFLICT (user_id) DO NOTHING;

-- Insert user hobbies
INSERT INTO user_profile_hobbies (user_profile_id, hobby_id)
VALUES
  (1, 1),
  (2, 1),
  (3, 1),
  (4, 1),
  (5, 1)
ON CONFLICT (user_profile_id, hobby_id) DO NOTHING;

INSERT INTO dating_pool (
    profile_id, 
    my_gender_id, 
    looking_for_gender_id, 
    my_age, 
    age_min, 
    age_max, 
    my_location, 
    actual_score
) VALUES (
    1, 
    1,  -- Male
    2,  -- Looking for Female
    34, -- 1990-01-01 = 34 years old
    18, -- 18 years old age min
    100, -- 100 age max
    'tk3e2s', -- tk3e2s ==58.3859, 24.5002
    1000
), (
    2, 
    1,  -- Male
    2,  -- Looking for Female
    32, -- 1992-05-10 = 32 years old
    18, -- 18 years old age min
    100, -- 100 age max
    'tk3e2s', -- tk3e2s ==58.3859, 24.5002
    1000
), (
    3, 
    2,  -- Female
    1,  -- Looking for Male
    36, -- 1988-11-20 = 36 years old
    18, -- 18 years old age min
    100, -- 100 age max
    'tk3e2s', -- tk3e2s ==58.3859, 24.5002
    1000
), (
    4, 
    2,  -- Female
    1,  -- Looking for Male
    34, -- 1990-01-01 = 34 years old
    18, -- 18 years old age min
    100, -- 100 age max
    'tk3e2s', -- tk3e2s ==58.3859, 24.5002
    1000
), (
    5, 
    1,  -- Male
    2,  -- Looking for Female
    34, -- 1990-01-01 = 34 years old
    18, -- 18 years old age min
    100, -- 100 age max
    'tk3e2s', -- tk3e2s ==58.3859, 24.5002
    1000
) 
ON CONFLICT (profile_id) DO NOTHING;


-- Insert geo hashes into separate table
INSERT INTO dating_pool_geo_hashes (dating_pool_id, geo_hash)
VALUES
  (1, 'tk3'),
  (2, 'tk3'),
  (3, 'tk3'),
  (4, 'tk3'),
  (5, 'tk3');

-- Insert hobbies into separate table
INSERT INTO dating_pool_hobbies (dating_pool_id, hobby_id)
VALUES
  (1, 1),
  (2, 1),
  (3, 1),
  (4, 1),
  (5, 1);

INSERT INTO connections (id) 
VALUES (1), (2), (3), (4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO user_connections (connection_id, user_id)
VALUES 
  (1, 1), (1, 2),  -- John and Jane
  (2, 1), (2, 3),  -- John and Alice
  (3, 3), (3, 4),  -- Alice and TestMatch
  (4, 2), (4, 4)  -- Jane and TestMatch
ON CONFLICT DO NOTHING;

-- Update sequence for connections
SELECT setval('connections_id_seq', (SELECT MAX(id) FROM connections));

-- Insert connection states for existing connections (making them all ACCEPTED)
INSERT INTO connection_log (connection_id, user_id, status, timestamp, requester_id, target_id) 
VALUES 
  (1, 1, 'ACCEPTED', NOW(), 1, 2),  -- John and Jane
  (2, 1, 'ACCEPTED', NOW(), 1, 3),  -- John and Alice
  (3, 3, 'ACCEPTED', NOW(), 3, 4),  -- Alice and TestMatch
  (4, 2, 'ACCEPTED', NOW(), 2, 4)   -- Jane and TestMatch
ON CONFLICT DO NOTHING;

-- Insert user messages
INSERT INTO user_messages (id, connection_id, sender_id, content, created_at)
VALUES 
  -- John and Jane conversation
  (1, 1, 1, 'Hey Jane, how are you doing?', NOW() - INTERVAL '2 days'),
  (2, 1, 2, 'Hi John! I am doing great. How about you?', NOW() - INTERVAL '2 days'),
  (3, 1, 1, 'I am good too. Working on a new project.', NOW() - INTERVAL '1 day'),
  
  -- John and Alice conversation
  (4, 2, 1, 'Hello Alice! Nice to meet you.', NOW() - INTERVAL '3 days'),
  (5, 2, 3, 'Hi John! Nice to meet you too.', NOW() - INTERVAL '3 days'),
  (6, 2, 3, 'I saw you like programming?', NOW() - INTERVAL '2 days'),
  
  -- Alice and TestMatch conversation
  (7, 3, 3, 'Hey there! I am Alice.', NOW() - INTERVAL '1 day'),
  (8, 3, 4, 'Hi Alice! I am Test. How are you?', NOW() - INTERVAL '1 day'),
  
  -- Jane and TestMatch conversation
  (9, 4, 2, 'Hello Test! I am Jane.', NOW() - INTERVAL '4 days'),
  (10, 4, 4, 'Hi Jane! Nice to meet you.', NOW() - INTERVAL '4 days'),
  (11, 4, 2, 'What do you like to do for fun?', NOW() - INTERVAL '3 days')
ON CONFLICT (id) DO NOTHING;

-- Update sequence for user_messages
SELECT setval('user_messages_id_seq', (SELECT MAX(id) FROM user_messages));

-- Insert message events to track sent, received, and read status
-- SENT events for all messages
INSERT INTO message_events (message_id, message_event_type, timestamp)
VALUES
  (1, 'SENT', NOW() - INTERVAL '2 days'),
  (2, 'SENT', NOW() - INTERVAL '2 days'),
  (3, 'SENT', NOW() - INTERVAL '1 day'),
  (4, 'SENT', NOW() - INTERVAL '3 days'),
  (5, 'SENT', NOW() - INTERVAL '3 days'),
  (6, 'SENT', NOW() - INTERVAL '2 days'),
  (7, 'SENT', NOW() - INTERVAL '1 day'),
  (8, 'SENT', NOW() - INTERVAL '1 day'),
  (9, 'SENT', NOW() - INTERVAL '4 days'),
  (10, 'SENT', NOW() - INTERVAL '4 days'),
  (11, 'SENT', NOW() - INTERVAL '3 days');

-- RECEIVED events for all messages
INSERT INTO message_events (message_id, message_event_type, timestamp)
VALUES
  (1, 'RECEIVED', NOW() - INTERVAL '2 days' + INTERVAL '1 minute'),
  (2, 'RECEIVED', NOW() - INTERVAL '2 days' + INTERVAL '1 minute'),
  (3, 'RECEIVED', NOW() - INTERVAL '1 day' + INTERVAL '1 minute'),
  (4, 'RECEIVED', NOW() - INTERVAL '3 days' + INTERVAL '1 minute'),
  (5, 'RECEIVED', NOW() - INTERVAL '3 days' + INTERVAL '1 minute'),
  (6, 'RECEIVED', NOW() - INTERVAL '2 days' + INTERVAL '1 minute'),
  (7, 'RECEIVED', NOW() - INTERVAL '1 day' + INTERVAL '1 minute'),
  (8, 'RECEIVED', NOW() - INTERVAL '1 day' + INTERVAL '1 minute'),
  (9, 'RECEIVED', NOW() - INTERVAL '4 days' + INTERVAL '1 minute'),
  (10, 'RECEIVED', NOW() - INTERVAL '4 days' + INTERVAL '1 minute'),
  (11, 'RECEIVED', NOW() - INTERVAL '3 days' + INTERVAL '1 minute');

-- READ events (for messages that have been read)
INSERT INTO message_events (message_id, message_event_type, timestamp)
VALUES
  (1, 'READ', NOW() - INTERVAL '2 days' + INTERVAL '5 minutes'),
  (2, 'READ', NOW() - INTERVAL '2 days' + INTERVAL '5 minutes'),
  (3, 'READ', NOW() - INTERVAL '1 day' + INTERVAL '5 minutes'),
  (4, 'READ', NOW() - INTERVAL '3 days' + INTERVAL '5 minutes'),
  (5, 'READ', NOW() - INTERVAL '3 days' + INTERVAL '5 minutes'),
  (6, 'READ', NOW() - INTERVAL '2 days' + INTERVAL '5 minutes'),
  (7, 'READ', NOW() - INTERVAL '1 day' + INTERVAL '5 minutes'),
  (8, 'READ', NOW() - INTERVAL '1 day' + INTERVAL '5 minutes'),
  (9, 'READ', NOW() - INTERVAL '4 days' + INTERVAL '5 minutes'),
  (10, 'READ', NOW() - INTERVAL '4 days' + INTERVAL '5 minutes');
-- Note: Message 11 doesn't have a READ event, so it will appear as unread