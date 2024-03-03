------------------------
-- for PostgreSQL 15+ --
------------------------

DROP TABLE IF EXISTS credentials;
DROP TABLE IF EXISTS topic_statuses;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS topics;
DROP TABLE IF EXISTS sections_moders;
DROP TABLE IF EXISTS sections;
DROP TABLE IF EXISTS usercards;



-- Создание БД
CREATE TABLE usercards(
  id serial primary key,
  nick varchar not null UNIQUE,
  about varchar not null default '',
  birthday date null,
  regdate date not null default CURRENT_DATE,
  isAdmin boolean not null default false,
  messages integer not null default 0 check (messages >= 0)
);



CREATE TABLE credentials(
  id serial primary key,
  email varchar not null,
  pwd varchar not null,
  salt varchar not null,
  saltmode integer not null default 1,   
  usercard integer references usercards(id)
);

CREATE TABLE sections(
  id serial primary key,
  name varchar not null,
  description varchar not null default ''
);

CREATE TABLE topic_statuses(
  id serial primary key,
  name char(12)
);

CREATE TABLE topics(
  id serial primary key,
  name varchar not null,
  description varchar not null default '',
  status integer not null,
  section integer references sections(id)
);

CREATE TABLE messages(
  uid serial primary key,
  topic integer references topics(id),
  author integer references usercards(id),
  message varchar not null, -- len constraint >2
  time timestamp not null default CURRENT_TIMESTAMP,
  reply integer references messages
);

CREATE TABLE sections_moders(
  section_id integer,
  moder_id integer,
  asigner_id integer references usercards(id),
  asigned date null default CURRENT_DATE,
  primary key(section_id, moder_id)
);


-- заполнение БД
INSERT INTO usercards(nick, about, birthday, regdate, isAdmin) 
VALUES
('admin', 'very oldboy','2003-06-07', '2024-02-18', true),
('ancient2', 'very old ','2000-02-15', '2024-02-19', false),
('ancient3', 'very boy','2000-02-15', '2024-02-19', false),
('ancient4', 'pew-pew','2000-02-15', '2024-02-19', false),
('ancient5', 'cake is a lie','2000-02-15', '2024-02-19', false),
('ancient6', 'Arent surrender our proud Varyag','1904-02-15', '2024-02-18', false),
('ancient7', 'fafboi','2000-02-15', '2024-02-19', false),
('ancient8', 'fire in the hole','2000-02-15', '2024-02-19', false),
('ancient9', 'no gimnicks','2000-02-15', '2024-02-19', false),
('internet_shark01', 'ARGH!','2000-02-15', '2024-02-19', false),
('internet_shark02', '🦈','2000-02-15', '2024-02-19', false);

INSERT INTO credentials(email, pwd, salt, saltmode, usercard)
VALUES     --SHA256 
-- 42777742   ->   a3da1153612b6c2fa0a80c68e537381c352e4f22fee7d6d89c695406de8c807d
('ytatichno@mail.ru', 'a3da1153612b6c2fa0a80c68e537381c352e4f22fee7d6d89c695406de8c807d', '42', 1, (select id from usercards where nick = 'admin')),  
('ancient2@mail.ru', '1234', '42', 0, (select id from usercards where nick = 'ancient2')),
('ancient3@mail.ru', '1234', '42', 0, (select id from usercards where nick = 'ancient3')),
('ancient4@mail.ru', '1234', '42', 0, (select id from usercards where nick = 'ancient4')),
('ancient5@mail.ru', '1234', '42', 0, (select id from usercards where nick = 'ancient5')),
('ancient6@mail.ru', '1234', '42', 0, (select id from usercards where nick = 'ancient6')),
('ancient7@mail.ru', '1234', '42', 0, (select id from usercards where nick = 'ancient7')),
('ancient8@mail.ru', '1234', '42', 0, (select id from usercards where nick = 'ancient8')),
('ancient9@mail.ru', '1234', '42', 0, (select id from usercards where nick = 'ancient9')),
('internet_shark01@sea.mail.ru', '0000', '1234', 0, (select id from usercards where nick = 'internet_shark01')),
('internet_shark02@ocean.mail.ru', '0000', '1234', 0, (select id from usercards where nick = 'internet_shark02'));

