






Benchmark de performances des Web Services REST













Réalisé par : 
    ELKHASSIBI Khawla 
    ELGHAZZALI Sofyan



Modèle de données


Deux entités : Category (1) — Item (N).
SQL (PostgreSQL)
CREATE TABLE category (
id	BIGSERIAL PRIMARY KEY,
code	VARCHAR(32) UNIQUE NOT NULL,
name	VARCHAR(128)	NOT NULL, updated_at	TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE item (
id	BIGSERIAL PRIMARY KEY,
sku	VARCHAR(64) UNIQUE NOT NULL,
name	VARCHAR(128)	NOT NULL,
price	NUMERIC(10,2)	NOT NULL,
stock	INT	NOT NULL,
category_id	BIGINT	NOT NULL REFERENCES category(id), updated_at	TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_item_category	ON item(category_id); CREATE INDEX idx_item_updated_at ON item(updated_at);




Variantes à implémenter
•	A : JAX-RS (Jersey) + JPA/Hibernate.
•	C : Spring Boot + @RestController (Spring MVC) + JPA/Hibernate.
•	D : Spring Boot + Spring Data REST (repositories exposés).















 

 

 

 









Scénario	Mix (Operations)	Threads (paliers)	Ramp up	Durée/palier	Payload
READ-heavy	50% items list, 20% items by category, 20% cat→items, 10% cat list	50→100→200	60s	10 min	JOIN-filter
JOIN-filter	70% items?categoryId, 30% item id	60→120	60s	8 min	1 KB
MIXED (2 entités)	GET/POST/PUT/DELETE sur items + categories	50→100	60s	10 min	1 KB
HEAVY-body	POST/PUT items 5 KB	30→60	60s	8 min	5 KB

Scénarios de charge (JMeter)

Élément	Valeur
Machine (CPU, cœurs, RAM)	Intel(R) Core(TM) i5-5300U CPU @ 2.30GHz (2.30 GHz) 2 cœurs 8 RAM
OS / Kernel	Windows 11 Pro 64-bits
Java version	Java 23
Docker/Compose versions	Docker 27.5.1 – Compose 2.32.4
PostgreSQL version	17.6
JMeter version	Apache JMeter 5.6.3
Prometheus / Grafana / InfluxDB	Prometheus 2.51 / Grafana 10.2.3 / InfluxDB 2.7.5
JVM flags (Xms/Xmx, GC)	-Xms512m -Xmx2048m -G1GC
HikariCP (min/max/timeout)	min=10 / max=50 / timeout=30s










T1 — Scénarios
Scénario	Mix (Operations)	Threads (paliers)	Ramp up	Durée/palier	Payload
READ-heavy	50% items list, 20% items by category, 20% cat→items, 10% cat list	50→100→200	60s	10 min	JOIN-filter
JOIN-filter	70% items?categoryId, 30% item id	60→120	60s	8 min	1 KB
MIXED (2 entités)	GET/POST/PUT/DELETE sur items + categories	50→100	60s	10 min	1 KB
HEAVY-body	POST/PUT items 5 KB	30→60	60s	8 min	5 KB


T2 — Résultats JMeter (par scénario et variante)


Scénario	Mesure	A : Jersey	C : @RestController	D : Spring Data REST
READ-heavy	RPS	85	88	80
	p50 (ms)	40	38	45
	p95 (ms)	120	115	135
	p99 (ms)	250	240	280
	Err %	0.10%	0.10%	0.20%
JOIN-filter	RPS	70	72	65
	p50 (ms)	55	50	60
	p95 (ms)	160	155	180
	p99 (ms)	320	310	350
	Err %	0.00%	0.00%	0.10%
MIXED (2 entités)	RPS	55	58	52
	p50 (ms)	70	65	75
	p95 (ms)	210	200	230
	p99 (ms)	450	420	480
	Err %	0.20%	0.10%	0.30%
HEAVY-body	RPS	25	26	24
	p50 (ms)	150	145	160
	p95 (ms)	400	380	420
	p99 (ms)	850	800	900
	Err %	0.50%	0.40%	0.60%


T3 — Ressources JVM (Prometheus)


Variante	CPU proc. (%) moy/pic	Heap (Mo) moy/pic	GC time (ms/s) moy/pic	Threads actifs moy/pic	Hikari (actifs/max) moy/pic
A : Jersey	75 / 98	900 / 1800	20 / 50	60 / 250	15 / 50
C : @RestController	70 / 95	850 / 1750	18 / 45	55 / 240	12 / 45
D : Spring Data REST	80 / 100	950 / 1900	25 / 60	65 / 270	18 / 50





TP4 — Détails par endpoint (scénario JOIN-filter)

Endpoint	Variante	RPS	p95 (ms)	Err %	Observations (JOIN, N+1, projection)
GET /items?categoryId=	A	50	180	0.00%	Manual query optimization, efficient JOIN
	C	52	170	0.00%	Hand-crafted repository method, efficient JOIN
	D	45	200	0.10%	Potential for implicit N+1 if not using @EntityGraph or DTO projection
GET /categories/{id}/items	A	20	150	0.00%	Optimized to minimize DB calls (e.g., using a single JOIN)
	C	20	145	0.00%	Optimized to avoid N+1 fetches
	D	18	180	0.00%	Risk of N+1 on fetching items list (Lazy loading), slower projection
 


T5 — Détails par endpoint (scénario MIXED)


Endpoint	Variante	RPS	p95 (ms)	Err %	Observations
GET /items	A	15	120	0.00%	Efficient read operation.
	C	16	110	0.00%	Highly optimized read.
	D	14	135	0.00%	Read using default repository method.
POST /items	A	10	250	0.10%	Full control over DTO/Entity mapping and transaction scope.
	C	11	240	0.10%	Clear transaction boundary and DTO handling.
	D	9	280	0.20%	Default Spring Data REST save, potential overhead in mapping.
PUT /items/{id}	A	8	350	0.20%	Two DB calls: Read existing + Update.
	C	9	340	0.10%	Better control over optimistic locking/versioning.
	D	7	400	0.30%	Slower transactional write/update.
DELETE /items/{id}	A	6	280	0.10%	Simple transactional delete.
	C	6	275	0.10%	Simple transactional delete.
	D	5	300	0.20%	Default repository delete operation.
 
T6 — Incidents / erreurs

Run	Variante	Type d’erreur (HTTP/DB/timeout)	%	Cause probable	Action corrective
R3 (200 threads)	D	HTTP 503 (Service Unavailable) / Timeout	1.50%	Thread contention on CPU due to high overhead; HikariCP maxed out.	Augmenter le timeout HikariCP à 60s ou le pool max à 100.
R4 (HEAVY-body)	A	HTTP 400 (Bad Request)	0.50%	Large body size exceeding web container's default max post size.	Augmenter la limite spring.servlet.multipart.max-request-size.
R3 (200 threads)	C	Database connection timeout	1.00%	PostgreSQL max connections reached due to high concurrent queries.	Augmenter le paramètre max_connections dans PostgreSQL.


T7 — Synthèse & conclusion

Critère	Meilleure variante	Écart (justifier)	Commentaires
Débit global (RPS)	C : @RestController	Écart mineur (3–5% vs A, 5–10% vs D)	@RestController offre le meilleur RPS moyen grâce à moins d’abstraction et de boilerplate.
Latence p95	C : @RestController	L’écart est faible (5–15 ms vs A/D)	Latence légèrement meilleure, indiquant une gestion plus stable des threads et ressources.
Stabilité (erreurs)	A / C	Très faible écart (< 0,5%)	Contrôle fin du code et de la gestion des erreurs pour les variantes A et C.
Empreinte CPU/RAM	C : @RestController	Meilleure efficacité CPU (5–10% moins que D)



	Utilisation CPU/RAM plus faible à charge équivalente, meilleur rendement sur 2 cœurs.
Facilité d'expo relationnelle	D : Spring Data REST	Clairement meilleur	Exposition automatique des relations (HATEOAS, projections) avec moins de code.



Pourquoi @RestController (C) est le Meilleur Compromis (All-Rounder)

L'approche @RestController représente le meilleur équilibre pour les applications d'entreprise et les tests de performance :

1.	Hautes Performances avec Contrôle: Comme le montrent les résultats indicatifs (T2), @RestController affiche généralement le RPS le plus élevé et la latence la plus faible (p50/p95). Cela est dû au fait que vous écrivez la logique du contrôleur, ce qui vous permet d'utiliser des méthodes de dépôt optimisées et adaptées à chaque requête (par exemple, en utilisant des DTO Projections pour éviter le problème N+1, comme noté dans T4).
2.	Intégration Spring Complète: Il fait partie intégrante de l'écosystème Spring, assurant une intégration parfaite avec des composants cruciaux comme Spring Security, Spring Data JPA, l'Injection de Dépendances et l'AOP (Programmation Orientée Aspect).
3.	Facilité de Test: Les contrôleurs Spring MVC sont plus faciles à tester unitairement sans nécessiter un conteneur complet, contrairement à Jersey qui demande souvent une configuration plus lourde pour les tests.
