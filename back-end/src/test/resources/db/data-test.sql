-- Insert roles
MERGE INTO user_role_types (id, name) VALUES (1, 'ROLE_USER');
MERGE INTO user_role_types (id, name) VALUES (2, 'ROLE_MODERATOR');
MERGE INTO user_role_types (id, name) VALUES (3, 'ROLE_ADMIN');
-- Insert genders
MERGE INTO user_gender_types (id, name) VALUES (1, 'MALE');
MERGE INTO user_gender_types (id, name) VALUES (2, 'FEMALE');
MERGE INTO user_gender_types (id, name) VALUES (3, 'OTHER');
-- Insert connection types
MERGE INTO connection_types (id, name) VALUES (1, 'SEEN');
MERGE INTO connection_types (id, name) VALUES (2, 'OPENED_PROFILE');
MERGE INTO connection_types (id, name) VALUES (3, 'JUST_FRIENDS');
MERGE INTO connection_types (id, name) VALUES (4, 'MAYBE_MORE');
MERGE INTO connection_types (id, name) VALUES (5, 'INTERESTED');
MERGE INTO connection_types (id, name) VALUES (6, 'CLOSED');

-- Insert message event types
MERGE INTO message_event_types (id, name) VALUES (1, 'SENT');
MERGE INTO message_event_types (id, name) VALUES (2, 'RECEIVED');
MERGE INTO message_event_types (id, name) VALUES (3, 'READ');
-- Insert activity log types
MERGE INTO activity_log_types (id, name) VALUES (1, 'CREATED');
MERGE INTO activity_log_types (id, name) VALUES (2, 'VERIFIED');
MERGE INTO activity_log_types (id, name) VALUES (3, 'LOGIN');
MERGE INTO activity_log_types (id, name) VALUES (4, 'LOGOUT');
-- Insert attribute change types
MERGE INTO attribute_change_types (id, name) VALUES (1, 'CREATED');
MERGE INTO attribute_change_types (id, name) VALUES (2, 'GENDER');
MERGE INTO attribute_change_types (id, name) VALUES (3, 'BIRTHDATE');
MERGE INTO attribute_change_types (id, name) VALUES (4, 'LOCATION');
-- Insert preference change types
MERGE INTO preference_change_types (id, name) VALUES (1, 'CREATED');
MERGE INTO preference_change_types (id, name) VALUES (2, 'GENDER');
MERGE INTO preference_change_types (id, name) VALUES (3, 'AGE_MIN');
MERGE INTO preference_change_types (id, name) VALUES (4, 'AGE_MAX');
MERGE INTO preference_change_types (id, name) VALUES (5, 'DISTANCE');
MERGE INTO preference_change_types (id, name) VALUES (6, 'TOLERANCE');
-- Insert profile change types
MERGE INTO profile_change_types (id, name) VALUES (1, 'CREATED');
MERGE INTO profile_change_types (id, name) VALUES (2, 'AGE');
MERGE INTO profile_change_types (id, name) VALUES (3, 'BIO');
MERGE INTO profile_change_types (id, name) VALUES (4, 'PHOTO');
MERGE INTO profile_change_types (id, name) VALUES (5, 'INTERESTS');
-- Insert hobby categories
MERGE INTO hobby (id, name, category, sub_category) VALUES (1, '3D printing', 'General', 'Indoors');
MERGE INTO hobby (id, name, category, sub_category) VALUES (2, 'Acrobatics', 'General', 'Indoors');
MERGE INTO hobby (id, name, category, sub_category) VALUES (3, 'Acting', 'General', 'Indoors');
MERGE INTO hobby (id, name, category, sub_category) VALUES (4, 'Amateur radio', 'General', 'Indoors');
MERGE INTO hobby (id, name, category, sub_category) VALUES (5, 'Animation', 'General', 'Indoors');
MERGE INTO hobby (id, name, category, sub_category) VALUES (6, 'Aquascaping', 'General', 'Indoors');
MERGE INTO hobby (id, name, category, sub_category) VALUES (7, 'Astrology', 'General', 'Indoors');
MERGE INTO hobby (id, name, category, sub_category) VALUES (8, 'Astronomy', 'General', 'Indoors');
MERGE INTO hobby (id, name, category, sub_category) VALUES (9, 'Baking', 'General', 'Indoors');
MERGE INTO hobby (id, name, category, sub_category) VALUES (10, 'Baton twirling', 'General', 'Indoors');
MERGE INTO hobby (id, name, category, sub_category) VALUES (11, 'Blogging', 'General', 'Indoors');
MERGE INTO hobby (id, name, category, sub_category) VALUES (12, 'Building', 'General', 'Indoors');
MERGE INTO hobby (id, name, category, sub_category) VALUES (13, 'Board/tabletop games', 'General', 'Indoors');
MERGE INTO hobby (id, name, category, sub_category) VALUES (14, 'Book discussion clubs', 'General', 'Indoors');
MERGE INTO hobby (id, name, category, sub_category) VALUES (15, 'Book restoration', 'General', 'Indoors');