INSERT INTO sections(name, description)
VALUES
('Подслушано ВМК', 'Самые свежие новости и обсуждения нашего факультета. Предложка для мемов открыта!'),
('Подслушано МехМат', 'Самые свежие новости и обсуждения нашего факультета. Предложка для мемов открыта!'),
('Подслушано ФизФак', 'Самые свежие новости и обсуждения нашего факультета. Предложка для мемов открыта!'),
('Подслушано ФКИ', 'Самые свежие новости и обсуждения нашего факультета. Предложка для мемов открыта!'),
('Подслушано ИСП', 'Самые свежие новости и обсуждения нашего института. Предложка для мемов открыта!'),
('Подслушано ИПМ', 'Самые свежие новости и обсуждения нашего института. Предложка для мемов открыта!');

INSERT INTO topic_statuses(name) VALUES ('Обычная'),('Свежая'),('Пассивная'),('Активная'),('Переехала');

INSERT INTO topics (name, description, status, section) 
VALUES
('Расписание пар и полезные ссылки', 'Расписание есть на сайте факультета: https://cs.msu.ru/vesna2024.pdf', 
(SELECT id from topic_statuses where name = 'Обычная'), (SELECT id from sections where name = 'Подслушано ВМК')),
('Расписание пар и полезные ссылки', 'Расписание есть на сайте факультета: https://math.msu.ru/vesna2024.pdf', 
(SELECT id from topic_statuses where name = 'Обычная'), (SELECT id from sections where name = 'Подслушано МехМат')),
('Расписание пар и полезные ссылки', 'Расписание есть на сайте факультета: https://phys.msu.ru/vesna2024.pdf', 
(SELECT id from topic_statuses where name = 'Обычная'), (SELECT id from sections where name = 'Подслушано ФизФак')),
('Расписание пар и полезные ссылки', 'Расписание есть на сайте факультета: https://astro.msu.ru/vesna2024.pdf', 
(SELECT id from topic_statuses where name = 'Обычная'), (SELECT id from sections where name = 'Подслушано ФКИ')),
('Расписание пар и полезные ссылки', 'Расписание есть на сайте института: https://isp.ras.ru/vesna2024.pdf', 
(SELECT id from topic_statuses where name = 'Обычная'), (SELECT id from sections where name = 'Подслушано ИСП')),
('Расписание пар и полезные ссылки', 'Расписание есть на сайте института: https://keldysh.ras.ru/vesna2024.pdf', 
(SELECT id from topic_statuses where name = 'Обычная'), (SELECT id from sections where name = 'Подслушано ИПМ'));

INSERT INTO sections_moders(section_id, moder_id, asigner_id, asigned) 
VALUES
((SELECT id from sections where name = 'Подслушано ВМК'),
 (SELECT id from usercards where nick = 'admin'),
 (SELECT id from usercards where nick = 'admin'), 
 CURRENT_DATE),
((SELECT id from sections where name = 'Подслушано МехМат'),
 (SELECT id from usercards where nick = 'ancient2'),
 (SELECT id from usercards where nick = 'admin'), 
 CURRENT_DATE),
((SELECT id from sections where name = 'Подслушано ФизФак'),
 (SELECT id from usercards where nick = 'ancient3'),
 (SELECT id from usercards where nick = 'admin'), 
 CURRENT_DATE),
((SELECT id from sections where name = 'Подслушано ФКИ'),
 (SELECT id from usercards where nick = 'ancient4'),
 (SELECT id from usercards where nick = 'admin'), 
 CURRENT_DATE),
((SELECT id from sections where name = 'Подслушано ИСП'),
 (SELECT id from usercards where nick = 'ancient5'),
 (SELECT id from usercards where nick = 'admin'), 
 CURRENT_DATE),
((SELECT id from sections where name = 'Подслушано ИПМ'),
 (SELECT id from usercards where nick = 'ancient6'),
 (SELECT id from usercards where nick = 'admin'), 
 CURRENT_DATE),
