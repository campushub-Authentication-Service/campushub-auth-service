# campushub-auth-service
# CampusHub – User / Authentication Service

**Grup 2 | Kullanıcı ve Kimlik Doğrulama Servisi**

CampusHub projesinin Kullanıcı ve Kimlik Doğrulama Servisi, kullanıcı kaydı, giriş, JWT (JSON Web Token) oluşturma ve doğrulama işlemlerini gerçekleştirir. Ayrıca diğer mikroservislerin kullanıcı profili bilgilerine erişebilmesini sağlar.

Servis, Java 17 ve Spring Boot 3.4.3 kullanılarak geliştirilmiştir. Kullanıcı verileri PostgreSQL veritabanında saklanır. Parolalar BCrypt ile hashlenir ve kimlik doğrulama işlemleri stateless JWT yapısıyla gerçekleştirilir.

## 1. Kullanılan Teknolojiler

| Teknoloji | Kullanım Amacı |
|---|---|
| Java 17 | Backend programlama dili |
| Spring Boot 3.4.3 | Backend framework |
| Spring Data JPA | Veritabanı işlemleri |
| Spring Security | Güvenlik yapılandırması |
| PostgreSQL 15 | Ana veritabanı |
| H2 Database | Test veritabanı |
| JWT (JJWT 0.12.6) | Token oluşturma ve doğrulama |
| BCrypt | Parola hashleme |
| Maven | Bağımlılık ve proje yönetimi |
| Docker & Docker Compose | Konteynerleştirme |
| SpringDoc OpenAPI / Swagger | API dokümantasyonu |

## 2. Servis ve Port Bilgileri

| Parametre | Değer |
|---|---|
| Servis Adı | User / Authentication Service |
| Local URL | `http://localhost:8002` |
| Port | `8002` |
| Veritabanı | PostgreSQL 15 |
| Veritabanı Adı | `auth_db` |
| Docker Network | `campushub-net` |

Servis varsayılan olarak 8002 portunda çalışır.

## 3. Projeyi Çalıştırma

### Gereksinimler

- Java 17
- Maven (veya projedeki Maven Wrapper)
- Docker Desktop ve Docker Compose
- PostgreSQL 15 (Docker kullanılmayacaksa)

### Yöntem 1: Docker ile Çalıştırma (Önerilen)

Repository bilgisayara klonlanır:

```bash
git clone <REPOSITORY_URL>
cd <PROJECT_FOLDER>
```

`.env.example` dosyası örnek alınarak `.env` dosyası oluşturulur ve gerekli ortam değişkenleri düzenlenir.

Ardından servis aşağıdaki komutla başlatılır:

```bash
docker compose up --build -d
```

Çalışan konteynerleri kontrol etmek için:

```bash
docker compose ps
```

Logları görüntülemek için:

```bash
docker compose logs -f
```

Servisi durdurmak için:

```bash
docker compose down
```

### Yöntem 2: Maven ile Çalıştırma

Öncelikle PostgreSQL servisinin çalıştığından ve veritabanı bağlantı ayarlarının yapılandırıldığından emin olunmalıdır.

Projeyi derlemek için:

```bash
./mvnw clean install
```

Windows üzerinde:

```bash
mvnw.cmd clean install
```

Uygulamayı başlatmak için:

```bash
./mvnw spring-boot:run
```

Windows üzerinde:

```bash
mvnw.cmd spring-boot:run
```

Uygulama varsayılan olarak aşağıdaki adreste çalışır:

`http://localhost:8002`

## 4. Ortam Değişkenleri

Servisin yapılandırılması için aşağıdaki ortam değişkenleri kullanılır:

| Değişken | Açıklama |
|---|---|
| `SERVER_PORT` | Uygulamanın çalışacağı port |
| `DB_URL` | PostgreSQL bağlantı adresi |
| `DB_USER` | Veritabanı kullanıcı adı |
| `DB_PASSWORD` | Veritabanı parolası |
| `JWT_SECRET` | JWT imzalama anahtarı |
| `JWT_EXPIRATION` | JWT geçerlilik süresi |

Örnek yapılandırma:

```env
SERVER_PORT=8002
DB_URL=jdbc:postgresql://localhost:5432/auth_db
DB_USER=postgres
DB_PASSWORD=your_password
JWT_SECRET=your_secret_key
JWT_EXPIRATION=86400000
```

