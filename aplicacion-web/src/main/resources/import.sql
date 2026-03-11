-- Datos iniciales de listas — Hibernate crea las tablas con ddl-auto=update
-- Cada sentencia DEBE estar en UNA sola línea (requisito de Hibernate import.sql)

INSERT INTO listas (id, nombre, proveedor) VALUES (1,'DURAVIT','DURAVIT') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (2,'DESES_PLAST','DESES_PLAST') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (3,'ALMANDOZ','ALMANDOZ') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (4,'BEL_GIOCO','BEL_GIOCO') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (5,'NADIR','NADIR') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (6,'TRAMONTINA','TRAMONTINA') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (7,'WHEATON','WHEATON') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (8,'CAMPAGNA','CAMPAGNA') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (9,'CHEF','CHEF') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (11,'KUFO','KUFO') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (12,'DAYSAL','DAYSAL') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (13,'GUADIX','GUADIX') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (14,'LOEKEMEYER','LOEKEMEYER') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (15,'LUMILAGRO','LUMILAGRO') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (16,'MAN_FER','MAN_FER') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (17,'MARINEX','MARINEX') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (18,'COLORES','COLORES') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (19,'DATOMAX','DATOMAX') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (20,'PLASTIC_HOUSE','PLASTIC_HOUSE') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (21,'YESI','YESI') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (22,'RESPONTECH','RESPONTECH') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (23,'RIGOLLEAU','RIGOLLEAU') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (24,'LEMA','LEMA') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (25,'RODECA','RODECA') ON CONFLICT (id) DO NOTHING;
INSERT INTO listas (id, nombre, proveedor) VALUES (26,'DIFPLAST','DIFPLAST') ON CONFLICT (id) DO NOTHING;

-- Sincronizar el contador de la secuencia de IDs para que los próximos INSERTs no colisionen
SELECT setval('listas_id_seq', (SELECT MAX(id) FROM listas));
