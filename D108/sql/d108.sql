-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- 호스트: mysql:3306
-- 생성 시간: 25-02-12 02:42
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
(38, 23, 'ACCEPTED');

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
  `is_used` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 테이블의 덤프 데이터 `GameTopicEasy`
--

INSERT INTO `GameTopicEasy` (`easy_topic_id`, `topic`, `topic_en`, `is_used`) VALUES
(1, '집', 'house', NULL),
(2, '나무', 'tree', NULL),
(3, '해', 'sun', NULL),
(4, '달', 'moon', NULL),
(5, '강아지', 'puppy', NULL),
(6, '고양이', 'cat', NULL),
(7, '자동차', 'car', NULL),
(8, '버스', 'bus', NULL),
(9, '비행기', 'airplane', NULL),
(10, '배', 'ship', NULL),
(11, '사과', 'apple', NULL),
(12, '바나나', 'banana', NULL),
(13, '책', 'book', NULL),
(14, '의자', 'chair', NULL),
(15, '테이블', 'table', NULL),
(16, '꽃', 'flower', NULL),
(17, '산', 'mountain', NULL),
(18, '구름', 'cloud', NULL),
(19, '우산', 'umbrella', NULL),
(20, '물', 'water', NULL),
(21, '불', 'fire', NULL),
(22, '연필', 'pencil', NULL),
(23, '공', 'ball', NULL),
(24, '시계', 'clock', NULL),
(25, '전화기', 'phone', NULL),
(26, '컴퓨터', NULL, NULL),
(27, '책상', NULL, NULL),
(28, '창문', NULL, NULL),
(29, '문', NULL, NULL),
(30, '침대', NULL, NULL),
(31, '인형', NULL, NULL),
(32, '소파', NULL, NULL),
(33, 'TV', NULL, NULL),
(34, '냉장고', NULL, NULL),
(35, '병', NULL, NULL),
(36, '컵', NULL, NULL),
(37, '신발', NULL, NULL),
(38, '모자', NULL, NULL),
(39, '티셔츠', NULL, NULL),
(40, '바지', NULL, NULL),
(41, '스푼', NULL, NULL),
(42, '포크', NULL, NULL),
(43, '칼', NULL, NULL),
(44, '접시', NULL, NULL),
(45, '치마', NULL, NULL),
(46, '우유', NULL, NULL),
(47, '달걀', NULL, NULL),
(48, '빵', NULL, NULL),
(49, '치즈', NULL, NULL),
(50, '나비', NULL, NULL),
(51, '물고기', NULL, NULL),
(52, '새', NULL, NULL),
(53, '꽃병', NULL, NULL),
(54, '사탕', NULL, NULL),
(55, '케이크', NULL, NULL),
(56, '피자', NULL, NULL),
(57, '햄버거', NULL, NULL),
(58, '샌드위치', NULL, NULL),
(59, '도넛', NULL, NULL),
(60, '아이스크림', NULL, NULL),
(61, '라면', NULL, NULL),
(62, '커피', NULL, NULL),
(63, '차', NULL, NULL),
(64, '소금', NULL, NULL),
(65, '후추', NULL, NULL),
(66, '바다', NULL, NULL),
(67, '호수', NULL, NULL),
(68, '강', NULL, NULL),
(69, '공원', NULL, NULL),
(70, '학교', NULL, NULL),
(71, '병원', NULL, NULL),
(72, '우체국', NULL, NULL),
(73, '은행', NULL, NULL),
(74, '시장', NULL, NULL),
(75, '공장', NULL, NULL),
(76, '교회', NULL, NULL),
(77, '사원', NULL, NULL),
(78, '마을', NULL, NULL),
(79, '도시', NULL, NULL),
(80, '다리', NULL, NULL),
(81, '길', NULL, NULL),
(82, '도로', NULL, NULL),
(83, '신호등', NULL, NULL),
(84, '교차로', NULL, NULL),
(85, '버스정류장', NULL, NULL),
(86, '지하철', NULL, NULL),
(87, '트럭', NULL, NULL),
(88, '자전거', NULL, NULL),
(89, '오토바이', NULL, NULL),
(90, '비누', NULL, NULL),
(91, '칫솔', NULL, NULL),
(92, '치약', NULL, NULL),
(93, '샴푸', NULL, NULL),
(94, '수건', NULL, NULL),
(95, '거울', NULL, NULL),
(96, '빗', NULL, NULL),
(97, '손목시계', NULL, NULL),
(98, '안경', NULL, NULL),
(99, '지갑', NULL, NULL),
(100, '우편함', NULL, NULL);

