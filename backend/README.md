# 本地上传验证

## 本地 JWT 模式

本项目不实现用户登录或令牌签发服务。生产环境使用外部 OAuth2/OIDC 服务验证 JWT；
本地验证使用 `local` profile，由应用生成一个仅用于开发的 HS256 JWT。

使用 Java 21 和 Maven 启动：

```powershell
cd backend
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

启动完成后，在控制台复制 `LOCAL_UPLOAD_JWT=` 后面的完整令牌。它默认在 60 分钟后过期。

上传一个 JPEG、PNG 或 PDF（不超过 10 MiB）：

```powershell
$token = '<复制 LOCAL_UPLOAD_JWT 的值>'
curl.exe -X POST http://localhost:8080/api/upload `
  -H "Authorization: Bearer $token" `
  -F "file=@C:\path\to\example.pdf"
```

成功时返回 HTTP 201：

```json
{ "file_id": "...", "access_url": "..." }
```

`access_url` 不是公开链接；读取它也必须携带同一个有效 JWT：

```powershell
curl.exe http://localhost:8080/api/files/<file_id> `
  -H "Authorization: Bearer $token" `
  --output downloaded-file
```

在浏览器地址栏直接打开该 URL 不会自动携带 `Authorization` 请求头，因此会得到 HTTP 401。
前端应通过 `fetch` 或生成的 OpenAPI 客户端附加 Bearer Token 后读取文件，而不是直接跳转。

无令牌或无效令牌返回 HTTP 401；文件大于 10 MiB 返回 HTTP 413；非 JPEG/PNG/PDF 返回
HTTP 415。上传内容默认存到 `backend/uploads/`，可通过 `UPLOAD_DIRECTORY` 修改。

## 生产模式

使用 `prod` profile 时，必须设置以下环境变量，指向真实的身份提供方：

```powershell
$env:JWT_ISSUER_URI = 'https://idp.example.com/issuer'
$env:JWT_JWK_SET_URI = 'https://idp.example.com/.well-known/jwks.json'
mvn spring-boot:run "-Dspring-boot.run.profiles=prod"
```

不要在生产环境启用 `local` profile，也不要使用 `application-local.yml` 中的默认开发密钥。
