-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- 호스트: mysql:3306
-- 생성 시간: 25-02-10 01:15
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
                                 `topic` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 테이블의 덤프 데이터 `GameTopicEasy`
--

INSERT INTO `GameTopicEasy` (`easy_topic_id`, `topic`) VALUES
                                                           (1, '집'),
                                                           (2, '나무'),
                                                           (3, '해'),
                                                           (4, '달'),
                                                           (5, '강아지'),
                                                           (6, '고양이'),
                                                           (7, '자동차'),
                                                           (8, '버스'),
                                                           (9, '비행기'),
                                                           (10, '배'),
                                                           (11, '사과'),
                                                           (12, '바나나'),
                                                           (13, '책'),
                                                           (14, '의자'),
                                                           (15, '테이블'),
                                                           (16, '꽃'),
                                                           (17, '산'),
                                                           (18, '구름'),
                                                           (19, '우산'),
                                                           (20, '물'),
                                                           (21, '불'),
                                                           (22, '연필'),
                                                           (23, '공'),
                                                           (24, '시계'),
                                                           (25, '전화기'),
                                                           (26, '컴퓨터'),
                                                           (27, '책상'),
                                                           (28, '창문'),
                                                           (29, '문'),
                                                           (30, '침대'),
                                                           (31, '인형'),
                                                           (32, '소파'),
                                                           (33, 'TV'),
                                                           (34, '냉장고'),
                                                           (35, '병'),
                                                           (36, '컵'),
                                                           (37, '신발'),
                                                           (38, '모자'),
                                                           (39, '티셔츠'),
                                                           (40, '바지'),
                                                           (41, '스푼'),
                                                           (42, '포크'),
                                                           (43, '칼'),
                                                           (44, '접시'),
                                                           (45, '치마'),
                                                           (46, '우유'),
                                                           (47, '달걀'),
                                                           (48, '빵'),
                                                           (49, '치즈'),
                                                           (50, '나비'),
                                                           (51, '물고기'),
                                                           (52, '새'),
                                                           (53, '꽃병'),
                                                           (54, '사탕'),
                                                           (55, '케이크'),
                                                           (56, '피자'),
                                                           (57, '햄버거'),
                                                           (58, '샌드위치'),
                                                           (59, '도넛'),
                                                           (60, '아이스크림'),
                                                           (61, '라면'),
                                                           (62, '커피'),
                                                           (63, '차'),
                                                           (64, '소금'),
                                                           (65, '후추'),
                                                           (66, '바다'),
                                                           (67, '호수'),
                                                           (68, '강'),
                                                           (69, '공원'),
                                                           (70, '학교'),
                                                           (71, '병원'),
                                                           (72, '우체국'),
                                                           (73, '은행'),
                                                           (74, '시장'),
                                                           (75, '공장'),
                                                           (76, '교회'),
                                                           (77, '사원'),
                                                           (78, '마을'),
                                                           (79, '도시'),
                                                           (80, '다리'),
                                                           (81, '길'),
                                                           (82, '도로'),
                                                           (83, '신호등'),
                                                           (84, '교차로'),
                                                           (85, '버스정류장'),
                                                           (86, '지하철'),
                                                           (87, '트럭'),
                                                           (88, '자전거'),
                                                           (89, '오토바이'),
                                                           (90, '비누'),
                                                           (91, '칫솔'),
                                                           (92, '치약'),
                                                           (93, '샴푸'),
                                                           (94, '수건'),
                                                           (95, '거울'),
                                                           (96, '빗'),
                                                           (97, '손목시계'),
                                                           (98, '안경'),
                                                           (99, '지갑'),
                                                           (100, '우편함');

-- --------------------------------------------------------

--
-- 테이블 구조 `GameTopicHard`
--