**Not:** Bu değerler örnektir. Gerçek yapılandırma `.env.example` ve uygulama ayarlarıyla uyumlu olmalıdır. Gerçek parola ve JWT anahtarı GitHub'a yüklenmemelidir.

## 5. API Endpointleri

Tüm endpointler aşağıdaki temel URL üzerinden çağrılır:

`http://localhost:8002`

### 5.1 Kullanıcı Kaydı

**POST** `/auth/register`

Yeni kullanıcı oluşturur.

Request Body:

```json
{
  "name": "Ali Yılmaz",
  "email": "ali@ogrenci.edu.tr",
  "password": "123456"
}
```

Başarılı Yanıt – `201 Created`:

```json
{
  "id": 42,
  "name": "Ali Yılmaz",
  "email": "ali@ogrenci.edu.tr"
}
```

Kurallar:
- Name, email ve password alanları zorunludur.
- Parola en az 6 karakter olmalıdır.
- E-posta adresi benzersiz olmalıdır.
- E-posta küçük harfe dönüştürülerek saklanır.
- Yeni kayıt olan kullanıcının varsayılan rolü `STUDENT` olur.
- Kullanıcı kayıt isteği üzerinden rol belirleyemez.

Olası Hatalar:
- `400 Bad Request`: Geçersiz veya eksik veri.
- `409 Conflict`: E-posta adresi zaten kayıtlı.

### 5.2 Kullanıcı Girişi

**POST** `/auth/login`

Kullanıcının e-posta ve parolasını doğrular. Başarılı girişte JWT üretir.

Request Body:

```json
{
  "email": "ali@ogrenci.edu.tr",
  "password": "123456"
}
```

Başarılı Yanıt – `200 OK`:

```json
{
  "token": "<JWT_TOKEN>",
  "userId": 42
}
```

Olası Hatalar:
- `400 Bad Request`: Geçersiz istek.
- `401 Unauthorized`: E-posta veya parola hatalı.

### 5.3 JWT Token Doğrulama

**GET** `/auth/validate`

Gönderilen JWT token'ın geçerli olup olmadığını kontrol eder.

Request Header:

```http
Authorization: Bearer <JWT_TOKEN>
```

Başarılı Yanıt – `200 OK`:

```json
{
  "valid": true,
  "userId": 42,
  "email": "ali@ogrenci.edu.tr",
  "role": "STUDENT"
}
```

Olası Hatalar:
- `401 Unauthorized`: Geçersiz veya süresi dolmuş token.

Bu endpoint özellikle API Gateway ve diğer mikroservisler tarafından kullanılabilir.

### 5.4 Kullanıcı Profil Bilgisi

**GET** `/users/{id}`

Belirtilen kullanıcıya ait profil bilgilerini getirir.

Örnek:

```http
GET http://localhost:8002/users/42
```

Başarılı Yanıt – `200 OK`:

```json
{
  "id": 42,
  "name": "Ali Yılmaz",
  "email": "ali@ogrenci.edu.tr",
  "role": "STUDENT"
}
```

Olası Hatalar:
- `404 Not Found`: Kullanıcı bulunamadı.

Bu endpoint özellikle Registration Service (Grup 4) tarafından kullanıcı bilgilerine erişmek için kullanılabilir.

### 5.5 Sağlık Kontrolü (Health Check)

**GET** `/health`

Servisin çalışır durumda olup olmadığını kontrol eder.

Örnek:

```http
GET http://localhost:8002/health
```

Başarılı Yanıt – `200 OK`:

```json
{
  "service": "auth-service",
  "status": "UP",
  "version": "1.0.0"
}
```

## 6. Kullanıcı Rolleri

Sistemde üç farklı kullanıcı rolü tanımlanmıştır:

| Rol | Açıklama |
|---|---|
| `STUDENT` | Standart öğrenci hesabı |
| `CLUB_ADMIN` | Kulüp yöneticisi |
| `SYSTEM_ADMIN` | Sistem yöneticisi |

Genel kullanıcı kayıt işleminde herkese varsayılan olarak `STUDENT` rolü atanır. Yönetici rolleri normal kayıt isteği üzerinden atanamaz.

## 7. JWT Kimlik Doğrulama Akışı

