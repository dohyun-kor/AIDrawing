-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- 호스트: mysql:3306
-- 생성 시간: 25-02-13 08:43
-- 서버 버전: 8.0.41
-- PHP 버전: 8.2.27

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 데이터베이스: `d108`
--

-- --------------------------------------------------------

--
-- 테이블 구조 `Friend`
--

CREATE TABLE `Friend` (
                          `user_id` int NOT NULL,
                          `friend_id` int NOT NULL,
                          `status` enum('PENDING','ACCEPTED','BLOCKED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 테이블의 덤프 데이터 `Friend`
--

INSERT INTO `Friend` (`user_id`, `friend_id`, `status`) VALUES
                                                            (23, 18, 'ACCEPTED'),
                                                            (23, 19, 'ACCEPTED'),
                                                            (38, 23, 'ACCEPTED'),
                                                            (48, 23, 'ACCEPTED');

-- --------------------------------------------------------

--
-- 테이블 구조 `Game`
--

CREATE TABLE `Game` (
                        `game_id` int NOT NULL COMMENT '게임 고유 ID',
                        `room_id` int NOT NULL COMMENT '방 ID',
                        `user_id` int NOT NULL COMMENT '게임 진행자 또는 참여 사용자 ID',
                        `start_time` datetime DEFAULT NULL COMMENT '시작 시간',
                        `end_time` datetime DEFAULT NULL COMMENT '종료 시간',
                        `mode` int DEFAULT NULL COMMENT '게임 모드 (예: 0=AI, 1=User 등)',
                        `difficulty` int DEFAULT NULL COMMENT '난이도 (예: 0=쉬움, 1=보통, 2=어려움 등)',
                        `score` int DEFAULT NULL,
                        `rank` int DEFAULT NULL,
                        `remaining_time` datetime DEFAULT NULL COMMENT '남은 시간'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 테이블 구조 `GameTopicEasy`
--

CREATE TABLE `GameTopicEasy` (
                                 `easy_topic_id` int NOT NULL,
                                 `topic` varchar(255) DEFAULT NULL,
                                 `topic_en` varchar(255) DEFAULT NULL,
                                 `is_used` tinyint(1) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 테이블의 덤프 데이터 `GameTopicEasy`
--

INSERT INTO `GameTopicEasy` (`easy_topic_id`, `topic`, `topic_en`, `is_used`) VALUES
                                                                                  (1, '집', 'house', 0),
                                                                                  (2, '나무', 'tree', 0),
                                                                                  (3, '해', 'sun', 0),
                                                                                  (4, '달', 'moon', 0),
                                                                                  (5, '강아지', 'dog', 0),
                                                                                  (6, '고양이', 'cat', 0),
                                                                                  (7, '자동차', 'car', 0),
                                                                                  (8, '버스', 'bus', 0),
                                                                                  (9, '비행기', 'airplane', 0),
                                                                                  (10, '배', 'boat', 0),
                                                                                  (11, '사과', 'apple', 0),
                                                                                  (12, '바나나', 'banana', 0),
                                                                                  (13, '책', 'book', 0),
                                                                                  (14, '의자', 'chair', 0),
                                                                                  (15, '테이블', 'table', 0),
                                                                                  (16, '꽃', 'flower', 0),
                                                                                  (17, '산', 'mountain', 0),
                                                                                  (18, '구름', 'cloud', 0),
                                                                                  (19, '우산', 'umbrella', 0),
                                                                                  (20, '물', 'water', 0),
                                                                                  (21, '불', 'fire', 0),
                                                                                  (22, '연필', 'pencil', 0),
                                                                                  (23, '공', 'ball', 0),
                                                                                  (24, '시계', 'clock', 0),
                                                                                  (25, '전화기', 'phone', 0),
                                                                                  (26, '컴퓨터', 'computer', 0),
                                                                                  (27, '책상', 'desk', 0),
                                                                                  (28, '창문', 'window', 0),
                                                                                  (29, '문', 'door', 0),
                                                                                  (30, '침대', 'bed', 0),
                                                                                  (31, '인형', 'doll', 0),
                                                                                  (32, '소파', 'sofa', 0),
                                                                                  (33, '텔레비전', 'television', 0),
                                                                                  (34, '냉장고', 'refrigerator', 0),
                                                                                  (35, '병', 'disease', 0),
                                                                                  (36, '컵', 'cup', 0),
                                                                                  (37, '신발', 'shoes', 0),
                                                                                  (38, '모자', 'cap', 0),
                                                                                  (39, '티셔츠', 'T-shirt', 0),
                                                                                  (40, '바지', 'pants', 0),
                                                                                  (41, '스푼', 'spoon', 0),
                                                                                  (42, '포크', 'fork', 0),
                                                                                  (43, '칼', 'knife', 0),
                                                                                  (44, '접시', 'plate', 0),
                                                                                  (45, '치마', 'skirt', 0),
                                                                                  (46, '우유', 'milk', 0),
                                                                                  (47, '달걀', 'egg', 0),
                                                                                  (48, '빵', 'bread', 0),
                                                                                  (49, '치즈', 'cheese', 0),
                                                                                  (50, '나비', 'butterfly', 0),
                                                                                  (51, '물고기', 'fish', 0),
                                                                                  (52, '새', 'bird', 0),
                                                                                  (53, '꽃병', 'vase', 0),
                                                                                  (54, '사탕', 'candy', 0),
                                                                                  (55, '케이크', 'cake', 0),
                                                                                  (56, '피자', 'pizza', 0),
                                                                                  (57, '햄버거', 'hamburger', 0),
                                                                                  (58, '샌드위치', 'sandwich', 0),
                                                                                  (59, '도넛', 'donut', 0),
                                                                                  (60, '아이스크림', 'ice cream', 0),
                                                                                  (61, '라면', 'ramen', 0),
                                                                                  (62, '커피', 'coffee', 0),
                                                                                  (63, '차', 'tea', 0),
                                                                                  (64, '소금', 'salt', 0),
                                                                                  (65, '하늘', 'sky', 0),
                                                                                  (66, '바다', 'sea', 0),
                                                                                  (67, '호수', 'lake', 0),
                                                                                  (68, '강', 'river', 0),
                                                                                  (69, '공원', 'park', 0),
                                                                                  (70, '학교', 'school', 0),
                                                                                  (71, '병원', 'hospital', 0),
                                                                                  (72, '우체국', 'post office', 0),
                                                                                  (73, '은행', 'bank', 0),
                                                                                  (74, '시장', 'market', 0),
                                                                                  (75, '공장', 'factory', 0),
                                                                                  (76, '교회', 'church', 0),
                                                                                  (77, '사찰', 'temple', 0),
                                                                                  (78, '마을', 'village', 0),
                                                                                  (79, '도시', 'city', 0),
                                                                                  (80, '다리', 'bridge', 0),
                                                                                  (81, '길', 'road', 0),
                                                                                  (82, '얼굴', 'face', 0),
                                                                                  (83, '신호등', 'traffic lights', 0),
                                                                                  (84, '교차로', 'crossroad', 0),
                                                                                  (85, '버스정류장', 'bus stop', 0),
                                                                                  (86, '지하철', 'subway', 0),
                                                                                  (87, '트럭', 'truck', 0),
                                                                                  (88, '자전거', 'bicycle', 0),
                                                                                  (89, '오토바이', 'motorcycle', 0),
                                                                                  (90, '비누', 'soap', 0),
                                                                                  (91, '칫솔', 'toothbrush', 0),
                                                                                  (92, '치약', 'toothpaste', 0),
                                                                                  (93, '샴푸', 'shampoo', 0),
                                                                                  (94, '수건', 'towel', 0),
                                                                                  (95, '거울', 'mirror', 0),
                                                                                  (96, '빗', 'hair brush', 0),
                                                                                  (97, '손목시계', 'wristwatch', 0),
                                                                                  (98, '안경', 'glasses', 0),
                                                                                  (99, '지갑', 'wallet', 0),
                                                                                  (100, '우편함', 'mailbox', 0),
                                                                                  (101, '비', 'rain', 0),
                                                                                  (103, '가방', 'bag', 0),
                                                                                  (104, '양말', 'socks', 0),
                                                                                  (105, '별', 'star', 0),
                                                                                  (106, '닭', 'chicken', 0),
                                                                                  (107, '눈', 'snow', 0),
                                                                                  (108, '소', 'cow', 0),
                                                                                  (109, '돼지', 'pig', 0),
                                                                                  (110, '배구공', 'volleyball', 0),
                                                                                  (111, '양', 'sheep', 0),
                                                                                  (113, '거북이', 'turtle', 0),
                                                                                  (114, '사람', 'person', 0),
                                                                                  (115, '동물', 'animal', 0),
                                                                                  (116, '음악', 'music', 0),
                                                                                  (117, '영화', 'movie', 0),
                                                                                  (119, '인터넷', 'internet', 0),
                                                                                  (120, '카메라', 'camera', 0),
                                                                                  (122, '음식', 'food', 0),
                                                                                  (123, '게임', 'game', 0),
                                                                                  (124, '음료수', 'drink', 0),
                                                                                  (125, '햇볕', 'sunlight', 0),
                                                                                  (126, '자연', 'nature', 0),
                                                                                  (127, '음악가', 'musician', 0),
                                                                                  (128, '서점', 'bookstore', 0),
                                                                                  (129, '연필깎이', 'sharpener', 0),
                                                                                  (130, '장난감', 'toy', 0),
                                                                                  (131, '카드', 'card', 0),
                                                                                  (132, '마법', 'magic', 0),
                                                                                  (133, '하늘색', 'sky blue', 0),
                                                                                  (134, '길이', 'length', 0),
                                                                                  (135, '라디오', 'radio', 0),
                                                                                  (136, '콘서트', 'concert', 0),
                                                                                  (137, '버튼', 'button', 0),
                                                                                  (138, '지구', 'earth', 0),
                                                                                  (139, '시간', 'time', 0),
                                                                                  (140, '바람', 'wind', 0),
                                                                                  (142, '사과나무', 'apple tree', 0),
                                                                                  (143, '밥', 'rice', 0),
                                                                                  (144, '빗자루', 'broom', 0),
                                                                                  (145, '솜', 'cotton', 0),
                                                                                  (147, '여행', 'travel', 0),
                                                                                  (148, '날개', 'wing', 0),
                                                                                  (149, '달리기', 'running', 0),
                                                                                  (150, '영수증', 'receipt', 0),
                                                                                  (151, '게임기', 'gaming console', 0),
                                                                                  (152, '구두', 'shoes', 0),
                                                                                  (153, '책상', 'desk', 0),
                                                                                  (154, '토끼', 'rabbit', 0),
                                                                                  (155, '손', 'hand', 0),
                                                                                  (156, '눈', 'eye', 0),
                                                                                  (157, '돌', 'stone', 0),
                                                                                  (158, '코', 'nose', 0),
                                                                                  (159, '수박', 'watermelon', 0),
                                                                                  (160, '버터', 'butter', 0);

-- --------------------------------------------------------

--
-- 테이블 구조 `GameTopicHard`
--

CREATE TABLE `GameTopicHard` (
                                 `hard_topic_id` int NOT NULL,
                                 `topic` varchar(255) DEFAULT NULL,
                                 `topic_en` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                 `is_used` tinyint(1) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 테이블의 덤프 데이터 `GameTopicHard`
--

INSERT INTO `GameTopicHard` (`hard_topic_id`, `topic`, `topic_en`, `is_used`) VALUES
                                                                                  (1, '추상화', 'abstract painting', 0),
                                                                                  (2, '초승달', 'crescent moon', 0),
                                                                                  (3, '대통령', 'president', 0),
                                                                                  (4, '오로라', 'aurora', 0),
                                                                                  (5, '종달새', 'lark', 0),
                                                                                  (6, '지구본', 'globe', 0),
                                                                                  (7, '북극성', 'Polaris', 0),
                                                                                  (8, '만년설', 'permanent snow', 0),
                                                                                  (9, '동상', 'bronze statue', 0),
                                                                                  (10, '비눗방울', 'soap bubbles', 0),
                                                                                  (11, '만리장성', 'Great Wall of China', 0),
                                                                                  (12, '우주정거장', 'space station', 0),
                                                                                  (13, '정글', 'jungle', 0),
                                                                                  (14, '암벽등반', 'rock climbing', 0),
                                                                                  (15, '유성우', 'meteor shower', 0),
                                                                                  (16, '줄다리기', 'tug-of-war', 0),
                                                                                  (17, '나침반', 'compass', 0),
                                                                                  (18, '미로', 'maze', 0),
                                                                                  (19, '일식', 'solar eclipse', 0),
                                                                                  (20, '용암', 'lava', 0),
                                                                                  (21, '암흑', 'darkness', 0),
                                                                                  (22, '도룡뇽', 'Salamander', 0),
                                                                                  (23, '아코디언', 'accordion', 0),
                                                                                  (24, '벽난로', 'fireplace', 0),
                                                                                  (25, '진공청소기', 'vacuum', 0),
                                                                                  (26, '우주', 'space', 0),
                                                                                  (27, '아킬레스건', 'Achilles tendon', 0),
                                                                                  (28, '산호초', 'coral reef', 0),
                                                                                  (29, '해적선', 'pirate ship', 0),
                                                                                  (30, '낙뢰', 'lightning strike', 0),
                                                                                  (31, '칼슘', 'Calcium', 0),
                                                                                  (32, '바람개비', 'pinwheel', 0),
                                                                                  (33, '단백질', 'protein', 0),
                                                                                  (34, '차례', 'order', 0),
                                                                                  (35, '낙하산', 'parachute', 0),
                                                                                  (36, '아지트', 'hideout', 0),
                                                                                  (37, '자율주행차', 'self-driving car', 0),
                                                                                  (38, '전쟁터', 'battleground', 0),
                                                                                  (39, '바이러스', 'virus', 0),
                                                                                  (40, '자기장', 'magnetic field', 0),
                                                                                  (41, '사춘기', 'adolescence', 0),
                                                                                  (42, '아르바이트', 'part-time job', 0),
                                                                                  (43, '바다거북', 'sea turtle', 0),
                                                                                  (44, '자극', 'stimulus', 0),
                                                                                  (45, '다리미', 'steam iron', 0),
                                                                                  (47, '가습기', 'humidifier', 0),
                                                                                  (48, '마그마', 'magma', 0),
                                                                                  (49, '모닥불', 'bonfire', 0),
                                                                                  (50, '갈라파고스', 'Galapagos', 0),
                                                                                  (51, '타조', 'ostrich', 0),
                                                                                  (52, '자동차 경주', 'car race', 0),
                                                                                  (53, '지진', 'earthquake', 0),
                                                                                  (54, '아쿠아리움', 'aquarium', 0),
                                                                                  (55, '나방', 'moth', 0),
                                                                                  (56, '일출 ', 'sunrise', 0),
                                                                                  (57, '가전제품', 'home appliances', 0),
                                                                                  (58, '나노기술', 'nanotechnology', 0),
                                                                                  (59, '난기류', 'turbulence', 0),
                                                                                  (60, '차선', 'lane', 0),
                                                                                  (61, '감염병', 'infectious disease', 0),
                                                                                  (62, '발목', 'ankle', 0),
                                                                                  (63, '반도체', 'semiconductor', 0),
                                                                                  (64, '박쥐', 'bat', 0),
                                                                                  (65, '농구코트', 'baseball court', 0),
                                                                                  (66, '퍼즐', 'puzzle', 0),
                                                                                  (67, '사전', 'dictionary', 0),
                                                                                  (68, '고래상어', 'whale shark', 0),
                                                                                  (69, '자가용', 'private car', 0),
                                                                                  (70, '자격증', 'certification', 0),
                                                                                  (71, '자원봉사', 'volunteering', 0),
                                                                                  (72, '차별', 'discrimination', 0),
                                                                                  (73, '바코드', 'barcode', 0),
                                                                                  (74, '가뭄', 'drought', 0),
                                                                                  (75, '별똥별', 'shooting star', 0),
                                                                                  (76, '개울', 'stream', 0),
                                                                                  (77, '패턴', 'pattern', 0),
                                                                                  (78, '그림자', 'shadow', 0),
                                                                                  (79, '파이프', 'pipe', 0),
                                                                                  (80, '가위바위보', 'rock-paper-scissors', 0),
                                                                                  (81, '다이너마이트', 'dynamite', 0),
                                                                                  (82, '고대유적', 'ancient ruins', 0),
                                                                                  (83, '바위굴', 'cave', 0),
                                                                                  (84, '예술', 'art', 0),
                                                                                  (85, '망원경', 'telescope', 0),
                                                                                  (86, '나무늘보', 'sloth', 0),
                                                                                  (87, '하드웨어', 'hardware', 0),
                                                                                  (88, '하모니카', 'harmonica', 0),
                                                                                  (89, '타임머신', 'time machine', 0),
                                                                                  (90, '해마', 'sea horse', 0),
                                                                                  (91, '하마', 'hippo', 0),
                                                                                  (92, '탁구', 'table tennis', 0),
                                                                                  (93, '파노라마', 'panorama', 0),
                                                                                  (94, '아나콘다', 'anaconda', 0),
                                                                                  (95, '카멜레온', 'Chameleon', 0),
                                                                                  (96, '난방기', 'heater', 0),
                                                                                  (97, '모래시계', 'hourglass', 0),
                                                                                  (98, '탈모', 'hair loss', 0),
                                                                                  (99, '자외선', 'ultraviolet ray', 0),
                                                                                  (100, '바베큐', 'barbecue', 0),
                                                                                  (142, '잠자리', 'dragonfly', 0),
                                                                                  (143, '종이비행기', 'paper airplane', 0),
                                                                                  (144, '알파벳', 'alphabet', 0),
                                                                                  (145, '별빛', 'starlight', 0),
                                                                                  (146, '배추', 'cabbage', 0),
                                                                                  (147, '집게', 'clamp', 0),
                                                                                  (148, '농장', 'farm', 0),
                                                                                  (149, '연구소', 'laboratory', 0),
                                                                                  (150, '페인트', 'paint', 0),
                                                                                  (151, '계단', 'stairs', 0);

-- --------------------------------------------------------

--
-- 테이블 구조 `GameTopicNormal`
--

CREATE TABLE `GameTopicNormal` (
                                   `normal_topic_id` int NOT NULL,
                                   `topic` varchar(255) DEFAULT NULL,
                                   `topic_en` varchar(255) DEFAULT NULL,
                                   `is_used` tinyint(1) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 테이블의 덤프 데이터 `GameTopicNormal`
--

INSERT INTO `GameTopicNormal` (`normal_topic_id`, `topic`, `topic_en`, `is_used`) VALUES
                                                                                      (1, '피라미드', 'pyramid', 0),
                                                                                      (2, '카메라', 'camera', 0),
                                                                                      (3, '해적선', 'pirate ship', 0),
                                                                                      (4, '우주선', 'spaceship', 0),
                                                                                      (5, '성', 'castle', 0),
                                                                                      (6, '로봇', 'robot', 0),
                                                                                      (7, '악어', 'crocodile', 0),
                                                                                      (8, '공룡', 'dinosaur', 0),
                                                                                      (9, '오리', 'duck', 0),
                                                                                      (10, '펭귄', 'penguin', 0),
                                                                                      (11, '기차', 'train', 0),
                                                                                      (12, '도자기', 'pottery', 0),
                                                                                      (13, '사막', 'desert', 0),
                                                                                      (14, '음악', 'music', 0),
                                                                                      (15, '섬', 'island', 0),
                                                                                      (16, '용', 'dragon', 0),
                                                                                      (17, '마법사', 'wizard', 0),
                                                                                      (18, '도서관', 'library', 0),
                                                                                      (19, '놀이공원', 'amusement park', 0),
                                                                                      (20, '회전목마', 'merry-go-round', 0),
                                                                                      (21, '탑', 'tower', 0),
                                                                                      (22, '커피', 'coffee', 0),
                                                                                      (23, '폭포', 'waterfall', 0),
                                                                                      (24, '해변', 'beach', 0),
                                                                                      (25, '모래성', 'sand castle', 0),
                                                                                      (26, '열기구', 'hot-air balloon', 0),
                                                                                      (27, '풍선', 'balloon', 0),
                                                                                      (28, '파라솔', 'parasol', 0),
                                                                                      (29, '축구경기장', 'soccer stadium', 0),
                                                                                      (30, '다이어리', 'diary', 0),
                                                                                      (31, '야구장', 'baseball park', 0),
                                                                                      (32, '바닥', 'floor', 0),
                                                                                      (33, '잠수함', 'submarine', 0),
                                                                                      (34, '피아노', 'piano', 0),
                                                                                      (35, '기타', 'guitar', 0),
                                                                                      (36, '드럼', 'drum', 0),
                                                                                      (37, '바이올린', 'violin', 0),
                                                                                      (38, '카페', 'cafe', 0),
                                                                                      (39, '스마트폰', 'smartphone', 0),
                                                                                      (40, '인터넷', 'Internet', 0),
                                                                                      (41, '다이아몬드', 'diamond', 0),
                                                                                      (42, '마술사', 'magician', 0),
                                                                                      (43, '노트북', 'laptop', 0),
                                                                                      (44, '바위', 'rock', 0),
                                                                                      (45, '자석', 'magnet', 0),
                                                                                      (46, '북극곰', 'polar bear', 0),
                                                                                      (47, '사자', 'lion', 0),
                                                                                      (48, '호랑이', 'tiger', 0),
                                                                                      (49, '기린', 'giraffe', 0),
                                                                                      (50, '코뿔소', 'Rhinoceros', 0),
                                                                                      (51, '얼룩말', 'zebra', 0),
                                                                                      (52, '늑대', 'wolf', 0),
                                                                                      (53, '여우', 'fox', 0),
                                                                                      (54, '원숭이', 'monkey', 0),
                                                                                      (55, '침팬지', 'chimpanzee', 0),
                                                                                      (56, '부엉이', 'owl', 0),
                                                                                      (57, '독수리', 'eagle', 0),
                                                                                      (58, '다이어트', 'diet', 0),
                                                                                      (59, '무지개', 'rainbow', 0),
                                                                                      (60, '바람', 'wind', 0),
                                                                                      (61, '은하수', 'milky way', 0),
                                                                                      (62, '행성', 'planet', 0),
                                                                                      (63, '위성', 'satellite', 0),
                                                                                      (64, '우주인', 'astronaut', 0),
                                                                                      (65, '벽난로', 'astronaut', 0),
                                                                                      (66, '캠핑카', 'camping car', 0),
                                                                                      (67, '텐트', 'tent', 0),
                                                                                      (68, '바퀴', 'wheel', 0),
                                                                                      (69, '낚시대', 'fishing rod', 0),
                                                                                      (70, '요트', 'yacht', 0),
                                                                                      (71, '다이빙', 'diving', 0),
                                                                                      (72, '서핑', 'surfing', 0),
                                                                                      (73, '스노보드', 'snowboard', 0),
                                                                                      (74, '스키', 'ski', 0),
                                                                                      (75, '썰매', 'sleigh', 0),
                                                                                      (76, '아기', 'baby', 0),
                                                                                      (77, '눈사람', 'snowman', 0),
                                                                                      (78, '크리스마스트리', 'christmas tree', 0),
                                                                                      (79, '후추', 'pepper', 0),
                                                                                      (80, '석양', 'sunset', 0),
                                                                                      (81, '카레', 'curry', 0),
                                                                                      (82, '파도', 'wave', 0),
                                                                                      (83, '손전등', 'flashlight', 0),
                                                                                      (84, '카펫', 'carpet', 0),
                                                                                      (85, '전구', 'light bulb', 0),
                                                                                      (86, '달팽이', 'snail', 0),
                                                                                      (87, '파인애플', 'pineapple', 0),
                                                                                      (88, '자물쇠', 'lock', 0),
                                                                                      (89, '열쇠', 'key', 0),
                                                                                      (90, '지도', 'map', 0),
                                                                                      (91, '지구본', 'globe', 0),
                                                                                      (92, '마스크', 'mask', 0),
                                                                                      (93, '라디오', 'radio', 0),
                                                                                      (94, '스피커', 'speaker', 0),
                                                                                      (95, '헬리콥터', 'helicopter', 0),
                                                                                      (96, '풍차', 'windmill', 0),
                                                                                      (97, '사료', 'feed', 0),
                                                                                      (98, '보물상자', 'treasure chest', 0),
                                                                                      (99, '화산', 'volcano', 0),
                                                                                      (100, '우주정거장', 'space station', 0),
                                                                                      (119, '가방 ', 'bag', 0),
                                                                                      (120, '양말', 'socks', 0),
                                                                                      (121, '별', 'star', 0),
                                                                                      (122, '닭', 'chicken', 0),
                                                                                      (123, '눈', 'snow', 0),
                                                                                      (124, '소', 'cow', 0),
                                                                                      (125, '돼지', 'pig', 0),
                                                                                      (126, '배구공', 'volleyball', 0),
                                                                                      (127, '의자', 'chair', 0),
                                                                                      (128, '거북이', 'turtle', 0),
                                                                                      (129, '배', 'pear', 0),
                                                                                      (130, '병', 'bottle', 0),
                                                                                      (131, '햇볕', 'sunlight', 0),
                                                                                      (132, '운동화', 'sneakers', 0),
                                                                                      (133, '시계', 'watch', 0),
                                                                                      (134, '팬', 'fan', 0),
                                                                                      (135, '편지', 'letter', 0),
                                                                                      (136, '신문', 'newspaper', 0),
                                                                                      (137, '떡', 'rice cake', 0),
                                                                                      (138, '햄버거', 'burger', 0),
                                                                                      (139, '피자', 'pizza', 0),
                                                                                      (140, '초콜릿', 'chocolate', 0),
                                                                                      (141, '도넛', 'doughnut', 0),
                                                                                      (142, '치즈', 'cheese', 0),
                                                                                      (143, '국수', 'noodles', 0),
                                                                                      (144, '장미', 'rose', 0),
                                                                                      (145, '아이스크림', 'ice cream', 0),
                                                                                      (146, '쿠키', 'cookie', 0),
                                                                                      (147, '빨강', 'red', 0),
                                                                                      (148, '파랑', 'blue', 0),
                                                                                      (149, '노랑', 'yellow', 0),
                                                                                      (150, '초록', 'green', 0),
                                                                                      (151, '검정', 'black', 0),
                                                                                      (152, '하양', 'white', 0),
                                                                                      (153, '핑크', 'pink', 0),
                                                                                      (154, '주황', 'orange', 0),
                                                                                      (155, '사과나무', 'apple tree', 0),
                                                                                      (156, '마라톤', 'marathon', 0),
                                                                                      (157, '화장품', 'cosmetics', 0),
                                                                                      (158, '기차역', 'train station', 0),
                                                                                      (159, '놀이기구', 'ride', 0),
                                                                                      (160, '식물', 'plant', 0),
                                                                                      (161, '아이폰', 'iPhone', 0),
                                                                                      (162, '고양이', 'cat', 0),
                                                                                      (163, '개', 'dog', 0),
                                                                                      (164, '악기', 'instrument', 0),
                                                                                      (165, '비행기', 'airplane', 0),
                                                                                      (166, '전화기', 'telephone', 0),
                                                                                      (167, '카메라렌즈', 'camera lens', 0),
                                                                                      (168, '목걸이', 'necklace', 0),
                                                                                      (169, '머리핀', 'hairpin', 0),
                                                                                      (170, '모터', 'motor', 0),
                                                                                      (171, '양초', 'candle', 0),
                                                                                      (172, '꽃다발', 'bouquet', 0),
                                                                                      (173, '청바지', 'jeans', 0),
                                                                                      (174, '코트', 'coat', 0),
                                                                                      (175, '리본', 'ribbon', 0),
                                                                                      (176, '숲', 'forest', 0),
                                                                                      (177, '폭죽', 'fireworks', 0),
                                                                                      (178, '꽃병', 'vase', 0),
                                                                                      (179, '시계탑', 'clock tower', 0),
                                                                                      (180, '커튼', 'curtain', 0),
                                                                                      (181, '부채', 'fan', 0),
                                                                                      (182, '장갑', 'gloves', 0),
                                                                                      (183, '스카프', 'scarf', 0),
                                                                                      (184, '귀걸이', 'earrings', 0),
                                                                                      (185, '운동장', 'sports field', 0),
                                                                                      (186, '배낭', 'backpack', 0),
                                                                                      (187, '정원', 'garden', 0),
                                                                                      (188, '무대', 'stage', 0),
                                                                                      (189, '우편물', 'mail', 0),
                                                                                      (190, '손목시계', 'wristwatch', 0),
                                                                                      (191, '체스', 'chess', 0),
                                                                                      (192, '당근', 'carrot', 0),
                                                                                      (193, '토마토', 'tomato', 0),
                                                                                      (194, '감자', 'potato', 0),
                                                                                      (195, '오렌지', 'orange', 0),
                                                                                      (196, '귤', 'tangerine', 0);

-- --------------------------------------------------------

--
-- 테이블 구조 `Item`
--

CREATE TABLE `Item` (
                        `item_id` int NOT NULL,
                        `name` varchar(255) DEFAULT NULL COMMENT '아이템 이름',
                        `category` varchar(255) DEFAULT NULL COMMENT '아이템 분류(기능 아이템, 치장 아이템 등)',
                        `price` int DEFAULT NULL COMMENT '가격',
                        `description` text COMMENT '아이템 설명',
                        `link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '아이템의 링크'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 테이블의 덤프 데이터 `Item`
--

INSERT INTO `Item` (`item_id`, `name`, `category`, `price`, `description`, `link`) VALUES
                                                                                       (1, 'sonic', '1', 1000, '쏘닉 아이콘입니다. 항상 빠릅니다.', 'https://i12d108.p.ssafy.io/api/item/sonic.png'),
                                                                                       (2, 'mario', '1', 1000, '마 리 오', 'https://i12d108.p.ssafy.io/api/item/frame7.png'),
                                                                                       (3, 'frame', '2', 1000, '굉장히 사실적인 액자 틀\r\n세기의 명화를 담기에 좋다.', 'https://i12d108.p.ssafy.io/api/item/frame.png');

-- --------------------------------------------------------

--
-- 테이블 구조 `MyFurniture`
--

CREATE TABLE `MyFurniture` (
                               `myfurniture_id` int NOT NULL,
                               `user_id` int NOT NULL COMMENT '사용자 ID',
                               `fur_id` int DEFAULT NULL COMMENT '가구 ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 테이블의 덤프 데이터 `MyFurniture`
--

INSERT INTO `MyFurniture` (`myfurniture_id`, `user_id`, `fur_id`) VALUES
                                                                      (1, 33, 2),
                                                                      (2, 33, 1),
                                                                      (3, 33, 1),
                                                                      (4, 23, 13);

-- --------------------------------------------------------

--
-- 테이블 구조 `MyItem`
--

CREATE TABLE `MyItem` (
                          `purchase_id` int NOT NULL,
                          `item_id` int NOT NULL COMMENT '아이템 ID',
                          `user_id` int NOT NULL COMMENT '사용자 ID',
                          `purchase_date` datetime DEFAULT NULL COMMENT '구매일'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 테이블의 덤프 데이터 `MyItem`
--

INSERT INTO `MyItem` (`purchase_id`, `item_id`, `user_id`, `purchase_date`) VALUES
                                                                                (9, 1, 33, '2025-02-06 23:35:45'),
                                                                                (10, 1, 33, '2025-02-07 01:51:45'),
                                                                                (11, 2, 33, '2025-02-07 01:58:55'),
                                                                                (12, 2, 33, '2025-02-07 02:03:53'),
                                                                                (13, 3, 33, '2025-02-07 02:04:10'),
                                                                                (14, 2, 23, '2025-02-07 02:05:59'),
                                                                                (15, 3, 23, '2025-02-07 02:06:26'),
                                                                                (32, 1, 44, '2025-02-10 15:13:40'),
                                                                                (33, 2, 44, '2025-02-10 15:13:47'),
                                                                                (34, 1, 23, '2025-02-10 16:41:35');

-- --------------------------------------------------------

--
-- 테이블 구조 `PaintingAssessment`
--

CREATE TABLE `PaintingAssessment` (
                                      `painting_assessment_id` int NOT NULL,
                                      `user_id` int NOT NULL,
                                      `writer_id` int NOT NULL,
                                      `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                      `score` float DEFAULT NULL,
                                      `picture_id` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 테이블 구조 `Picture`
--

CREATE TABLE `Picture` (
                           `picture_id` int NOT NULL,
                           `user_id` int NOT NULL COMMENT '사용자 ID',
                           `image_url` varchar(255) DEFAULT NULL,
                           `topic` varchar(255) DEFAULT NULL,
                           `title` varchar(255) DEFAULT NULL,
                           `description` text,
                           `is_displayed` tinyint(1) DEFAULT NULL,
                           `created_at` datetime DEFAULT NULL,
                           `rotation` int DEFAULT '0',
                           `x_val` float DEFAULT NULL COMMENT 'x 위치 ',
                           `y_val` float DEFAULT NULL COMMENT 'y 위치 ',
                           `funiture` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 테이블의 덤프 데이터 `Picture`
--

INSERT INTO `Picture` (`picture_id`, `user_id`, `image_url`, `topic`, `title`, `description`, `is_displayed`, `created_at`, `rotation`, `x_val`, `y_val`, `funiture`) VALUES
                                                                                                                                                                          (3, 14, 'image url', 'banana', 'happy', 'happy!!', 1, NULL, 90, 0, 0, NULL),
                                                                                                                                                                          (4, 33, 'image', 'person', 'man', 'man', 0, NULL, 0, 0, 0, NULL),
                                                                                                                                                                          (5, 33, 'image', 'chair', 'chair', 'chair!', 0, NULL, 0, 0, 0, NULL);

-- --------------------------------------------------------

--
-- 테이블 구조 `Ranking`
--

CREATE TABLE `Ranking` (
                           `ranking_id` int NOT NULL,
                           `user_id` int NOT NULL COMMENT '사용자 ID',
                           `rank_position` int DEFAULT NULL COMMENT '순위',
                           `win_rate` float DEFAULT NULL COMMENT '승률',
                           `updated_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 테이블의 덤프 데이터 `Ranking`
--

INSERT INTO `Ranking` (`ranking_id`, `user_id`, `rank_position`, `win_rate`, `updated_at`) VALUES
                                                                                               (3, 8, NULL, NULL, NULL),
                                                                                               (4, 15, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- 테이블 구조 `Room`
--

CREATE TABLE `Room` (
                        `room_id` int NOT NULL,
                        `host_id` int NOT NULL COMMENT '방 생성자 (호스트) ID',
                        `room_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '방 제목',
                        `status` enum('WAIT','PLAY') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'WAIT' COMMENT '방 상태 (예: 대기중, 시작됨 등)',
                        `max_players` int DEFAULT '4' COMMENT '최대 플레이어 수',
                        `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '방 생성 시간',
                        `rounds` int NOT NULL DEFAULT '1' COMMENT '라운드 수 입니다.',
                        `mode` enum('USER','AI') NOT NULL DEFAULT 'USER' COMMENT '게임 모드입니다.',
                        `level` enum('EASY','NORMAL','HARD') NOT NULL DEFAULT 'NORMAL' COMMENT '게임 난이도입니다.',
                        `round_time` int NOT NULL DEFAULT '60' COMMENT '라운드당 게임 제한 시간입니다.'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 테이블의 덤프 데이터 `Room`
--

INSERT INTO `Room` (`room_id`, `host_id`, `room_name`, `status`, `max_players`, `created_at`, `rounds`, `mode`, `level`, `round_time`) VALUES
    (584, 23, '제목이 없습니다!', 'WAIT', 4, '2025-02-13 17:41:42', 2, 'USER', 'EASY', 5);

-- --------------------------------------------------------

--
-- 테이블 구조 `RoomParticipant`
--

CREATE TABLE `RoomParticipant` (
                                   `id` int NOT NULL,
                                   `room_id` int NOT NULL COMMENT '방 ID',
                                   `user_id` int NOT NULL COMMENT '참가한 사용자 ID',
                                   `joined_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 테이블 구조 `RoundGame`
--

CREATE TABLE `RoundGame` (
                             `round_game_id` int NOT NULL COMMENT '라운드 게임 고유 ID',
                             `game_id` int NOT NULL COMMENT '게임 ID',
                             `room_id` int NOT NULL COMMENT '방 ID',
                             `user_id` int NOT NULL COMMENT '참여 사용자 ID',
                             `topic` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 테이블 구조 `Settings`
--

CREATE TABLE `Settings` (
                            `setting_id` int NOT NULL,
                            `user_id` int NOT NULL COMMENT '사용자 ID',
                            `sound` tinyint(1) DEFAULT '1' COMMENT '사운드 설정 여부',
                            `vibration` tinyint(1) DEFAULT '1' COMMENT '진동 설정 여부',
                            `updated_at` datetime DEFAULT NULL COMMENT '설정 변경 시간'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 테이블 구조 `Users`
--

CREATE TABLE `Users` (
                         `user_id` int NOT NULL,
                         `email` varchar(255) DEFAULT NULL COMMENT '이메일 (구글/카카오 연동 등)',
                         `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '사용자 고유 ID',
                         `password` varchar(255) DEFAULT NULL COMMENT '일반 로그인 시 비밀번호',
                         `nickname` varchar(255) DEFAULT NULL COMMENT '닉네임 (중복 체크)',
                         `points` int DEFAULT '0' COMMENT '보유 포인트',
                         `games_won` int DEFAULT '0' COMMENT '승리 게임 수',
                         `total_games` int DEFAULT '0' COMMENT '총 게임 수',
                         `level` int DEFAULT '1' COMMENT '사용자 레벨',
                         `exp` int DEFAULT '0' COMMENT '사용자 경험치',
                         `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '가입일',
                         `userProfileItemId` int NOT NULL DEFAULT '1' COMMENT '유저 프로필 아이템 ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 테이블의 덤프 데이터 `Users`
--

INSERT INTO `Users` (`user_id`, `email`, `id`, `password`, `nickname`, `points`, `games_won`, `total_games`, `level`, `exp`, `created_at`, `userProfileItemId`) VALUES
                                                                                                                                                                    (1, 'dnen14@naver.com', 'ssafy', 'ssafy', 'ssafyKing', 0, 0, 0, 1, 0, '2025-02-03 06:02:24', 1),
                                                                                                                                                                    (8, 'string', 'string', 'string', 'string', 0, 0, 0, 1, 0, '2025-02-04 04:58:16', 1),
                                                                                                                                                                    (10, '', 'megacoffee', 'newpassword', 'as', 0, 0, 0, 1, 0, '2025-02-04 05:00:33', 1),
                                                                                                                                                                    (13, '', 'asdd', 'A!', 'asd', 0, 0, 0, 1, 0, '2025-02-04 05:20:17', 1),
                                                                                                                                                                    (14, '', 'user14gg', 'newnewpassword', 'newnicknick', 0, 0, 0, 1, 0, '2025-02-04 05:24:14', 1),
                                                                                                                                                                    (15, '', 'juheon', 'A!', 'nick2', 0, 0, 0, 1, 0, '2025-02-04 05:29:22', 1),
                                                                                                                                                                    (18, '', 'a1234564789', 'A!', 'aaaa', 0, 0, 0, 1, 0, '2025-02-04 06:14:49', 1),
                                                                                                                                                                    (19, NULL, 'user123', 'pass1234', '홍길동', 0, 0, 0, 1, 0, '2025-02-04 06:52:01', 1),
                                                                                                                                                                    (23, '', 'id01', 'A!', 'aaaaa', 7000, 0, 0, 1, 0, '2025-02-06 04:44:34', 1),
                                                                                                                                                                    (33, NULL, 'admin', 'd108', 'admin', 99989000, 0, 0, 1, 0, '2025-02-06 07:31:52', 1),
                                                                                                                                                                    (34, NULL, 'id92', 'A!', 'asAA', 0, 0, 0, 1, 0, '2025-02-07 03:03:00', 1),
                                                                                                                                                                    (38, NULL, '도승국', '도승국', '도승국', 0, 0, 0, 1, 0, '2025-02-08 23:27:54', 1),
                                                                                                                                                                    (41, NULL, '1', '1', '1', 0, 0, 0, 1, 0, '2025-02-10 10:38:21', 1),
                                                                                                                                                                    (42, NULL, '2', '1', '2', 0, 0, 0, 1, 0, '2025-02-10 10:38:40', 1),
                                                                                                                                                                    (43, NULL, '12344', '12344', '1234', 0, 0, 0, 1, 0, '2025-02-10 12:52:50', 1),
                                                                                                                                                                    (44, NULL, 'dd108', 'DD108@', 'dd108', 98111, 0, 0, 1, 0, '2025-02-10 13:16:05', 1),
                                                                                                                                                                    (45, NULL, 'ddd', 'DDD@', 'ddd', 0, 0, 0, 1, 0, '2025-02-10 13:59:38', 1),
                                                                                                                                                                    (46, NULL, '도승국1', '도승국1', '도승국1', 0, 0, 0, 1, 0, '2025-02-10 16:28:52', 1),
                                                                                                                                                                    (47, NULL, '도승국2', '도승국2', '도승국2', 0, 0, 0, 1, 0, '2025-02-10 16:48:20', 1),
                                                                                                                                                                    (48, NULL, 'd', 'A1!', 'a1', 0, 0, 0, 1, 0, '2025-02-11 12:34:01', 1),
                                                                                                                                                                    (49, NULL, 'jhjh', 'passwororro', 'jh', 0, 0, 0, 1, 0, '2025-02-11 12:47:06', 1),
                                                                                                                                                                    (50, NULL, 'hello', 'A!', 'hihi', 0, 0, 0, 1, 0, '2025-02-11 22:44:27', 1);

--
-- 덤프된 테이블의 인덱스
--

--
-- 테이블의 인덱스 `Friend`
--
ALTER TABLE `Friend`
    ADD PRIMARY KEY (`user_id`,`friend_id`),
  ADD KEY `fk_friend_friend` (`friend_id`);

--
-- 테이블의 인덱스 `Game`
--
ALTER TABLE `Game`
    ADD PRIMARY KEY (`game_id`),
  ADD KEY `fk_game_room` (`room_id`),
  ADD KEY `fk_game_user` (`user_id`);

--
-- 테이블의 인덱스 `GameTopicEasy`
--
ALTER TABLE `GameTopicEasy`
    ADD PRIMARY KEY (`easy_topic_id`);

--
-- 테이블의 인덱스 `GameTopicHard`
--
ALTER TABLE `GameTopicHard`
    ADD PRIMARY KEY (`hard_topic_id`);

--
-- 테이블의 인덱스 `GameTopicNormal`
--
ALTER TABLE `GameTopicNormal`
    ADD PRIMARY KEY (`normal_topic_id`);

--
-- 테이블의 인덱스 `Item`
--
ALTER TABLE `Item`
    ADD PRIMARY KEY (`item_id`);

--
-- 테이블의 인덱스 `MyFurniture`
--
ALTER TABLE `MyFurniture`
    ADD PRIMARY KEY (`myfurniture_id`),
  ADD KEY `fk_myfurniture_user` (`user_id`);

--
-- 테이블의 인덱스 `MyItem`
--
ALTER TABLE `MyItem`
    ADD PRIMARY KEY (`purchase_id`),
  ADD KEY `fk_myitem_item` (`item_id`),
  ADD KEY `fk_myitem_user` (`user_id`);

--
-- 테이블의 인덱스 `PaintingAssessment`
--
ALTER TABLE `PaintingAssessment`
    ADD PRIMARY KEY (`painting_assessment_id`) USING BTREE,
  ADD KEY `fk_userId` (`user_id`) USING BTREE,
  ADD KEY `fk_writer_id` (`writer_id`),
  ADD KEY `fk_picture_id` (`picture_id`);

--
-- 테이블의 인덱스 `Picture`
--
ALTER TABLE `Picture`
    ADD PRIMARY KEY (`picture_id`),
  ADD KEY `fk_picture_user` (`user_id`);

--
-- 테이블의 인덱스 `Ranking`
--
ALTER TABLE `Ranking`
    ADD PRIMARY KEY (`ranking_id`),
  ADD KEY `fk_ranking_user` (`user_id`);

--
-- 테이블의 인덱스 `Room`
--
ALTER TABLE `Room`
    ADD PRIMARY KEY (`room_id`),
  ADD KEY `fk_room_host` (`host_id`);

--
-- 테이블의 인덱스 `RoomParticipant`
--
ALTER TABLE `RoomParticipant`
    ADD PRIMARY KEY (`id`),
  ADD KEY `fk_roomparticipant_room` (`room_id`),
  ADD KEY `fk_roomparticipant_user` (`user_id`);

--
-- 테이블의 인덱스 `RoundGame`
--
ALTER TABLE `RoundGame`
    ADD PRIMARY KEY (`round_game_id`),
  ADD KEY `fk_roundgame_game` (`game_id`),
  ADD KEY `fk_roundgame_user` (`user_id`);

--
-- 테이블의 인덱스 `Settings`
--
ALTER TABLE `Settings`
    ADD PRIMARY KEY (`setting_id`),
  ADD KEY `fk_settings_user` (`user_id`);

--
-- 테이블의 인덱스 `Users`
--
ALTER TABLE `Users`
    ADD PRIMARY KEY (`user_id`);

--
-- 덤프된 테이블의 AUTO_INCREMENT
--

--
-- 테이블의 AUTO_INCREMENT `Game`
--
ALTER TABLE `Game`
    MODIFY `game_id` int NOT NULL AUTO_INCREMENT COMMENT '게임 고유 ID';

--
-- 테이블의 AUTO_INCREMENT `GameTopicEasy`
--
ALTER TABLE `GameTopicEasy`
    MODIFY `easy_topic_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=161;

--
-- 테이블의 AUTO_INCREMENT `GameTopicHard`
--
ALTER TABLE `GameTopicHard`
    MODIFY `hard_topic_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=152;

--
-- 테이블의 AUTO_INCREMENT `GameTopicNormal`
--
ALTER TABLE `GameTopicNormal`
    MODIFY `normal_topic_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=197;

--
-- 테이블의 AUTO_INCREMENT `Item`
--
ALTER TABLE `Item`
    MODIFY `item_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- 테이블의 AUTO_INCREMENT `MyFurniture`
--
ALTER TABLE `MyFurniture`
    MODIFY `myfurniture_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- 테이블의 AUTO_INCREMENT `MyItem`
--
ALTER TABLE `MyItem`
    MODIFY `purchase_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=35;

--
-- 테이블의 AUTO_INCREMENT `PaintingAssessment`
--
ALTER TABLE `PaintingAssessment`
    MODIFY `painting_assessment_id` int NOT NULL AUTO_INCREMENT;

--
-- 테이블의 AUTO_INCREMENT `Picture`
--
ALTER TABLE `Picture`
    MODIFY `picture_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- 테이블의 AUTO_INCREMENT `Ranking`
--
ALTER TABLE `Ranking`
    MODIFY `ranking_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- 테이블의 AUTO_INCREMENT `Room`
--
ALTER TABLE `Room`
    MODIFY `room_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=585;

--
-- 테이블의 AUTO_INCREMENT `RoomParticipant`
--
ALTER TABLE `RoomParticipant`
    MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- 테이블의 AUTO_INCREMENT `RoundGame`
--
ALTER TABLE `RoundGame`
    MODIFY `round_game_id` int NOT NULL AUTO_INCREMENT COMMENT '라운드 게임 고유 ID';

--
-- 테이블의 AUTO_INCREMENT `Settings`
--
ALTER TABLE `Settings`
    MODIFY `setting_id` int NOT NULL AUTO_INCREMENT;

--
-- 테이블의 AUTO_INCREMENT `Users`
--
ALTER TABLE `Users`
    MODIFY `user_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=51;

--
-- 덤프된 테이블의 제약사항
--

--
-- 테이블의 제약사항 `Friend`
--
ALTER TABLE `Friend`
    ADD CONSTRAINT `fk_friend_friend` FOREIGN KEY (`friend_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_friend_user` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE;

--
-- 테이블의 제약사항 `Game`
--
ALTER TABLE `Game`
    ADD CONSTRAINT `fk_game_room` FOREIGN KEY (`room_id`) REFERENCES `Room` (`room_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_game_user` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE;

--
-- 테이블의 제약사항 `MyFurniture`
--
ALTER TABLE `MyFurniture`
    ADD CONSTRAINT `fk_myfurniture_user` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE;

--
-- 테이블의 제약사항 `MyItem`
--
ALTER TABLE `MyItem`
    ADD CONSTRAINT `fk_myitem_item` FOREIGN KEY (`item_id`) REFERENCES `Item` (`item_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_myitem_user` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE;

--
-- 테이블의 제약사항 `PaintingAssessment`
--
ALTER TABLE `PaintingAssessment`
    ADD CONSTRAINT `fk_picture_id` FOREIGN KEY (`picture_id`) REFERENCES `Picture` (`picture_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_writer_id` FOREIGN KEY (`writer_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT;

--
-- 테이블의 제약사항 `Picture`
--
ALTER TABLE `Picture`
    ADD CONSTRAINT `fk_picture_user` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE;

--
-- 테이블의 제약사항 `Ranking`
--
ALTER TABLE `Ranking`
    ADD CONSTRAINT `fk_ranking_user` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE;

--
-- 테이블의 제약사항 `Room`
--
ALTER TABLE `Room`
    ADD CONSTRAINT `fk_room_host` FOREIGN KEY (`host_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE;

--
-- 테이블의 제약사항 `RoomParticipant`
--
ALTER TABLE `RoomParticipant`
    ADD CONSTRAINT `fk_roomparticipant_room` FOREIGN KEY (`room_id`) REFERENCES `Room` (`room_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_roomparticipant_user` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE;

--
-- 테이블의 제약사항 `RoundGame`
--
ALTER TABLE `RoundGame`
    ADD CONSTRAINT `fk_roundgame_game` FOREIGN KEY (`game_id`) REFERENCES `Game` (`game_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_roundgame_user` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE;

--
-- 테이블의 제약사항 `Settings`
--
ALTER TABLE `Settings`
    ADD CONSTRAINT `fk_settings_user` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