CREATE TABLE `GameTopicHard` (
                                 `hard_topic_id` int NOT NULL,
                                 `topic` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 테이블의 덤프 데이터 `GameTopicHard`
--

INSERT INTO `GameTopicHard` (`hard_topic_id`, `topic`) VALUES
                                                           (1, '추상화'),
                                                           (2, '몽환'),
                                                           (3, '자유'),
                                                           (4, '운명'),
                                                           (5, '영혼'),
                                                           (6, '시간'),
                                                           (7, '기억'),
                                                           (8, '꿈'),
                                                           (9, '상상력'),
                                                           (10, '우울'),
                                                           (11, '희망'),
                                                           (12, '혼돈'),
                                                           (13, '운'),
                                                           (14, '감정'),
                                                           (15, '정체성'),
                                                           (16, '불안'),
                                                           (17, '심연'),
                                                           (18, '미로'),
                                                           (19, '역설'),
                                                           (20, '혁명'),
                                                           (21, '암흑'),
                                                           (22, '붕괴'),
                                                           (23, '경이'),
                                                           (24, '고독'),
                                                           (25, '무한'),
                                                           (26, '우주'),
                                                           (27, '평행우주'),
                                                           (28, '공허'),
                                                           (29, '전환'),
                                                           (30, '공명'),
                                                           (31, '인내'),
                                                           (32, '역경'),
                                                           (33, '절망'),
                                                           (34, '심리'),
                                                           (35, '명상'),
                                                           (36, '몰입'),
                                                           (37, '영원'),
                                                           (38, '해탈'),
                                                           (39, '선과악'),
                                                           (40, '운명의굴레'),
                                                           (41, '불멸'),
                                                           (42, '탄생'),
                                                           (43, '소멸'),
                                                           (44, '무상'),
                                                           (45, '미묘함'),
                                                           (46, '이중성'),
                                                           (47, '자각'),
                                                           (48, '의식'),
                                                           (49, '무의식'),
                                                           (50, '감각'),
                                                           (51, '존재'),
                                                           (52, '변증법'),
                                                           (53, '전율'),
                                                           (54, '신비'),
                                                           (55, '모순'),
                                                           (56, '변이'),
                                                           (57, '진화'),
                                                           (58, '자비'),
                                                           (59, '초월'),
                                                           (60, '연대'),
                                                           (61, '갈등'),
                                                           (62, '분열'),
                                                           (63, '융합'),
                                                           (64, '심상'),
                                                           (65, '추억'),
                                                           (66, '수수께끼'),
                                                           (67, '침묵'),
                                                           (68, '고요'),
                                                           (69, '불가사의'),
                                                           (70, '복잡성'),
                                                           (71, '조화'),
                                                           (72, '역동성'),
                                                           (73, '해방'),
                                                           (74, '간극'),
                                                           (75, '격변'),
                                                           (76, '흐름'),
                                                           (77, '패턴'),
                                                           (78, '그림자'),
                                                           (79, '반사'),
                                                           (80, '경계'),
                                                           (81, '불가항력'),
                                                           (82, '상실'),
                                                           (83, '수렴'),
                                                           (84, '발산'),
                                                           (85, '고리'),
                                                           (86, '겹침'),
                                                           (87, '혼연일체'),
                                                           (88, '환영'),
                                                           (89, '파동'),
                                                           (90, '불확실성'),
                                                           (91, '초현실'),
                                                           (92, '환상'),
                                                           (93, '은유'),
                                                           (94, '시공'),
                                                           (95, '투영'),
                                                           (96, '공존'),
                                                           (97, '모호함'),
                                                           (98, '정체'),
                                                           (99, '실존'),
                                                           (100, '내면');

-- --------------------------------------------------------

--
-- 테이블 구조 `GameTopicNormal`
--

CREATE TABLE `GameTopicNormal` (
                                   `normal_topic_id` int NOT NULL,
                                   `topic` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 테이블의 덤프 데이터 `GameTopicNormal`
--

INSERT INTO `GameTopicNormal` (`normal_topic_id`, `topic`) VALUES
                                                               (1, '피라미드'),
                                                               (2, '고층빌딩'),
                                                               (3, '해적선'),
                                                               (4, '우주선'),
                                                               (5, '성'),
                                                               (6, '로봇'),
                                                               (7, '악어'),
                                                               (8, '공룡'),
                                                               (9, '오리'),
                                                               (10, '펭귄'),
                                                               (11, '기차'),
                                                               (12, '열차'),
                                                               (13, '사막'),
                                                               (14, '정글'),
                                                               (15, '섬'),
                                                               (16, '용'),
                                                               (17, '마법사'),
                                                               (18, '도서관'),
                                                               (19, '놀이공원'),
                                                               (20, '회전목마'),
                                                               (21, '탑'),
                                                               (22, '동상'),
                                                               (23, '폭포'),
                                                               (24, '해변'),
                                                               (25, '모래성'),
                                                               (26, '열기구'),
                                                               (27, '풍선'),
                                                               (28, '자동차경주'),
                                                               (29, '축구경기장'),
                                                               (30, '농구코트'),
                                                               (31, '야구장'),
                                                               (32, '전쟁터'),
                                                               (33, '잠수함'),
                                                               (34, '피아노'),
                                                               (35, '기타'),
                                                               (36, '드럼'),
                                                               (37, '바이올린'),
                                                               (38, '카메라'),
                                                               (39, '스마트폰'),
                                                               (40, '인터넷'),
                                                               (41, '다이아몬드'),
                                                               (42, '루비'),
                                                               (43, '사파리'),
                                                               (44, '산호초'),
                                                               (45, '해마'),
                                                               (46, '북극곰'),
                                                               (47, '사자'),
                                                               (48, '호랑이'),
                                                               (49, '기린'),
                                                               (50, '코뿔소'),
                                                               (51, '얼룩말'),
                                                               (52, '늑대'),
                                                               (53, '여우'),
                                                               (54, '원숭이'),
                                                               (55, '침팬지'),
                                                               (56, '부엉이'),
                                                               (57, '독수리'),
                                                               (58, '수리'),
                                                               (59, '무지개'),
                                                               (60, '별똥별'),
                                                               (61, '은하수'),
                                                               (62, '행성'),
                                                               (63, '위성'),
                                                               (64, '우주인'),
                                                               (65, '벽난로'),
                                                               (66, '캠핑카'),
                                                               (67, '텐트'),
                                                               (68, '모닥불'),
                                                               (69, '낚시대'),
                                                               (70, '요트'),
                                                               (71, '다이빙'),
                                                               (72, '서핑'),
                                                               (73, '스노우보드'),
                                                               (74, '스키'),
                                                               (75, '썰매'),
                                                               (76, '얼음조각'),
                                                               (77, '눈사람'),
                                                               (78, '크리스마스트리'),
                                                               (79, '할로윈호박'),
                                                               (80, '석양'),
                                                               (81, '일출'),
                                                               (82, '바람개비'),
                                                               (83, '손전등'),
                                                               (84, '망원경'),
                                                               (85, '전구'),
                                                               (86, '달팽이'),
                                                               (87, '모래시계'),
                                                               (88, '자물쇠'),
                                                               (89, '열쇠'),
                                                               (90, '지도'),
                                                               (91, '지구본'),
                                                               (92, '만화경'),
                                                               (93, '라디오'),
                                                               (94, '스피커'),
                                                               (95, '헬리콥터'),
                                                               (96, '풍차'),
                                                               (97, '미끄럼틀'),
                                                               (98, '보물상자'),
                                                               (99, '화산'),
                                                               (100, '우주정거장');

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
-- 테이블 구조 `MyFurniture`
--

CREATE TABLE `MyFurniture` (
                               `myfurniture_id` int NOT NULL,
                               `user_id` int NOT NULL COMMENT '사용자 ID',
                               `fur_id` int DEFAULT NULL COMMENT '가구 ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


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
                           `created_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


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
-- 테이블 구조 `Room`
--

CREATE TABLE `Room` (
                        `room_id` int NOT NULL,
                        `host_id` int NOT NULL COMMENT '방 생성자 (호스트) ID',
                        `room_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '방 제목',
                        `status` enum('WAIT','PLAY') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'WAIT' COMMENT '방 상태 (예: 대기중, 시작됨 등)',
                        `max_players` int DEFAULT '4' COMMENT '최대 플레이어 수',
                        `now_players` int DEFAULT '1' COMMENT '현재 참가한 플레이어 수',
                        `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '방 생성 시간'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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

                                                                                                                                                      (38, NULL, '도승국', '도승국', '도승국', 0, 0, 0, 1, 0, '2025-02-08 23:27:54', 1);

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
    MODIFY `easy_topic_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=101;

--
-- 테이블의 AUTO_INCREMENT `GameTopicHard`
--
ALTER TABLE `GameTopicHard`
    MODIFY `hard_topic_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=101;

--
-- 테이블의 AUTO_INCREMENT `GameTopicNormal`
--
ALTER TABLE `GameTopicNormal`
    MODIFY `normal_topic_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=101;

--
-- 테이블의 AUTO_INCREMENT `Item`
--
ALTER TABLE `Item`
    MODIFY `item_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- 테이블의 AUTO_INCREMENT `MyFurniture`
--
ALTER TABLE `MyFurniture`
    MODIFY `myfurniture_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- 테이블의 AUTO_INCREMENT `MyItem`
--
ALTER TABLE `MyItem`
    MODIFY `purchase_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

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
    MODIFY `room_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

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
    MODIFY `user_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=41;

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