-- --------------------------------------------------------

--
-- 테이블 구조 `GameTopicHard`
--

CREATE TABLE `GameTopicHard` (
  `hard_topic_id` int NOT NULL,
  `topic` varchar(255) DEFAULT NULL,
  `topic_en` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `is_used` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 테이블의 덤프 데이터 `GameTopicHard`
--

INSERT INTO `GameTopicHard` (`hard_topic_id`, `topic`, `topic_en`, `is_used`) VALUES
(1, '추상화', 'abstract painting', NULL),
(2, '초승달', 'crescent moon', NULL),
(3, '대통령', 'president', NULL),
(4, '오로라', 'aurora', NULL),
(5, '종달새', 'lark', NULL),
(6, '지구본', 'globe', NULL),
(7, '북극성', 'Polaris', NULL),
(8, '만년설', 'permanent snow', NULL),
(9, '동상', 'bronze statue', NULL),
(10, '비눗방울', 'soap bubbles', NULL),
(11, '만리장성', 'Great Wall of China', NULL),
(12, '우주정거장', 'space station', NULL),
(13, '정글', 'jungle', NULL),
(14, '암벽등반', 'rock climbing', NULL),
(15, '유성우', 'meteor shower', NULL),
(16, '줄다리기', 'tug-of-war', NULL),
(17, '나침반', 'compass', NULL),
(18, '미로', 'maze', NULL),
(19, '일식', 'solar eclipse', NULL),
(20, '용암', 'lava', NULL),
(21, '암흑', 'darkness', NULL),
(22, '도룡뇽', 'Salamander', NULL),
(23, '아코디언', 'accordion', NULL),
(24, '벽난로', 'fireplace', NULL),
(25, '진공청소기', 'vacuum', NULL),
(26, '우주', '', NULL),
(27, '평행우주', '', NULL),
(28, '공허', '', NULL),
(29, '전환', '', NULL),
(30, '공명', '', NULL),
(31, '인내', '', NULL),
(32, '역경', '', NULL),
(33, '절망', '', NULL),
(34, '심리', '', NULL),
(35, '명상', '', NULL),
(36, '몰입', '', NULL),
(37, '영원', '', NULL),
(38, '해탈', '', NULL),
(39, '선과악', '', NULL),
(40, '운명의굴레', '', NULL),
(41, '불멸', '', NULL),
(42, '탄생', '', NULL),
(43, '소멸', '', NULL),
(44, '무상', '', NULL),
(45, '미묘함', '', NULL),
(46, '이중성', '', NULL),
(47, '자각', '', NULL),
(48, '의식', '', NULL),
(49, '무의식', '', NULL),
(50, '감각', '', NULL),
(51, '존재', '', NULL),
(52, '변증법', '', NULL),
(53, '전율', '', NULL),
(54, '신비', '', NULL),
(55, '모순', '', NULL),
(56, '변이', '', NULL),
(57, '진화', '', NULL),
(58, '자비', '', NULL),
(59, '초월', '', NULL),
(60, '연대', '', NULL),
(61, '갈등', '', NULL),
(62, '분열', '', NULL),
(63, '융합', '', NULL),
(64, '심상', '', NULL),
(65, '추억', '', NULL),
(66, '수수께끼', '', NULL),
(67, '침묵', '', NULL),
(68, '고요', '', NULL),
(69, '불가사의', '', NULL),
(70, '복잡성', '', NULL),
(71, '조화', '', NULL),
(72, '역동성', '', NULL),
(73, '해방', '', NULL),
(74, '간극', '', NULL),
(75, '격변', '', NULL),
(76, '흐름', '', NULL),
(77, '패턴', '', NULL),
(78, '그림자', '', NULL),
(79, '반사', '', NULL),
(80, '경계', '', NULL),
(81, '불가항력', '', NULL),
(82, '상실', '', NULL),
(83, '수렴', '', NULL),
(84, '발산', '', NULL),
(85, '고리', '', NULL),
(86, '겹침', '', NULL),
(87, '혼연일체', '', NULL),
(88, '환영', '', NULL),
(89, '파동', '', NULL),
(90, '불확실성', '', NULL),
(91, '초현실', '', NULL),
(92, '환상', '', NULL),
(93, '은유', '', NULL),
(94, '시공', '', NULL),
(95, '투영', '', NULL),
(96, '공존', '', NULL),
(97, '모호함', '', NULL),
(98, '정체', '', NULL),
(99, '실존', '', NULL),
(100, '내면', '', NULL);

-- --------------------------------------------------------

--
-- 테이블 구조 `GameTopicNormal`
--

CREATE TABLE `GameTopicNormal` (
  `normal_topic_id` int NOT NULL,
  `topic` varchar(255) DEFAULT NULL,
  `topic_en` varchar(255) DEFAULT NULL,
  `is_used` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 테이블의 덤프 데이터 `GameTopicNormal`
--

INSERT INTO `GameTopicNormal` (`normal_topic_id`, `topic`, `topic_en`, `is_used`) VALUES
(1, '피라미드', 'pyramid', NULL),
(2, '카메라', 'camera', NULL),
(3, '해적선', 'pirate ship', NULL),
(4, '우주선', 'spaceship', NULL),
(5, '성', 'castle', NULL),
(6, '로봇', 'robot', NULL),
(7, '악어', 'crocodile', NULL),
(8, '공룡', 'dinosaur', NULL),
(9, '오리', 'duck', NULL),
(10, '펭귄', 'penguin', NULL),
(11, '기차', 'train', NULL),
(12, '도자기', 'pottery', NULL),
(13, '사막', 'desert', NULL),
(14, '음악', 'music', NULL),
(15, '섬', 'island', NULL),
(16, '용', 'dragon', NULL),
(17, '마법사', 'wizard', NULL),
(18, '도서관', 'library', NULL),
(19, '놀이공원', 'amusement park', NULL),
(20, '회전목마', 'merry-go-round', NULL),
(21, '탑', 'tower', NULL),
(22, '커피', 'coffee', NULL),
(23, '폭포', 'waterfall', NULL),
(24, '해변', 'beach', NULL),
(25, '모래성', 'sand castle', NULL),
(26, '열기구', NULL, NULL),
(27, '풍선', NULL, NULL),
(28, '자동차경주', NULL, NULL),
(29, '축구경기장', NULL, NULL),
(30, '농구코트', NULL, NULL),
(31, '야구장', NULL, NULL),
(32, '전쟁터', NULL, NULL),
(33, '잠수함', NULL, NULL),
(34, '피아노', NULL, NULL),
(35, '기타', NULL, NULL),
(36, '드럼', NULL, NULL),
(37, '바이올린', NULL, NULL),
(38, '카메라', NULL, NULL),
(39, '스마트폰', NULL, NULL),
(40, '인터넷', NULL, NULL),
(41, '다이아몬드', NULL, NULL),
(42, '루비', NULL, NULL),
(43, '사파리', NULL, NULL),
(44, '산호초', NULL, NULL),
(45, '해마', NULL, NULL),
(46, '북극곰', NULL, NULL),
(47, '사자', NULL, NULL),
(48, '호랑이', NULL, NULL),
(49, '기린', NULL, NULL),
(50, '코뿔소', NULL, NULL),
(51, '얼룩말', NULL, NULL),
(52, '늑대', NULL, NULL),
(53, '여우', NULL, NULL),
(54, '원숭이', NULL, NULL),
(55, '침팬지', NULL, NULL),
(56, '부엉이', NULL, NULL),
(57, '독수리', NULL, NULL),
(58, '수리', NULL, NULL),
(59, '무지개', NULL, NULL),
(60, '별똥별', NULL, NULL),
(61, '은하수', NULL, NULL),
(62, '행성', NULL, NULL),
(63, '위성', NULL, NULL),
(64, '우주인', NULL, NULL),
(65, '벽난로', NULL, NULL),
(66, '캠핑카', NULL, NULL),
(67, '텐트', NULL, NULL),
(68, '모닥불', NULL, NULL),
(69, '낚시대', NULL, NULL),
(70, '요트', NULL, NULL),
(71, '다이빙', NULL, NULL),
(72, '서핑', NULL, NULL),
(73, '스노우보드', NULL, NULL),
(74, '스키', NULL, NULL),
(75, '썰매', NULL, NULL),
(76, '얼음조각', NULL, NULL),
(77, '눈사람', NULL, NULL),
(78, '크리스마스트리', NULL, NULL),
(79, '할로윈호박', NULL, NULL),
(80, '석양', NULL, NULL),
(81, '일출', NULL, NULL),
(82, '바람개비', NULL, NULL),
(83, '손전등', NULL, NULL),
(84, '망원경', NULL, NULL),
(85, '전구', NULL, NULL),
(86, '달팽이', NULL, NULL),
(87, '모래시계', NULL, NULL),
(88, '자물쇠', NULL, NULL),
(89, '열쇠', NULL, NULL),
(90, '지도', NULL, NULL),
(91, '지구본', NULL, NULL),
(92, '만화경', NULL, NULL),
(93, '라디오', NULL, NULL),
(94, '스피커', NULL, NULL),
(95, '헬리콥터', NULL, NULL),
(96, '풍차', NULL, NULL),
(97, '미끄럼틀', NULL, NULL),
(98, '보물상자', NULL, NULL),
(99, '화산', NULL, NULL),
(100, '우주정거장', NULL, NULL);

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
(2, 'mario', '1', 1000, '마 리 오', 'https://i12d108.p.ssafy.io/api/item/mario.png'),
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
  `y_val` float DEFAULT NULL COMMENT 'y 위치 '
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 테이블의 덤프 데이터 `Picture`
--

INSERT INTO `Picture` (`picture_id`, `user_id`, `image_url`, `topic`, `title`, `description`, `is_displayed`, `created_at`, `rotation`, `x_val`, `y_val`) VALUES
(3, 14, 'image url', 'banana', 'happy', 'happy!!', 1, NULL, 90, 0, 0),
(4, 33, 'image', 'person', 'man', 'man', 1, NULL, 0, 0.1, 0.1),
(5, 33, 'image', 'chair', 'chair', 'chair!', 0, NULL, 0, 0, 0);

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
(137, 41, 'ㅏ', 'WAIT', 4, '2025-02-12 11:42:10', 1, 'USER', 'EASY', 30);

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

-- --------------------------------------------------------

--
-- 테이블 구조 `GuestBook`
--

CREATE TABLE `PaintingAssessment` (
                             `painting_assessment_id` int NOT NULL,
                             `user_id` int NOT NULL,
                             `writer_id` int NOT NULL,
                             'picture_id' int NOT NULL,
                             `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                             `score` float DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 덤프된 테이블의 인덱스
--

--
-- 테이블의 인덱스 `GuestBook`
--
ALTER TABLE `PaintingAssessment`
    ADD PRIMARY KEY (`painting_assessment_id`),
    ADD KEY `fk_userId` (`user_id`) USING BTREE;

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
-- 테이블의 AUTO_INCREMENT `GuestBook`
--
ALTER TABLE `PaintingAssessment`
    MODIFY `painting_assessment_id` int NOT NULL AUTO_INCREMENT;
COMMIT;

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
  MODIFY `myfurniture_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- 테이블의 AUTO_INCREMENT `MyItem`
--
ALTER TABLE `MyItem`
  MODIFY `purchase_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=35;

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
  MODIFY `room_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=138;

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