((SELECT id from sections where name = 'Подслушано ВМК'),
 (SELECT id from usercards where nick = 'internet_shark01'),
 (SELECT id from usercards where nick = 'admin'), 
 CURRENT_DATE);

INSERT INTO messages(topic, author, message) 
VALUES
((SELECT id from topics where name = 'Подслушано ВМК'),
 (SELECT id from usercards where nick = 'admin'),
 'Кто придумал ставить англ первой парой?'),
((SELECT id from topics where name = 'Подслушано ВМК'),
 (SELECT id from usercards where nick = 'ancient2'),
 'Кто придумал ставить линал после обеда?'),
((SELECT id from topics where name = 'Подслушано ВМК'),
 (SELECT id from usercards where nick = 'ancient3'),
 'Кто придумал матан две пары подряд ставтить?'),
((SELECT id from topics where name = 'Подслушано ВМК'),
 (SELECT id from usercards where nick = 'ancient4'),
 'Кто придумал ставить русский первой парой в суботу?'),
((SELECT id from topics where name = 'Подслушано ВМК'),
 (SELECT id from usercards where nick = 'ancient5'),
 'Кто придумал хадить на руский на ВМК?'),
((SELECT id from topics where name = 'Подслушано ФКИ'),
 (SELECT id from usercards where nick = 'ancient6'),
 'Где искать П12? Подскажите! Пара через 5 мин'),
((SELECT id from topics where name = 'Подслушано ВМК'),
 (SELECT id from usercards where nick = 'ancient7'),
 'Кто придумал пары?'),
((SELECT id from topics where name = 'Подслушано ВМК'),
 (SELECT id from usercards where nick = 'ancient8'),
 'Пары не удары можно пропустить😎'),
((SELECT id from topics where name = 'Подслушано МехМат'),
 (SELECT id from usercards where nick = 'ancient9'),
 '😂😂🤣'),
((SELECT id from topics where name = 'Подслушано ВМК'),
 (SELECT id from usercards where nick = 'ancient1'),
 'Лучше пальцем чистить фары, чем сидеть 4 пары'),
((SELECT id from topics where name = 'Подслушано ВМК'),
 (SELECT id from usercards where nick = 'ancient2'),
 'Кто придумал матан?'),
((SELECT id from topics where name = 'Подслушано ВМК'),
 (SELECT id from usercards where nick = 'ancient2'),
 'Коши'),
((SELECT id from topics where name = 'Подслушано МехМат'),
 (SELECT id from usercards where nick = 'ancient3'),
 'Кто придумал ставить АнГем 5ой парой?'),
((SELECT id from topics where name = 'Подслушано МехМат'),
 (SELECT id from usercards where nick = 'ancient4'),
 'Кто придумал теорию чисел на 1ом курсе?'),
((SELECT id from topics where name = 'Подслушано ВМК'),
 (SELECT id from usercards where nick = 'ancient5'),
 'Кот🙀'),
((SELECT id from topics where name = 'Подслушано ФизФак'),
 (SELECT id from usercards where nick = 'ancient6'),
 'Обновите ссылку! Эта не актуальна'),
((SELECT id from topics where name = 'Подслушано ВМК'),
 (SELECT id from usercards where nick = 'ancient7'),
 'Кто придумал ставить парой?'),
((SELECT id from topics where name = 'Подслушано МехМат'),
 (SELECT id from usercards where nick = 'ancient8'),
 'МехМатушка!'),
((SELECT id from topics where name = 'Подслушано МехМат'),
 (SELECT id from usercards where nick = 'ancient8'),
 'Коши! - не души🙏'),
((SELECT id from topics where name = 'Подслушано ВМК'),
 (SELECT id from usercards where nick = 'ancient9'),
 'Кто придумал выкладывать расписание в pdf'),
((SELECT id from topics where name = 'Подслушано ФКИ'),
 (SELECT id from usercards where nick = 'internet_shark01'),
 'АУ!'),
((SELECT id from topics where name = 'Подслушано ФКИ'),
 (SELECT id from usercards where nick = 'admin'),
 'Ау-Ау-Ау');