1. Kullanıcı `/auth/register` endpointine POST isteği göndererek kayıt olur.
2. Kullanıcı `/auth/login` endpointi üzerinden giriş yapar.
3. E-posta ve parola doğruysa sunucu JWT token üretir.
4. Kullanıcı veya ilgili servis token'ı `Authorization: Bearer <TOKEN>` başlığıyla gönderir.
5. API Gateway veya diğer servisler `/auth/validate` endpointi üzerinden token'ı doğrular.
6. Kullanıcı profil bilgileri gerektiğinde `/users/{id}` endpointi çağrılır.

JWT içerisinde kullanıcıya ait e-posta (`sub`), `userId`, `role` ve `name` bilgileri taşınır.

## 8. Güvenlik

- Parolalar BCrypt ile hashlenerek saklanır.
- Açık metin parolalar veritabanında tutulmaz.
- JWT tokenları HMAC-SHA tabanlı imza ile korunur.
- JWT imzalama anahtarı ortam değişkeninden alınır.
- E-posta adresleri normalize edilir.
- Hatalı girişlerde e-posta veya parolanın hangisinin yanlış olduğu açıklanmaz.
- Genel kayıt isteği üzerinden yönetici rolü atanması engellenmiştir.
- CORS yapılandırması ile frontend uygulamalarının iletişim kurması desteklenir.

## 9. API Dokümantasyonu (Swagger)

Servis çalışırken interaktif API dokümantasyonuna aşağıdaki adresten ulaşılabilir:

`http://localhost:8002/swagger-ui.html`

Swagger üzerinden endpointler incelenebilir ve API istekleri doğrudan test edilebilir.

## 10. Testler

Serviste birim ve entegrasyon testleri bulunmaktadır.

Test altyapısında:
- H2 bellek içi test veritabanı kullanılır.
- AuthService birim testleri çalıştırılır.
- MockMvc ile HTTP entegrasyon testleri gerçekleştirilir.
- Toplam 15 test bulunmaktadır.

Testleri çalıştırmak için:

```bash
./mvnw test
```

Windows üzerinde:

```bash
mvnw.cmd test
```

## 11. Postman ile API Testi

API endpointleri Postman üzerinden test edilebilir.

Örnek test sırası:

1. `POST /auth/register` – Kullanıcı kaydı.
2. `POST /auth/login` – Kullanıcı girişi ve JWT alma.
3. `GET /auth/validate` – JWT doğrulama.
4. `GET /users/{id}` – Kullanıcı profil bilgisi.
5. `GET /health` – Servis sağlık kontrolü.

Postman koleksiyonu repository içerisindeki `postman/` klasöründe bulunmaktadır.

## 12. Proje Mimarisi

Servis katmanlı mimariyle geliştirilmiştir.

| Katman | Sorumluluk |
|---|---|
| Controller | HTTP isteklerini karşılar |
| Service | İş mantığını ve kimlik doğrulama işlemlerini yürütür |
| Repository | Veritabanı erişimini sağlar |
| Model | User ve Role veri modellerini içerir |
| DTO | İstek ve yanıt nesnelerini tanımlar |
| Security | JWT, BCrypt ve güvenlik ayarlarını yönetir |
| Exception | Merkezi hata yönetimini sağlar |

## 13. Proje Durumu

| Özellik | Durum |
|---|---|
| Kullanıcı kaydı | Tamamlandı |
| Kullanıcı girişi | Tamamlandı |
| JWT üretimi ve doğrulaması | Tamamlandı |
| Kullanıcı rolleri | Tamamlandı |
| Kullanıcı profil sorgulama | Tamamlandı |
| Health Check | Tamamlandı |
| Docker yapılandırması | Tamamlandı |
| Swagger dokümantasyonu | Tamamlandı |
| Birim ve entegrasyon testleri | Tamamlandı |
| Postman koleksiyonu | Tamamlandı |

## 14. Proje Ekibi

**Grup 2 – User / Authentication Service**

- Zişan Yılmazsoy – GitHub ve Docker Sorumlusu
- Yurdanur Çiftçi – Postman ve Test Sorumlusu
- Ülkü Gedik – API Entegrasyonu Sorumlusu
- Elanur Erarslan – Genel Yönetim ve Cloud Sorumlusu

---

**CampusHub – Group 2 | User / Authentication Service**